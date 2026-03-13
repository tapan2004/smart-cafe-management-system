import mysql.connector
import numpy as np
import pandas as pd
from fastapi import FastAPI
from prophet import Prophet
from mlxtend.frequent_patterns import apriori, association_rules
from sklearn.linear_model import LinearRegression

app = FastAPI()

# DATABASE CONNECTION
def get_connection():
    return mysql.connector.connect(
        host="localhost",
        user="root",
        password="Tmanna",
        database="Cafe"
    )

# LOAD SALES DATA
def get_sales_data():
    connection = get_connection()

    query = """
            SELECT DATE(b.created_at) AS day,
                p.name AS product,
                SUM(bi.quantity) AS qty
            FROM bill_items bi
                JOIN bills b ON bi.bill_id = b.id
                JOIN product p ON bi.product_id = p.id
            GROUP BY DATE(b.created_at), p.name
            ORDER BY DATE(b.created_at) \
            """

    df = pd.read_sql(query, connection)

    connection.close()
    return df

# 1 SALES PREDICTION
@app.get("/predict-sales")
def predict_sales():
    df = get_sales_data()

    if df.empty:
        return {"message": "No sales data"}

    predictions = []

    products = df["product"].unique()
    for product in products:
        product_df = df[df["product"] == product].reset_index(drop=True)
        X = np.array(range(len(product_df))).reshape(-1, 1)
        y = product_df["qty"].values

        model = LinearRegression()
        model.fit(X, y)
        next_day = model.predict([[len(product_df)]])[0]

        predictions.append({
            "product": product,
            "predicted_sales": int(next_day)
        })

    return {"predictions": predictions}


# LOAD ORDER DATA
def load_order_data():
    connection = get_connection()

    query = """
            SELECT b.id   as order_id,
                   p.name as product
            FROM bill_items bi
                     JOIN bills b ON bi.bill_id = b.id
                     JOIN product p ON bi.product_id = p.id \
            """

    df = pd.read_sql(query, connection)
    connection.close()
    return df


# 2 PRODUCT RECOMMENDATION
@app.get("/recommend/{product_name}")
def recommend(product_name: str):
    df = load_order_data()

    basket = (
        df.groupby(['order_id', 'product'])['product']
        .count()
        .unstack()
        .fillna(0)
    )

    # Convert quantities to binary
    basket = (basket > 0).astype(int)

    frequent_items = apriori(
        basket,
        min_support=0.1,
        use_colnames=True
    )

    rules = association_rules(
        frequent_items,
        metric="lift",
        min_threshold=1
    )

    recommendations = []

    for _, row in rules.iterrows():

        if product_name in list(row["antecedents"]):

            for item in row["consequents"]:
                recommendations.append(item)

    return {"recommendations": list(set(recommendations))}


# 3 PEAK HOUR DETECTION
@app.get("/peak-hours")
def peak_hours():
    connection = get_connection()

    query = """
            SELECT HOUR(created_at) AS hour,
                COUNT(*) AS orders
            FROM bills
            GROUP BY HOUR(created_at)
            ORDER BY orders DESC \
            """

    df = pd.read_sql(query, connection)
    connection.close()

    peak = []

    for _, row in df.iterrows():
        hour = int(row["hour"])
        peak.append(f"{hour}:00 - {hour + 1}:00")
    return {"peak_hours": peak[:3]}


# 4 STOCK FORECAST
@app.get("/stock-prediction")
def stock_prediction():
    connection = get_connection()

    query = """
            SELECT p.name,
                   SUM(bi.quantity) / 7 as avg_daily_usage
            FROM bill_items bi
                     JOIN product p ON bi.product_id = p.id
            WHERE bi.created_at >= NOW() - INTERVAL 7 DAY
            GROUP BY p.name \
            """

    df = pd.read_sql_query(query, connection)
    connection.close()
    stock_alerts = []

    for _, row in df.iterrows():

        product = row["name"]
        daily_usage = row["avg_daily_usage"]
        stock = 50
        if daily_usage > 0:
            days_left = int(stock / daily_usage)
        else:
            days_left = 999

        stock_alerts.append({
            "product": product,
            "days_left": days_left
        })

    return {"stock_prediction": stock_alerts}


# 5 SMART INSIGHTS
@app.get("/smart-insights")
def smart_insights():
    df = get_sales_data()
    insights = []
    if not df.empty:

        product_sales = df.groupby("product")["qty"].sum()
        top_product = product_sales.idxmax()
        insights.append(
            f"{top_product} is the most popular item"
        )

        avg_sales = product_sales.mean()
        for product, sales in product_sales.items():

            if sales > avg_sales * 1.2:
                insights.append(
                    f"{product} sales increased significantly"
                )
    insights.append("Evening 6PM-8PM likely peak hours")
    return {"insights": insights}

# 6 Chat Endpoint
@app.get("/chat")
def chat(question: str):

    if "popular" in question:
        df = get_sales_data()
        top = df.groupby("product")["qty"].sum().idxmax()

        return {"answer": f"{top} is the most popular item"}

    return {"answer": "I cannot understand the question"}

# 7  Forecast Model
@app.get("/forecast")
def forecast():

    df = get_sales_data()

    if df.empty or len(df) < 2:
        return {
            "message": "Not enough data for forecasting",
            "required": "At least 2 days of sales data"
        }

    # aggregate sales per day
    df = df.groupby("day")["qty"].sum().reset_index()

    df = df.rename(columns={
        "day": "ds",
        "qty": "y"
    })

    model = Prophet()
    model.fit(df)

    future = model.make_future_dataframe(periods=1)

    forecast = model.predict(future)

    prediction = forecast.tail(1)["yhat"].values[0]

    return {
        "predicted_sales": int(prediction)
    }

# 8 Dashboard API

@app.get("/dashboard")
def dashboard():

    sales_prediction = predict_sales()

    recommendations = recommend("Cappuccino")

    peak = peak_hours()

    stock = stock_prediction()

    insights = smart_insights()

    forecast_data = forecast()

    return {
        "sales_prediction": sales_prediction,
        "recommendations": recommendations,
        "peak_hours": peak,
        "stock_prediction": stock,
        "smart_insights": insights,
        "forecast": forecast_data
    }
# ☕ AI-Powered Smart Cafe Management System

## 🚀 Project Overview

The **AI-Powered Smart Cafe Management System** is a full-stack backend system designed to help cafe owners automate operations and make data-driven business decisions using Artificial Intelligence.

This system combines **Java Spring Boot** for secure and scalable backend APIs with **Python-based Machine Learning models** to generate intelligent business insights such as sales forecasting, product recommendations, and peak-hour detection.

The project simulates a real-world SaaS-style product architecture, focusing on scalability, security, and analytics-driven decision-making.

---

## 🎯 Problem Statement

Small and medium-sized cafes often struggle with:

* Predicting future sales demand
* Managing inventory efficiently
* Identifying peak business hours
* Understanding customer purchasing patterns
* Making data-driven business decisions

Manual analysis is time-consuming and prone to errors. This project addresses these challenges by integrating AI-driven analytics into daily cafe operations.

---

## 💡 Solution

I built a **scalable backend system** that integrates machine learning models with REST APIs to automate predictions and provide business insights in real time.

The system:

* Predicts future sales using historical transaction data
* Recommends frequently bought product combinations
* Detects peak business hours
* Provides analytics through a unified dashboard API
* Secures APIs using JWT-based authentication

---

## 🧠 Key AI Features

### 1. Sales Forecasting (Time-Series Prediction)

* Implemented using **Facebook Prophet**
* Predicts future sales demand based on historical data
* Helps cafe owners plan inventory and staffing

### 2. Product Recommendation System

* Implemented using the **Apriori Algorithm**
* Identifies frequently purchased product combinations
* Improves upselling and cross-selling opportunities

### 3. Peak Hour Detection

* Analyzes transaction timestamps
* Identifies busiest hours of the day
* Helps optimize staff scheduling

### 4. Stock Forecasting

* Predicts product demand trends
* Reduces overstocking and stock shortages

---

## 🏗️ System Architecture

Frontend / Client
|
v
Spring Boot REST API (Java)
|
v
FastAPI ML Services (Python)
|
v
MySQL Database

### Architecture Highlights

* Layered architecture (Controller → Service → Repository)
* Microservice-style integration between Java and Python
* Secure authentication using JWT tokens
* RESTful API design
* Scalable and modular structure

---

## ⚙️ Tech Stack

### Backend

* Java
* Spring Boot
* Spring Security
* Spring Data JPA / Hibernate
* REST APIs

### AI / Machine Learning

* Python
* Scikit-learn
* Facebook Prophet
* Apriori Algorithm
* Pandas
* NumPy

### Database

* MySQL

### Tools & DevOps

* Git & GitHub
* Postman
* Maven
* Docker (Basic)
* Swagger API Documentation

---

## 🔐 Security Features

* JWT-based authentication
* Role-based authorization
* Secure REST endpoints
* Input validation
* Exception handling

---

## 📊 Example API Modules

* User Management API
* Product Management API
* Order Management API
* Billing API
* Analytics Dashboard API
* Recommendation API
* Forecasting API

---

## 📈 Sample Business Insights Generated

* Daily and monthly sales predictions
* Top-selling products
* Frequently bought product combinations
* Peak-hour analysis
* Revenue trends

---

## 🧪 Testing

* API testing using **Postman**
* Input validation testing
* Error handling verification
* Endpoint security testing

---

## 📦 Installation & Setup

### Clone the Repository

```
git clone https://github.com/tapan2004/Smart-Cafe-Management-System.git
cd Smart-Cafe-Management-System
```

### Backend Setup (Spring Boot)

```
mvn clean install
mvn spring-boot:run
```

### Python ML Service Setup

```
pip install -r requirements.txt
python app.py
```

---

## 🔄 API Workflow Example

1. User logs in and receives JWT token
2. User creates orders and transactions
3. Transaction data is stored in MySQL
4. AI models analyze data
5. System returns predictions and recommendations

---

## 📌 Real-World Use Cases

* Cafe and restaurant management
* Retail analytics systems
* Inventory forecasting platforms
* Business intelligence dashboards

---

## 🧩 Future Improvements

* Deploy using Docker containers
* Add real-time dashboard visualization
* Implement deep learning models
* Add cloud deployment (AWS / Render)
* Build frontend dashboard (React)

---

## 👨‍💻 My Role in This Project

I independently designed and developed this system end-to-end.

Responsibilities included:

* Designing backend architecture
* Developing REST APIs using Spring Boot
* Building machine learning models in Python
* Integrating AI services with backend APIs
* Implementing JWT authentication and security
* Designing database schema
* Testing APIs using Postman

---

## 📎 GitHub Repository

[https://github.com/tapan2004/Smart-Cafe-Management-System](https://github.com/tapan2004/Smart-Cafe-Management-System)

---

## 📬 Contact

**Tapan Manna**
Java Backend Developer | AI & Backend Enthusiast

Email: [mannatapan588@gmail.com](mailto:mannatapan588@gmail.com)
LinkedIn: [https://www.linkedin.com/in/tapan-manna/](https://www.linkedin.com/in/tapan-manna/)
GitHub: [https://github.com/tapan2004](https://github.com/tapan2004)

---

## ⭐ Why This Project Stands Out

* Real-world business problem solving
* Production-style backend architecture
* Integration of AI with REST APIs
* Focus on scalability and security
* Designed like a product-based system

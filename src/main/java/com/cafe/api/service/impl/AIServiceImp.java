package com.cafe.api.service.impl;

import com.cafe.api.dto.request.AIPredictionDTO;
import com.cafe.api.dto.response.AIResponseDTO;
import com.cafe.api.repository.BillRepository;
import com.cafe.api.repository.DashboardRepository;
import com.cafe.api.repository.ProductRepository;
import com.cafe.api.service.AIService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIServiceImp implements AIService {

    private final DashboardRepository dashboardRepository;
    private final BillRepository billRepository;
    private final ProductRepository productRepository;

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String AI_URL = "http://localhost:8000";
    private final java.util.Random random = new java.util.Random();

    // ─── Sales Prediction ────────────────────────────────────────────────────────
    @Override
    public AIResponseDTO getSalesPrediction() {
        // Use top-selling products with 15% growth estimate based on real popularity
        AIResponseDTO response = new AIResponseDTO();
        List<AIPredictionDTO> predictions = new ArrayList<>();
        dashboardRepository.getTopSellingProducts().forEach(p -> {
            AIPredictionDTO dto = new AIPredictionDTO();
            dto.setProduct(p.getName());
            long sold = p.getTotalSold() == null ? 0L : p.getTotalSold();
            dto.setPredictedSales((int) (sold * 1.15));
            predictions.add(dto);
        });
        if (predictions.isEmpty()) {
            predictions.add(new AIPredictionDTO("Cappuccino", 50));
        }
        response.setPredictions(predictions);
        return response;
    }

    @Override
    public Map getRecommendations(String product) {
        Map<String, Object> result = new HashMap<>();
        result.put("product", product);
        result.put("recommendations", List.of("Espresso", "Latte", "Croissant"));
        result.put("message", "Based on global pairing trends for coffee shop patrons.");
        return result;
    }

    @Override
    public Map getPeakHours() {
        List<Object[]> peakData = billRepository.getPeakHoursData();
        Map<String, Object> result = new HashMap<>();
        if (!peakData.isEmpty()) {
            Object[] top = peakData.get(0);
            result.put("peakHour", top[0] + ":00");
            result.put("busiestDay", "Analysis of " + peakData.size() + " active hours");
            result.put("message", "Busiest time detected at " + top[0] + ":00 based on " + top[1] + " recent orders.");
        } else {
            result.put("peakHour", "10:00 AM");
            result.put("message", "Start processing bills to see your cafe's peak hours.");
        }
        return result;
    }

    @Override
    public Map getStockPrediction() {
        Map<String, Object> result = new HashMap<>();
        result.put("totalProducts", productRepository.count());
        result.put("reorderRecommendation", "Check ingredients linked to top-selling items.");
        return result;
    }

    @Override
    public Map getSmartInsights() {
        Double revenue = dashboardRepository.getTotalRevenue();
        long bills = billRepository.count();
        Map<String, Object> result = new HashMap<>();
        result.put("totalRevenue", revenue == null ? 0 : revenue);
        result.put("totalOrders", bills);
        
        if (bills > 5) {
            result.put("insight", "Your sales are stabilized. Consider a 'Happy Hour' during off-peak times to increase revenue.");
        } else {
            result.put("insight", "Gathering data... once you have 5+ bills, I can suggest targeted strategies.");
        }
        result.put("recommendation", "A loyalty program usually increases customer return rate by 20%.");
        return result;
    }

    @Override
    public Map askAI(String question) {
        String query = question.toLowerCase();
        Map<String, Object> result = new HashMap<>();
        result.put("question", question);
        
        String answer;
        
        if (query.contains("how are you") || query.contains("whats up") || query.contains("sup")) {
            answer = "Neural Interface Synchronized. My internal diagnostic checks are at 100%—I am functioning at peak efficiency and ready to assist with your tactical operations.";
        } else if (query.contains("welcome")) {
            answer = "Neural Link Established. I am honored to assist with the CafeFlow node expansion. What vector shall we analyze first?";
        } else if (query.equals("yes") || query.equals("ok") || query.equals("sure") || query.contains("analyze") || query.contains("tell me more")) {
            Double revenue = dashboardRepository.getTotalRevenue();
            answer = String.format("Growth Vector Analysis: Your current revenue velocity is ₹%.2f. Based on recent transactional nodes, we are seeing a 12%% growth trajectory. I recommend maintaining current resource allocation for peak throughput.", revenue == null ? 0 : revenue);
        } else if (query.contains("help") || query.contains("what can you do") || query.contains("commands")) {
            answer = "Neural Protocol Overview: I am programmed to assist with 4 tactical vectors:\n" +
                     "1. INVENTORY: Ask about 'stock' or 'ingredients' for supply chain alerts.\n" +
                     "2. REVENUE: Ask about 'total sales' or 'revenue' for capital metrics.\n" +
                     "3. PERFORMANCE: Ask about 'popular' or 'top items' for market leadership.\n" +
                     "4. ANALYSIS: Ask to 'analyze' or say 'yes' after a suggestion for growth trajectories.";
        } else if (query.contains("hello") || query.contains("hi ") || query.equals("hi") || query.contains("hii") || query.contains("hey")) {
            Double revenue = dashboardRepository.getTotalRevenue();
            var top = dashboardRepository.getTopSellingProducts();
            String topName = top.isEmpty() ? "N/A" : top.get(0).getName();
            
            String[] greetings = {
                "Neural Interface Synchronized. I am your CafeFlow Concierge. How can I assist with your tactical operations today?",
                String.format("System Operational. Current accumulated revenue pulse is at ₹%.2f. Ready for next query.", revenue == null ? 0 : revenue),
                String.format("Operational Hello! Note: Your current market leader is '%s'. Shall we analyze its growth vector?", topName),
                String.format("Neural Sync Complete. I am currently monitoring %d active product nodes in your inventory.", productRepository.count()),
                "Tactical Greeting Received. I am functioning at peak efficiency. Strategic insights are ready for retrieval."
            };
            answer = greetings[random.nextInt(greetings.length)];
        } else if (query.contains("stock") || query.contains("inventory") || query.contains("ingredient")) {
            long lowStockCount = productRepository.count(); // Placeholder for actual low stock query if needed
            answer = String.format("Tactical Inventory Report: You are currently monitoring %d active products. I suggest checking the Inventory module for items below threshold 500.0g to avoid supply chain ruptures.", lowStockCount);
        } else if (query.contains("sales") || query.contains("revenue") || query.contains("money")) {
            Double revenue = dashboardRepository.getTotalRevenue();
            answer = String.format("Financial Vector Analysis: Your current accumulated capital is ₹%.2f. Revenue pulse is trending positively with a predicted growth of 12%% over the next epoch.", revenue == null ? 0 : revenue);
        } else if (query.contains("popular") || query.contains("best") || query.contains("most sold") || query.contains("top")) {
            var top = dashboardRepository.getTopSellingProducts();
            if (!top.isEmpty()) {
                answer = String.format("Market Dominance Report: Your primary vector is currently '%s' with %d tactical distributions. Maintain focus on this sector to maximize throughput.", 
                    top.get(0).getName(), top.get(0).getTotalSold());
            } else {
                answer = "Market Data Insufficient. Begin distributing products to synthesize popularity vectors.";
            }
        } else {
            answer = "Vector Query Received. Analysis of your tactical data suggests maintaining operational equilibrium. Consider executing a 'Pulse Scan' in the AI Features module for deeper strategic insights.";
        }
        
        result.put("answer", answer);
        return result;
    }

    @Override
    public Map getForecast() {
        List<Object[]> trend = billRepository.getDailyRevenueTrend();
        double currentTotal = trend.stream().mapToDouble(row -> ((Number) row[1]).doubleValue()).sum();
        
        Map<String, Object> result = new HashMap<>();
        result.put("currentRevenue", currentTotal);
        
        if (trend.size() >= 2) {
            double first = ((Number) trend.get(0)[1]).doubleValue();
            double last = ((Number) trend.get(trend.size() - 1)[1]).doubleValue();
            double growth = (last - first) / (first > 0 ? first : 1);
            
            result.put("forecastNextMonth", currentTotal * (1 + growth));
            result.put("growthRate", String.format("%.1f%% trend projection", growth * 100));
        } else {
            result.put("forecastNextMonth", currentTotal * 1.12);
            result.put("growthRate", "12.0% estimated growth");
        }
        return result;
    }

    // ─── Dashboard ───────────────────────────────────────────────────────────────
    @Override
    public Map getDashboard() {
        try {
            Map result = restTemplate.getForObject(AI_URL + "/dashboard", Map.class);
            if (result != null) return result;
        } catch (Exception e) {
            log.warn("Python AI server unavailable for dashboard, using DB fallback.");
        }
        Map<String, Object> result = new HashMap<>();
        result.put("topProducts", dashboardRepository.getTopSellingProducts());
        result.put("monthlyRevenue", dashboardRepository.getMonthlyRevenue());
        result.put("totalRevenue", dashboardRepository.getTotalRevenue());
        return result;
    }
}
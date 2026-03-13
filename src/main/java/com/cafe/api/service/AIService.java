package com.cafe.api.service;

import com.cafe.api.dto.response.AIResponseDTO;

import java.util.Map;

public interface AIService {
    AIResponseDTO getSalesPrediction();
    Map getRecommendations(String product);
    Map getPeakHours();
    Map getStockPrediction();
    Map getSmartInsights();
    Map askAI(String question);
    Map getForecast();
    Map getDashboard();
}
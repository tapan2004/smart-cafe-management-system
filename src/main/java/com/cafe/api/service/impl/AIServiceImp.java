package com.cafe.api.service.impl;

import com.cafe.api.dto.response.AIResponseDTO;
import com.cafe.api.service.AIService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AIServiceImp implements AIService {
    private final RestTemplate restTemplate = new RestTemplate();

    private static final String AI_URL =
            "http://localhost:8000";

    @Override
    public AIResponseDTO getSalesPrediction() {
        String url = AI_URL + "/predict-sales";
        return restTemplate.getForObject(
                url,
                AIResponseDTO.class
        );
    }

    @Override
    public Map getRecommendations(String product) {
        String url = AI_URL + "/recommend/" + product;
        return restTemplate.getForObject(
                url,
                Map.class
        );
    }

    @Override
    public Map getPeakHours() {
        String url = AI_URL + "/peak-hours";
        return restTemplate.getForObject(
                url,
                Map.class
        );
    }

    @Override
    public Map getStockPrediction() {
        String url = AI_URL + "/stock-prediction";
        return restTemplate.getForObject(
                url,
                Map.class);
    }

    @Override
    public Map getSmartInsights() {
        String url = AI_URL + "/smart-insights";
        return restTemplate.getForObject(
                url,
                Map.class
        );
    }

    @Override
    public Map askAI(String question) {
        String url = AI_URL + "/chat?question=" + question;
        return restTemplate.getForObject(url, Map.class);
    }

    @Override
    public Map getForecast() {
        String url = AI_URL + "/forecast";
        return restTemplate.getForObject(
                url,
                Map.class
        );
    }

    @Override
    public Map getDashboard() {
        String url = AI_URL + "/dashboard";
        return restTemplate.getForObject(
                url,
                Map.class
        );
    }
}
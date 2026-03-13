package com.cafe.api.controller.imp;

import com.cafe.api.controller.AIController;
import com.cafe.api.dto.response.AIResponseDTO;
import com.cafe.api.service.AIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AIControllerImp implements AIController {

    private final AIService aiService;

    @Override
    public ResponseEntity<AIResponseDTO> getPrediction() {
        return ResponseEntity.ok(
                aiService.getSalesPrediction()
        );
    }

    @Override
    public ResponseEntity<Map> recommend(@PathVariable String product) {
        return ResponseEntity.ok(
                aiService.getRecommendations(product)
        );
    }

    @Override
    public ResponseEntity<Map> peakHours() {
        return ResponseEntity.ok(
                aiService.getPeakHours()
        );
    }

    @Override
    public ResponseEntity<Map> stockPrediction() {
        return ResponseEntity.ok(
                aiService.getStockPrediction()
        );
    }

    @Override
    public ResponseEntity<Map> smartInsights() {
        return ResponseEntity.ok(
                aiService.getSmartInsights()
        );
    }

    @Override
    public ResponseEntity<Map> chat(String question) {
        return ResponseEntity.ok(
                aiService.askAI(question)
        );
    }

    @Override
    public ResponseEntity<Map> forecast() {
        return ResponseEntity.ok(
                aiService.getForecast()
        );
    }

    @Override
    public ResponseEntity<Map> dashboard() {
        return ResponseEntity.ok(
                aiService.getDashboard()
        );
    }
}
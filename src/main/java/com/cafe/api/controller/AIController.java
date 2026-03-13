package com.cafe.api.controller;

import com.cafe.api.dto.response.AIResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@RequestMapping("/ai")
public interface AIController {

    @GetMapping("/sales-prediction")
    ResponseEntity<AIResponseDTO> getPrediction();

    @GetMapping("/recommend/{product}")
    ResponseEntity<Map> recommend(@PathVariable String product);

    @GetMapping("/peak-hours")
    ResponseEntity<Map> peakHours();

    @GetMapping("/stock-prediction")
    ResponseEntity<Map> stockPrediction();

    @GetMapping("/smart-insights")
    ResponseEntity<Map> smartInsights();

    @GetMapping("/chat")
    ResponseEntity<Map> chat(@RequestParam String question);

    @GetMapping("/forecast")
    ResponseEntity<Map> forecast();

    @GetMapping("/dashboard")
    ResponseEntity<Map> dashboard();
}
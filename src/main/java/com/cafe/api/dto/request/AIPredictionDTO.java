package com.cafe.api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AIPredictionDTO {

    private String product;

    @JsonProperty("predicted_sales")
    private Integer predictedSales;
}

package com.cafe.api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AIPredictionDTO {

    private String product;

    @JsonProperty("predicted_sales")
    private Integer predictedSales;
}

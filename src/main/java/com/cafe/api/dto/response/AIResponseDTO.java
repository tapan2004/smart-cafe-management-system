package com.cafe.api.dto.response;

import com.cafe.api.dto.request.AIPredictionDTO;
import lombok.Data;

import java.util.List;

@Data
public class AIResponseDTO {
    private List<AIPredictionDTO> predictions;
}

package com.prisme.back.dto;

import lombok.Data;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

@Data
public class ProgressionUpdateRequest {
    @Min(0)
    @Max(100)
    private Integer progression;

    private String statut;
}
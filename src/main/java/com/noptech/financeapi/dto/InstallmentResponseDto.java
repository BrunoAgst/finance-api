package com.noptech.financeapi.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class InstallmentResponseDto {
    private BigDecimal installmentAmount;
    private Integer installmentNumber;
    private LocalDateTime installmentDueDate;
    private String installmentId;
}

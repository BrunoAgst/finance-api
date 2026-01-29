package com.noptech.financeapi.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DebtUpdateRequestDto {
    private String name;
    private BigDecimal amount;
    private LocalDateTime date;
    private Boolean fixed;
    private Integer installmentNumber;
}

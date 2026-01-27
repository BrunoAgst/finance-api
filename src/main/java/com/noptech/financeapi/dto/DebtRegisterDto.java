package com.noptech.financeapi.dto;

import com.noptech.financeapi.util.Category;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class DebtRegisterDto {
    private String name;
    private BigDecimal amount;
    private Category category;
    private Long userId;
    private LocalDateTime date;
    private Integer installmentNumber;
    private Boolean fixed;
}

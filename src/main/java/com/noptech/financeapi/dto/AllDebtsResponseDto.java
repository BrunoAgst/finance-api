package com.noptech.financeapi.dto;

import com.noptech.financeapi.util.Category;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class AllDebtsResponseDto {
    private Long id;
    private String name;
    private BigDecimal amount;
    private Category category;
    private LocalDate date;
    private BigDecimal installmentAmount;
    private Integer installmentNumber;
    private Boolean fixed;
    private LocalDate installmentDueDate;
}

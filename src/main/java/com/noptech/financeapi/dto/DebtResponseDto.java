package com.noptech.financeapi.dto;

import com.noptech.financeapi.util.Category;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class DebtResponseDto {
    private Long id;
    private String name;
    private BigDecimal amount;
    private Category category;
    private LocalDateTime date;
    private List<InstallmentResponseDto> installments;
}

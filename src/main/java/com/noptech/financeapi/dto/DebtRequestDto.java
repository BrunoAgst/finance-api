package com.noptech.financeapi.dto;

import com.noptech.financeapi.util.Category;
import jakarta.validation.constraints.AssertTrue;
import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DebtRequestDto {
    @NonNull
    private String name;
    @NonNull
    private BigDecimal amount;
    @NonNull
    private Category category;
    @NonNull
    private LocalDateTime date;
    private Integer installmentNumber;
    @NonNull
    private Boolean fixed;

    @AssertTrue(message = "Fixed debt cannot be installment")
    private boolean isValidFixedInstallment() {
        return !fixed || !category.name().equals("INSTALLMENT_CREDIT");
    }

    @AssertTrue(message = "Installment number must be at least 1 for installment credit")
    private boolean isValidInstallmentFields() {
        if (category.name().equals("INSTALLMENT_CREDIT")) {
            return installmentNumber != null && installmentNumber >= 1;
        }
        return true;
    }
}

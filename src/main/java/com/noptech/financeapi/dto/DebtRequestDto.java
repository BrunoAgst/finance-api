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
    private LocalDateTime dueDate;
    @NonNull
    private Boolean isInstallment;
    private Integer installmentNumber;
    @NonNull
    private Boolean fixed;

    @AssertTrue(message = "Fixed debt cannot be installment")
    private boolean isValidFixedInstallment() {
        return !fixed || !isInstallment;
    }

    @AssertTrue(message = "Invalid installment fields")
    private boolean isValidInstallmentFields() {
        if (isInstallment) {
            return installmentNumber != null && installmentNumber > 0 && category.name().equals("CREDIT");
        }
        return true;
    }
}

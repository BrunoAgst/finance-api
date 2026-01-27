package com.noptech.financeapi.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface DebtsAndInstallments {
    Long getId();
    String getDebtName();
    BigDecimal getDebtAmount();
    Integer getCategory();
    Long getUserId();
    LocalDate getDebtDate();
    Boolean getFixed();
    Long getInstallmentId();
    BigDecimal getInstallmentAmount();
    Integer getInstallmentNumber();
    LocalDate getInstallmentDueDate();
}

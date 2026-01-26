package com.noptech.financeapi.service;

import com.noptech.financeapi.dto.DebtConsultDto;
import com.noptech.financeapi.dto.DebtRegisterDto;

public interface DebtService {
    void createDebtForUser(DebtRegisterDto debt);
    DebtConsultDto getDebtsById(String userId, Long debtId);
}

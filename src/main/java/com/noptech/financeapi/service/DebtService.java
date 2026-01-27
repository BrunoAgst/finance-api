package com.noptech.financeapi.service;

import com.noptech.financeapi.dto.DebtConsultDto;
import com.noptech.financeapi.dto.DebtRegisterDto;
import com.noptech.financeapi.dto.DebtsAndInstallmentsDto;

import java.util.List;

public interface DebtService {
    void createDebtForUser(DebtRegisterDto debt);
    DebtConsultDto getDebtsById(String userId, Long debtId);
    List<DebtsAndInstallmentsDto> getAllDebtsForUser(String userId);
}

package com.noptech.financeapi.service;

import com.noptech.financeapi.dto.DebtDto;

public interface DebtService {
    void createDebtForUser(DebtDto debt);
}

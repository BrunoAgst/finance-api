package com.noptech.financeapi.repository;

import com.noptech.financeapi.entity.Debt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DebtRepository extends JpaRepository<Debt, Long> {}

package com.noptech.financeapi.repository;

import com.noptech.financeapi.entity.Debt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DebtRepository extends JpaRepository<Debt, Long> {
    Optional<Debt> findByIdAndUserId(Long id, Long userId);
}

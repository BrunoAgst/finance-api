package com.noptech.financeapi.repository;

import com.noptech.financeapi.entity.Installment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InstallmentRepository extends JpaRepository<Installment, Long> {
    Optional<List<Installment>> findByDebtId(Long debtId);
}

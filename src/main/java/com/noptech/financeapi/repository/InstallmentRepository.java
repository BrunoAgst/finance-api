package com.noptech.financeapi.repository;

import com.noptech.financeapi.entity.Installment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstallmentRepository extends JpaRepository<Installment, Long> {}

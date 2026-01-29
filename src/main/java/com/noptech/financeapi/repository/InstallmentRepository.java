package com.noptech.financeapi.repository;

import com.noptech.financeapi.entity.Installment;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface InstallmentRepository extends JpaRepository<Installment, Long> {
    List<Installment> findByDebtId(Long debtId);

    @Modifying
    @Transactional
    @Query(value = """
            DELETE FROM installments 
            WHERE debt_id = :debtId
        """, nativeQuery = true
    )
    void deleteAllByDebtId(@Param("debtId") Long debtId);
}

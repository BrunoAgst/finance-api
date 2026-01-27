package com.noptech.financeapi.repository;

import com.noptech.financeapi.dto.DebtsAndInstallments;
import com.noptech.financeapi.entity.Debt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DebtRepository extends JpaRepository<Debt, Long> {
    Optional<Debt> findByIdAndUserId(Long id, Long userId);
    @Query(value = """
        SELECT 
            d.id as debt_id,
            d.name as debt_name,
            d.amount as debtAmount,
            d.category,
            d.user_id,
            d.date as debtDate,
            d.fixed,
            i.id as installment_id,
            i.installment_amount,
            i.installment_number,
            i.installment_due_date
        FROM 
            debts d
        LEFT JOIN 
            installments i ON d.id = i.debt_id 
            AND DATE_TRUNC('month', i.installment_due_date) = DATE_TRUNC('month', CURRENT_DATE)
        WHERE 
            d.date >= CURRENT_DATE - INTERVAL '30 days'
            AND d.user_id = :userId
        ORDER BY 
            d.date DESC, 
            d.id, 
            i.installment_number
    """, nativeQuery = true)
    List<DebtsAndInstallments> findDebtsByUserIdLast30Days(@Param("userId") Long userId);
}

package com.noptech.financeapi.repository;

import com.noptech.financeapi.dto.DebtsAndInstallments;
import com.noptech.financeapi.entity.Debt;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface DebtRepository extends JpaRepository<Debt, Long> {

    @Modifying
    @Transactional
    @Query(value = """
        UPDATE debts SET 
                name = :name, 
                amount = :amount, 
                date = :date, 
                fixed = :fixed 
        WHERE id = :id
        """, nativeQuery = true
    )
    Integer updateDebtById(
            @Param("id") Long id,
            @Param("name") String name,
            @Param("amount") BigDecimal amount,
            @Param("date") LocalDateTime date,
            @Param("fixed") Boolean fixed
    );

    void deleteById(Long id);

    Debt findByIdAndUserId(Long id, Long userId);

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

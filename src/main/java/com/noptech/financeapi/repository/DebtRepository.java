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
            d.id as id,
            d.name as debt_name,
            d.amount as debtAmount,
            d.category,
            d.user_id,
            d.date as debtDate,
            d.due_date as dueDate,
            d.fixed,
            NULL as installment_id,
            CASE 
                WHEN d.category = 6 THEN ROUND(d.amount / COUNT(i.id), 2)
                ELSE NULL 
            END as installment_amount,
            CASE 
                WHEN d.category = 6 THEN COUNT(i.id)
                ELSE NULL 
            END as installment_number,
            NULL as installment_due_date
        FROM 
            debts d
        LEFT JOIN 
            installments i ON d.id = i.debt_id
        WHERE 
            d.date >= CURRENT_DATE - INTERVAL '30 days'
            AND d.user_id = :userId
        GROUP BY 
            d.id, d.name, d.amount, d.category, d.user_id, d.date, d.due_date, d.fixed
        ORDER BY 
            d.date DESC
    """, nativeQuery = true)
    List<DebtsAndInstallments> findDebtsByUserIdLast30Days(@Param("userId") Long userId);

    @Query(value = """
        SELECT 
            d.id as id,
            d.name as debt_name,
            d.amount as debtAmount,
            d.category,
            d.user_id,
            d.date as debtDate,
            d.due_date as dueDate,
            d.fixed,
            i.id as installment_id,
            i.installment_amount,
            i.installment_number,
            i.installment_due_date
        FROM 
            debts d
        LEFT JOIN 
            installments i ON d.id = i.debt_id 
            AND EXTRACT(YEAR FROM i.installment_due_date) = :year
            AND EXTRACT(MONTH FROM i.installment_due_date) = :month
        WHERE 
            d.user_id = :userId
            AND (
                (d.category = 1 AND EXTRACT(YEAR FROM d.due_date) = :year AND EXTRACT(MONTH FROM d.due_date) = :month)
                OR (d.category != 1 AND d.category != 6 AND EXTRACT(YEAR FROM d.date) = :year AND EXTRACT(MONTH FROM d.date) = :month)
                OR (d.category = 6 AND i.id IS NOT NULL)
            )
        ORDER BY 
            d.date DESC, 
            d.id, 
            i.installment_number
    """, nativeQuery = true)
    List<DebtsAndInstallments> findDebtsByUserIdAndMonth(
            @Param("userId") Long userId,
            @Param("year") Integer year,
            @Param("month") Integer month
    );
}

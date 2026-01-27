package com.noptech.financeapi.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "installments")
@Data
public class Installment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "installment_amount", precision = 15, scale = 2)
    private BigDecimal installmentAmount;

    @Column(name = "installment_number")
    private Integer installmentNumber;

    @Column(name = "installment_due_date")
    private LocalDateTime installmentDueDate;

    @Column(name = "debt_id")
    private Long debtId;
}

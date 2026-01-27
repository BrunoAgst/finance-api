package com.noptech.financeapi.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "debts")
@Data
public class Debt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "debt_name")
    private String name;

    @Column(name = "debt_amount", precision = 15, scale = 2)
    private BigDecimal amount;

    private Integer category;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "is_installment")
    private Boolean isInstallment;

    @Column(name = "installment_id")
    private String installmentId;

    private Boolean fixed;
}

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

    private String name;

    @Column(precision = 15, scale = 2)
    private BigDecimal amount;

    private Integer category;

    @Column(name = "user_id")
    private Long userId;

    private LocalDateTime date;

    private Boolean fixed;
}

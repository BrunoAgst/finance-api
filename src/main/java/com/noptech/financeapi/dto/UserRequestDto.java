package com.noptech.financeapi.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserRequestDto {
    private String name;
    private String email;
    private String password;
    private BigDecimal salary;
    private Integer creditCardClosingDate;
}

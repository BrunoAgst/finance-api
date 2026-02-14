package com.noptech.financeapi.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class UserDto {
    private String firstname;
    private String lastname;
    private String email;
    private String username;
    private BigDecimal salary;
    private Integer creditCardClosingDate;
}

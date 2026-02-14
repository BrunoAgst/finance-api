package com.noptech.financeapi.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class UserDto {
    private String name;
    private String email;
    private BigDecimal salary;
    private String keycloakId;
    private Integer creditCardClosingDate;
}

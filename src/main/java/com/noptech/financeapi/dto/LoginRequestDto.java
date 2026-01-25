package com.noptech.financeapi.dto;

import lombok.Data;

@Data
public class LoginRequestDto {
    private String email;
    private String password;
}

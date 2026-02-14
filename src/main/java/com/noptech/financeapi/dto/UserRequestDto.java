package com.noptech.financeapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserRequestDto {

    @NotBlank(message = "firstname is required")
    private String firstname;

    @NotBlank(message = "username is required")
    @Size(min = 3, max = 50, message = "username must be between 3 and 50 characters")
    private String username;

    @NotBlank(message = "lastname is required")
    private String lastname;

    @NotBlank(message = "email is required")
    @Email(message = "email should be valid")
    private String email;

    private BigDecimal salary;
    private Integer creditCardClosingDate;
}

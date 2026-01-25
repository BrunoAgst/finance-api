package com.noptech.financeapi.controller;

import com.noptech.financeapi.dto.LoginRequestDto;
import com.noptech.financeapi.dto.LoginResponseDto;
import com.noptech.financeapi.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final LoginService loginService;

    @PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
    public LoginResponseDto login(@RequestBody LoginRequestDto loginRequest) {
        var token = loginService.login(loginRequest.getEmail(), loginRequest.getPassword());
        return LoginResponseDto.builder().token(token).build();
    }
}

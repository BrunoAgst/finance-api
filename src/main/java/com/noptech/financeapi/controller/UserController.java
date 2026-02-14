package com.noptech.financeapi.controller;

import com.noptech.financeapi.dto.MessageResponseDto;
import com.noptech.financeapi.dto.UserDto;
import com.noptech.financeapi.dto.UserRequestDto;
import com.noptech.financeapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/users", consumes = "application/json", produces = "application/json")
    public ResponseEntity<MessageResponseDto> saveUser(@RequestBody UserRequestDto userRequest) {

        var user = UserDto.builder()
                .name(userRequest.getName())
                .email(userRequest.getEmail())
                .salary(userRequest.getSalary())
                .keycloakId("")
                .creditCardClosingDate(userRequest.getCreditCardClosingDate())
                .build();

        userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(MessageResponseDto.builder().message("User created successfully").build());
    }
}

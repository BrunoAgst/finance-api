package com.noptech.financeapi.controller;

import com.noptech.financeapi.dto.MessageResponseDTO;
import com.noptech.financeapi.dto.UserDto;
import com.noptech.financeapi.dto.UserRequestDto;
import com.noptech.financeapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping(value = "/users", consumes = "application/json", produces = "application/json")
    public ResponseEntity<MessageResponseDTO> saveUser(@RequestBody UserRequestDto userRequest) {

        var user = UserDto.builder()
                .name(userRequest.getName())
                .email(userRequest.getEmail())
                .password(userRequest.getPassword())
                .salary(userRequest.getSalary())
                .creditCardClosingDate(userRequest.getCreditCardClosingDate())
                .build();

        userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(MessageResponseDTO.builder().message("User created successfully").build());
    }
}

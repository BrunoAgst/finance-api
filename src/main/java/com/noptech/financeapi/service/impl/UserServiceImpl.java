package com.noptech.financeapi.service.impl;

import com.noptech.financeapi.dto.UserDto;
import com.noptech.financeapi.entity.User;
import com.noptech.financeapi.repository.UserRepository;
import com.noptech.financeapi.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void createUser(UserDto user) {
        log.info("[UserService] Creating user: {}", user.getEmail());

        var findUser = userRepository.findByEmail(user.getEmail());
        if (findUser.isPresent()) {
            log.error("[UserService] User creation failed. Email already in use: {}", user.getEmail());
            throw new IllegalArgumentException("Email already in use");
        }

        var passwordHash = passwordEncoder.encode(user.getPassword());

        var data = new User();
        data.setName(user.getName());
        data.setEmail(user.getEmail());
        data.setPassword(passwordHash);
        data.setSalary(user.getSalary());
        data.setCreditCardClosingDate(user.getCreditCardClosingDate());

        userRepository.save(data);
        log.info("[UserService] User created successfully: {}", user.getEmail());

    }
}

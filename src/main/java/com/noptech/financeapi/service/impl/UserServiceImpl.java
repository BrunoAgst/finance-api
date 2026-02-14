package com.noptech.financeapi.service.impl;

import com.noptech.financeapi.dto.UserDto;
import com.noptech.financeapi.entity.User;
import com.noptech.financeapi.exception.DataAccessException;
import com.noptech.financeapi.repository.UserRepository;
import com.noptech.financeapi.service.KeycloakAdminService;
import com.noptech.financeapi.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final KeycloakAdminService keycloakAdminService;

    @Override
    @Transactional
    public void createUser(UserDto user) {

        try {
            log.info("[UserService] Creating user: {}", user.getEmail());

            var findUser = userRepository.findByEmail(user.getEmail());
            if (findUser.isPresent()) {
                log.error("[UserService] User creation failed. Email already in use: {}", user.getEmail());
                throw new IllegalArgumentException("Email already in use");
            }

            var keycloakId = keycloakAdminService.createUser(user.getUsername(), user.getEmail(), user.getFirstname(), user.getLastname());
            log.info("[UserService] Keycloak user created with ID: {}", keycloakId);

            keycloakAdminService.assignRole(keycloakId);
            log.info("[UserService] Role assigned to Keycloak user with ID: {}", keycloakId);

            var data = new User();
            data.setFirstName(user.getFirstname());
            data.setLastName(user.getLastname());
            data.setEmail(user.getEmail());
            data.setUsername(user.getUsername());
            data.setSalary(user.getSalary());
            data.setKeycloakId(UUID.fromString(keycloakId));
            data.setCreditCardClosingDate(user.getCreditCardClosingDate());

            userRepository.save(data);
            log.info("[UserService] User created successfully: {}", user.getEmail());
        } catch (Exception e) {
            log.error("[UserService] Error creating user: {}. Error: {}", user.getEmail(), e.getMessage());
            throw new DataAccessException("Error creating user: " + e.getMessage());
        }
    }
}

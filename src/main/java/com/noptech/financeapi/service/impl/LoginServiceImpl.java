package com.noptech.financeapi.service.impl;

import com.noptech.financeapi.exception.InvalidCredentialsException;
import com.noptech.financeapi.exception.NotFoundException;
import com.noptech.financeapi.repository.UserRepository;
import com.noptech.financeapi.service.JwtService;
import com.noptech.financeapi.service.LoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public String login(String email, String password) {
        log.info("[LoginService] Attempting login for email: {}", email);
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));

        if(!passwordEncoder.matches(password, user.getPassword())) {
            log.error("[LoginService] Invalid password for user with email: {}", email);
            throw new InvalidCredentialsException("Invalid email or password");
        }

        var token = jwtService.generateToken(user.getId());
        log.info("[LoginService] Login successful for email: {}", email);

        return token;
    }
}

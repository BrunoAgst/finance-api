package com.noptech.financeapi.service;

public interface JwtService {
    String generateToken(Long userId);
}

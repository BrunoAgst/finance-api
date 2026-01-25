package com.noptech.financeapi.service;

public interface JwtService {
    String generateToken(Long userId);
    Boolean validateToken(String token);
    String extractUserId(String token);
}

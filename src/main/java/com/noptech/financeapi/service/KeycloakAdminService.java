package com.noptech.financeapi.service;

public interface KeycloakAdminService {
    String createUser(String username, String email, String firstName, String lastName);
    void assignRole(String userId);
    void resetPassword(String userId, String newPassword);
}

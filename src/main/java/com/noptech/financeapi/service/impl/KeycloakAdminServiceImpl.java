package com.noptech.financeapi.service.impl;

import com.noptech.financeapi.exception.KeyCloakException;
import com.noptech.financeapi.service.KeycloakAdminService;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class KeycloakAdminServiceImpl implements KeycloakAdminService {

    @Value("${keycloak.server-url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    private Keycloak getKeycloakInstance() {
        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .grantType("client_credentials")
                .build();
    }

    @Override
    public String createUser(String username, String email, String firstName, String lastName) {
        var keycloak = getKeycloakInstance();

        try {
            var realmResource = keycloak.realm(realm);
            var usersResource = realmResource.users();

            var user = new UserRepresentation();
            user.setUsername(username);
            user.setEmail(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEnabled(true);
            user.setEmailVerified(false);

            user.setRequiredActions(List.of(
                    "VERIFY_EMAIL",
                    "UPDATE_PASSWORD"
            ));

            var response = usersResource.create(user);

            if (response.getStatus() == 201) {

                var locationHeader = response.getHeaderString("Location");
                var userId = locationHeader.substring(locationHeader.lastIndexOf('/') + 1);

                usersResource.get(userId).executeActionsEmail(
                        List.of("VERIFY_EMAIL", "UPDATE_PASSWORD")
                );

                log.info("[KeycloakAdminService] - Create user successfully Keycloak! ID: {}", userId);
                return userId;

            } else {
                log.error("[KeycloakAdminService] - Error creating user in Keycloak. Status: {}, Response: {}", response.getStatus(), response.readEntity(String.class));
                throw new KeyCloakException("Error creating user in Keycloak. Status: " + response.getStatus() + ", Response: " + response.readEntity(String.class));
            }

        } catch (Exception e) {
            log.error("[KeycloakAdminService] - Error creating user in Keycloak: {}", e.getMessage());
            throw new KeyCloakException("Error creating user in Keycloak: " + e.getMessage());
        } finally {
            keycloak.close();
        }
    }

    @Override
    public void assignRole(String userId) {
        Keycloak keycloak = getKeycloakInstance();

        try {
            RealmResource realmResource = keycloak.realm(realm);

            var role = realmResource.roles().get("USER").toRepresentation();

            realmResource.users()
                    .get(userId)
                    .roles()
                    .realmLevel()
                    .add(Collections.singletonList(role));

            log.info("[KeycloakAdminService] - Role USER assigning to user {}", userId);

        } catch (Exception e) {
            log.error("[KeycloakAdminService] - Error assigning role to user {}: {}", userId, e.getMessage());
            throw new KeyCloakException("Erro assigning role: " + e.getMessage());
        } finally {
            keycloak.close();
        }
    }

    @Override
    public void resetPassword(String userId, String newPassword) {
        Keycloak keycloak = getKeycloakInstance();

        try {
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(newPassword);
            credential.setTemporary(false);

            RealmResource realmResource = keycloak.realm(realm);
            realmResource.users().get(userId).resetPassword(credential);

            log.info("[KeycloakAdminService] - Password reset successfully for user {}", userId);

        } catch (Exception e) {
            log.error("[KeycloakAdminService] - Error resetting password for user {}: {}", userId, e.getMessage());
            throw new KeyCloakException("Error resetting password: " + e.getMessage());
        } finally {
            keycloak.close();
        }
    }
}

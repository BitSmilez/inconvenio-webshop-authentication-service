package com.bitsmilez.authentificationmicroservice.core.service.interfaces;

import com.bitsmilez.authentificationmicroservice.port.requests.CreateUserRequest;
import org.keycloak.representations.AccessTokenResponse;

import java.io.IOException;

public interface IUserService {

    Integer createKeycloakUser(CreateUserRequest user);
    AccessTokenResponse login(String username, String password);

    Integer verifyToken(String accessToken) throws IOException;

    }

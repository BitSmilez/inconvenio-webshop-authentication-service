package com.bitsmilez.authentificationmicroservice.core.service.impl;

import com.bitsmilez.authentificationmicroservice.core.service.interfaces.IUserService;
import com.bitsmilez.authentificationmicroservice.port.requests.CreateUserRequest;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;


@Service
public class UserService implements IUserService {
    @Value("${keycloak.realm}")
    public String realm;

    private final KeycloakProvider kcProvider;


    public UserService(KeycloakProvider keycloakProvider) {
        this.kcProvider = keycloakProvider;
    }

    public javax.ws.rs.core.Response createKeycloakUser(CreateUserRequest user) {
        UsersResource usersResource = kcProvider.getInstance().realm(realm).users();
        CredentialRepresentation credentialRepresentation = createPasswordCredentials(user.getPassword());

        UserRepresentation kcUser = new UserRepresentation();
        kcUser.setUsername(user.getEmail());
        kcUser.setCredentials(Collections.singletonList(credentialRepresentation));
        kcUser.setFirstName(user.getFirstname());
        kcUser.setLastName(user.getLastname());
        kcUser.setEmail(user.getEmail());
        kcUser.setEnabled(true);
        kcUser.setEmailVerified(false);

        javax.ws.rs.core.Response response = usersResource.create(kcUser);

        if (response.getStatus() == 201) {
            // Used for future wishlist entries 
//            User localUser = new User();
//            localUser.setFirstName(kcUser.getFirstName());
//            localUser.setLastName(kcUser.getLastName());
        }

        return response;

    }
    public AccessTokenResponse login(String username, String password){
        Keycloak keycloak = kcProvider.newKeycloakBuilderWithPasswordCredentials(username, password).build();
        try {
            return  keycloak.tokenManager().getAccessToken();
        }
        catch (Exception e){
            return null;

        }
    }


    public Integer verifyToken(String accessToken) throws IOException {
        return kcProvider.introspectToken(accessToken);
    }




    private static CredentialRepresentation createPasswordCredentials(String password) {
        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(false);
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue(password);
        return passwordCredentials;
    }








}

package com.bitsmilez.authentificationmicroservice.core.service.impl;

import com.bitsmilez.authentificationmicroservice.port.requests.CreateUserRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.admin.client.token.TokenManager;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

import javax.ws.rs.core.Response;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UsersResource usersResource;

    @Mock
    private Keycloak keycloak;

    @Mock
    private RealmResource realmResource;

    @Mock
    private Response response;

    @Mock
    private KeycloakProvider kcProvider;

    @Mock
    private TokenManager tokenManager;

    @Mock
    private KeycloakBuilder keycloakBuilder;

    @InjectMocks
    private UserService userService;


    @Test
    void createKeycloakUserTest_successful() {
        when(keycloak.realm(null)).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.create(any(UserRepresentation.class))).thenReturn(response);
        when(kcProvider.getInstance()).thenReturn(keycloak);
        when(response.getStatus()).thenReturn(201);
        CreateUserRequest createUserRequest = createUserRequest();

        Integer result = userService.createKeycloakUser(createUserRequest);
        assertEquals("Response Code should be 201", 201, result);
    }

    @Test
    void createKeycloakUserTest_correctUserRepresentation() {
        when(keycloak.realm(null)).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.create(any(UserRepresentation.class))).thenReturn(response);
        when(kcProvider.getInstance()).thenReturn(keycloak);
        CreateUserRequest createUserRequest = createUserRequest();
        userService.createKeycloakUser(createUserRequest);

        ArgumentCaptor<UserRepresentation> userCaptor = ArgumentCaptor.forClass(UserRepresentation.class);
        verify(usersResource).create(userCaptor.capture());
        UserRepresentation capturedUser = userCaptor.getValue();
        assertEquals("Email should match", "test@example.com", capturedUser.getEmail());
        assertEquals("First name should match", "John", capturedUser.getFirstName());
        assertEquals("Last name should match", "Doe", capturedUser.getLastName());
    }

    @Test
    void createKeycloakUserTest_failed() {
        when(keycloak.realm(null)).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.create(any(UserRepresentation.class))).thenReturn(response);
        when(kcProvider.getInstance()).thenReturn(keycloak);
        when(response.getStatus()).thenReturn(400);
        CreateUserRequest createUserRequest = createInvalidUserRequest();

        Integer result = userService.createKeycloakUser(createUserRequest);
        assertEquals("Response Code should be 400", 400, result);
    }

    @Test
    void loginTest_successful() {
        String username = "testuser";
        String password = "testpassword";
        AccessTokenResponse accessTokenResponse = new AccessTokenResponse();
        when(kcProvider.newKeycloakBuilderWithPasswordCredentials(anyString(), anyString())).thenReturn(keycloakBuilder);
        when(keycloakBuilder.build()).thenReturn(keycloak);
        when(keycloak.tokenManager()).thenReturn(tokenManager);
        when(tokenManager.getAccessToken()).thenReturn(accessTokenResponse);

        AccessTokenResponse result = userService.login(username, password);

        verify(kcProvider).newKeycloakBuilderWithPasswordCredentials(username, password);
        verify(keycloak).tokenManager();
        verify(tokenManager).getAccessToken();
        assertEquals("",accessTokenResponse, result);
    }

    @Test
    void verifyTokenTest() throws IOException {
        when(kcProvider.introspectToken(anyString())).thenReturn(1);
        Integer result = userService.verifyToken("access-token");
        assertEquals("Should return 1", 1, result);
    }

    @Test
    public void testCreatePasswordCredentials() {
        String password = "mypassword";
        CredentialRepresentation result = UserService.createPasswordCredentials(password);
        assertNotNull(result);
        assertEquals("", password, result.getValue());
        assertFalse(result.isTemporary());
        assertEquals("", CredentialRepresentation.PASSWORD, result.getType());
    }


    private CreateUserRequest createUserRequest() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setEmail("test@example.com");
        createUserRequest.setFirstname("John");
        createUserRequest.setLastname("Doe");
        createUserRequest.setPassword("password");

        return createUserRequest;
    }

    private CreateUserRequest createInvalidUserRequest() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setEmail(null);
        createUserRequest.setFirstname("John");
        createUserRequest.setLastname("Doe");
        createUserRequest.setPassword("null");

        return createUserRequest;
    }

}

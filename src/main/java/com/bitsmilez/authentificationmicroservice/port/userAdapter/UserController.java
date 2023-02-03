package com.bitsmilez.authentificationmicroservice.port.userAdapter;



import com.bitsmilez.authentificationmicroservice.core.service.UserService;
import com.bitsmilez.authentificationmicroservice.port.requests.CreateUserRequest;
import com.bitsmilez.authentificationmicroservice.port.requests.LoginRequest;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Response;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/user")
public class UserController {
    private final UserService userService;




    public UserController(UserService userService) {
        this.userService = userService;
    }
	

    @PostMapping(value = "/create")
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest user) {
        Response createdResponse = userService.createKeycloakUser(user);
        return ResponseEntity.status(createdResponse.getStatus()).build();

    }

    @PostMapping("/login")
    public ResponseEntity<AccessTokenResponse> login(@NotNull @RequestBody LoginRequest loginRequest) {
        Keycloak keycloak = userService.login(loginRequest.getUsername(), loginRequest.getPassword());

        AccessTokenResponse accessTokenResponse = null;
        try {
            accessTokenResponse = keycloak.tokenManager().getAccessToken();


            HttpHeaders headers = new HttpHeaders();
            headers.add("Set-Cookie", "keycloak_token=" + accessTokenResponse.getToken() + "; Path=/; HttpOnly;");

            return ResponseEntity.status(HttpStatus.OK).headers(headers).body(accessTokenResponse);

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(accessTokenResponse);
        }

    }

	

}

package com.bitsmilez.authentificationmicroservice.port.userAdapter;


import com.bitsmilez.authentificationmicroservice.core.service.UserService;
import com.bitsmilez.authentificationmicroservice.port.requests.CreateUserRequest;
import com.bitsmilez.authentificationmicroservice.port.requests.LoginRequest;
import com.bitsmilez.authentificationmicroservice.port.requests.VerifyToken;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Response;
import java.io.IOException;

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
        AccessTokenResponse response = userService.login(loginRequest.getUsername(), loginRequest.getPassword());

        if(response != null) {



            HttpHeaders headers = new HttpHeaders();
            headers.add("Set-Cookie", "token=" + response.getToken() + "; Path=/; HttpOnly;");

            return ResponseEntity.status(HttpStatus.OK).headers(headers).body(response);

        } else   {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

    }
    @GetMapping("/verify")
    @ResponseBody
	public ResponseEntity<?> verify(@NotNull @RequestBody VerifyToken accessToken) throws IOException {
        int statusCode = userService.verifyToken(accessToken.getAccessToken());

          return ResponseEntity.status(statusCode).body(null);
    }

    @PostMapping("/add-to-cart")
    public ResponseEntity<?> publishAddToCartEvent(@RequestBody ObjectNode objectNode) throws IOException {
        String accessToken=  objectNode.get("access_token").asText();
        int statusCode = userService.verifyToken(accessToken);
        if (statusCode==200){
            String productID = objectNode.get("productID").asText();
            int quantity = objectNode.get("quantity").asInt();
            return userService.addCart(productID,quantity);

        }

    else{
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        }

    }

    @PostMapping("/remove-from-cart")
    public ResponseEntity<?> publishRemoveFromCartEvent(@RequestBody ObjectNode objectNode) {
        return null;
    }

    @PostMapping("/update-cart")
    public ResponseEntity<?> publishUpdateCartEvent(@RequestBody ObjectNode objectNode) {
        return null;

    }

}

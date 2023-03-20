package com.bitsmilez.authentificationmicroservice.port.userAdapter;

import com.bitsmilez.authentificationmicroservice.core.service.interfaces.IGateway;
import com.bitsmilez.authentificationmicroservice.core.service.interfaces.IUserService;
import com.bitsmilez.authentificationmicroservice.port.requests.CreateUserRequest;
import com.bitsmilez.authentificationmicroservice.port.requests.LoginRequest;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.io.IOException;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/user")
public class UserController {
    private final IUserService userService;
    private final IGateway gateway;


    public UserController(IUserService userService, IGateway gateway) {
        this.userService = userService;
        this.gateway = gateway;
    }


    @PostMapping(value = "/create")
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest user) {
        Integer createdResponse = userService.createKeycloakUser(user);
        return ResponseEntity.status(createdResponse).build();

    }

    @PostMapping("/login")
    public ResponseEntity<AccessTokenResponse> login(@NotNull @RequestBody LoginRequest loginRequest) {
        AccessTokenResponse response = userService.login(loginRequest.getUsername(), loginRequest.getPassword());

        if (response != null) {


            HttpHeaders headers = new HttpHeaders();
            headers.add("Set-Cookie", "token=" + response.getToken() + "; Path=/; HttpOnly;");

            return ResponseEntity.status(HttpStatus.OK).headers(headers).body(response);

        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

    }

    @PostMapping("/add-to-cart")
    public ResponseEntity<?> publishAddToCartEvent(@RequestBody ObjectNode objectNode, @RequestHeader(name = "Authorization", required = false) String bearerToken) throws IOException {

        if (bearerToken == null || bearerToken.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        bearerToken = (bearerToken.replace("Bearer ", ""));
        int statusCode = userService.verifyToken(bearerToken);

        if (statusCode == 200) {
            String productID = objectNode.get("productID").asText();
            int quantity = objectNode.get("quantity").asInt();
            String cartID = objectNode.get("cartID").asText();
            return gateway.publishAddToCartEvent(productID, quantity, cartID);

        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        }

    }

    @PostMapping("/remove-from-cart")
    public ResponseEntity<?> publishRemoveFromCartEvent(@RequestBody ObjectNode objectNode, @RequestHeader("Authorization") String bearerToken) throws IOException {
        bearerToken = (bearerToken.replace("Bearer ", ""));
        int statusCode = userService.verifyToken(bearerToken);

        if (statusCode == 200) {
            String productID = objectNode.get("productID").asText();
            String cartID = objectNode.get("cartID").asText();
            return gateway.publishRemoveFromCartEvent(productID, cartID);

        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        }
    }

    @PostMapping("/update-cart")
    public ResponseEntity<?> publishUpdateCartEvent(@RequestBody ObjectNode objectNode, @RequestHeader("Authorization") String bearerToken) throws IOException {
        bearerToken = (bearerToken.replace("Bearer ", ""));
        int statusCode = userService.verifyToken(bearerToken);
        if (statusCode == 200) {
            String productID = objectNode.get("productID").asText();
            int quantity = objectNode.get("quantity").asInt();
            String cartID = objectNode.get("cartID").asText();
            return gateway.publishUpdateCartEvent(productID, quantity, cartID);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        }

    }

}

package com.bitsmilez.authentificationmicroservice.port.requests;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserRequest {
    String username;
    String password;
    String email;
    String firstname;
    String lastname;
}

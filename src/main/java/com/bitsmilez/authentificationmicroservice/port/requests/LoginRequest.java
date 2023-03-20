package com.bitsmilez.authentificationmicroservice.port.requests;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

    String username;
    String password;
}

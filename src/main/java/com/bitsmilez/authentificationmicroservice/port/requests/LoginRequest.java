package com.bitsmilez.authentificationmicroservice.port.requests;

import lombok.Getter;

@Getter
public class LoginRequest {

    String username;
    String password;
}

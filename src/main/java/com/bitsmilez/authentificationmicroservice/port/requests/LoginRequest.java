package com.bitsmilez.authentificationmicroservice.port.requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    String username;
    String password;
}

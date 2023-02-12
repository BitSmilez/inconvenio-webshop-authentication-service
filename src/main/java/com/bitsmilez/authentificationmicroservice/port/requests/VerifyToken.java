package com.bitsmilez.authentificationmicroservice.port.requests;

import lombok.Data;
import lombok.Getter;

@Getter
public class VerifyToken {
    String accessToken;
}

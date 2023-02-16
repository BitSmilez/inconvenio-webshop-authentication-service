package com.bitsmilez.authentificationmicroservice.core.service.interfaces;

import org.springframework.http.ResponseEntity;

import java.io.IOException;

public interface IGateway {

    ResponseEntity<?> publishAddToCartEvent(String productID, Integer amount) throws IOException;
    ResponseEntity<?> publishRemoveFromCartEvent(String productID, String cartID) throws IOException;

    ResponseEntity<?> publishUpdateCartEvent(String productID,int quantity, String cartID) throws IOException;
}

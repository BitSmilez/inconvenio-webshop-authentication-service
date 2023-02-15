package com.bitsmilez.authentificationmicroservice.core.service.interfaces;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;

public interface IGateway {

    ResponseEntity<?> publishAddToCartEvent(String productID, Integer amount) throws IOException;
    ResponseEntity<?> publishRemoveFromCartEvent(@RequestBody ObjectNode objectNode);
    ResponseEntity<?> publishUpdateCartEvent(@RequestBody ObjectNode objectNode);
}

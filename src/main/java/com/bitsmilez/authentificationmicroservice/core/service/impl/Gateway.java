package com.bitsmilez.authentificationmicroservice.core.service.impl;

import com.bitsmilez.authentificationmicroservice.core.service.interfaces.IGateway;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class Gateway implements IGateway {
    @Override
    public ResponseEntity<?> publishAddToCartEvent(String productID, Integer amount, String cartID) throws IOException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.accumulate("productID", productID);
        jsonObject.accumulate("quantity", amount.toString());
        jsonObject.accumulate("cartID", cartID);
        String json = jsonObject.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);

        String url = "http://localhost:8085/cart/add-to-cart";

        return generateRequest(url, body);
    }

    @Override
    public ResponseEntity<?> publishRemoveFromCartEvent(String productID, String cartID) throws IOException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.accumulate("productID", productID);
        jsonObject.accumulate("cartID", cartID);
        String json = jsonObject.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);

        String url = "http://localhost:8085/cart/remove-from-cart";

        return generateRequest(url, body);
    }

    @Override
    public ResponseEntity<?> publishUpdateCartEvent(String productID, int quantity, String cartID) throws IOException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.accumulate("productID", productID);
        jsonObject.accumulate("cartID", cartID);
        jsonObject.accumulate(("quantity"), quantity);
        String json = jsonObject.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);

        String url = "http://localhost:8085/cart/update-cart";

        return generateRequest(url, body);
    }

    public ResponseEntity<?> generateRequest(String URL, RequestBody body) throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(URL)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        String response = client.newCall(request).execute().toString();
        int codeIndex = response.indexOf("code=");
        int commaIndex = response.indexOf(",", codeIndex);

        String codeStr = response.substring(codeIndex + 5, commaIndex);
        int code = Integer.parseInt(codeStr);

        return new ResponseEntity<>(HttpStatus.valueOf(code));
    }
}

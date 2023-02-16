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
    public ResponseEntity<?> publishAddToCartEvent(String productID, Integer amount) throws IOException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.accumulate("productID", productID);
        jsonObject.accumulate("quantity",amount.toString());
        String json = jsonObject.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"),json);

        String url = "http://localhost:8080/cart/add-to-cart";
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        String response = client.newCall(request).execute().toString();
        int codeIndex = response.indexOf("code="); // find the index of "code=" in the string
        int commaIndex = response.indexOf(",", codeIndex); // find the index of the next comma after "code="

        String codeStr = response.substring(codeIndex + 5, commaIndex); // extract the substring that represents the code number
        int code = Integer.parseInt(codeStr); // convert the string to an integer
        return  new ResponseEntity<>(HttpStatus.valueOf(code));

    }

    @Override
    public ResponseEntity<?> publishRemoveFromCartEvent(String productID, String cartID) throws IOException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.accumulate("productID", productID);
        jsonObject.accumulate("cartID",cartID);
        String json = jsonObject.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"),json);

        String url = "http://localhost:8080/cart/remove-from-cart";
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        String response = client.newCall(request).execute().toString();
        int codeIndex = response.indexOf("code="); // find the index of "code=" in the string
        int commaIndex = response.indexOf(",", codeIndex); // find the index of the next comma after "code="

        String codeStr = response.substring(codeIndex + 5, commaIndex); // extract the substring that represents the code number
        int code = Integer.parseInt(codeStr); // convert the string to an integer
        return  new ResponseEntity<>(HttpStatus.valueOf(code));
    }

    @Override
    public ResponseEntity<?> publishUpdateCartEvent(String productID, int quantity,String cartID) throws IOException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.accumulate("productID", productID);
        jsonObject.accumulate("cartID",cartID);
        jsonObject.accumulate(("quantity"),quantity);
        String json = jsonObject.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"),json);

        String url = "http://localhost:8080/cart/update-cart";
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        String response = client.newCall(request).execute().toString();
        int codeIndex = response.indexOf("code="); // find the index of "code=" in the string
        int commaIndex = response.indexOf(",", codeIndex); // find the index of the next comma after "code="

        String codeStr = response.substring(codeIndex + 5, commaIndex); // extract the substring that represents the code number
        int code = Integer.parseInt(codeStr); // convert the string to an integer
        return  new ResponseEntity<>(HttpStatus.valueOf(code));

    }


}

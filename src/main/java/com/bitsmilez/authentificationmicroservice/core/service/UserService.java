package com.bitsmilez.authentificationmicroservice.core.service;

import com.bitsmilez.authentificationmicroservice.port.requests.CreateUserRequest;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import org.json.JSONObject;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;


@Service
public class UserService {
    @Value("${keycloak.realm}")
    public String realm;

    private final KeycloakProvider kcProvider;


    public UserService(KeycloakProvider keycloakProvider) {
        this.kcProvider = keycloakProvider;
    }

    public javax.ws.rs.core.Response createKeycloakUser(CreateUserRequest user) {
        UsersResource usersResource = kcProvider.getInstance().realm(realm).users();
        CredentialRepresentation credentialRepresentation = createPasswordCredentials(user.getPassword());

        UserRepresentation kcUser = new UserRepresentation();
        kcUser.setUsername(user.getEmail());
        kcUser.setCredentials(Collections.singletonList(credentialRepresentation));
        kcUser.setFirstName(user.getFirstname());
        kcUser.setLastName(user.getLastname());
        kcUser.setEmail(user.getEmail());
        kcUser.setEnabled(true);
        kcUser.setEmailVerified(false);

        javax.ws.rs.core.Response response = usersResource.create(kcUser);

        if (response.getStatus() == 201) {
            // Used for future wishlist entries 
//            User localUser = new User();
//            localUser.setFirstName(kcUser.getFirstName());
//            localUser.setLastName(kcUser.getLastName());
        }

        return response;

    }
    public AccessTokenResponse login(String username, String password){
        Keycloak keycloak = kcProvider.newKeycloakBuilderWithPasswordCredentials(username, password).build();
        try {
            return  keycloak.tokenManager().getAccessToken();
        }
        catch (Exception e){
            return null;

        }
    }


    public Integer verifyToken(String accessToken) throws IOException {
        return kcProvider.introspectToken(accessToken);
    }

    public ResponseEntity<?> addCart(String productID, Integer amount) throws IOException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.accumulate("productID", productID);
        jsonObject.accumulate("quantity",amount.toString());
        String json = jsonObject.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"),json);

        String url = "http://localhost:8080/add-to-cart";
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
        System.out.println(code);
        return  new ResponseEntity<>(HttpStatus.valueOf(code));

    }


    private static CredentialRepresentation createPasswordCredentials(String password) {
        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(false);
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue(password);
        return passwordCredentials;
    }








}

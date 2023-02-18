package com.bitsmilez.authentificationmicroservice.core.service.impl;


import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.squareup.okhttp.OkHttpClient;
import lombok.Getter;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Base64;

@Configuration
@Getter
 class KeycloakProvider {

    @Value("${keycloak.auth-server-url}")
    public String serverURL;
    @Value("${keycloak.realm}")
    public String realm;
    @Value("${keycloak.resource}")
    public String clientID;
    @Value("${keycloak.credentials.secret}")
    public String clientSecret;

    private static final Keycloak keycloak = null;
    private final OkHttpClient client = new OkHttpClient();
    public KeycloakProvider() {
    }

    public Keycloak getInstance() {

        return KeycloakBuilder.builder()
                .realm(realm)
                .serverUrl(serverURL)
                .clientId(clientID)
                .clientSecret(clientSecret)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .build();
    }


    public KeycloakBuilder newKeycloakBuilderWithPasswordCredentials(String username, String password) {
        return KeycloakBuilder.builder()
                .realm(realm)
                .serverUrl(serverURL)
                .clientId(clientID)
                .clientSecret(clientSecret)
                .username(username)
                .password(password);
    }

    public JsonNode refreshToken(String refreshToken) throws UnirestException {
        String url = serverURL + "/realms/" + realm + "/protocol/openid-connect/token";
        return Unirest.post(url)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .field("client_id", clientID)
                .field("client_secret", clientSecret)
                .field("refresh_token", refreshToken)
                .field("grant_type", "refresh_token")
                .asJson().getBody();
    }
    public  Integer introspectToken(String accessToken) throws IOException {
        String url = serverURL+"realms/"+realm+"/protocol/openid-connect/token/introspect";


        String authorization = "Basic " + Base64.getEncoder().encodeToString((this.clientID + ":" + this.clientSecret).getBytes());

        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(url);
        post.addHeader("Authorization", authorization);
        post.addHeader("Accept", "application/json");
        post.addHeader("Content-Type", "application/x-www-form-urlencoded");
        post.setEntity(new StringEntity("token=" + accessToken));

        HttpResponse response = httpClient.execute(post);
        JSONObject jsonResponse = new JSONObject (EntityUtils.toString(response.getEntity()));
        return Boolean.parseBoolean(String.valueOf(jsonResponse.get("active")))  ? 200  :401;
    }

}
package com.bitsmilez.authentificationmicroservice;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.util.Base64;

public class IntrospectToken {
    public static void main(String[] args) throws Exception {
        String url = "http://localhost:18080/auth/realms/inconvenio/protocol/openid-connect/token/introspect";
        String clientId = "spring-boot-client";
        String clientSecret = "2511b6af-1bd5-4dc1-bd20-f3d689c2d625";
        String accessToken = "eyJhbGciOilJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJFQmRGeTdTei1BOEVxdDQwamhlc21TbHhKbHlhSk0tRVhFdDlESVpUdTh3In0.eyJleHAiOjE2NzYwNTE2NjEsImlhdCI6MTY3NjA1MTM2MSwianRpIjoiZWIzMDZlODItMDY5OC00Y2NmLTk1ZjMtNDIxZWM1MTY0NzRjIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDoxODA4MC9hdXRoL3JlYWxtcy9pbmNvbnZlbmlvIiwic3ViIjoiNGU0NzM4NDUtMzY2ZS00ZGQzLWI0ZjEtNGI5ZDUwYTRmZWViIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoic3ByaW5nLWJvb3QtY2xpZW50Iiwic2Vzc2lvbl9zdGF0ZSI6IjVmMjIxM2IwLWM1NzgtNGZhZC1hNzRmLWI1NjE2MzE0ZDI1MyIsImFjciI6IjEiLCJzY29wZSI6ImVtYWlsIHByb2ZpbGUiLCJzaWQiOiI1ZjIyMTNiMC1jNTc4LTRmYWQtYTc0Zi1iNTYxNjMxNGQyNTMiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsIm5hbWUiOiJiaXIgcGFyYW0iLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJ0ZXN0QGdtYWlsLmNvbSIsImdpdmVuX25hbWUiOiJiaXIiLCJmYW1pbHlfbmFtZSI6InBhcmFtIiwiZW1haWwiOiJ0ZXN0QGdtYWlsLmNvbSJ9.BlJe8VpWXJNPnC5quIq44tpmKjlXw17Q5SPNVwhY6nD-Q0La_fL_2kdBvuqTj14usR5S914HBjvT3QTAczv0KYY1LMSiTVQAvYUxjSttC7M7ZnejSQBfXA4_YPmyzfclBXDvgcKkNoO6ZdBUeovNmh22bh4WiqKC-YPiW9mawtZkvpppDs4LAkedMra91nSxw8zCd7Wt1EpE4QDMGGCreHHXZlPtE2zZuNRLsXB89kB9OaXEzKjluIYSa5Bdi-N-m2ySI0d6AJc7da-GyiD9OguUezLZqPc5UuU7Xf4T98umNJigEHMYSuC1_9RlwjALptOQoVYOFJwne7XQtpm7Kg";

        String authorization = "Basic " + Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());

        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(url);
        post.addHeader("Authorization", authorization);
        post.addHeader("Accept", "application/json");
        post.addHeader("Content-Type", "application/x-www-form-urlencoded");
        post.setEntity(new StringEntity("token=" + accessToken));

        HttpResponse response = httpClient.execute(post);
    }
}

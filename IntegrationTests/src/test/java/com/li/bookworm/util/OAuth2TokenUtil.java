package com.li.bookworm.util;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.MemoryDataStoreFactory;
import com.li.bookworm.constants.TestConstants;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.token.TokenService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.util.Collections;

public class OAuth2TokenUtil {

//    @Value("${spring.security.oauth2.client.provider.google.authorization-uri}")
    private final String authUri = TestConstants.OAUTH_URL;
//    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private final String clientId = TestConstants.CLIENT_ID;
//    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private final String clientSecret = TestConstants.CLIENT_SECRET;
//    @Value("${spring.security.oauth2.client.registration.google.scope}")
    private final String scope = TestConstants.SCOPE;
//    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private final String redirectUri = TestConstants.REDIRECT_URL;
    
    private String accessToken = null;


    public String getAccessToken() throws IOException, GeneralSecurityException, InterruptedException {

        GoogleClientSecrets.Details web = new GoogleClientSecrets.Details();
        web.setClientId(clientId);
        web.setClientSecret(clientSecret);
        GoogleClientSecrets clientSecrets = new GoogleClientSecrets().setWeb(web);

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                clientSecrets,
                Collections.singleton(scope))
                .setDataStoreFactory(new MemoryDataStoreFactory())
                .setAccessType("offline")
                .build();

        HttpServer server = HttpServer.create(new InetSocketAddress(TestConstants.HTTP_PORT), 0);
        server.createContext("/redirect", exchange -> {
            String query = exchange.getRequestURI().getQuery();
            String response = "Authorization code received.";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();

            String code = null;
            for(String param : query.split("&")) {
                if(param.startsWith("code=")) {
                    code = param.split("=")[1];
                    break;
                }
            }
            if (code != null) {
                accessToken = flow.newTokenRequest(code).setRedirectUri(redirectUri).execute().getAccessToken();
            }
            server.stop(0);
        });

        server.start();

        String authorizationUrl = flow.newAuthorizationUrl().setRedirectUri(redirectUri).build();
        java.awt.Desktop.getDesktop().browse(URI.create(authorizationUrl));

        while(server.getAddress() != null) {
            Thread.sleep(100);
        }

        return accessToken;

    }
}

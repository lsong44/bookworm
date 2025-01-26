package com.li.bookworm.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class CustomOpaqueTokenIntrospector implements OpaqueTokenIntrospector {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${spring.security.oauth2.resourceserver.opaque-token.introspection-uri}")
    private String introspectionUri;

    @Override
    public OAuth2AuthenticatedPrincipal introspect(String token) {
        try {
            String uri = introspectionUri + "?access_token=" + token;
            GoogleTokenInfo tokenInfo = restTemplate.getForObject(uri, GoogleTokenInfo.class);
            if (token == null || tokenInfo.getError() != null) {
                throw new OAuth2AuthenticationException("Invalid token");
            }
            return tokenInfo.toOAuth2AuthenticatedPrincipal();
        } catch (HttpClientErrorException e) {
            throw new OAuth2AuthenticationException("Invalid token");
        }
    }
}

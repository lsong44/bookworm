package com.li.bookworm.config;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class GoogleTokenInfo {
    private String sub;
    private String email;
    private String error;
    private String scope;

    public OAuth2AuthenticatedPrincipal toOAuth2AuthenticatedPrincipal() {
        Map<String, Object> attributes = Map.of(
                "sub", sub,
                "email", email,
                "scope", scope
        );
        List<GrantedAuthority> authorities = scope != null ?
                Arrays.stream(scope.split(" "))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList())
                : List.of();
        return new DefaultOAuth2AuthenticatedPrincipal(attributes, authorities);
    }

}

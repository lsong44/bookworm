package com.li.bookworm.config.oauth;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;

import java.util.*;
import java.util.stream.Collectors;

@Data
public class GoogleTokenInfo {
    private String sub;
    private String email;
    private String error;
    private String scope;

    public OAuth2AuthenticatedPrincipal toOAuth2AuthenticatedPrincipal() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("sub", sub);
        if (email != null) {
            attributes.put("email", email);
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        if (scope != null) {
            attributes.put("scope", scope);
            authorities = Arrays.stream(scope.split(" "))
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }

        return new DefaultOAuth2AuthenticatedPrincipal(attributes, authorities);
    }

}

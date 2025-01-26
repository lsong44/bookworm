package com.li.bookworm.config;

import com.azure.core.http.HttpHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UserInfoFilter extends OncePerRequestFilter {

    @Value("${spring.security.oauth2.client.provider.google.user-info-uri}")
    private String USERINFO_ENDPOINT;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (SecurityContextHolder.getContext().getAuthentication() instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken authentication = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getToken() instanceof Jwt) {
                Jwt jwt = (Jwt) authentication.getToken();
                String accessToken = jwt.getTokenValue();

                try {
                    RestTemplate restTemplate = new RestTemplate();
                    HttpHeaders headers = new HttpHeaders();
                    headers.setBearerAuth(accessToken);
                    HttpEntity<String> entity = new HttpEntity<>(headers);
                    ResponseEntity<Map> userInfoResponse = restTemplate.exchange(USERINFO_ENDPOINT,
                            HttpMethod.GET, entity, Map.class);

                    if (userInfoResponse.getStatusCode().is2xxSuccessful()) {
                        Map<String, Object> userInfo = userInfoResponse.getBody();
                        Collection<String> scopes = (Collection<String>) userInfo.get("scope");
                        if (scopes != null) {
                            List<GrantedAuthority> authorities = scopes.stream()
                                    .map(scope -> new SimpleGrantedAuthority("SCOPE_" + scope))
                                    .collect(Collectors.toList());
                            SecurityContextHolder.getContext().setAuthentication(
                                    new JwtAuthenticationToken(jwt, authorities, authentication.getName())
                            );

                        }
                    }
                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
            }

        }
        filterChain.doFilter(request, response);

    }

}

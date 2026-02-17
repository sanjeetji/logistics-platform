package com.logistics.gateway.security;

import java.util.Objects;

import com.logistics.platform.security.jwt.JwtUtils;
import com.logistics.platform.security.jwt.SecurityConstants;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationWebFilter implements WebFilter {

    private final JwtUtils jwtUtils;

    public JwtAuthenticationWebFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    @org.springframework.lang.NonNull
    public Mono<Void> filter(@org.springframework.lang.NonNull ServerWebExchange exchange,
            @org.springframework.lang.NonNull WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();

        // Whitelisted endpoints that do NOT require authentication
        List<String> publicEndpoints = List.of(
                "/auth-service/api/v1/auth/login",
                "/auth-service/api/v1/auth/register",
                "/auth-service/api/v1/auth/refresh",
                "/auth-service/api/v1/auth/forgot-password",
                "/auth-service/api/v1/auth/reset-password");

        // Check if path matches any public endpoint
        if (publicEndpoints.stream().anyMatch(path::startsWith)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(SecurityConstants.TOKEN_PREFIX.length());

        try {
            String username = jwtUtils.extractUsername(token);

            if (username != null && jwtUtils.validateToken(token, username)) {
                // Extract roles from token
                List<String> roles = jwtUtils.extractClaim(token,
                        claims -> claims.get(SecurityConstants.CLAIM_ROLES, List.class));

                List<SimpleGrantedAuthority> authorities = roles != null
                        ? roles.stream()
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList())
                        : List.of();

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username,
                        null, authorities);

                return Objects.requireNonNull(chain.filter(exchange)
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication)));
            }
        } catch (Exception e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
}

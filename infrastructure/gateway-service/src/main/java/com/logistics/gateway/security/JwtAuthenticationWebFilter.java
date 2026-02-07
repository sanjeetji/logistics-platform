package com.logistics.gateway.security;

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
    public Mono<Void> filter(@org.springframework.lang.NonNull ServerWebExchange exchange,
            @org.springframework.lang.NonNull WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();

        // Skip authentication for auth endpoints
        if (path.startsWith("/auth-service/api/v1/auth/")) {
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

                return chain.filter(exchange)
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
            }
        } catch (Exception e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
}

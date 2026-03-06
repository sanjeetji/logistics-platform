package com.logistics.auth.config;

import com.logistics.platform.security.jwt.JwtUtils;
import com.logistics.platform.security.jwt.SecurityConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import com.logistics.auth.service.TokenBlacklistService;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;
    private final TokenBlacklistService tokenBlacklistService;

    public JwtAuthenticationFilter(JwtUtils jwtUtils, UserDetailsService userDetailsService,
            TokenBlacklistService tokenBlacklistService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Override
    protected void doFilterInternal(@org.springframework.lang.NonNull HttpServletRequest request,
            @org.springframework.lang.NonNull HttpServletResponse response,
            @org.springframework.lang.NonNull FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader(SecurityConstants.AUTH_HEADER);
        final String jwt;
        final String userEmail;

        System.out.println("DEBUG: JwtAuthenticationFilter processing request: " + request.getRequestURI());

        if (authHeader == null || !authHeader.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);

        if (tokenBlacklistService.isBlacklisted(jwt)) {
            filterChain.doFilter(request, response);
            return;
        }

        userEmail = jwtUtils.extractUsername(jwt);

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtUtils.validateToken(jwt, userEmail)) {

                // Extract roles and permissions from token
                @SuppressWarnings("unchecked")
                java.util.List<String> roles = (java.util.List<String>) jwtUtils.extractClaim(jwt,
                        claims -> claims.get(SecurityConstants.CLAIM_ROLES, java.util.List.class));
                @SuppressWarnings("unchecked")
                java.util.List<String> permissions = (java.util.List<String>) jwtUtils.extractClaim(jwt,
                        claims -> claims.get(SecurityConstants.CLAIM_PERMISSIONS, java.util.List.class));

                java.util.Set<org.springframework.security.core.GrantedAuthority> authorities = new java.util.HashSet<>();

                if (roles != null) {
                    for (String role : roles) {
                        authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority(role));
                    }
                }
                if (permissions != null) {
                    for (String permission : permissions) {
                        authorities.add(
                                new org.springframework.security.core.authority.SimpleGrantedAuthority(permission));
                    }
                }

                // Try to load UserDetails for principal, but fallback to just email if we want
                // to avoid DB hit.
                // Keeping DB hit for now to ensure user hasn't been deleted or suspended since
                // token was issued.
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        authorities); // Use authorities from token instead of DB to allow custom roles
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}

package com.logistics.platform.security.jwt;

import com.logistics.platform.common.dto.enums.UserType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtils {

    @Value("${logistics.auth.jwt.secret:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}")
    private String secret;

    @Value("${logistics.auth.jwt.expiration:86400000}")
    private long jwtExpiration;

    // Generate Token for User with Roles, Permissions, and Organization ID
    public String generateToken(String username, List<String> roles, List<String> permissions, Long organizationId,
            UserType userType) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(SecurityConstants.CLAIM_ROLES, roles);
        claims.put(SecurityConstants.CLAIM_PERMISSIONS, permissions);
        claims.put(SecurityConstants.CLAIM_USER_TYPE, userType.name());

        // Add Organization ID if present (for B2B tenants)
        if (organizationId != null) {
            claims.put(SecurityConstants.CLAIM_ORG_ID, organizationId);
        }

        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    public boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Helper to get Organization ID from token
    public Long extractOrganizationId(String token) {
        Claims claims = extractAllClaims(token);
        Object orgId = claims.get(SecurityConstants.CLAIM_ORG_ID);
        if (orgId != null) {
            return Long.valueOf(orgId.toString());
        }
        return null;
    }
}

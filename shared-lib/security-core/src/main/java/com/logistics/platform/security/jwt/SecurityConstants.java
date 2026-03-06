package com.logistics.platform.security.jwt;

public class SecurityConstants {
    public static final String AUTH_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final long EXPIRATION_TIME = 86400000; // 24 hours
    public static final long REFRESH_EXPIRATION_TIME = 604800000; // 7 days

    // JWT Claims
    public static final String CLAIM_ROLES = "roles";
    public static final String CLAIM_PERMISSIONS = "permissions";
    public static final String CLAIM_ORG_ID = "orgId";
    public static final String CLAIM_USER_TYPE = "userType";

    private SecurityConstants() {
        // Private constructor to prevent instantiation
    }
}

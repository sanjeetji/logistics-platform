package com.logistics.auth.model;

import com.logistics.platform.common.dto.enums.UserType;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "user_tenants")
public class UserTenant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Long organizationId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType role; // Role within this specific organization

    public UserTenant() {
    }

    public UserTenant(UUID id, User user, Long organizationId, UserType role) {
        this.id = id;
        this.user = user;
        this.organizationId = organizationId;
        this.role = role;
    }

    public static UserTenantBuilder builder() {
        return new UserTenantBuilder();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public UserType getRole() {
        return role;
    }

    public void setRole(UserType role) {
        this.role = role;
    }

    public static class UserTenantBuilder {
        private UUID id;
        private User user;
        private Long organizationId;
        private UserType role;

        UserTenantBuilder() {
        }

        public UserTenantBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public UserTenantBuilder user(User user) {
            this.user = user;
            return this;
        }

        public UserTenantBuilder organizationId(Long organizationId) {
            this.organizationId = organizationId;
            return this;
        }

        public UserTenantBuilder role(UserType role) {
            this.role = role;
            return this;
        }

        public UserTenant build() {
            return new UserTenant(id, user, organizationId, role);
        }
    }
}

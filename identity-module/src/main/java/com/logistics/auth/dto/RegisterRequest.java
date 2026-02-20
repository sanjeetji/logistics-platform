package com.logistics.auth.dto;

import com.logistics.platform.common.dto.enums.UserType;

public class RegisterRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phone;
    private UserType userType;
    private Long organizationId; // Nullable for B2C
    private Long teamId; // Nullable

    public RegisterRequest() {
    }

    public RegisterRequest(String firstName, String lastName, String email, String password, String phone,
            UserType userType, Long organizationId, Long teamId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.userType = userType;
        this.organizationId = organizationId;
        this.teamId = teamId;
    }

    public static RegisterRequestBuilder builder() {
        return new RegisterRequestBuilder();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public static class RegisterRequestBuilder {
        private String firstName;
        private String lastName;
        private String email;
        private String password;
        private String phone;
        private UserType userType;
        private Long organizationId;
        private Long teamId;

        RegisterRequestBuilder() {
        }

        public RegisterRequestBuilder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public RegisterRequestBuilder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public RegisterRequestBuilder email(String email) {
            this.email = email;
            return this;
        }

        public RegisterRequestBuilder password(String password) {
            this.password = password;
            return this;
        }

        public RegisterRequestBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public RegisterRequestBuilder userType(UserType userType) {
            this.userType = userType;
            return this;
        }

        public RegisterRequestBuilder organizationId(Long organizationId) {
            this.organizationId = organizationId;
            return this;
        }

        public RegisterRequestBuilder teamId(Long teamId) {
            this.teamId = teamId;
            return this;
        }

        public RegisterRequest build() {
            return new RegisterRequest(firstName, lastName, email, password, phone, userType, organizationId, teamId);
        }
    }
}

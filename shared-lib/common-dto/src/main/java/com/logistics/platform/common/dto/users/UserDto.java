package com.logistics.platform.common.dto.users;

import com.logistics.platform.common.dto.enums.UserType;
import java.util.UUID;

public class UserDto {
    private UUID id;
    private String username;
    private String email;
    private String phone;
    private String firstName;
    private String lastName;
    private UserType userType;
    private boolean active;
    private Long organizationId;
    private Long teamId;

    public UserDto() {
    }

    public UserDto(UUID id, String username, String email, String phone, String firstName, String lastName,
            UserType userType, boolean active, Long organizationId, Long teamId) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userType = userType;
        this.active = active;
        this.organizationId = organizationId;
        this.teamId = teamId;
    }

    public static UserDtoBuilder builder() {
        return new UserDtoBuilder();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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

    public static class UserDtoBuilder {
        private UUID id;
        private String username;
        private String email;
        private String phone;
        private String firstName;
        private String lastName;
        private UserType userType;
        private boolean active;
        private Long organizationId;
        private Long teamId;

        UserDtoBuilder() {
        }

        public UserDtoBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public UserDtoBuilder username(String username) {
            this.username = username;
            return this;
        }

        public UserDtoBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserDtoBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public UserDtoBuilder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public UserDtoBuilder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public UserDtoBuilder userType(UserType userType) {
            this.userType = userType;
            return this;
        }

        public UserDtoBuilder active(boolean active) {
            this.active = active;
            return this;
        }

        public UserDtoBuilder organizationId(Long organizationId) {
            this.organizationId = organizationId;
            return this;
        }

        public UserDtoBuilder teamId(Long teamId) {
            this.teamId = teamId;
            return this;
        }

        public UserDto build() {
            return new UserDto(id, username, email, phone, firstName, lastName, userType, active, organizationId,
                    teamId);
        }
    }
}

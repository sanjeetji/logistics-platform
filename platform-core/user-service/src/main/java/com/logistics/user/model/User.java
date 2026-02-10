package com.logistics.user.model;

import com.logistics.platform.common.dto.enums.UserStatus;
import com.logistics.platform.common.dto.enums.UserType;
import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "users")
@SQLDelete(sql = "UPDATE users SET deleted = true WHERE id=?")
@SQLRestriction("deleted=false")
public class User extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Enumerated(EnumType.STRING)
    private UserType userType;

    private String phone;

    @Embedded
    private UserProfile profile;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    @Column(nullable = false)
    private boolean deleted = false;

    public User() {
    }

    public User(String email, String firstName, String lastName, UserType userType, String phone, UserProfile profile,
            UserStatus status, boolean deleted) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userType = userType;
        this.phone = phone;
        this.profile = profile;
        this.status = status;
        this.deleted = deleted;
    }

    public static UserBuilder builder() {
        return new UserBuilder();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public UserProfile getProfile() {
        return profile;
    }

    public void setProfile(UserProfile profile) {
        this.profile = profile;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public static class UserBuilder {
        private String email;
        private String firstName;
        private String lastName;
        private UserType userType;
        private String phone;
        private UserProfile profile;
        private UserStatus status = UserStatus.ACTIVE;
        private boolean deleted = false;

        UserBuilder() {
        }

        public UserBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserBuilder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public UserBuilder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public UserBuilder userType(UserType userType) {
            this.userType = userType;
            return this;
        }

        public UserBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public UserBuilder profile(UserProfile profile) {
            this.profile = profile;
            return this;
        }

        public UserBuilder status(UserStatus status) {
            this.status = status;
            return this;
        }

        public UserBuilder deleted(boolean deleted) {
            this.deleted = deleted;
            return this;
        }

        public User build() {
            return new User(email, firstName, lastName, userType, phone, profile, status, deleted);
        }

        public String toString() {
            return "User.UserBuilder(email=" + this.email + ", firstName=" + this.firstName + ", lastName="
                    + this.lastName + ", userType=" + this.userType + ", phone=" + this.phone + ", profile="
                    + this.profile + ", status=" + this.status + ", deleted=" + this.deleted + ")";
        }
    }
}

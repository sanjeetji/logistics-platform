package com.logistics.user.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class UserProfile {
    private String avatarUrl;
    private String preferences; // JSON string or specific fields
    private String address;
    private String language;

    public UserProfile() {
    }

    public UserProfile(String avatarUrl, String preferences, String address, String language) {
        this.avatarUrl = avatarUrl;
        this.preferences = preferences;
        this.address = address;
        this.language = language;
    }

    public static UserProfileBuilder builder() {
        return new UserProfileBuilder();
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getPreferences() {
        return preferences;
    }

    public void setPreferences(String preferences) {
        this.preferences = preferences;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserProfile that = (UserProfile) o;
        return java.util.Objects.equals(avatarUrl, that.avatarUrl) &&
                java.util.Objects.equals(preferences, that.preferences) &&
                java.util.Objects.equals(address, that.address) &&
                java.util.Objects.equals(language, that.language);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(avatarUrl, preferences, address, language);
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "avatarUrl='" + avatarUrl + '\'' +
                ", preferences='" + preferences + '\'' +
                ", address='" + address + '\'' +
                ", language='" + language + '\'' +
                '}';
    }

    public static class UserProfileBuilder {
        private String avatarUrl;
        private String preferences;
        private String address;
        private String language;

        UserProfileBuilder() {
        }

        public UserProfileBuilder avatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
            return this;
        }

        public UserProfileBuilder preferences(String preferences) {
            this.preferences = preferences;
            return this;
        }

        public UserProfileBuilder address(String address) {
            this.address = address;
            return this;
        }

        public UserProfileBuilder language(String language) {
            this.language = language;
            return this;
        }

        public UserProfile build() {
            return new UserProfile(avatarUrl, preferences, address, language);
        }

        public String toString() {
            return "UserProfile.UserProfileBuilder(avatarUrl=" + this.avatarUrl + ", preferences=" + this.preferences
                    + ", address=" + this.address + ", language=" + this.language + ")";
        }
    }
}

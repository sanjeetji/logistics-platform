package com.logistics.user.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {
    private String avatarUrl;
    private String preferences; // JSON string or specific fields
    private String address;
    private String language;
}

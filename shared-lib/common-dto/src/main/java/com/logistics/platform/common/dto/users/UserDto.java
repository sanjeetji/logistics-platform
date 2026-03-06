package com.logistics.platform.common.dto.users;

import com.logistics.platform.common.dto.enums.UserType;
import com.logistics.platform.common.dto.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String address;
    private String avatarUrl;
    private String preferences;
    private String firstName;
    private String lastName;
    private UserType userType;
    private UserStatus status;
    private java.time.LocalDateTime lastLogin;
    private Long organizationId;
    private Long teamId;
}

package com.logistics.user.mapper;

import com.logistics.platform.common.dto.users.UserDto;
import com.logistics.user.model.User;
import com.logistics.user.model.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    @Mapping(target = "preferences", source = "profile.preferences")
    @Mapping(target = "avatarUrl", source = "profile.avatarUrl")
    @Mapping(target = "status", source = "status")
    UserDto toDto(User user);

    @Mapping(target = "profile.preferences", source = "preferences")
    @Mapping(target = "profile.avatarUrl", source = "avatarUrl")
    @Mapping(target = "deleted", ignore = true)
    User toEntity(UserDto userDto);
}

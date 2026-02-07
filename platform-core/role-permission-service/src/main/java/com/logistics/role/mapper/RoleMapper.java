package com.logistics.role.mapper;

import com.logistics.role.dto.PermissionDto;
import com.logistics.role.dto.RoleDto;
import com.logistics.role.model.Permission;
import com.logistics.role.model.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleMapper {

    PermissionDto toDto(Permission permission);

    Permission toEntity(PermissionDto permissionDto);

    @Mapping(target = "parentRoleId", source = "parentRole.id")
    RoleDto toDto(Role role);

    @Mapping(target = "parentRole", ignore = true)
    @Mapping(target = "childRoles", ignore = true)
    Role toEntity(RoleDto roleDto);

    List<RoleDto> toDtoList(List<Role> roles);
}

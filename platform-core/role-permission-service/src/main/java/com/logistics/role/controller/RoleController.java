package com.logistics.role.controller;

import com.logistics.platform.common.dto.response.ApiResponse;
import com.logistics.role.dto.PermissionDto;
import com.logistics.role.dto.RoleDto;
import com.logistics.role.mapper.RoleMapper;
import com.logistics.role.model.Permission;
import com.logistics.role.model.Role;
import com.logistics.role.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;
    private final RoleMapper roleMapper;

    @GetMapping
    public ResponseEntity<ApiResponse<List<RoleDto>>> getAllRoles() {
        return ResponseEntity.ok(ApiResponse.success(roleMapper.toDtoList(roleService.getAllRoles())));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RoleDto>> createRole(@RequestBody RoleDto roleDto) {
        Role role = roleMapper.toEntity(roleDto);
        return ResponseEntity
                .ok(ApiResponse.success(roleMapper.toDto(roleService.createRole(role)), "Role created successfully"));
    }

    @PostMapping("/permissions")
    public ResponseEntity<ApiResponse<PermissionDto>> createPermission(@RequestBody PermissionDto permissionDto) {
        Permission permission = roleMapper.toEntity(permissionDto);
        return ResponseEntity
                .ok(ApiResponse.success(roleMapper.toDto(roleService.createPermission(permission)),
                        "Permission created successfully"));
    }

    @PostMapping("/{roleId}/permissions")
    public ResponseEntity<ApiResponse<RoleDto>> assignPermissions(@PathVariable Long roleId,
            @RequestBody Set<Long> permissionIds) {
        return ResponseEntity
                .ok(ApiResponse.success(roleMapper.toDto(roleService.assignPermissionsToRole(roleId, permissionIds)),
                        "Permissions assigned successfully"));
    }

    @PutMapping("/{roleId}/parent/{parentRoleId}")
    public ResponseEntity<ApiResponse<RoleDto>> assignParentRole(@PathVariable Long roleId,
            @PathVariable Long parentRoleId) {
        return ResponseEntity
                .ok(ApiResponse.success(roleMapper.toDto(roleService.assignParentRole(roleId, parentRoleId)),
                        "Parent role assigned successfully"));
    }
}

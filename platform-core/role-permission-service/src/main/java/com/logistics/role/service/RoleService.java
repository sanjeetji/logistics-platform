package com.logistics.role.service;

import com.logistics.role.model.Permission;
import com.logistics.role.model.Role;
import com.logistics.role.repository.PermissionRepository;
import com.logistics.role.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Role getRoleById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Role ID must not be null");
        }
        return roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));
    }

    public Role createRole(Role role) {
        if (roleRepository.findByName(role.getName()).isPresent()) {
            throw new RuntimeException("Role already exists: " + role.getName());
        }
        return roleRepository.save(role);
    }

    public Permission createPermission(Permission permission) {
        if (permissionRepository.findByName(permission.getName()).isPresent()) {
            throw new RuntimeException("Permission already exists: " + permission.getName());
        }
        return permissionRepository.save(permission);
    }

    @Transactional
    public Role assignPermissionsToRole(Long roleId, Set<Long> permissionIds) {
        Role role = getRoleById(roleId);
        if (permissionIds == null) {
            throw new IllegalArgumentException("Permission IDs must not be null");
        }
        List<Permission> permissions = permissionRepository.findAllById(permissionIds);
        role.setPermissions(new HashSet<>(permissions));
        return roleRepository.save(role);
    }

    @Transactional
    public Role assignParentRole(Long roleId, Long parentRoleId) {
        Role role = getRoleById(roleId);
        Role parentRole = getRoleById(parentRoleId);

        if (role.getId().equals(parentRole.getId())) {
            throw new RuntimeException("Cannot assign role as its own parent");
        }

        // Basic cycle detection could be added here

        role.setParentRole(parentRole);
        return roleRepository.save(role);
    }
}

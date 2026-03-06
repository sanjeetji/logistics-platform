package com.logistics.usermanagement.config;

import com.logistics.platform.common.dto.enums.UserType;
import com.logistics.usermanagement.entity.Role;
import com.logistics.usermanagement.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Seeds the default system roles into the database on application startup.
 * Skips any role that already exists (idempotent).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RoleDataSeeder implements ApplicationRunner {

    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        seedRoles();
    }

    private void seedRoles() {
        int seeded = 0;

        for (UserType userType : UserType.values()) {
            String roleName = userType.name(); // e.g. "SUPER_ADMIN", "DRIVER"
            if (!roleRepository.existsByNameAndTenantId(roleName, "SYSTEM") &&
                    !roleRepository.existsByNameAndTenantId("ROLE_" + roleName, "SYSTEM")) {

                Role role = Role.builder()
                        .name("ROLE_" + roleName)
                        .description("Default system role for " + roleName)
                        .tenantId("SYSTEM")
                        .roleType(Role.RoleType.SYSTEM)
                        .active(true)
                        .build();

                roleRepository.save(java.util.Objects.requireNonNull(role));
                log.debug("Seeded system role: {}", role.getName());
                seeded++;
            }
        }

        if (seeded > 0) {
            log.info("RoleDataSeeder: seeded {} new system roles into the database", seeded);
        } else {
            log.info("RoleDataSeeder: all system roles already exist — skipping seed");
        }
    }
}

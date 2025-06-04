package com.plazoleta.user_microservice.infrastructure.configuration;

import com.plazoleta.user_microservice.domain.model.*;
import com.plazoleta.user_microservice.domain.spi.IPasswordEncoderPort;
import com.plazoleta.user_microservice.domain.spi.IRolePersistencePort;
import com.plazoleta.user_microservice.domain.spi.IUserPersistencePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;


@Profile("!test")
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final IRolePersistencePort rolePersistencePort;
    private final IUserPersistencePort userPersistencePort;
    private final IPasswordEncoderPort passwordEncoder;

    public DataInitializer(IRolePersistencePort rolePersistencePort,
                           IUserPersistencePort userPersistencePort,
                           IPasswordEncoderPort passwordEncoder) {
        this.rolePersistencePort = rolePersistencePort;
        this.userPersistencePort = userPersistencePort;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        logger.info("🚀 Iniciando configuración de datos...");
        try {
            initRoles();
            initAdminUser();
            logger.info("✅ Configuración de datos completada exitosamente.");
        } catch (Exception e) {
            logger.error("❌ Error durante la inicialización de datos", e);
            throw new RuntimeException("Fallo en la inicialización de datos", e);
        }
    }

    private void initRoles() {
        logger.info("🔧 Verificando roles...");

        if (rolePersistencePort.getRoleByName(RoleList.ROLE_ADMIN).isPresent()) {
            logger.info("⚠️ Los roles ya estaban inicializados. Saltando creación.");
            return;
        }

        logger.info("📝 Creando roles por defecto...");
        createDefaultRoles();
    }

    private void createDefaultRoles() {
        createRoleIfNotExists(RoleList.ROLE_ADMIN, "Administrador del sistema");
        createRoleIfNotExists(RoleList.ROLE_OWNER, "Propietario de restaurante");
        createRoleIfNotExists(RoleList.ROLE_EMPLOYED, "Empleado de restaurante");
        createRoleIfNotExists(RoleList.ROLE_CUSTOMER, "Cliente");

        logger.info("✅ Roles inicializados correctamente.");
    }

    private void createRoleIfNotExists(RoleList roleEnum, String description) {
        Optional<Role> existingRole = rolePersistencePort.getRoleByName(roleEnum);
        if (existingRole.isEmpty()) {
            Role newRole = new Role(null, roleEnum, description);
            rolePersistencePort.saveRole(newRole);
            logger.info("🔨 Rol creado: {}", roleEnum);
        } else {
            logger.debug("ℹ️ Rol ya existe: {} con ID: {}", roleEnum, existingRole.get().getId());
        }
    }

    private void initAdminUser() {
        logger.info("👤 Verificando usuario administrador...");

        String adminEmail = "admin@plazoleta.com";
        Optional<User> existingUser = userPersistencePort.getUserByEmail(adminEmail);

        if (existingUser.isPresent()) {
            logger.info("ℹ️ Usuario ADMIN ya existe.");
            logger.info("   📧 Email existente: {}", existingUser.get().getEmail());
            return;
        }

        logger.info("🔨 Creando usuario administrador por defecto...");

        Role adminRole = rolePersistencePort.getRoleByName(RoleList.ROLE_ADMIN)
                .orElseThrow(() -> new IllegalStateException("❌ No se encontró el rol ADMIN. Asegúrate de crear los roles primero."));

        User adminUser = User.builder()
                .firstName("Super")
                .lastName("Admin")
                .identityNumber("999999999")
                .phoneNumber("+573000000000")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .email(adminEmail)
                .password(passwordEncoder.encode("admin123"))
                .role(adminRole)
                .build();

        userPersistencePort.saveUser(adminUser);

        logger.info("✅ Usuario ADMIN creado correctamente:");
        logger.info("   📧 Email: {}", adminEmail);
        logger.info("   🔐 Password: admin123");
        logger.warn("⚠️ IMPORTANTE: Cambia la contraseña por defecto en producción.");
    }
}

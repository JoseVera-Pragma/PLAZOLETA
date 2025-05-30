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
            logger.error("❌ Error durante la inicialización de datos: {}", e.getMessage(), e);
            throw new RuntimeException("Fallo en la inicialización de datos", e);
        }
    }

    private void initRoles() {
        logger.info("🔧 Verificando roles...");

        try {
            Role adminRole = rolePersistencePort.getRoleByName(RoleList.ROLE_ADMIN);
            if (adminRole != null) {
                logger.info("⚠️ Los roles ya estaban inicializados. Saltando creación.");
                return;
            }
        } catch (Exception e) {
            logger.debug("Rol ADMIN no encontrado, procediendo a crear roles.");
        }

        logger.info("📝 Creando roles por defecto...");
        createDefaultRoles();
    }

    private void createDefaultRoles() {
        createRoleIfNotExists(RoleList.ROLE_ADMIN, "Administrador del sistema");
        createRoleIfNotExists(RoleList.ROLE_OWNER, "Propietario de restaurante");
        createRoleIfNotExists(RoleList.ROLE_EMPLOYED, "Empleado de restaurante");
        createRoleIfNotExists(RoleList.ROLE_CUSTOMER, "Cliente");

        logger.info("✅ Roles inicializados correctamente:");
        logger.info("   - ADMIN: Administrador del sistema");
        logger.info("   - OWNER: Propietario de restaurante");
        logger.info("   - EMPLOYED: Empleado de restaurante");
        logger.info("   - CUSTOMER: Cliente");
    }

    private void createRoleIfNotExists(RoleList roleEnum, String description) {
        try {
            Role existingRole = rolePersistencePort.getRoleByName(roleEnum);
            if (existingRole == null) {
                logger.info("🔨 Creando rol: {}", roleEnum);
                rolePersistencePort.saveRole(new Role(null, roleEnum, description));
                logger.debug("✅ Rol creado: {}", roleEnum);
            } else {
                logger.debug("ℹ️ Rol ya existe: {} con ID: {}", roleEnum, existingRole.getId());
            }
        } catch (Exception e) {
            logger.info("🔨 Rol no encontrado, creando: {}", roleEnum);
            rolePersistencePort.saveRole(new Role(null, roleEnum, description));
            logger.debug("✅ Rol creado: {}", roleEnum);
        }
    }

    private void initAdminUser() {
        logger.info("👤 Verificando usuario administrador...");

        Email adminEmail = new Email("admin@plazoleta.com");
        User existingUser = userPersistencePort.getUserByEmail(adminEmail);

        if (existingUser == null) {
            logger.info("🔨 Creando usuario administrador por defecto...");

            Role adminRole = rolePersistencePort.getRoleByName(RoleList.ROLE_ADMIN);
            if (adminRole == null) {
                logger.error("❌ No se encontró el rol ADMIN. Asegúrate de que los roles se hayan creado correctamente.");
                throw new RuntimeException("Role ADMIN not found. Roles must be initialized first.");
            }

            User adminUser = User.builder()
                    .firstName("Super")
                    .lastName("Admin")
                    .identityNumber(new IdentityNumber("999999999"))
                    .phoneNumber(new PhoneNumber("+573000000000"))
                    .dateOfBirth(LocalDate.of(1990, 1, 1))
                    .email(adminEmail)
                    .password(passwordEncoder.encode("admin123"))
                    .role(adminRole)
                    .build();

            userPersistencePort.saveUser(adminUser);

            logger.info("✅ Usuario ADMIN creado correctamente:");
            logger.info("   📧 Email: admin@plazoleta.com");
            logger.info("   🔐 Password: admin123");
            logger.warn("⚠️  IMPORTANTE: Cambia la contraseña por defecto en producción!");
        } else {
            logger.info("ℹ️ Usuario ADMIN ya existe. No se creó uno nuevo.");
            logger.info("   📧 Email existente: {}", existingUser.getEmail().getValue());
        }
    }
}
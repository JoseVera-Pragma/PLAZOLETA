package com.plazoleta.user_microservice.domain.model;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.profiles.active=test")
class RoleTest {

    @Test
    void shouldCreateRoleEmpty() {
        Role role = new Role();

        assertNull(role.getId());
        assertNull(role.getDescription());
        assertNull(role.getName());
    }

    @Test
    void shouldCreateRoleWithValidData() {
        Role role = new Role( 1L, RoleList.ROLE_ADMIN,"Administrador del sistema");

        assertEquals(1L, role.getId());
        assertEquals(RoleList.ROLE_ADMIN, role.getName());
        assertEquals("Administrador del sistema", role.getDescription());
    }

    @Test
    void shouldCreateWhenDescriptionIsNull() {
        Role role = new Role(2L, RoleList.ROLE_OWNER, null);

        assertEquals(2L, role.getId());
        assertEquals(RoleList.ROLE_OWNER, role.getName());
        assertNull(role.getDescription());
    }

    @Test
    void shouldCreateWhenIdIsNull() {
        Role role = new Role(null, RoleList.ROLE_OWNER,"null");

        assertNull(role.getId());
        assertEquals(RoleList.ROLE_OWNER, role.getName());
        assertEquals("null", role.getDescription());
    }

    @Test
    void shouldEditRole() {
        Role role = new Role(10L, RoleList.ROLE_ADMIN,"Description");

        assertEquals(10L, role.getId());
        assertEquals(RoleList.ROLE_ADMIN, role.getName());
        assertEquals("Description", role.getDescription());

        role.setId(2L);
        role.setName(RoleList.ROLE_CUSTOMER);
        role.setDescription("NewDescription");

        assertEquals(2L, role.getId());
        assertEquals(RoleList.ROLE_CUSTOMER, role.getName());
        assertEquals("NewDescription", role.getDescription());
    }

    @Test
    void isAdminShouldReturnTrueOnlyForAdminRole() {
        Role admin = new Role(1L, RoleList.ROLE_ADMIN,"Administrador");
        assertTrue(admin.isAdmin());
        assertFalse(admin.isOwner());
        assertFalse(admin.isEmployed());
        assertFalse(admin.isCustomer());
    }

    @Test
    void isOwnerShouldReturnTrueOnlyForOwnerRole() {
        Role owner = new Role(2L, RoleList.ROLE_OWNER,"Propietario");
        assertFalse(owner.isAdmin());
        assertTrue(owner.isOwner());
        assertFalse(owner.isEmployed());
        assertFalse(owner.isCustomer());
    }

    @Test
    void isEmployedShouldReturnTrueOnlyForEmployedRole() {
        Role employed = new Role(3L, RoleList.ROLE_EMPLOYED,"Empleado");
        assertFalse(employed.isAdmin());
        assertFalse(employed.isOwner());
        assertTrue(employed.isEmployed());
        assertFalse(employed.isCustomer());
    }

    @Test
    void isCustomerShouldReturnTrueOnlyForCustomerRole() {
        Role customer = new Role(4L, RoleList.ROLE_CUSTOMER,"Cliente");

        assertFalse(customer.isAdmin());
        assertFalse(customer.isOwner());
        assertFalse(customer.isEmployed());
        assertTrue(customer.isCustomer());
    }

    @Test
    void rolesWithSameIdShouldBeEqual() {
        Role r1 = new Role(10L, RoleList.ROLE_ADMIN,"Description");
        Role r2 = new Role(10L, RoleList.ROLE_CUSTOMER,"Other Description");

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void rolesWithSameIdButDifferentDescriptionsAndRoleNamesShouldBeEqual() {
        Role r1 = new Role(100L, RoleList.ROLE_ADMIN,"");
        Role r2 = new Role(100L, RoleList.ROLE_CUSTOMER,"Some description");
        assertEquals(r1, r2);
    }

    @Test
    void rolesWithNullIdsShouldBeEqualIfIdsAreNull() {
        Role r1 = new Role(null, RoleList.ROLE_ADMIN,"Description");
        Role r2 = new Role(null, RoleList.ROLE_CUSTOMER,"Other Description");
        assertEquals(r1, r2);
    }

    @Test
    void sameRoleMustBeEqualToItself() {
        Role r1 = new Role(10L, RoleList.ROLE_ADMIN,"Description");
        assertEquals(r1, r1);
    }

    @Test
    void rolesWithDifferentIdsShouldNotBeEqual() {
        Role r1 = new Role(1L, RoleList.ROLE_ADMIN,"One");
        Role r2 = new Role(2L, RoleList.ROLE_ADMIN,"Two");
        assertNotEquals(r1, r2);
    }

    @Test
    void roleShouldNotBeEqualToNull() {
        Role r1 = new Role(2L, RoleList.ROLE_ADMIN,"One");
        assertNotEquals(null, r1);
    }

    @Test
    void roleShouldNotBeEqualToDifferentClassObject() {
        Role r1 = new Role(2L, RoleList.ROLE_ADMIN,"One");
        String other = "NotARole";
        assertNotEquals(r1, other);
    }
}
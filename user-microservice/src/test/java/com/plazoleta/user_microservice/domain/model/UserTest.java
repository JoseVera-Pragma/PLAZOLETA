package com.plazoleta.user_microservice.domain.model;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.profiles.active=test")
class UserTest {

    private final String dummyFirstName = "Jose";
    private final String dummyLastName = "Vera";
    private final String dummyIdentity = "12345678";
    private final String dummyPhone = "+573001112233";
    private final String dummyEmail = "test@email.com";
    private final Role dummyRole = new Role(1L, RoleList.ROLE_ADMIN,"Admin role");
    private final LocalDate dummyBirthday = LocalDate.of(2000, 1, 1);
    private final String dummyPassword = "secret123";

    @Test
    void shouldCreateUserWithValidData() {
        User user = User.builder()
                .id(1L)
                .firstName(dummyFirstName)
                .lastName(dummyLastName)
                .identityNumber(dummyIdentity)
                .phoneNumber(dummyPhone)
                .email(dummyEmail)
                .password(dummyPassword)
                .dateOfBirth(dummyBirthday)
                .role(dummyRole)
                .build();

        assertEquals(1L, user.getId());
        assertEquals("Jose", user.getFirstName());
        assertEquals("Vera", user.getLastName());
        assertEquals(dummyIdentity, user.getIdentityNumber());
        assertEquals(dummyPhone, user.getPhoneNumber());
        assertEquals(dummyEmail, user.getEmail());
        assertEquals("secret123", user.getPassword());
        assertEquals(dummyRole, user.getRole());
    }

    @Test
    void usersWithSameIdShouldBeEqual() {
        User u1 = User.builder()
                .id(1L)
                .firstName("A")
                .lastName("B")
                .identityNumber(dummyIdentity)
                .phoneNumber(dummyPhone)
                .email(dummyEmail)
                .password("p")
                .build();

        User u2 = User.builder()
                .id(1L)
                .firstName("X")
                .lastName("Y")
                .identityNumber(dummyIdentity)
                .phoneNumber(dummyPhone)
                .email(dummyEmail)
                .password("p")
                .build();


        assertTrue(u1.equals(u1));
        assertEquals(u1, u2);
        assertEquals(u1.hashCode(), u2.hashCode());
    }

    @Test
    void usersWithDifferentIdsShouldNotBeEqual() {
        User u1 = User.builder().id(1L).firstName("A").lastName("B")
                .identityNumber(dummyIdentity)
                .phoneNumber(dummyPhone)
                .email(dummyEmail)
                .password("p")
                .build();

        User u2 = User.builder().id(2L).firstName("A").lastName("B")
                .identityNumber(dummyIdentity)
                .phoneNumber(dummyPhone)
                .email(dummyEmail)
                .password("p")
                .build();

        assertNotEquals(u1, u2);
    }

    @Test
    void compareUserWhitNullShouldNotBeEqual() {
        User u1 = User.builder().id(1L).firstName("A").lastName("B")
                .identityNumber(dummyIdentity)
                .phoneNumber(dummyPhone)
                .email(dummyEmail)
                .password("p")
                .build();

        assertNotEquals(u1, null);
    }
}

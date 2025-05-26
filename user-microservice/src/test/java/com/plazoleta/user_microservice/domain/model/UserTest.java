package com.plazoleta.user_microservice.domain.model;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.profiles.active=test")
class UserTest {

    private final String dummyFirstName = "Jose";
    private final String dummyLastName = "Vera";
    private final IdentityNumber dummyIdentity = new IdentityNumber("12345678");
    private final PhoneNumber dummyPhone = new PhoneNumber("+573001112233");
    private final Email dummyEmail = new Email("test@email.com");
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

        assertEquals("Jose", user.getFirstName());
        assertEquals("Vera", user.getLastName());
        assertEquals(dummyIdentity, user.getIdentityNumber());
        assertEquals(dummyPhone, user.getPhoneNumber());
        assertEquals(dummyEmail, user.getEmail());
        assertEquals("secret123", user.getPassword());
        assertEquals(dummyRole, user.getRole());
    }

    @Test
    void shouldThrowExceptionWhenFirstNameIsMissing() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            User.builder()
                    .lastName(dummyLastName)
                    .identityNumber(dummyIdentity)
                    .phoneNumber(dummyPhone)
                    .email(dummyEmail)
                    .password(dummyPassword)
                    .build();
        });
        assertEquals("First name is required", e.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenFirstNameIsBlank() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            User.builder()
                    .firstName("")
                    .lastName(dummyLastName)
                    .identityNumber(dummyIdentity)
                    .phoneNumber(dummyPhone)
                    .email(dummyEmail)
                    .password(dummyPassword)
                    .build();
        });
        assertEquals("First name is required", e.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenFirstNameIsNull() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            User.builder()
                    .firstName(null)
                    .lastName(dummyLastName)
                    .identityNumber(dummyIdentity)
                    .phoneNumber(dummyPhone)
                    .email(dummyEmail)
                    .password(dummyPassword)
                    .build();
        });
        assertEquals("First name is required", e.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenLastNameIsMissing() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            User.builder()
                    .firstName(dummyFirstName)
                    .identityNumber(dummyIdentity)
                    .phoneNumber(dummyPhone)
                    .email(dummyEmail)
                    .password(dummyPassword)
                    .build();
        });
        assertEquals("Last name is required", e.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenLastNameIsBlank() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            User.builder()
                    .firstName(dummyFirstName)
                    .lastName("")
                    .identityNumber(dummyIdentity)
                    .phoneNumber(dummyPhone)
                    .email(dummyEmail)
                    .password(dummyPassword)
                    .build();
        });
        assertEquals("Last name is required", e.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenLastNameIsNull() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            User.builder()
                    .firstName(dummyFirstName)
                    .lastName(null)
                    .identityNumber(dummyIdentity)
                    .phoneNumber(dummyPhone)
                    .email(dummyEmail)
                    .password(dummyPassword)
                    .build();
        });
        assertEquals("Last name is required", e.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenIdentityNumberIsMissing() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            User.builder()
                    .firstName(dummyFirstName)
                    .lastName(dummyLastName)
                    .phoneNumber(dummyPhone)
                    .email(dummyEmail)
                    .password(dummyPassword)
                    .build();
        });
        assertEquals("Identity number is required", e.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenIdentityNumberIsNull() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            User.builder()
                    .firstName(dummyFirstName)
                    .lastName(dummyLastName)
                    .identityNumber(null)
                    .phoneNumber(dummyPhone)
                    .email(dummyEmail)
                    .password(dummyPassword)
                    .build();
        });
        assertEquals("Identity number is required", e.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenIdentityNumberIsInvalid() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            IdentityNumber identity = new IdentityNumber("CC12345678");
            User.builder()
                    .firstName(dummyFirstName)
                    .lastName(dummyLastName)
                    .identityNumber(identity)
                    .phoneNumber(dummyPhone)
                    .email(dummyEmail)
                    .password(dummyPassword)
                    .build();
        });
        assertEquals("Identity number is invalid, only can contain numbers.", e.getMessage());
    }

    @Test
    void shoulThrowExceptionWhenPhoneNumberIsMissing(){
        Exception e =assertThrows(IllegalArgumentException.class, () -> {
            User.builder()
                    .firstName(dummyFirstName)
                    .lastName(dummyLastName)
                    .identityNumber(dummyIdentity)
                    .email(dummyEmail)
                    .password(dummyPassword)
                    .build();
        });
        assertEquals("Phone number is required",e.getMessage());
    }

    @Test
    void shoulThrowExceptionWhenPhoneNumberIsNull(){
        Exception e =assertThrows(IllegalArgumentException.class, () -> {
            User.builder()
                    .firstName(dummyFirstName)
                    .lastName(dummyLastName)
                    .identityNumber(dummyIdentity)
                    .phoneNumber(null)
                    .email(dummyEmail)
                    .password(dummyPassword)
                    .build();
        });
        assertEquals("Phone number is required",e.getMessage());
    }

    @Test
    void shoulThrowExceptionWhenPhoneNumberIsInvalid(){
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            PhoneNumber phoneNumber = new PhoneNumber("+57300111223333");
            User.builder()
                    .firstName(dummyFirstName)
                    .lastName(dummyLastName)
                    .identityNumber(dummyIdentity)
                    .phoneNumber(phoneNumber)
                    .email(dummyEmail)
                    .password(dummyPassword)
                    .build();
        });
        assertEquals("Phone number is not valid: +57300111223333",e.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenEmailIsMissing() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            User.builder()
                    .firstName("Jose")
                    .lastName("Vera")
                    .identityNumber(dummyIdentity)
                    .phoneNumber(dummyPhone)
                    .password("pass")
                    .build();
        });
        assertEquals("Email is required", e.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenEmailIsNull() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            User.builder()
                    .firstName("Jose")
                    .lastName("Vera")
                    .identityNumber(dummyIdentity)
                    .email(null)
                    .phoneNumber(dummyPhone)
                    .password("pass")
                    .build();
        });
        assertEquals("Email is required", e.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenEmailIsInvalid() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            Email email = new Email("test@email");

            User.builder()
                    .firstName("Jose")
                    .lastName("Vera")
                    .identityNumber(dummyIdentity)
                    .phoneNumber(dummyPhone)
                    .email(email)
                    .password("pass")
                    .build();
        });
        assertEquals("Email not is valid: test@email", e.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsMissing() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            User.builder()
                    .firstName("Jose")
                    .lastName("Vera")
                    .identityNumber(dummyIdentity)
                    .phoneNumber(dummyPhone)
                    .email(dummyEmail)
                    .build();
        });
        assertEquals("Password is required", e.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsBlank() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            User.builder()
                    .firstName("Jose")
                    .lastName("Vera")
                    .identityNumber(dummyIdentity)
                    .phoneNumber(dummyPhone)
                    .email(dummyEmail)
                    .password("")
                    .build();
        });
        assertEquals("Password is required", e.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsNull() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            User.builder()
                    .firstName("Jose")
                    .lastName("Vera")
                    .identityNumber(dummyIdentity)
                    .phoneNumber(dummyPhone)
                    .email(dummyEmail)
                    .password(null)
                    .build();
        });
        assertEquals("Password is required", e.getMessage());
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

    @Test
    void shouldSetAndGetAllUserFieldsCorrectly() {
        IdentityNumber identityNumber = new IdentityNumber("123456789");
        PhoneNumber phoneNumber = new PhoneNumber("+573001112233");
        Email email = new Email("jose@test.com");
        Role role = new Role(1L, RoleList.ROLE_ADMIN,"Admin role");

        User user = User.builder()
                .firstName("Inicial")
                .lastName("Inicial")
                .identityNumber(identityNumber)
                .phoneNumber(phoneNumber)
                .email(email)
                .password("initialPass")
                .build();

        user.setId(100L);
        user.setFirstName("Jose");
        user.setLastName("Vera");
        user.setIdentityNumber(new IdentityNumber("987654321"));
        user.setPhoneNumber(new PhoneNumber("+573001112299"));
        user.setEmail(new Email("updated@email.com"));
        user.setPassword("newPassword123");
        user.setDateOfBirth(LocalDate.of(1995, 5, 10));
        user.setRole(role);

        assertEquals(100L, user.getId());
        assertEquals("Jose", user.getFirstName());
        assertEquals("Vera", user.getLastName());
        assertEquals(new IdentityNumber("987654321"), user.getIdentityNumber());
        assertEquals(new PhoneNumber("+573001112299"), user.getPhoneNumber());
        assertEquals(new Email("updated@email.com"), user.getEmail());
        assertEquals("newPassword123", user.getPassword());
        assertEquals(LocalDate.of(1995, 5, 10), user.getDateOfBirth());
        assertEquals(role, user.getRole());
    }

}

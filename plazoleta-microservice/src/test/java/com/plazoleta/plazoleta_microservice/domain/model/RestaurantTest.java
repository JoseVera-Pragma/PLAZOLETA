package com.plazoleta.plazoleta_microservice.domain.model;

import com.plazoleta.plazoleta_microservice.domain.exception.DomainException;
import com.plazoleta.plazoleta_microservice.domain.exception.restaurant.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.profiles.active=test")
class RestaurantTest {

    private final String validName = "Valid Name";
    private final String validNit = "123456";
    private final String validAddress = "Valid Address";
    private final String validPhoneNumber = "+1234567890";
    private final String validUrlLogo = "http://valid.logo/url.png";
    private final Long validIdOwner = 1L;

    @Test
    void shouldCreateValidRestaurant(){
        Restaurant restaurant = new Restaurant.Builder()
                .id(1L)
                .name(validName)
                .nit(validNit)
                .address(validAddress)
                .phoneNumber(validPhoneNumber)
                .urlLogo(validUrlLogo)
                .idOwner(validIdOwner)
                .build();

        assertEquals(validName, restaurant.getName());
        assertEquals(validNit, restaurant.getNit());
        assertEquals(validAddress, restaurant.getAddress());
        assertEquals(validPhoneNumber, restaurant.getPhoneNumber());
        assertEquals(validUrlLogo, restaurant.getUrlLogo());
        assertEquals(validIdOwner, restaurant.getIdOwner());
        assertEquals(1L, restaurant.getId());
    }

    @Test
    void buildShouldThrowDomainExceptionWhenAllFieldsAreNull() {
        Restaurant.Builder builder = new Restaurant.Builder();

        DomainException exception = assertThrows(DomainException.class, builder::build);
        assertEquals("All required fields must be provided", exception.getMessage());
    }

    @Test
    void shouldThrowWhenNameIsBlank(){
        assertThrows(InvalidRestaurantNameException.class, ()->new Restaurant.Builder().name(""));
    }

    @Test
    void shouldThrowWhenNameIsNull(){
        assertThrows(InvalidRestaurantNameException.class, ()->new Restaurant.Builder().name(null));
    }

    @Test
    void shouldThrowWhenNitIsBlank(){
        assertThrows(InvalidRestaurantNitException.class, ()->new Restaurant.Builder().nit(""));
    }

    @Test
    void shouldThrowWhenNitIsNull(){
        assertThrows(InvalidRestaurantNitException.class, ()->new Restaurant.Builder().nit(null));
    }

    @Test
    void shouldThrowWhenAddressIsBlank(){
        assertThrows(InvalidRestaurantAddressException.class, ()->new Restaurant.Builder().address(""));
    }

    @Test
    void shouldThrowWhenAddressIsNull(){
        assertThrows(InvalidRestaurantAddressException.class, ()->new Restaurant.Builder().address(null));
    }

    @Test
    void shouldThrowWhenPhoneNumberIsBlank(){
        assertThrows(MissingPhoneNumberException.class, ()->new Restaurant.Builder().phoneNumber(""));
    }

    @Test
    void shouldThrowWhenPhoneNumberIsNull(){
        assertThrows(MissingPhoneNumberException.class, ()->new Restaurant.Builder().phoneNumber(null));
    }

    @Test
    void shouldThrowWhenUrlLogoIsBlank(){
        assertThrows(InvalidRestaurantUrlLogoException.class, ()->new Restaurant.Builder().urlLogo(""));
    }

    @Test
    void shouldThrowWhenUrlLogoIsNull(){
        assertThrows(InvalidRestaurantUrlLogoException.class, ()->new Restaurant.Builder().urlLogo(null));
    }

    @Test
    void shouldThrowWhenIdOwnerIsNull(){
        assertThrows(InvalidRestaurantOwnerIdException.class, ()->new Restaurant.Builder().idOwner(null));
    }

    @Test
    void shouldThrowWhenAnyRequiredFieldMissingBeforeBuild() {
        Restaurant.Builder builder = new Restaurant.Builder()
                .name(validName)
                .nit(validNit)
                .address(validAddress)
                .phoneNumber(validPhoneNumber)
                .urlLogo(validUrlLogo);
        assertThrows(DomainException.class, builder::build);
    }

    @Test
    void testSettersAndGetters() {
        Restaurant restaurant = new Restaurant.Builder()
                .id(10L)
                .name(validName)
                .nit(validNit)
                .address(validAddress)
                .phoneNumber(validPhoneNumber)
                .urlLogo(validUrlLogo)
                .idOwner(validIdOwner)
                .build();

        restaurant.setName("Updated Name");
        restaurant.setNit("111222333");
        restaurant.setAddress("789 New Address");
        restaurant.setPhoneNumber("+573009876543");
        restaurant.setUrlLogo("https://newlogo.com/logo.png");
        restaurant.setId(11L);
        restaurant.setIdOwner(303L);

        assertEquals("Updated Name", restaurant.getName());
        assertEquals("111222333", restaurant.getNit());
        assertEquals("789 New Address", restaurant.getAddress());
        assertEquals("+573009876543", restaurant.getPhoneNumber());
        assertEquals("https://newlogo.com/logo.png", restaurant.getUrlLogo());
        assertEquals(11L, restaurant.getId());
        assertEquals(303L, restaurant.getIdOwner());
    }

    @Test
    void testEqualsAndHashCode() {
        Restaurant r1 = new Restaurant.Builder()
                .id(1L)
                .name("A")
                .nit("1")
                .address("X")
                .phoneNumber("+1")
                .urlLogo("U")
                .idOwner(1L)
                .build();

        Restaurant r2 = new Restaurant.Builder()
                .id(1L)
                .name("B")
                .nit("2")
                .address("Y")
                .phoneNumber("+2")
                .urlLogo("V")
                .idOwner(2L)
                .build();

        Restaurant r3 = new Restaurant.Builder()
                .id(2L)
                .name("C")
                .nit("3")
                .address("Z")
                .phoneNumber("+3")
                .urlLogo("W")
                .idOwner(3L)
                .build();

        assertEquals(r1, r2);
        assertNotEquals(r1, r3);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertNotEquals(r1.hashCode(), r3.hashCode());
    }

    @Test
    void buildThrowsWhenNameIsNull() {
        Restaurant.Builder builder = new Restaurant.Builder()
                .nit(validNit)
                .address(validAddress)
                .phoneNumber(validPhoneNumber)
                .urlLogo(validUrlLogo)
                .idOwner(validIdOwner);

        DomainException ex = assertThrows(DomainException.class, builder::build);
        assertEquals("All required fields must be provided", ex.getMessage());
    }

    @Test
    void buildThrowsWhenNitIsNull() {
        Restaurant.Builder builder = new Restaurant.Builder()
                .name(validName)
                .address(validAddress)
                .phoneNumber(validPhoneNumber)
                .urlLogo(validUrlLogo)
                .idOwner(validIdOwner);

        DomainException ex = assertThrows(DomainException.class, builder::build);
        assertEquals("All required fields must be provided", ex.getMessage());
    }

    @Test
    void buildThrowsWhenAddressIsNull() {
        Restaurant.Builder builder = new Restaurant.Builder()
                .name(validName)
                .nit(validNit)
                .phoneNumber(validPhoneNumber)
                .urlLogo(validUrlLogo)
                .idOwner(validIdOwner);

        DomainException ex = assertThrows(DomainException.class, builder::build);
        assertEquals("All required fields must be provided", ex.getMessage());
    }

    @Test
    void buildThrowsWhenPhoneNumberIsNull() {
        Restaurant.Builder builder = new Restaurant.Builder()
                .name(validName)
                .nit(validNit)
                .address(validAddress)
                .urlLogo(validUrlLogo)
                .idOwner(validIdOwner);

        DomainException ex = assertThrows(DomainException.class, builder::build);
        assertEquals("All required fields must be provided", ex.getMessage());
    }

    @Test
    void buildThrowsWhenUrlLogoIsNull() {
        Restaurant.Builder builder = new Restaurant.Builder()
                .name(validName)
                .nit(validNit)
                .address(validAddress)
                .phoneNumber(validPhoneNumber)
                .idOwner(validIdOwner);

        DomainException ex = assertThrows(DomainException.class, builder::build);
        assertEquals("All required fields must be provided", ex.getMessage());
    }

    @Test
    void buildThrowsWhenIdOwnerIsNull() {
        Restaurant.Builder builder = new Restaurant.Builder()
                .name(validName)
                .nit(validNit)
                .address(validAddress)
                .phoneNumber(validPhoneNumber)
                .urlLogo(validUrlLogo);

        DomainException ex = assertThrows(DomainException.class, builder::build);
        assertEquals("All required fields must be provided", ex.getMessage());
    }

    @Test
    void buildSucceedsWhenAllFieldsPresent() {
        Restaurant.Builder builder = new Restaurant.Builder()
                .name(validName)
                .nit(validNit)
                .address(validAddress)
                .phoneNumber(validPhoneNumber)
                .urlLogo(validUrlLogo)
                .idOwner(validIdOwner);

        assertDoesNotThrow(builder::build);
    }

    @Test
    void equalsShouldReturnTrueForSameInstance() {
        Restaurant restaurant = new Restaurant.Builder()
                .id(1L)
                .name("Name")
                .nit("123456")
                .address("Address")
                .phoneNumber("+123456789")
                .urlLogo("http://logo.url")
                .idOwner(1L)
                .build();

        assertTrue(restaurant.equals(restaurant));
    }

    @Test
    void equalsShouldReturnFalseWhenComparedWithNull() {
        Restaurant restaurant = new Restaurant.Builder()
                .id(1L)
                .name("Name")
                .nit("123456")
                .address("Address")
                .phoneNumber("+123456789")
                .urlLogo("http://logo.url")
                .idOwner(1L)
                .build();

        assertFalse(restaurant.equals(null));
    }

    @Test
    void equalsShouldReturnFalseWhenComparedWithDifferentClass() {
        Restaurant restaurant = new Restaurant.Builder()
                .id(1L)
                .name("Name")
                .nit("123456")
                .address("Address")
                .phoneNumber("+123456789")
                .urlLogo("http://logo.url")
                .idOwner(1L)
                .build();

        Object other = new Object();

        assertFalse(restaurant.equals(other));
    }
}
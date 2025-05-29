package com.plazoleta.plazoleta_microservice.application.mapper;

import com.plazoleta.plazoleta_microservice.domain.model.PhoneNumber;
import com.plazoleta.plazoleta_microservice.domain.model.RestaurantName;
import com.plazoleta.plazoleta_microservice.domain.model.RestaurantNit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.profiles.active=test")
class IValueObjectMapperTest {

    private IValueObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(IValueObjectMapper.class);
    }

    @Test
    void toRestaurantNameShouldReturnCorrectObject() {
        RestaurantName name = mapper.toRestaurantName("La Parrilla");
        assertNotNull(name);
        assertEquals("La Parrilla", name.getValue());
    }

    @Test
    void fromRestaurantNameShouldReturnCorrectString() {
        String name = mapper.fromRestaurantName(new RestaurantName("La Parrilla"));
        assertEquals("La Parrilla", name);
    }

    @Test
    void toRestaurantNitShouldReturnCorrectObject() {
        RestaurantNit nit = mapper.toRestaurantNit("987654321");
        assertNotNull(nit);
        assertEquals("987654321", nit.getValue());
    }

    @Test
    void fromRestaurantNitShouldReturnCorrectString() {
        String nit = mapper.fromRestaurantNit(new RestaurantNit("987654321"));
        assertEquals("987654321", nit);
    }

    @Test
    void toPhoneNumberShouldReturnCorrectObject() {
        PhoneNumber phone = mapper.toPhoneNumber("+573001112233");
        assertNotNull(phone);
        assertEquals("+573001112233", phone.getValue());
    }

    @Test
    void fromPhoneNumberShouldReturnCorrectString() {
        String phone = mapper.fromPhoneNumber(new PhoneNumber("+573001112233"));
        assertEquals("+573001112233", phone);
    }

    @Test
    void nullValuesShouldReturnNull() {
        assertNull(mapper.toRestaurantName(null));
        assertNull(mapper.fromRestaurantName(null));
        assertNull(mapper.toRestaurantNit(null));
        assertNull(mapper.fromRestaurantNit(null));
        assertNull(mapper.toPhoneNumber(null));
        assertNull(mapper.fromPhoneNumber(null));
    }

}
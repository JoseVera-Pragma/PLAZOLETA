package com.plazoleta.plazoleta_microservice.infrastructure.out.feing;

import com.plazoleta.plazoleta_microservice.infrastructure.out.feing.dto.SmsRequestDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SmsSenderAdapterTest {

    @Mock
    private SmsFeignClient smsFeignClient;

    @InjectMocks
    private SmsSenderAdapter smsSenderAdapter;

    @Test
    void sendSms_shouldCallFeignClientWithCorrectDto() {
        String phone = "+123456789";
        String message = "Hello from tests";
        SmsRequestDto expectedDto = new SmsRequestDto(phone, message);

        smsSenderAdapter.sendSms(phone, message);

        ArgumentCaptor<SmsRequestDto> dtoCaptor = ArgumentCaptor.forClass(SmsRequestDto.class);
        verify(smsFeignClient).sendSms(dtoCaptor.capture());

        SmsRequestDto actualDto = dtoCaptor.getValue();
        assertEquals(expectedDto.getPhoneNumber(), actualDto.getPhoneNumber());
        assertEquals(expectedDto.getMessage(), actualDto.getMessage());
    }

}
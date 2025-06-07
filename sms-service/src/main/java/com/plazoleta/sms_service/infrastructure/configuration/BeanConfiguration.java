package com.plazoleta.sms_service.infrastructure.configuration;

import com.plazoleta.sms_service.infrastructure.out.sms.TwilioConfig;
import com.plazoleta.sms_service.infrastructure.out.sms.TwilioSmsNotificationAdapter;
import com.plazoleta.sms_service.application.hanlder.ISendSmsHandler;
import com.plazoleta.sms_service.application.hanlder.impl.SendSmsHandlerImpl;
import com.plazoleta.sms_service.application.mapper.ISmsRequestMapper;
import com.plazoleta.sms_service.domain.api.ISendSmsServicePort;
import com.plazoleta.sms_service.domain.spi.ISmsSenderPort;
import com.plazoleta.sms_service.domain.usecase.SmsMessageUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BeanConfiguration {
    private final ISmsRequestMapper smsRequestMapper;
    private final TwilioConfig twilioConfig;

    @Bean
    public ISmsSenderPort smsSenderPort(){
        return new TwilioSmsNotificationAdapter(twilioConfig);
    }

    @Bean
    public ISendSmsServicePort sendSmsServicePort(ISmsSenderPort smsSenderPort){
        return new SmsMessageUseCase(smsSenderPort);
    }

    @Bean
    public ISendSmsHandler sendSmsHandler(ISendSmsServicePort sendSmsServicePort){
        return new SendSmsHandlerImpl(sendSmsServicePort,smsRequestMapper);
    }
}

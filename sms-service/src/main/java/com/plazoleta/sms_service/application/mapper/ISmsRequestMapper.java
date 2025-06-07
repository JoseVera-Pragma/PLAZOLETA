package com.plazoleta.sms_service.application.mapper;

import com.plazoleta.sms_service.application.dto.request.SmsRequestDto;
import com.plazoleta.sms_service.domain.model.SmsMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface ISmsRequestMapper {
    @Mapping(target = "phoneNumber", source = "phoneNumber")
    @Mapping(target = "message", source = "message")
    SmsMessage toSmsMessage(SmsRequestDto dto);
}

package com.plazoleta.traceability_microservice.infrastructure.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plazoleta.traceability_microservice.application.dto.request.TraceabilityRequestDto;
import com.plazoleta.traceability_microservice.application.dto.response.EfficiencyResponseDto;
import com.plazoleta.traceability_microservice.application.dto.response.EmployeeEfficiencyRankingResponseDto;
import com.plazoleta.traceability_microservice.application.dto.response.TraceabilityResponseDto;
import com.plazoleta.traceability_microservice.application.handler.ITraceabilityHandler;
import com.plazoleta.traceability_microservice.infrastructure.config.security.adapter.JwtTokenAdapter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TraceabilityController.class)
@AutoConfigureMockMvc(addFilters = false)
class TraceabilityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ITraceabilityHandler traceabilityHandler;

    @MockitoBean
    private JwtTokenAdapter jwtTokenAdapter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void saveTraceability_shouldReturn201Created() throws Exception {
        TraceabilityRequestDto requestDto = TraceabilityRequestDto.builder()
                .restaurantId(1L)
                .orderId(1L)
                .customerId(2L)
                .customerEmail("customer@example.com")
                .previousState("READY")
                .newState("DELIVERED")
                .employedId(3L)
                .employedEmail("employee@example.com")
                .build();

        mockMvc.perform(post("/traceability")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated());

        verify(traceabilityHandler).saveTraceability(any(TraceabilityRequestDto.class));
    }

    @Test
    void getTraceability_shouldReturn200_andList() throws Exception {
        Long orderId = 1L;

        List<TraceabilityResponseDto> responseList = List.of(
                TraceabilityResponseDto.builder()
                        .previousState("READY")
                        .newState("DELIVERED")
                        .date(LocalDateTime.now())
                        .build()
        );

        when(traceabilityHandler.getTraceabilityByOrder(orderId)).thenReturn(responseList);

        mockMvc.perform(get("/traceability/{orderId}", orderId))
                .andExpect(status().isOk());
    }

    @Test
    void saveTraceability_shouldReturn400_whenInvalidRequest() throws Exception {
        TraceabilityRequestDto invalidDto = TraceabilityRequestDto.builder()
                .orderId(1L)
                .customerId(2L)
                .newState("DELIVERED")
                .employedId(3L)
                .employedEmail("invalid-email")
                .build();

        mockMvc.perform(post("/traceability")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "OWNER")
    void shouldReturnOrderEfficiencies() throws Exception {
        Long restaurantId = 1L;
        List<EfficiencyResponseDto> mockList = List.of(
                new EfficiencyResponseDto(100L, 15L),
                new EfficiencyResponseDto(101L, 20L)
        );

        when(traceabilityHandler.getOrderEfficiencies(restaurantId)).thenReturn(mockList);

        mockMvc.perform(get("/traceability/efficiency/restaurant/{restaurantId}/orders", restaurantId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].orderId").value(100))
                .andExpect(jsonPath("$[0].durationInMinutes").value(15));
    }

    @Test
    @WithMockUser(roles = "OWNER")
    void shouldReturnEmployeeRanking() throws Exception {
        Long restaurantId = 1L;
        List<EmployeeEfficiencyRankingResponseDto> mockList = List.of(
                new EmployeeEfficiencyRankingResponseDto(10L, "juan@email.com", 12L),
                new EmployeeEfficiencyRankingResponseDto(11L, "ana@email.com", 18L)
        );

        when(traceabilityHandler.getEmployeeRankingByRestaurant(restaurantId)).thenReturn(mockList);

        mockMvc.perform(get("/traceability/ranking/restaurant/{restaurantId}", restaurantId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[1].employeeEmail").value("ana@email.com"))
                .andExpect(jsonPath("$[1].durationInMinutes").value(18));
    }
}
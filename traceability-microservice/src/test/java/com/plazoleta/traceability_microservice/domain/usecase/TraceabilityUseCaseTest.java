package com.plazoleta.traceability_microservice.domain.usecase;

import com.plazoleta.traceability_microservice.domain.exception.UserNotFoundException;
import com.plazoleta.traceability_microservice.domain.model.EfficiencyReport;
import com.plazoleta.traceability_microservice.domain.model.EmployeeEfficiencyRanking;
import com.plazoleta.traceability_microservice.domain.model.Traceability;
import com.plazoleta.traceability_microservice.domain.spi.IAuthenticatedUserPort;
import com.plazoleta.traceability_microservice.domain.spi.ITraceabilityPersistencePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TraceabilityUseCaseTest {

    @Mock
    private ITraceabilityPersistencePort persistencePort;

    @Mock
    private IAuthenticatedUserPort authenticatedUserPort;

    @InjectMocks
    private TraceabilityUseCase traceabilityUseCase;

    private final Long RESTAURANT_ID = 1L;
    private final Long EMPLOYEE_ID = 100L;

    private List<Traceability> createMockLogs() {
        return List.of(
                Traceability.builder()
                        .restaurantId(RESTAURANT_ID)
                        .orderId(10L)
                        .date(LocalDateTime.of(2024, 6, 1, 10, 0))
                        .newState("PENDING")
                        .employedId(null)
                        .employedEmail(null)
                        .build(),
                Traceability.builder()
                        .restaurantId(RESTAURANT_ID)
                        .orderId(10L)
                        .date(LocalDateTime.of(2024, 6, 1, 10, 30))
                        .newState("DELIVERED")
                        .employedId(EMPLOYEE_ID)
                        .employedEmail("empleado@example.com")
                        .build()
        );
    }

    @Test
    void saveTraceability_shouldSaveWithCurrentDate() {
        Traceability inputTraceability = Traceability.builder()
                .orderId(1L)
                .customerId(2L)
                .customerEmail("customer@example.com")
                .newState("PENDING")
                .build();

        ArgumentCaptor<Traceability> captor = ArgumentCaptor.forClass(Traceability.class);

        traceabilityUseCase.saveTraceability(inputTraceability);

        verify(persistencePort).saveTraceability(captor.capture());
        Traceability saved = captor.getValue();
        assertNotNull(saved.getDate());
        assertEquals(inputTraceability.getOrderId(), saved.getOrderId());
        assertEquals(inputTraceability.getNewState(), saved.getNewState());
    }

    @Test
    void findTraceabilityForCustomer_shouldReturnTraceabilityList() {
        Long orderId = 5L;
        Long customerId = 10L;

        Traceability traceability = Traceability.builder()
                .orderId(orderId)
                .customerId(customerId)
                .customerEmail("customer@example.com")
                .previousState("PENDING")
                .newState("DELIVERED")
                .build();

        when(authenticatedUserPort.getCurrentUserId()).thenReturn(Optional.of(customerId));
        when(persistencePort.findTraceabilityByOrderAndCustomer(orderId, customerId))
                .thenReturn(List.of(traceability));

        List<Traceability> result = traceabilityUseCase.findTraceabilityForCustomer(orderId);

        assertEquals(1, result.size());
        assertEquals(orderId, result.getFirst().getOrderId());
        assertEquals(customerId, result.getFirst().getCustomerId());
    }

    @Test
    void findTraceabilityForCustomer_shouldThrowException_whenUserNotFound() {
        when(authenticatedUserPort.getCurrentUserId()).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            traceabilityUseCase.findTraceabilityForCustomer(1L);
        });
    }

    @Test
    void testGetOrderEfficienciesByRestaurant() {
        when(persistencePort.findAllByRestaurantId(RESTAURANT_ID)).thenReturn(createMockLogs());

        List<EfficiencyReport> result = traceabilityUseCase.getOrderEfficienciesByRestaurant(RESTAURANT_ID);

        assertEquals(1, result.size());
        EfficiencyReport report = result.getFirst();
        assertEquals(10L, report.getOrderId());
        assertEquals(Duration.ofMinutes(30), report.getDuration());
        assertNull(report.getEmployeeId());
        assertNull(report.getEmployeeEmail());
    }

    @Test
    void testGetEmployeeEfficiencyRanking() {
        when(persistencePort.findAllByRestaurantId(RESTAURANT_ID)).thenReturn(createMockLogs());

        List<EmployeeEfficiencyRanking> rankings = traceabilityUseCase.getEmployeeEfficiencyRanking(RESTAURANT_ID);

        assertEquals(1, rankings.size());
        EmployeeEfficiencyRanking ranking = rankings.get(0);
        assertEquals(EMPLOYEE_ID, ranking.getEmployeeId());
        assertEquals("empleado@example.com", ranking.getEmployeeEmail());
        assertEquals(Duration.ofMinutes(30), ranking.getDuration());
    }

}
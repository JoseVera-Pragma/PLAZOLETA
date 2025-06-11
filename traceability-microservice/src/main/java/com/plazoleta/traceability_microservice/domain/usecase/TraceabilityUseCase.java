package com.plazoleta.traceability_microservice.domain.usecase;

import com.plazoleta.traceability_microservice.domain.api.ITraceabilityServicePort;
import com.plazoleta.traceability_microservice.domain.exception.UserNotFoundException;
import com.plazoleta.traceability_microservice.domain.model.EfficiencyReport;
import com.plazoleta.traceability_microservice.domain.model.EmployeeEfficiencyRanking;
import com.plazoleta.traceability_microservice.domain.model.Traceability;
import com.plazoleta.traceability_microservice.domain.spi.IAuthenticatedUserPort;
import com.plazoleta.traceability_microservice.domain.spi.ITraceabilityPersistencePort;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TraceabilityUseCase implements ITraceabilityServicePort {

    private final ITraceabilityPersistencePort persistencePort;
    private final IAuthenticatedUserPort authenticatedUserPort;

    public TraceabilityUseCase(ITraceabilityPersistencePort persistencePort, IAuthenticatedUserPort authenticatedUserPort) {
        this.persistencePort = persistencePort;
        this.authenticatedUserPort = authenticatedUserPort;
    }

    @Override
    public void saveTraceability(Traceability traceability) {
        Traceability traceabilityToSave = traceability.whitDate(LocalDateTime.now());
        persistencePort.saveTraceability(traceabilityToSave);
    }

    @Override
    public List<Traceability> findTraceabilityForCustomer(Long orderId) {
        Long customerId = authenticatedUserPort.getCurrentUserId().orElseThrow(() ->
                new UserNotFoundException("User not found.")
        );
        return persistencePort.findTraceabilityByOrderAndCustomer(orderId, customerId);
    }

    @Override
    public List<EfficiencyReport> getOrderEfficienciesByRestaurant(Long restaurantId) {
        List<Traceability> logs = persistencePort.findAllByRestaurantId(restaurantId);

        return logs.stream()
                .collect(Collectors.groupingBy(Traceability::getOrderId))
                .entrySet()
                .stream()
                .map(entry -> {
                    List<Traceability> logsPerOrder = entry.getValue();
                    logsPerOrder.sort(Comparator.comparing(Traceability::getDate));

                    LocalDateTime start = logsPerOrder.getFirst().getDate();
                    LocalDateTime end = logsPerOrder.getLast().getDate();

                    return new EfficiencyReport(
                            entry.getKey(),
                            Duration.between(start, end),
                            null,
                            null
                    );
                })
                .toList();
    }

    @Override
    public List<EmployeeEfficiencyRanking> getEmployeeEfficiencyRanking(Long restaurantId) {
        List<Traceability> logs = persistencePort.findAllByRestaurantId(restaurantId);

        Map<Long, List<Traceability>> logsByOrder = logs.stream()
                .collect(Collectors.groupingBy(Traceability::getOrderId));

        List<EfficiencyReport> orderEfficiencies = logsByOrder.entrySet().stream()
                .map(entry -> {
                    List<Traceability> logsPerOrder = entry.getValue();
                    logsPerOrder.sort(Comparator.comparing(Traceability::getDate));
                    LocalDateTime start = logsPerOrder.getFirst().getDate();
                    LocalDateTime end = logsPerOrder.getLast().getDate();
                    Traceability last = logsPerOrder.getLast();

                    return new EfficiencyReport(
                            entry.getKey(),
                            Duration.between(start, end),
                            last.getEmployedId(),
                            last.getEmployedEmail()
                    );
                })
                .filter(report -> report.getEmployeeId() != null)
                .toList();

        return orderEfficiencies.stream()
                .collect(Collectors.groupingBy(EfficiencyReport::getEmployeeId))
                .entrySet()
                .stream()
                .map(entry -> {
                    Long employeeId = entry.getKey();
                    List<EfficiencyReport> reports = entry.getValue();

                    String email = reports.getFirst().getEmployeeEmail();
                    Duration total = reports.stream()
                            .map(EfficiencyReport::getDuration)
                            .reduce(Duration.ZERO, Duration::plus);
                    Duration average = total.dividedBy(reports.size());
                    return new EmployeeEfficiencyRanking(employeeId, email, average);
                })
                .sorted(Comparator.comparing(EmployeeEfficiencyRanking::getDuration))
                .toList();
    }
}

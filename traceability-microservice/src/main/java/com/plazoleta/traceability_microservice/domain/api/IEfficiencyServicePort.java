package com.plazoleta.traceability_microservice.domain.api;

import com.plazoleta.traceability_microservice.domain.model.EfficiencyReport;
import com.plazoleta.traceability_microservice.domain.model.EmployeeEfficiencyRanking;

import java.util.List;

public interface IEfficiencyServicePort {
    List<EfficiencyReport> getOrderEfficienciesByRestaurant(Long restaurantId);
    List<EmployeeEfficiencyRanking> getEmployeeEfficiencyRanking(Long restaurantId);
}

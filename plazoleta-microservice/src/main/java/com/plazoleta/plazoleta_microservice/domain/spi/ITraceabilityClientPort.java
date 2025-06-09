package com.plazoleta.plazoleta_microservice.domain.spi;

import com.plazoleta.plazoleta_microservice.domain.model.Traceability;

public interface ITraceabilityClientPort {
    void saveTraceability(Traceability traceability);
}

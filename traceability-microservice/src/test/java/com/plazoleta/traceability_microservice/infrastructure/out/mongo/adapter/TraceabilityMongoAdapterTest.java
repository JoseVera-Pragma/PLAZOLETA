package com.plazoleta.traceability_microservice.infrastructure.out.mongo.adapter;

import com.plazoleta.traceability_microservice.domain.model.Traceability;
import com.plazoleta.traceability_microservice.infrastructure.out.mongo.entity.TraceabilityDocument;
import com.plazoleta.traceability_microservice.infrastructure.out.mongo.mapper.TraceabilityDocumentMapper;
import com.plazoleta.traceability_microservice.infrastructure.out.mongo.repository.TraceabilityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TraceabilityMongoAdapterTest {


    @Mock
    private TraceabilityRepository repository;

    @Mock
    private TraceabilityDocumentMapper mapper;

    @InjectMocks
    private TraceabilityMongoAdapter adapter;

    @Test
    void saveTraceability_shouldCallRepositoryWithMappedDocument() {
        Traceability traceability = Traceability.builder()
                .orderId(1L)
                .customerId(2L)
                .customerEmail("test@test.com")
                .build();

        TraceabilityDocument document = TraceabilityDocument.builder()
                .orderId(1L)
                .customerId(2L)
                .customerEmail("test@test.com")
                .build();

        when(mapper.toTraceabilityDocument(traceability)).thenReturn(document);

        adapter.saveTraceability(traceability);

        verify(repository).save(document);
    }

    @Test
    void findTraceabilityByOrderAndCustomer_shouldReturnMappedDomainList() {
        Long orderId = 1L;
        Long customerId = 2L;

        TraceabilityDocument document = TraceabilityDocument.builder()
                .orderId(orderId)
                .customerId(customerId)
                .customerEmail("test@test.com")
                .build();

        Traceability domain = Traceability.builder()
                .orderId(orderId)
                .customerId(customerId)
                .customerEmail("test@test.com")
                .build();

        when(repository.findByOrderIdAndCustomerId(orderId, customerId))
                .thenReturn(List.of(document));
        when(mapper.toTraceability(document)).thenReturn(domain);

        List<Traceability> result = adapter.findTraceabilityByOrderAndCustomer(orderId, customerId);

        assertEquals(1, result.size());
        assertEquals(domain, result.getFirst());
    }
}
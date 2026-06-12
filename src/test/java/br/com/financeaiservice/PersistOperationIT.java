package br.com.financeaiservice;

import br.com.financeaiservice.application.input.OperationInput;
import br.com.financeaiservice.application.usecase.PersistOperationUseCase;
import br.com.financeaiservice.domain.enums.Category;
import br.com.financeaiservice.domain.repository.OperationRepository;
import br.com.financeaiservice.infrastructure.database.OperationRepositoryImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PersistOperationIT {

    @Autowired
    private PersistOperationUseCase persistOperationUseCase;

    @Autowired
    private OperationRepositoryImpl operationRepository;

    @Test
    void shouldPersistOperationIntoDatabase() {
        OperationInput input = new OperationInput(
                "Pix mercado",
                new BigDecimal("50.00"),
                Category.GAS);

        var result = persistOperationUseCase.execute(input, UUID.randomUUID());

        assertNotNull(result);
        assertNotNull(result.operationId());

        var saved = operationRepository.findById(result.operationId());

        assertTrue(saved.isPresent());
        assertEquals("Pix mercado", saved.get().getDescription());
    }
}
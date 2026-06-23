package br.com.financeaiservice;

import br.com.financeaiservice.application.input.OperationInput;
import br.com.financeaiservice.application.output.OperationOutPut;
import br.com.financeaiservice.application.usecase.PersistOperationUseCase;
import br.com.financeaiservice.domain.entity.Operation;
import br.com.financeaiservice.domain.enums.Category;
import br.com.financeaiservice.domain.repository.OperationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersistOperationUseCaseTest {

    @Mock
    private OperationRepository operationRepository;

    @InjectMocks
    private PersistOperationUseCase useCase;

    @Test
    void shouldPersistOperationAndReturnOutput() {
        UUID customerId = UUID.randomUUID();
        OperationInput input = new OperationInput("Almoço", new BigDecimal("35.00"), Category.MARKET);

        Operation savedOperation = new Operation(customerId, "Almoço", new BigDecimal("35.00"), Category.MARKET);
        savedOperation.setOperationId(UUID.randomUUID());
        savedOperation.setDate(LocalDate.now());

        when(operationRepository.save(any(Operation.class))).thenReturn(savedOperation);

        OperationOutPut result = useCase.execute(input, customerId);

        assertNotNull(result);
        assertNotNull(result.operationId());
        assertEquals(customerId, result.customerId());
        assertEquals("Almoço", result.description());
        assertEquals(new BigDecimal("35.00"), result.amount());
        assertEquals(Category.MARKET, result.category());
    }

    @Test
    void shouldSaveOperationWithCorrectCustomerId() {
        UUID customerId = UUID.randomUUID();
        OperationInput input = new OperationInput("Farmácia", new BigDecimal("80.00"), Category.PHARMACY);

        Operation savedOperation = new Operation(customerId, "Farmácia", new BigDecimal("80.00"), Category.PHARMACY);
        savedOperation.setOperationId(UUID.randomUUID());
        savedOperation.setDate(LocalDate.now());

        when(operationRepository.save(any(Operation.class))).thenReturn(savedOperation);

        ArgumentCaptor<Operation> captor = ArgumentCaptor.forClass(Operation.class);

        useCase.execute(input, customerId);

        verify(operationRepository).save(captor.capture());
        assertEquals(customerId, captor.getValue().getCustomerID());
        assertEquals("Farmácia", captor.getValue().getDescription());
    }
}
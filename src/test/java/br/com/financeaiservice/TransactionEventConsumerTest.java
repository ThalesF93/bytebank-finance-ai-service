package br.com.financeaiservice;

import br.com.financeaiservice.application.input.OperationInput;
import br.com.financeaiservice.application.output.OperationOutPut;
import br.com.financeaiservice.application.usecase.PersistOperationUseCase;
import br.com.financeaiservice.domain.enums.Category;
import br.com.financeaiservice.infrastructure.client.AccountClient;
import br.com.financeaiservice.infrastructure.client.response.AccountResponse;
import br.com.financeaiservice.infrastructure.messaging.TransactionEventConsumer;
import br.com.financeaiservice.infrastructure.messaging.event.TransactionCreatedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.kafka.support.Acknowledgment;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionEventConsumerTest {

    @Mock
    private PersistOperationUseCase persistOperationUseCase;

    @Mock
    private AccountClient accountClient;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private VectorStore vectorStore;

    @Mock
    private Acknowledgment ack;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private TransactionEventConsumer consumer;

    private TransactionCreatedEvent buildEvent() {
        return new TransactionCreatedEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                "PIX",
                "COMPLETED",
                new BigDecimal("150.00"),
                LocalDateTime.now(),
                "Pagamento de boleto"
        );
    }

    @Test
    void shouldPersistOperationAndAck_whenEventIsNew() {
        TransactionCreatedEvent event = buildEvent();
        String idempotencyKey = "transaction:" + event.transactionId();

        UUID customerId = UUID.randomUUID();
        AccountResponse customer = new AccountResponse(customerId, "João", "joao@email.com");

        OperationOutPut output = new OperationOutPut(
                UUID.randomUUID(), customerId, "PIX", new BigDecimal("150.00"), Category.BANK_OPERATION, LocalDate.now()
        );

        when(redisTemplate.hasKey(idempotencyKey)).thenReturn(false);
        when(accountClient.findCustomerByAccountId(event.originAccountId())).thenReturn(customer);
        when(persistOperationUseCase.execute(any(OperationInput.class), eq(customerId))).thenReturn(output);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        doNothing().when(vectorStore).add(anyList());

        consumer.consume(event, 0, 0L, ack);

        verify(persistOperationUseCase).execute(any(OperationInput.class), eq(customerId));
        verify(valueOperations).set(eq(idempotencyKey), eq("1"), any());
        verify(ack).acknowledge();
    }

    @Test
    void shouldIgnoreEventAndAck_whenEventIsDuplicated() {
        TransactionCreatedEvent event = buildEvent();
        String idempotencyKey = "transaction:" + event.transactionId();

        when(redisTemplate.hasKey(idempotencyKey)).thenReturn(true);

        consumer.consume(event, 0, 0L, ack);

        verifyNoInteractions(persistOperationUseCase);
        verifyNoInteractions(accountClient);
        verify(ack).acknowledge();
    }

    @Test
    void shouldThrowKafkaProcessingException_whenAccountClientFails() {
        TransactionCreatedEvent event = buildEvent();
        String idempotencyKey = "transaction:" + event.transactionId();

        when(redisTemplate.hasKey(idempotencyKey)).thenReturn(false);
        when(accountClient.findCustomerByAccountId(event.originAccountId()))
                .thenThrow(new RuntimeException("Account service unavailable"));

        org.junit.jupiter.api.Assertions.assertThrows(
                br.com.financeaiservice.infrastructure.exception.customized_exceptions.KafkaProcessingException.class,
                () -> consumer.consume(event, 0, 0L, ack)
        );

        verifyNoInteractions(persistOperationUseCase);
        verify(ack, never()).acknowledge();
    }
}
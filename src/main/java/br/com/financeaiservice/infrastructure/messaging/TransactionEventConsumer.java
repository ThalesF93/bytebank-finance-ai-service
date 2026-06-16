package br.com.financeaiservice.infrastructure.messaging;

import br.com.financeaiservice.application.input.OperationInput;
import br.com.financeaiservice.application.usecase.PersistOperationUseCase;
import br.com.financeaiservice.domain.enums.Category;
import br.com.financeaiservice.infrastructure.client.AccountClient;
import br.com.financeaiservice.infrastructure.exception.KafkaProcessingException;
import br.com.financeaiservice.infrastructure.messaging.event.TransactionCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionEventConsumer {

    private final PersistOperationUseCase useCase;
    private final AccountClient accountClient;
    private final RedisTemplate<String, Object> redisTemplate;

    @KafkaListener(
            topics = "transaction.created",
            groupId = "finance-ai-group",
            containerFactory = "kafkaListenerContainerFactory" )
    public void consume(@Payload TransactionCreatedEvent event,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                        @Header(KafkaHeaders.OFFSET) long offset,
                        Acknowledgment ack){

        log.info("Event received from Kafka. Partition={} offset={} transactionId={}", partition, offset, event.transactionId());

        String idempotencyKey = "transaction:" + event.transactionId();

        if (redisTemplate.hasKey(idempotencyKey)) {
            log.warn("Duplicated Event ignored: {}", event.originAccountId());
            ack.acknowledge();
            return;
        }


        var input = new OperationInput(event.type(), event.amount(), Category.BANK_OPERATION);
        var customer = accountClient.findCustomerByAccountId(event.originAccountId());

        try {
            useCase.execute(input, customer.id());
            redisTemplate.opsForValue().set(idempotencyKey, "1", Duration.ofHours(24));
            ack.acknowledge();
            log.info("Operation persisted from Kafka event. transactionId={}", event.transactionId());
        } catch (Exception e) {
            log.error("Error while processing event: {}", e.getMessage());
            throw new KafkaProcessingException("Failed to process transaction event", e);
        }
    }
}

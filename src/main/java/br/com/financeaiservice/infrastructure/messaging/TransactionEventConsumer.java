package br.com.financeaiservice.infrastructure.messaging;

import br.com.financeaiservice.application.input.OperationInput;
import br.com.financeaiservice.application.usecase.PersistOperationUseCase;
import br.com.financeaiservice.domain.enums.Category;
import br.com.financeaiservice.infrastructure.client.AccountClient;
import br.com.financeaiservice.infrastructure.client.response.AccountResponse;
import br.com.financeaiservice.infrastructure.exception.customized_exceptions.KafkaProcessingException;
import br.com.financeaiservice.infrastructure.messaging.event.TransactionCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionEventConsumer {

    private final PersistOperationUseCase useCase;
    private final AccountClient accountClient;
    private final RedisTemplate<String, Object> redisTemplate;
    private final VectorStore vectorStore;

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

        try {

            log.info("Calling accounts-service for accountId={}", event.originAccountId());
            var customer = accountClient.findCustomerByAccountId(event.originAccountId());
            log.info("Received customer response: {}", customer);

            useCase.execute(input, customer.id());
            saveVector(event, customer);

            redisTemplate.opsForValue().set(idempotencyKey, "1", Duration.ofHours(24));
            ack.acknowledge();
            log.info("Operation persisted from Kafka event. transactionId={}", event.transactionId());

        } catch (Exception e) {
            log.error("Error while processing event: {}", e.getMessage(), e);
            throw new KafkaProcessingException("Failed to process transaction event", e);
        }
    }

    private void saveVector(TransactionCreatedEvent event, AccountResponse customer){

        String desc = event.description() != null ? event.description() : "Sem descrição";
        String description = String.format("Transação do tipo %s no valor de R$ %s, em %s para o cliente %s. Descrição: %s",
                event.type(), event.amount(), event.dateTime().toLocalDate() ,customer.id(), desc);

        Document doc = new Document(description, Map.of(
                "customerId", customer.id().toString(),
                "transactionId", event.transactionId(),
                "amount", event.amount(),
                "type", event.type(),
                "description", desc,
                "date", event.dateTime().toLocalDate()
        ));

        vectorStore.add(List.of(doc));
        log.info("Embedding saved to pgvector. transactionId={}", event.transactionId());
    }

}

package br.com.financeaiservice.infrastructure.messaging;

import br.com.financeaiservice.application.input.OperationInput;
import br.com.financeaiservice.application.usecase.PersistOperationUseCase;
import br.com.financeaiservice.domain.enums.Category;
import br.com.financeaiservice.infrastructure.client.AccountClient;
import br.com.financeaiservice.infrastructure.messaging.event.TransactionCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionEventConsumer {

    private final PersistOperationUseCase useCase;
    private final AccountClient accountClient;

    @KafkaListener(topics = "transaction.created")
    public void consume(TransactionCreatedEvent event){
        log.info("Event received from Kafka. transactionId={}", event.transactionId());

        var input = new OperationInput(event.type(), event.amount(), Category.BANK_OPERATION);
        var customer = accountClient.findCustomerByAccountId(event.originAccountId());

        useCase.execute(input, customer.id());
        log.info("Operation persisted from Kafka event. transactionId={}", event.transactionId());
    }
}

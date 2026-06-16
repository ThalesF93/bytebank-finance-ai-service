package br.com.financeaiservice.infrastructure.messaging.event;

import br.com.financeaiservice.domain.enums.Category;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionCreatedEvent(
        UUID transactionId,
        UUID originAccountId,
        UUID targetAccountId,
        String type,
        String status,
        BigDecimal amount,
        LocalDateTime dateTime,
        String description
) {

}

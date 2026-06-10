package br.com.financeaiservice.application.output;

import br.com.financeaiservice.domain.entity.Operation;
import br.com.financeaiservice.domain.enums.Category;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record OperationOutPut(UUID operationId, UUID customerId, String description, BigDecimal amount, Category category, LocalDate date) {

        public static OperationOutPut from(Operation operation){
            return new OperationOutPut(
                    operation.getCustomerID(),
                    operation.getOperationId(),
                    operation.getDescription(),
                    operation.getAmount(),
                    operation.getCategory(),
                    operation.getDate()
            );
        }
}

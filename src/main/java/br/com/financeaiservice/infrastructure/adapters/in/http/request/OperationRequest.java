package br.com.financeaiservice.infrastructure.adapters.in.http.request;

import br.com.financeaiservice.application.input.OperationInput;
import br.com.financeaiservice.domain.enums.Category;

import java.math.BigDecimal;
import java.util.UUID;

public record OperationRequest(String description, BigDecimal amount, Category category) {

    public OperationInput toInput(String customerId){
        return new OperationInput(UUID.fromString(customerId), description, amount, category);
    }
}

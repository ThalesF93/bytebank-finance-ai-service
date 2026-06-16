package br.com.financeaiservice.infrastructure.adapters.in.http.request;

import br.com.financeaiservice.application.input.OperationInput;
import br.com.financeaiservice.domain.enums.Category;

import java.math.BigDecimal;
import java.util.UUID;

public record OperationRequest(String description, BigDecimal amount, Category category) {

    public OperationInput toInput(){
        return new OperationInput(description, amount, category);
    }
}

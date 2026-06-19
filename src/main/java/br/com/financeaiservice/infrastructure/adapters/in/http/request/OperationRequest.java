package br.com.financeaiservice.infrastructure.adapters.in.http.request;

import br.com.financeaiservice.application.input.OperationInput;
import br.com.financeaiservice.domain.enums.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record OperationRequest(

        @NotBlank
        String description,

        @Positive
        @NotNull
        BigDecimal amount,

        @NotBlank
        Category category) {

    public OperationInput toInput(){
        return new OperationInput(description, amount, category);
    }
}

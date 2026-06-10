package br.com.financeaiservice.application.input;

import br.com.financeaiservice.domain.entity.Operation;
import br.com.financeaiservice.domain.enums.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record OperationInput(

        UUID customerID,

        @NotBlank
        String description,

        @Positive
        BigDecimal amount,

        Category category

) {
    public static Operation toEntity(OperationInput input){
        return new Operation(input.customerID, input.description, input.amount, input.category);
    }
}

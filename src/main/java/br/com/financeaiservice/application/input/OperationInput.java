package br.com.financeaiservice.application.input;

import br.com.financeaiservice.domain.entity.Operation;
import br.com.financeaiservice.domain.enums.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.ai.tool.annotation.ToolParam;

import java.math.BigDecimal;
import java.util.UUID;

public record OperationInput(

        @ToolParam(description = "Concise description of the transaction")
        @NotBlank
        String description,

        @ToolParam(description = "Transaction amount in Brazilian reais")
        @Positive
        BigDecimal amount,

        @ToolParam(description = "Transaction category. Accepted values: PERSONAL, PHARMACY, SCHOOL, SHOPPING, GAS, GROCERIES, WARDROBE, PET, HOUSE, MARKET, CHILDREN")
        Category category

) {
    public static Operation toEntity(OperationInput input){
        return new Operation(input.description, input.amount, input.category);
    }
}

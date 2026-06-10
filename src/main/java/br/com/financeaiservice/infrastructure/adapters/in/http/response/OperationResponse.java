package br.com.financeaiservice.infrastructure.adapters.in.http.response;

import br.com.financeaiservice.application.output.OperationOutPut;
import br.com.financeaiservice.domain.enums.Category;

import java.math.BigDecimal;
import java.util.UUID;

public record OperationResponse(
            String description,

            BigDecimal value,

            Category category
) {

public static OperationResponse from(OperationOutPut outPut){
    return new OperationResponse(outPut.description(), outPut.amount(), outPut.category());
}
}

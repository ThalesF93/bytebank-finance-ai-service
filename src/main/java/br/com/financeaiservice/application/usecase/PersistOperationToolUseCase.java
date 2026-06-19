package br.com.financeaiservice.application.usecase;

import br.com.financeaiservice.application.input.OperationInput;
import br.com.financeaiservice.application.output.OperationOutPut;
import br.com.financeaiservice.domain.entity.Operation;
import br.com.financeaiservice.infrastructure.context.UserContext;
import br.com.financeaiservice.infrastructure.database.OperationRepositoryImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class PersistOperationToolUseCase {

    private final OperationRepositoryImpl operationRepository;
    private final UserContext userContext;
    private final VectorStore vectorStore;


    @Tool(description = "Persists a new financial transaction")
    public OperationOutPut execute(OperationInput input) {
        log.info("Trying to save operation. userId={}, description={}, amount={}, category={}",
                userContext.getUserId(), input.description(), input.amount(), input.category());
        var operation = operationRepository.save(
                new Operation(UUID.fromString(userContext.getUserId()), input.description(), input.amount(), input.category())
        );

        String description = String.format("Transação do tipo %s no valor de R$ %s, em %s para o cliente %s. Descrição: %s ",
                operation.getCategory(), operation.getAmount(), operation.getDate(),operation.getCustomerID(), operation.getDescription());

        Document doc = Document.builder()
                .text(description)
                .metadata(Map.of(
                        "customerId", operation.getCustomerID(),
                        "transactionId", operation.getOperationId(),
                        "amount", operation.getAmount(),
                        "type", operation.getCategory(),
                        "description", operation.getDescription(),
                        "date", operation.getDate()
                ))
                .build();


        vectorStore.add(List.of(doc));
        return OperationOutPut.from(operation);
    }

}

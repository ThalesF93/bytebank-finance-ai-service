package br.com.financeaiservice.application.usecase;

import br.com.financeaiservice.application.input.OperationInput;
import br.com.financeaiservice.application.output.OperationOutPut;
import br.com.financeaiservice.domain.entity.Operation;
import br.com.financeaiservice.infrastructure.context.UserContext;
import br.com.financeaiservice.infrastructure.database.OperationRepositoryImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class PersistOperationToolUseCase {

    private final OperationRepositoryImpl operationRepository;
    private final UserContext userContext;


    @Tool(description = "Persists a new financial transaction")
    public OperationOutPut execute(OperationInput input) {
        log.info("Tentando salvar operação. userId={}, description={}, amount={}, category={}",
                userContext.getUserId(), input.description(), input.amount(), input.category());
        var operation = operationRepository.save(
                new Operation(UUID.fromString(userContext.getUserId()), input.description(), input.amount(), input.category())
        );
        return OperationOutPut.from(operation);
    }
}

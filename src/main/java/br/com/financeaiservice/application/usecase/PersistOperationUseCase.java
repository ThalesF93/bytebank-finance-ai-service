package br.com.financeaiservice.application.usecase;


import br.com.financeaiservice.application.input.OperationInput;
import br.com.financeaiservice.application.output.OperationOutPut;
import br.com.financeaiservice.domain.entity.Operation;
import br.com.financeaiservice.domain.repository.OperationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PersistOperationUseCase {

    private final OperationRepository operationRepository;

    public OperationOutPut execute(OperationInput input, UUID customerId){
        var operation = operationRepository.save(
                new Operation(customerId, input.description(), input.amount(), input.category()));

        return OperationOutPut.from(operation);
    }

}

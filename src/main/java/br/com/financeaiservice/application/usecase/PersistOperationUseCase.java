package br.com.financeaiservice.application.usecase;


import br.com.financeaiservice.application.input.OperationInput;
import br.com.financeaiservice.application.output.OperationOutPut;
import br.com.financeaiservice.domain.entity.Operation;
import br.com.financeaiservice.infrastructure.database.OperationRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PersistOperationUseCase {

    private final OperationRepositoryImpl operationRepository;

    public OperationOutPut execute(OperationInput input){
        var operation = operationRepository.save(
                new Operation(input.customerID(), input.description(), input.amount(), input.category()));

        return OperationOutPut.from(operation);
    }

}

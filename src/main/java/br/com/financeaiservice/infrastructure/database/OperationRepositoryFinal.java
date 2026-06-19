package br.com.financeaiservice.infrastructure.database;

import br.com.financeaiservice.domain.entity.Operation;
import br.com.financeaiservice.domain.repository.OperationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class OperationRepositoryFinal implements OperationRepository {

    private final OperationRepositoryImpl operationRepository;

    @Override
    public Operation save(Operation operation) {
        return operationRepository.save(operation);
    }

    @Override
    public Optional<Operation> findById(UUID id) {
        return operationRepository.findById(id);
    }
}

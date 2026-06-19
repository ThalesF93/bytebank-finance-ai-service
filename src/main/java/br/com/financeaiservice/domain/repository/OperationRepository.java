package br.com.financeaiservice.domain.repository;

import br.com.financeaiservice.domain.entity.Operation;

import java.util.Optional;
import java.util.UUID;

public interface OperationRepository {
    Operation save(Operation operation);
    Optional<Operation> findById(UUID id);
}

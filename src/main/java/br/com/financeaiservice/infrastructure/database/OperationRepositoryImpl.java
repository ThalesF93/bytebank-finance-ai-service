package br.com.financeaiservice.infrastructure.database;

import br.com.financeaiservice.domain.entity.Operation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OperationRepositoryImpl extends JpaRepository<Operation, UUID> {
}

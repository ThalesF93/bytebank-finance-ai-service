package br.com.financeaiservice.domain.entity;

import br.com.financeaiservice.domain.enums.Category;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Operation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private UUID operationId;

    @Column
    private UUID customerID;

    @Column
    private String description;

    @Column
    private BigDecimal amount;

    @Column
    @Enumerated(EnumType.STRING)
    private Category category;

    @Column
    @DateTimeFormat(pattern = "dd/MM/YYYY")
    @CreationTimestamp
    private LocalDate date;

    public Operation(UUID customerID, String description, BigDecimal amount, Category category) {
        this.customerID = customerID;
        this.description = description;
        this.amount = amount;
        this.category = category;
    }
}

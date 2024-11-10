package com.efedorchenko.timely.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "monthly_data_batches")
public class MonthlyDataBatch {

    @Id
    private Long id;

    @NotNull
    @Range(min = 2000, max = 3000)
    private int monthUID;

    private String events;

    private String fines;

    @NotNull
    private UUID userId;

}

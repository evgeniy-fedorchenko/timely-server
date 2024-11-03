package com.efedorchenko.timely.entity;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data // FIXME 01.11.2024 00:31
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

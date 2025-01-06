package com.efedorchenko.timely.model.data;

import com.efedorchenko.timely.model.validation.CurrentDatesRange;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.YearMonth;
import java.util.UUID;

@Getter
@ToString
@AllArgsConstructor
@CurrentDatesRange(startField = "startInclusive", endField = "endInclusive")
public class DataRangeRequest {

    @NotNull
    private final YearMonth startInclusive;

    @NotNull
    private final YearMonth endInclusive;

    @NotNull
    private final UUID requestedUserId; 

}

package com.efedorchenko.timely.model;

import com.efedorchenko.timely.model.validation.CurrentDatesRange;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.YearMonth;

@Getter
@ToString
@AllArgsConstructor
@CurrentDatesRange(startField = "start", endField = "end")
public class DataRangeRequest {

    @NotNull
    private final YearMonth start;

    @NotNull
    private final YearMonth end;

}

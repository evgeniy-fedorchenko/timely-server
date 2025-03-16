package com.efedorchenko.timely.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Helper {

//    Например, YearMonth.of(2025, 01) -> 202501
    private static final Function<YearMonth, Integer> MONTH_UID_CREATOR =
            yearMonth -> yearMonth.getYear() * 100 + yearMonth.getMonthValue();

    public static int getMonthUid(LocalDate localDate) {
        return MONTH_UID_CREATOR.apply(YearMonth.from(localDate));
    }

    public static int getMonthUid(YearMonth yearMonth) {
        return MONTH_UID_CREATOR.apply(yearMonth);
    }

}

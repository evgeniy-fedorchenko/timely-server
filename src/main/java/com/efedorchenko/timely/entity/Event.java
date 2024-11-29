package com.efedorchenko.timely.entity;

import com.efedorchenko.timely.model.data.UserDataType;
import com.efedorchenko.timely.model.validation.Constant;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.Duration;
import java.time.LocalDate;

@Entity
@Table(
        name = "events",
        schema = "data",
        indexes = @Index(name = "events_month_uid_user_id_idx", columnList = "month_uid,user_id")
)
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public final class Event extends UserDataEntity {

    @NotNull
    private LocalDate date;

    @Column(nullable = false)
    private int monthUid;

    @Convert(converter = WorkDurationConverter.class)
    @Column(columnDefinition = "numeric(5)", nullable = false)
    private Duration workDuration;

    @Nullable
    @Size(max = Constant.COMMENT_MAX_LEN)
    private String comment;

    @Override
    public UserDataType getType() {
        return UserDataType.EVENT;
    }

}

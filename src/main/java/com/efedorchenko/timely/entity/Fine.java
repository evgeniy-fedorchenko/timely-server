package com.efedorchenko.timely.entity;

import com.efedorchenko.timely.model.data.UserDataType;
import com.efedorchenko.timely.model.validation.Constant;
import jakarta.persistence.Column;
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

import java.time.LocalDate;

@Entity
@Table(
        name = "fines",
        schema = "data",
        indexes = @Index(name = "fines_month_uid_user_id_idx", columnList = "month_uid,user_id")
)
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public final class Fine extends UserDataEntity {

    @NotNull
    private LocalDate date;

    @Column(nullable = false)
    private int monthUid;

    @NotNull
    @Size(max = Constant.FINE_DESCRIPTION_MAX_LEN)
    private String description;

    @Column(nullable = false)
    private int amount;

    @Override
    public UserDataType getType() {
        return UserDataType.FINE;
    }

}

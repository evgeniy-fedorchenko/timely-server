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
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@Entity
@Table(
        name = "fines",
        schema = "data",
        indexes = {
                @Index(name = "fines_user_id_month_uid_idx", columnList = "user_id,month_uid"),   // Поиск диапазонов
                @Index(name = "fines_user_id_changed_at_idx", columnList = "user_id,changed_at")  // Запрос обновлений
        }
)
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public final class Fine extends UserDataEntity {

    @NotNull
    @Size(max = Constant.FINE_DESCRIPTION_MAX_LEN)
    private String description;

    @Column(nullable = false)
    private int amount;

    @Override
    public UserDataType getType() {
        return UserDataType.FINE;
    }

    @Override
    public boolean equalsLocal(UserDataEntity otherEntity) {
        if (otherEntity instanceof Fine otherFine) {
            return Objects.equals(this.description, otherFine.description)
                    && Objects.equals(this.amount, otherFine.amount);
        } else {
            return false;
        }
    }

}

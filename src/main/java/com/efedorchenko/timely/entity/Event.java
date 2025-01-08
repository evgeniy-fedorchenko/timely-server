package com.efedorchenko.timely.entity;

import com.efedorchenko.timely.model.data.UserDataType;
import com.efedorchenko.timely.model.validation.Constant;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.time.Duration;
import java.util.Objects;

@Entity
@Table(
        name = "events",
        schema = "data",
        indexes = {
                @Index(name = "events_user_id_month_uid_idx", columnList = "user_id,month_uid"),   // Поиск диапазонов
                @Index(name = "events_user_id_changed_at_idx", columnList = "user_id,changed_at")  // Запрос обновлений
        }
)
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public final class Event extends UserDataEntity {

    @Convert(converter = WorkDurationConverter.class)
    @Column(columnDefinition = "numeric(5)", nullable = false)
    private Duration workDuration;

    @Nullable
    @Size(max = Constant.COMMENT_MAX_LEN)
    private String comment;

    @Nullable
    @Column(columnDefinition = "data.event_status")
    @Enumerated(EnumType.STRING)
    @JdbcType(value = PostgreSQLEnumJdbcType.class)
    private EventStatus status;

    @Override
    public UserDataType getType() {
        return UserDataType.EVENT;
    }

    @Override
    public boolean equalsLocal(UserDataEntity otherEntity) {
        if (otherEntity instanceof Event otherEvent) {
            return Objects.equals(this.workDuration, otherEvent.workDuration)
                    && Objects.equals(this.comment, otherEvent.comment);
        } else {
            return false;
        }
    }
}

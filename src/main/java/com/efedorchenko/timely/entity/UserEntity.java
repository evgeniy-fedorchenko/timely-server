package com.efedorchenko.timely.entity;
import com.efedorchenko.timely.configuration.ApplicationProperties;
import com.efedorchenko.timely.model.validation.Constant;
import com.efedorchenko.timely.security.model.RoleType;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "users", schema = "users")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserEntity {

    @Id
    @EqualsAndHashCode.Include
    private UUID id;

    @NotNull
    private String name;

    /**
     * Позиция юзера в компании, занимаемая должность
     */
    @NotNull
    @Size(max = Constant.USER_POSITION_MAX_LEN)
    private String position;

    /**
     * Заработная плата юзера в час, ставка
     */
    @Nullable
    private Integer rate;

    /**
     * Созданное этим юзером пространство.
     * Заполнено только у юзеров с ролью {@link RoleType#CREATOR}, иначе {@code null}
     */
    @Nullable
    @OneToOne(mappedBy = "creator", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Space createdSpace;

    /**
     * Ссылка на пространство, в котором состоит юзер. Для {@link RoleType#CREATOR}
     * указывает на созданное им пространство, тк создатели состоят в своем же пространстве.
     * В случае, если заявка на вступление юзера только рассматривается, это поле тоже будет
     * заполнено этим пространством.
     * Для понимания окончательного статуса членства - смотри поле {@link UserEntity#spaceStatus}.
     * Если юзер не состоит ни в каком пространстве - содержит {@code null}
     */
    @Nullable
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consist_in_space_id")
    private Space consistsInSpace;

    /**
     * Статус пользователя в отношении членства в пространстве
     */
    @Column(columnDefinition = "users.space_consist_status")
    @Enumerated(EnumType.STRING)
    @JdbcType(value = PostgreSQLEnumJdbcType.class)
    private SpaceStatus spaceStatus;

    /**
     * Метка времени последнего изменения данных юзера.
     * Используется для получения клиентами актуальных данных по юзерам своего пространства
     */
    @UpdateTimestamp
    @Column(precision = ApplicationProperties.DB_TIMESTAMP_PRECISION)
    private Instant changedAt;

    /**
     * Список рабочих смен юзера, успешных, пропущенных и запланированных
     */
    @Nullable
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Event> events;

    /**
     * Список штрафов, полученных этим юзером
     */
    @Nullable
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Fine> fines;

    @NotNull
    public List<Event> getEvents() {
        return events == null ? Collections.emptyList() : new ArrayList<>(events);
    }

    @NotNull
    public List<Fine> getFines() {
        return fines == null ? Collections.emptyList() : new ArrayList<>(fines);
    }


    public void setEvents(@Nullable List<Event> events) {
        this.events = getNotNullList(events);
    }

    public void setFines(@Nullable List<Fine> fines) {
        this.fines = getNotNullList(fines);
    }

    @Override
    public String toString() {
        return "UserEntity{id=%s, name='%s', position='%s', rate='%d', createdSpace=%s, consistsInSpace=%s, eventCont=%d, finesCont=%d}"
                .formatted(id.toString(),
                        name,
                        position,
                        rate,
                        createdSpace == null ? null : createdSpace.toString(),
                        consistsInSpace == null ? null : consistsInSpace.toString(),
                        events == null || events.isEmpty() ? 0 : events.size(),
                        fines == null || fines.isEmpty() ? 0 : fines.size());
    }

    private <E extends UserDataEntity> List<E> getNotNullList(@Nullable List<E> dataList) {
        return Optional.ofNullable(dataList).map(List::copyOf).orElse(Collections.emptyList());
    }
}

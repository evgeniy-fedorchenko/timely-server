package com.efedorchenko.timely.entity;

import com.efedorchenko.timely.model.data.UserDataType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nullable;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate;

@Setter
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@MappedSuperclass
public abstract sealed class UserDataEntity permits Event, Fine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotNull
    private LocalDate date;

    @NotNull
    private int monthUid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Nullable
    private Instant deletedAt;

    @UpdateTimestamp
    private Instant changedAt;

    @JsonIgnore
    public abstract UserDataType getType();

    @Override
    public String toString() {
        return "UserDataEntity{id=%d, userId=%s}".formatted(id, user.getId().toString());
    }

    public abstract boolean equalsLocal(UserDataEntity otherEntity);
}

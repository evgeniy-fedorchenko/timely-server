package com.efedorchenko.timely.entity;
import com.efedorchenko.timely.configuration.ApplicationProperties;
import com.efedorchenko.timely.model.validation.Constant;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

    @NotNull
    @Size(max = Constant.USER_POSITION_MAX_LEN)
    private String position;

    @Nullable
    private Integer rate;

    @Nullable
    @OneToOne(mappedBy = "creator", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Space createdSpace;

    @Nullable
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consist_in_space_id")
    private Space consistsInSpace;

    @UpdateTimestamp
    @Column(precision = ApplicationProperties.DB_TIMESTAMP_PRECISION)
    private Instant changedAt;

    @Nullable
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Event> events;

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
        if (events == null) {
            this.events = Collections.emptyList();
        } else {
            this.events = List.copyOf(events);
        }
    }

    public void setFines(@Nullable List<Fine> fines) {
        if (fines == null) {
            this.fines = Collections.emptyList();
        } else {
            this.fines = List.copyOf(fines);
        }
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
}

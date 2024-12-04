package com.efedorchenko.timely.entity;

import com.efedorchenko.timely.model.validation.Constant;
import jakarta.annotation.Nullable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users", schema = "users")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserEntity implements Persistable<UUID> {

    @Id
    @EqualsAndHashCode.Include
    private UUID id;

    @NotNull
    private String name;   // default constraint size = 255

    @NotNull
    @Size(max = Constant.USER_POSITION_MAX_LEN)
    private String position;

    @Column(nullable = false)
    private int rate;

    @Nullable
    @OneToOne(mappedBy = "creator", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Space createdSpace;

    @Nullable
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consist_in_space_id")
    private Space consistsInSpace;

    @Nullable
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Event> events;

    @Nullable
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Fine> fines;

    @Transient
    private boolean isNew = true;

    @Override
    public boolean isNew() {
        return isNew;
    }

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


    }   @Override
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

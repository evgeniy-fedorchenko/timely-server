package com.efedorchenko.timely.entity;

import com.efedorchenko.timely.model.validation.Constant;
import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "spaces", schema = "users")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Space {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotNull
    @Size(max = Constant.SPACE_NAME_MAX_LEN)
    private String name;

    @NotNull
    @Size(max = Constant.SPACE_KEY_MAX_LEN)
    private String workerKey;

    @NotNull
    @Size(max = Constant.SPACE_KEY_MAX_LEN)
    private String bossKey;

    @OneToOne
    @JoinColumn(name = "who_created_user_id", nullable = false)
    private UserEntity creator;

    @Nullable
    @OneToMany(mappedBy = "consistsInSpace")
    private List<UserEntity> participants;

    @Override
    public String toString() {
        return "Space{id=%d, name='%s', workerKey='%s', bossKey='%s', creatorId=%s, participants count='%d'}"
                .formatted(
                        id,
                        name,
                        workerKey.substring(workerKey.length() / 2) + "***",
                        bossKey.substring(bossKey.length() / 2) + "***",
                        creator.getId(),
                        participants == null || participants.isEmpty() ? 0 : participants.size());
    }
}

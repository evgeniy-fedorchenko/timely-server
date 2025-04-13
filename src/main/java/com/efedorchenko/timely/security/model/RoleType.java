package com.efedorchenko.timely.security.model;

import com.efedorchenko.timely.data.entity.SpaceStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

@Getter
@AllArgsConstructor
public enum RoleType {

    WORKER(10, SpaceStatus.PENDING_WORKER, "Работник. Может создавать события Event только для себя и просматривать события Event у себя и других работников в своем пространстве"),

    BOSS(20, SpaceStatus.PENDING_BOSS, "Руководитель. Может создавать, удалять и корректировать события Event и Fine для себя и других участников своего пространства (для WORKER, BOSS, CREATOR). Принимает решения по заявкам на вступление других участников в пространство"),

    CREATOR(30, SpaceStatus.MEMBER, "Создатель пространства. Все полномочия роли BOSS + администрирование пространства. Назначается автоматически юзеру, создавшему пространство"),

    MODERATOR(40, SpaceStatus.NONE, "Техническая роль для управления приложением, прямой доступ к БД и аналитическим данным");

    private final int weight;
    private final SpaceStatus preAcceptSpaceStatus;
    private final String description;


    public static List<RoleType> parse(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .map(RoleType::valueOf)
                .toList();
    }

    public boolean spaceOpsAccess() {
        return this != WORKER;
    }

    /**
     * Вернуть роль под которой должен быть сохранен юзер, если он только запрашивает доступ к пространству.
     * <p>
     * Если юзер создал аккаунт без пространства (и заявки) ему автоматически присваивается роль
     * {@link RoleType#WORKER}, далее если юзер запрашивает вступление в пространство как {@link RoleType#BOSS} -
     * он все равно остается работником, до тех пор пока его заявку не примут.
     * Если юзер запрашивает доступ к пространству сразу при регистрации - ему присуждается роль
     * {@link RoleType#WORKER} и только после принятия заявки ему присваивается запрашиваемая роль. Если юзер
     * регистрировался под ролью {@link RoleType#CREATOR} или {@link RoleType#MODERATOR}, то он сохраняет свою роль.
     * <p>
     * Метод вычисляет, как должна измениться роль юзера, при присоединении к пространству. При этом не важно,
     * создается ли аккаунт юзера и/или пространства в данный момент или был создан ранее.
     * <p>
     * Есть роль, на которую указывает {@code this} имеет бОльший вес, чем {@link RoleType#BOSS}, будет возвращена
     * эта же роль. Иначе будет возвращена роль {@link RoleType#WORKER}
     */
    public RoleType doPreAccept() {
        return this.getWeight() > RoleType.BOSS.getWeight() ? this : RoleType.WORKER;
    }
}

package com.efedorchenko.timely.model.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

@Getter
@AllArgsConstructor
public enum RoleType {

    WORKER(10, "Работник. Может создавать события Event только для себя и просматривать события Event и Fine у себя и других работников в своем пространстве"),

    BOSS(20, "Руководитель. Может создавать, удалять и корректировать события Event и Fine для себя и других участников своего пространства (для WORKER, BOSS, CREATOR). Принимает решения по заявкам на вступление других участников в пространство"),

    CREATOR(30, "Создатель пространства. Все полномочия роли BOSS + администрирование пространства. Назначается автоматически юзер, создавшему пространство"),

    MODERATOR(40, "Техническая роль для управления приложением, прямой доступ к БД и аналитическим данным");

    private final int weight;
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

}

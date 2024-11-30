package com.efedorchenko.timely.model.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Getter
@AllArgsConstructor
public enum RoleType {

    WORKER(10),

    BOSS(20),

    CREATOR(30),

    MODERATOR(40);

    private final int weight;

    public static List<RoleType> parse(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .map(RoleType::valueOf)
                .toList();
    }

    public static RoleType getMax(Collection<RoleType> roleTypes) {
        if (roleTypes.isEmpty()) {
            return RoleType.WORKER;
        }
        if (roleTypes.size() == 1) {
            return roleTypes.iterator().next();
        }
        return roleTypes.stream()
                .max(Comparator.comparingInt(RoleType::getWeight))
                .orElse(null);
    }

    public boolean spaceOpsAccess() {
        return this != WORKER;
    }

}

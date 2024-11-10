package com.efedorchenko.timely.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.domain.Persistable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "user_details")
public class UserDetailsImpl implements UserDetails, Persistable<UUID> {

    @Id
    @EqualsAndHashCode.Include
    private UUID id;

    private String username;

    @ToString.Exclude
    private String password;

    @Nullable
    private String authorities; // FIXME 27.10.2024 02:43: переделать на связь с др таблицей

    @Transient
    private boolean isNew = true;

    @Override
    public boolean isNew() {
        return isNew;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (authorities == null) {
            return AuthorityUtils.NO_AUTHORITIES;
        }
        return Arrays.stream(authorities.split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public void setAuthorities(@NotNull List<String> authorities) {
        this.authorities = String.join(",", authorities);
    }
}

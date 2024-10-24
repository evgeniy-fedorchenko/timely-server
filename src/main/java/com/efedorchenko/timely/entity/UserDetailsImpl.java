package com.efedorchenko.timely.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.util.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "user_details")
public class UserDetailsImpl implements UserDetails, Persistable<UUID> {

    @Id
    @EqualsAndHashCode.Include
    private UUID id;

    private String username;

    @ToString.Exclude
    private String password;

    @Nullable
    private String authorities;

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

    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        if (authorities == null) {
            this.authorities = null;
        } else {
            this.authorities = authorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining());
        }
    }
}

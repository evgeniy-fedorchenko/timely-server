package com.efedorchenko.timely.entity;

import com.efedorchenko.timely.model.validation.Constant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.domain.Persistable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "user_details", schema = "security")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserDetailsImpl implements UserDetails, Persistable<UUID> {

    @Id
    @EqualsAndHashCode.Include
    private UUID id;

    @Size(max = Constant.USERNAME_MAX_LEN)
    @Column(unique = true, nullable = false)
    private String username;

    @NotNull
    @ToString.Exclude
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            schema = "security",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    @Transient
    @ToString.Exclude
    private boolean isNew = true;

    @Override
    public boolean isNew() {
        return isNew;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (roles == null) {
            return AuthorityUtils.NO_AUTHORITIES;
        }
        return roles.stream()
                .map(r -> r.getValue().name())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public void addRole(Role role) {
        if (role == null) {
            return;
        }
        if (roles == null) {
            this.setRoles(Set.of(role));
        } else {
            this.roles.add(role);
        }
    }
}

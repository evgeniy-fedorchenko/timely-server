package com.efedorchenko.timely.data.entity;

import com.efedorchenko.timely.data.validation.Constant;
import com.efedorchenko.timely.security.model.RoleType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@Entity
@Table(name = "user_details", schema = "security")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserDetailsImpl implements UserDetails {

    @Id
    @EqualsAndHashCode.Include
    private UUID id;

    @Size(max = Constant.USERNAME_MAX_LEN)
    @Column(unique = true, nullable = false)
    private String username;

    @NotNull
    @ToString.Exclude
    private String password;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (role == null) {
            return AuthorityUtils.NO_AUTHORITIES;
        }
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role.getRoleType().name());
        return Collections.singletonList(authority);
    }

    public boolean hasRole(RoleType roleType) {
        return this.role.getRoleType().equals(roleType);
    }

    @Override
    public String toString() {
        return "UserDetailsImpl{id=%s, username='%s', role=%s}"
                .formatted(id.toString(), username, role.getRoleType());
    }
}

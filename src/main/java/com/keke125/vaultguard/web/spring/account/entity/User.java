package com.keke125.vaultguard.web.spring.account.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.keke125.vaultguard.web.spring.util.AbstractEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
public class User extends AbstractEntity implements UserDetails {

    @NotBlank(message = "請填寫帳號!")
    @Size(min = 1, max = 128, message = "帳號長度必須在{min}-{max}字元之間!")
    @Column(unique = true)
    private String username;

    @JsonIgnore
    @NotBlank(message = "請填寫密碼!")
    @Setter
    private String hashedPassword;

    @JsonIgnore
    private String password;

    @NotBlank(message = "請填寫電子信箱!")
    @Email(message = "電子信箱格式錯誤!")
    @Column(unique = true)
    @Getter
    @Setter
    private String email;

    @Setter
    @Getter
    private boolean enabled;
    @Setter
    @Getter
    private boolean isAccountNonExpired;
    @Setter
    @Getter
    private boolean isAccountNonLocked;
    @Setter
    @Getter
    private boolean isCredentialsNonExpired;

    @NotNull
    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    @Getter
    @Setter
    private Set<Role> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role)).collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return this.hashedPassword;
    }

    public @NotNull String getUsername() {
        return username;
    }

    public void setUsername(@NotNull String username) {
        this.username = username;
    }
}
package com.keke125.vaultguard.web.spring.account.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.keke125.vaultguard.web.spring.password.entity.Password;
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

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
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
    @Getter
    private String hashedPassword;

    @NotBlank(message = "請填寫電子信箱!")
    @Email(message = "電子信箱格式錯誤!")
    @Column(unique = true)
    @Getter
    @Setter
    private String email;

    @Setter
    @Getter
    @JsonIgnore
    private boolean enabled;
    @Setter
    @Getter
    @JsonIgnore
    private boolean accountNonExpired;
    @Setter
    @Getter
    @JsonIgnore
    private boolean accountNonLocked;
    @Setter
    @Getter
    @JsonIgnore
    private boolean credentialsNonExpired;

    @NotNull
    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    @Getter
    @Setter
    @JsonIgnore
    private Set<Role> roles;

    @Getter
    @Setter
    private String activateAccountToken;

    @Getter
    @Setter
    private LocalDateTime lastSendVerificationCodeDate;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<VerificationCode> verificationCodes;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Password> passwords;

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

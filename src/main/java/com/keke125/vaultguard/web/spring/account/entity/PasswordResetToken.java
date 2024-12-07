package com.keke125.vaultguard.web.spring.account.entity;

import com.keke125.vaultguard.web.spring.util.AbstractEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToOne;
import lombok.*;

import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class PasswordResetToken extends AbstractEntity {

    // 30 min
    public static final int EXPIRATION = 30 * 60;

    private String token;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    private User user;

    private Date expiryDate;

    private boolean isValid;
}

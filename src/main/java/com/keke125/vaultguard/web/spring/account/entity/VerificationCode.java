package com.keke125.vaultguard.web.spring.account.entity;

import com.keke125.vaultguard.web.spring.util.AbstractEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Table(name = "verification_code")
public class VerificationCode extends AbstractEntity {

    // 30 min
    public static final int EXPIRATION = 30 * 60;

    private String token;

    @ManyToOne
    private User user;

    private Date expiryDate;

    private boolean isValid;

    private VerificationType verificationType;
}

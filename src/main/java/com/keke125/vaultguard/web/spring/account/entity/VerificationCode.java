package com.keke125.vaultguard.web.spring.account.entity;

import com.keke125.vaultguard.web.spring.util.AbstractEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Table(name = "verification_code")
public class VerificationCode extends AbstractEntity {

    // 30 min
    public static final int VERIFICATION_CODE_EXPIRATION = 30;
    // 3 min
    public static final int SEND_VERIFICATION_CODE_PERIOD = 3;

    private String token;

    @ManyToOne
    private User user;

    private LocalDateTime expiryDate;

    private boolean valid;

    private VerificationType verificationType;
}

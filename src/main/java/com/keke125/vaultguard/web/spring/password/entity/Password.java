package com.keke125.vaultguard.web.spring.password.entity;

import com.keke125.vaultguard.web.spring.util.AbstractEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@IdClass(PasswordId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "password")
public class Password extends AbstractEntity {
    @Id
    private String name;
    @Id
    private String username;
    private String password;
    @ElementCollection
    private List<String> urlList;
    private String notes;
    private String totp;
    private String createdDateTime = "";
    private String lastModifiedDateTime = "";
    private String userUid;
}

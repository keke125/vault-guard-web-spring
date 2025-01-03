package com.keke125.vaultguard.web.spring.password.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.keke125.vaultguard.web.spring.account.entity.User;
import com.keke125.vaultguard.web.spring.util.AbstractEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "password", uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "username", "user_uid"})})
public class Password extends AbstractEntity {
    private String name;
    private String username;
    private String password;
    @ElementCollection
    private List<String> urlList;
    private String notes;
    private String totp;
    private String createdDateTime = "";
    private String lastModifiedDateTime = "";
    @JsonIgnore
    @ManyToOne
    private User user;
}

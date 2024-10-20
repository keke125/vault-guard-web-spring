package com.keke125.vaultguard.web.spring.password.entity;

import com.keke125.vaultguard.web.spring.util.AbstractEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Data
public class PasswordId implements Serializable {
    private String uid;
    private String name;
    private String username;

    @Override
    public int hashCode() {
        if (getName() != null && getUsername() != null) {
            return Objects.hash(getName(), getUsername());
        }
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PasswordId other)) {
            return false; // null or other class
        }

        if (getName() != null && getUsername() != null) {
            return this.equals(other);
        }
        return super.equals(other);
    }
}

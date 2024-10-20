package com.keke125.vaultguard.web.spring.util;

import jakarta.persistence.*;
import lombok.Data;

@Data
@MappedSuperclass
public abstract class AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String uid;
    @Version
    private int version;

    @Override
    public int hashCode() {
        if (getUid() != null) {
            return getUid().hashCode();
        }
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AbstractEntity other)) {
            return false; // null or other class
        }

        if (getUid() != null) {
            return getUid().equals(other.getUid());
        }
        return super.equals(other);
    }
}

package com.architecture.layered.domain;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Core domain entity representing a user.
 * Enforces invariants: non-null ID and name, non-blank name, non-future birthВate.
 */
public record User(String id, String name, LocalDate birthDate) {
    public User {
        Objects.requireNonNull(id, "Id must not be null");
        Objects.requireNonNull(name, "Name must not be null");
        if (name.isBlank()) throw new IllegalArgumentException("Name blank");
        if (birthDate.isAfter(LocalDate.now())) throw new IllegalArgumentException("Future date");
    }
}

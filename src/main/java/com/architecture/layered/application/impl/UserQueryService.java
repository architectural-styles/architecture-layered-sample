package com.architecture.layered.application.impl;

import com.architecture.layered.application.api.QueryUseCase;
import com.architecture.layered.domain.User;
import com.architecture.layered.domain.exception.UserNotFoundException;
import com.architecture.layered.infrastructure.api.ReadRepository;

import java.util.List;

/**
 * Package-private.
 */
final class UserQueryService implements QueryUseCase {

    private final ReadRepository users;

    public UserQueryService(ReadRepository users) { this.users = users;}

    public User findById(String id) {
        return users.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    public List<User> findAll() {
        return users.findAll();
    }

    public List<User> findByNameStartingWith(String prefix) {
        return users.findByNameStartingWith(prefix);
    }

}

package com.architecture.layered.application.impl;

import com.architecture.layered.application.api.QueryUseCase;
import com.architecture.layered.application.api.query.UserView;
import com.architecture.layered.domain.User;
import org.springframework.transaction.annotation.Transactional;
import com.architecture.layered.domain.exception.UserNotFoundException;
import com.architecture.layered.infrastructure.api.ReadRepository;

import java.util.List;

/**
 * Package-private.
 */
@Transactional(readOnly = true)
class UserQueryService implements QueryUseCase {

    private final ReadRepository users;

    public UserQueryService(ReadRepository users) {
        this.users = users;
    }

    public UserView findById(String id) {
        return toView(
                users.findById(id).orElseThrow(() -> new UserNotFoundException(id))
        );
    }

    public List<UserView> findAll() {
        return users.findAll().stream().map(this::toView).toList();
    }

    public List<UserView> findByNameStartingWith(String prefix) {
        return users.findByNameStartingWith(prefix).stream().map(this::toView).toList();
    }

    private UserView toView(User u) {
        return new UserView(u.id(), u.name(), u.birthDate());
    }
}

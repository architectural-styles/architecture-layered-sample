package com.architecture.layered.application.impl;

import com.architecture.layered.application.api.CommandUseCase;
import com.architecture.layered.application.api.command.CreateUserCommand;
import com.architecture.layered.application.api.command.UpdateUserCommand;
import org.springframework.transaction.annotation.Transactional;
import com.architecture.layered.domain.User;
import com.architecture.layered.infrastructure.api.IdGenerator;
import com.architecture.layered.infrastructure.api.WriteRepository;

import java.util.Objects;

/**
 * Package-private.
 */
class UserCommandService implements CommandUseCase {

    private final IdGenerator idGenerator;
    private final WriteRepository repository;

    public UserCommandService(
            IdGenerator idGenerator,
            WriteRepository repository
    ) {
        this.idGenerator = idGenerator;
        this.repository = repository;
    }

    @Override
    @Transactional
    public String createUser(CreateUserCommand command) {
        Objects.requireNonNull(command, "User must not be null");
        String newId = idGenerator.nextId();
        repository.save(new User(newId, command.name(), command.birthDate()));
        return newId;
    }

    @Override
    @Transactional
    public void updateUser(UpdateUserCommand command) {
        repository.update(new User(command.id(), command.name(), command.birthDate()));
    }

    @Override
    @Transactional
    public void deleteUser(String id) {
        repository.deleteById(id);
    }
}

package com.architecture.layered.infrastructure.impl.jpa;

import com.architecture.layered.domain.User;
import com.architecture.layered.domain.exception.UserNotFoundException;
import com.architecture.layered.infrastructure.api.WriteRepository;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Profile;


/**
 * Package-private.
 */
@Profile("jpa")
@Transactional
final class JpaWriteRepository implements WriteRepository {

    private final EntityManager em;

    public JpaWriteRepository(EntityManager em) {
        this.em = em;
    }
    @Override
    public void save(User user) {
        UserJpaEntity entity = new UserJpaEntity(
                user.id(), // <-- использовать ID из домена
                user.name(),
                user.birthDate()
        );
        em.persist(entity);
    }

    @Override
    public void update(String id, User user) {
        UserJpaEntity entity = em.find(UserJpaEntity.class, id);
        if (entity == null) throw new UserNotFoundException("User not found: " + id);
        entity.setName(user.name());
        entity.setBirthDate(user.birthDate());
        em.merge(entity);
    }

    @Override
    public void deleteById(String id) {
        UserJpaEntity entity = em.find(UserJpaEntity.class, id);
        if (entity == null) {
            throw new UserNotFoundException("User not found: " + id);
        }
        em.remove(entity);
    }

}

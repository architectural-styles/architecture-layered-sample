package com.architecture.layered.infrastructure.impl.jpa;

import com.architecture.layered.infrastructure.api.ReadRepository;
import com.architecture.layered.infrastructure.api.WriteRepository;
import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;


/**
 * Package-private.
 */
@Profile("jpa")
@Configuration
class JpaConfig {

    @Bean
    WriteRepository jpaWriteRepository(EntityManager em) {
        return new JpaWriteRepository(em);
    }

    @Bean
    ReadRepository jpaReadRepository(UserJpaRepository jpa) {
        return new JpaReadRepository(jpa);
    }
}

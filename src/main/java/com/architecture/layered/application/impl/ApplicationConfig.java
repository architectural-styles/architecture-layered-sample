package com.architecture.layered.application.impl;

import com.architecture.layered.application.api.CommandUseCase;
import com.architecture.layered.application.api.QueryUseCase;
import com.architecture.layered.infrastructure.api.IdGenerator;
import com.architecture.layered.infrastructure.api.ReadRepository;
import com.architecture.layered.infrastructure.api.WriteRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Package-private.
 */
@Configuration
class ApplicationConfig {

    @Bean
    CommandUseCase commandService(IdGenerator idGenerator, WriteRepository repo) {
        return new UserCommandService(idGenerator, repo);
    }

    @Bean
    QueryUseCase queryService(ReadRepository repo) {
        return new UserQueryService(repo);
    }
}

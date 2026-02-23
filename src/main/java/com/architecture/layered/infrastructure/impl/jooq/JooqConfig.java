package com.architecture.layered.infrastructure.impl.jooq;

import com.architecture.layered.infrastructure.api.ReadRepository;
import com.architecture.layered.infrastructure.api.WriteRepository;
import org.jooq.DSLContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Package-private.
 */
@Profile("jooq")
@Configuration
class JooqConfig {

    @Bean
    WriteRepository jooqWriteRepository(DSLContext dsl) {
        return new JooqWriteRepository(dsl);
    }

    @Bean
    ReadRepository jooqReadRepository(DSLContext dsl) {
        return new JooqReadRepository(dsl);
    }
}
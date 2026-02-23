package com.architecture.layered.infrastructure.impl.id;

import com.architecture.layered.infrastructure.api.IdGenerator;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Package-private.
 */
final class SequentialIdGenerator implements IdGenerator {

    private final AtomicLong seq = new AtomicLong(0);

    @Override
    public String nextId() {
        return String.valueOf(seq.incrementAndGet());
    }

}

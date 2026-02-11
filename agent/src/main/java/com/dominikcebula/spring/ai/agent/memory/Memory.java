package com.dominikcebula.spring.ai.agent.memory;

import java.time.ZonedDateTime;
import java.util.UUID;

public record Memory(
        UUID id,
        String conversationId,
        String content,
        MemoryType memoryType,
        ZonedDateTime createdAt
) {
}

enum MemoryType {
    EPISODIC,
    SEMANTIC
}

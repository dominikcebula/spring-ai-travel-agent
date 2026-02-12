package com.dominikcebula.spring.ai.agent.memory;

import java.time.LocalDateTime;
import java.util.UUID;

public record Memory(
        UUID id,
        UUID conversationId,
        String content,
        MemoryType memoryType,
        LocalDateTime createdAt
) {
}

enum MemoryType {
    EPISODIC,
    SEMANTIC
}

package com.dominikcebula.spring.ai.agent.memory;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static java.util.Collections.singletonList;

@Service
public class MemoryService {
    private final VectorStore vectorStore;

    public MemoryService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void storeMemory(UUID conversationId, String content, MemoryType memoryType) {
        Memory memory = new Memory(UUID.randomUUID(), conversationId, content, memoryType, LocalDateTime.now());

        Document document = new Document(
                memory.id().toString(),
                memory.content(),
                Map.of(
                        "conversationId", memory.conversationId(),
                        "memoryType", memory.memoryType(),
                        "createdAt", memory.createdAt()
                )
        );

        vectorStore.add(singletonList(document));
    }
}

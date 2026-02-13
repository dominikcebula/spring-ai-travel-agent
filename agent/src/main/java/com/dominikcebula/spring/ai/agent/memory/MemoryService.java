package com.dominikcebula.spring.ai.agent.memory;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
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

    public List<Memory> retrieveMemory(UUID conversationId, String userPrompt, int limit, float distanceThreshold) {
        FilterExpressionBuilder filterExpressionBuilder = new FilterExpressionBuilder();

        Filter.Expression filterExpression = filterExpressionBuilder.eq("conversationId", conversationId).build();

        List<Document> documents = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(userPrompt)
                        .topK(limit)
                        .filterExpression(filterExpression)
                        .build());

        return documents.stream()
                .filter(doc -> doc.getScore() == null || doc.getScore() >= distanceThreshold)
                .map(this::mapToMemory)
                .toList();
    }

    private Memory mapToMemory(Document document) {
        return new Memory(
                UUID.fromString(document.getId()),
                (UUID) document.getMetadata().get(ChatMemory.CONVERSATION_ID),
                document.getText(),
                (MemoryType) document.getMetadata().get("memoryType"),
                (LocalDateTime) document.getMetadata().get("createdAt")
        );
    }
}

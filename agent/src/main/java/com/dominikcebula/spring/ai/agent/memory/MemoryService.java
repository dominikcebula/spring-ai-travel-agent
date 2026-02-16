package com.dominikcebula.spring.ai.agent.memory;

import com.dominikcebula.spring.ai.agent.memory.utils.DateUtils;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.util.Collections.singletonList;

@Service
public class MemoryService {
    private static final String META_CONVERSATION_ID = "conversationId";
    private static final String META_MEMORY_TYPE = "memoryType";
    private static final String META_CREATED_AT = "createdAt";

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
                        META_CONVERSATION_ID, memory.conversationId().toString(),
                        META_MEMORY_TYPE, memory.memoryType(),
                        META_CREATED_AT, memory.createdAt()
                )
        );

        vectorStore.add(singletonList(document));
    }

    public List<Memory> retrieveMemory(UUID conversationId, String userPrompt, int limit, float distanceThreshold) {
        FilterExpressionBuilder filterExpressionBuilder = new FilterExpressionBuilder();

        Filter.Expression filterExpression = filterExpressionBuilder.eq(META_CONVERSATION_ID, conversationId.toString()).build();

        List<Document> documents = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(userPrompt)
                        .topK(limit)
                        .filterExpression(filterExpression)
                        .similarityThreshold(distanceThreshold)
                        .build());

        return documents.stream()
                .map(this::mapToMemory)
                .toList();
    }

    private Memory mapToMemory(Document document) {
        return new Memory(
                UUID.fromString(document.getId()),
                UUID.fromString(document.getMetadata().get(META_CONVERSATION_ID).toString()),
                document.getText(),
                MemoryType.valueOf(document.getMetadata().get(META_MEMORY_TYPE).toString()),
                DateUtils.toLocalDateTime((Date) document.getMetadata().get(META_CREATED_AT))
        );
    }
}

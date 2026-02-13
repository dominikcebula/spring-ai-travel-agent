package com.dominikcebula.spring.ai.agent.memory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.dominikcebula.spring.ai.agent.memory.utils.ChatClientRequestUtils.getConversationId;

@Component
public class MemoryRecorderAdvisor implements CallAdvisor {
    private static final Logger LOGGER = LoggerFactory.getLogger(MemoryRecorderAdvisor.class);

    private final MemoryService memoryService;
    private final ChatModel chatModel;

    public MemoryRecorderAdvisor(MemoryService memoryService, ChatModel chatModel) {
        this.memoryService = memoryService;
        this.chatModel = chatModel;
    }

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        ChatClientResponse chatClientResponse = callAdvisorChain.nextCall(chatClientRequest);

        try {
            extractAndStoreMemories(chatClientRequest, chatClientResponse);
        } catch (Exception e) {
            LOGGER.warn("Error occurred while extracting memories from the conversation", e);
        }

        return chatClientResponse;
    }

    private void extractAndStoreMemories(ChatClientRequest chatClientRequest, ChatClientResponse chatClientResponse) {
        String userPrompt = chatClientRequest.prompt().getUserMessage().getText();
        String chatResponse = getChatResponse(chatClientResponse);

        MemoryExtractionResult memoryExtractionResult = extractMemories(userPrompt, chatResponse);

        storeExtractedMemories(chatClientRequest, memoryExtractionResult);
    }

    @NonNull
    private String getChatResponse(ChatClientResponse chatClientResponse) {
        return Optional.ofNullable(chatClientResponse.chatResponse())
                .map(response -> response.getResults().stream()
                        .map(Generation::getOutput)
                        .map(AssistantMessage::getText)
                        .collect(Collectors.joining()))
                .orElseThrow();
    }

    @NonNull
    private MemoryExtractionResult extractMemories(String userPrompt, String chatResponse) {
        String memoryExtractionUserMessage = getMemoryExtractionUserMessage(userPrompt, chatResponse);
        String memoryExtractionSystemMessage = getMemoryExtractionSystemMessage();

        ChatResponse memoryExtractionResponse = chatModel.call(new Prompt(List.of(
                new UserMessage(memoryExtractionUserMessage),
                new SystemMessage(memoryExtractionSystemMessage)
        )));

        String extractedMemories = memoryExtractionResponse.getResults().stream()
                .map(Generation::getOutput)
                .map(AssistantMessage::getText)
                .collect(Collectors.joining());

        return EXTRACTION_CONVERTER.convert(extractedMemories);
    }

    private void storeExtractedMemories(ChatClientRequest chatClientRequest, MemoryExtractionResult memoryExtractionResult) {
        memoryExtractionResult.memories()
                .forEach(memory -> memoryService.storeMemory(getConversationId(chatClientRequest), memory.content(), memory.memoryType()));
    }

    @NonNull
    private String getMemoryExtractionUserMessage(String userPrompt, String chatResponse) {
        return """
                USER SAID:
                """ +
                userPrompt
                + """
                
                ASSISTANT REPLIED:
                """ +
                chatResponse
                + """
                
                Extract up to 5 memories.
                """;
    }

    @NonNull
    private String getMemoryExtractionSystemMessage() {
        return """
                Extract long-term memories from a dialog with the user.
                
                A memory is either:
                
                1. EPISODIC: Personal experiences and user-specific preferences
                   Examples: "User prefers economy cars", "User prefers budget hotels"
                
                2. SEMANTIC: General domain knowledge and facts
                   Examples: "User needs a Schengen visa", "Berlin has comprehensive bike lanes"
                
                Limit extraction to clear, factual information. Do not infer information that was not explicitly stated.
                Return an empty array, if no memories can be extracted.
                
                The instance must conform to this JSON schema:
                """ +
                EXTRACTION_CONVERTER.getJsonSchema()
                + """
                
                    Do not include code fences, schema, or properties. Output a single-line JSON object.
                """.trim();
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE + 60;
    }

    private record MemoryCandidate(String content, MemoryType memoryType) {
    }

    private record MemoryExtractionResult(List<MemoryCandidate> memories) {
    }

    private static final BeanOutputConverter<MemoryExtractionResult> EXTRACTION_CONVERTER = new BeanOutputConverter<>(MemoryExtractionResult.class);
}

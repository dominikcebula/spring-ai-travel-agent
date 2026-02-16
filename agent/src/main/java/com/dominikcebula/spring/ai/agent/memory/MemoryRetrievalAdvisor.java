package com.dominikcebula.spring.ai.agent.memory;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.IntStream;

import static com.dominikcebula.spring.ai.agent.memory.utils.ChatClientRequestUtils.getConversationId;

@Component
public class MemoryRetrievalAdvisor implements CallAdvisor {
    private static final int MEMORY_LIMIT_5_MEMORIES = 5;
    private static final float SIMILARITY_90_PRC = 0.9f;

    private final MemoryService memoryService;

    public MemoryRetrievalAdvisor(MemoryService memoryService) {
        this.memoryService = memoryService;
    }

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        String userPrompt = chatClientRequest.prompt().getUserMessage().getText();

        List<Memory> memories = memoryService.retrieveMemory(
                getConversationId(chatClientRequest),
                userPrompt, MEMORY_LIMIT_5_MEMORIES, SIMILARITY_90_PRC);

        if (!memories.isEmpty()) {
            String memory = """
                    Use the Long-term MEMORY below if relevant. Keep answers factual and concise.
                    
                    ----- MEMORY -----
                    """ +
                    IntStream.range(0, memories.size())
                            .mapToObj(idx -> String.format("%d. Memory Type: %s, Memory Content: %s",
                                    idx + 1, memories.get(idx).memoryType(), memories.get(idx).content()))
                            .reduce("", (a, b) -> a + b + "\n")
                    + """
                    ------------------
                    """;

            chatClientRequest.prompt().augmentSystemMessage(message -> {
                String currentPrompt = message.getText();

                String promptWithMemory = new StringBuilder()
                        .append(currentPrompt)
                        .append("\n\n")
                        .append(memory)
                        .toString();

                return message.mutate()
                        .text(promptWithMemory)
                        .build();
            });
        }

        return callAdvisorChain.nextCall(chatClientRequest);
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE + 40;
    }
}

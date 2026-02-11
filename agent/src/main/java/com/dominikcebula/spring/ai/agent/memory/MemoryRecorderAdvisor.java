package com.dominikcebula.spring.ai.agent.memory;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

@Component
public class MemoryRecorderAdvisor implements CallAdvisor {
    private final MemoryService memoryService;
    private final ChatModel chatModel;

    public MemoryRecorderAdvisor(MemoryService memoryService, ChatModel chatModel) {
        this.memoryService = memoryService;
        this.chatModel = chatModel;
    }

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        return callAdvisorChain.nextCall(chatClientRequest);
    }

    @Override
    public String getName() {
        return "MemoryRecorder";
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 50;
    }
}

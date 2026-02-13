package com.dominikcebula.spring.ai.agent.memory.utils;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.lang.NonNull;

import java.util.Optional;
import java.util.UUID;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

public class ChatClientRequestUtils {
    private ChatClientRequestUtils() {
    }

    @NonNull
    public static UUID getConversationId(ChatClientRequest chatClientRequest) {
        return (UUID) Optional.of(chatClientRequest.context().get(CONVERSATION_ID)).orElseThrow();
    }
}

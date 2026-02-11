package com.dominikcebula.spring.ai.agent;

import com.dominikcebula.spring.ai.agent.memory.MemoryRecorderAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class AgentController {
    private final ChatClient chatClient;

    public AgentController(ChatClient.Builder chatClientBuilder, ToolCallbackProvider toolCallbackProvider, ChatMemory chatMemory, MemoryRecorderAdvisor memoryRecorderAdvisor) {
        this.chatClient = chatClientBuilder
                .defaultToolCallbacks(toolCallbackProvider)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
                        memoryRecorderAdvisor
                )
                .defaultSystem(
                        """
                                You are a helpful travel assistant who can help with booking flights, hotels, and rental cars.
                                Your primary responsibility is to help users search for, compare, and book flights, hotels, and rental cars efficiently and accurately.
                                
                                Use provided Flight Booking Tools, Hotels Booking Tools, and Cars Rental Tools to assist the user with their travel needs.
                                Always use the tools available to get information and perform actions on behalf of the user.
                                
                                Be professional, concise, and friendly.
                                Use clear, structured responses that are easy to scan.
                                Avoid unnecessary verbosity while ensuring all critical booking information is communicated.
                                Your goal is to act as a reliable, tool-driven travel booking assistant that helps users complete their travel arrangements with confidence and clarity.
                                
                                If the user asks for information that is not related to travel bookings, respond politely that you can only assist with travel bookings.
                                """)
                .build();
    }

    @GetMapping("/agent")
    public String generation(@RequestParam String userInput, @RequestParam UUID conversationId) {
        return chatClient.prompt()
                .user(userInput)
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();
    }
}

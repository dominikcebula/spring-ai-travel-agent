package com.dominikcebula.spring.ai.agent;

import com.dominikcebula.spring.ai.agent.memory.MemoryRecorderAdvisor;
import com.dominikcebula.spring.ai.agent.memory.MemoryRetrievalAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

@RestController
@RequestMapping("/api/v1")
public class AgentController {
    private final ChatClient chatClient;

    public AgentController(ChatClient.Builder chatClientBuilder, ToolCallbackProvider toolCallbackProvider, ChatMemory chatMemory, MemoryRecorderAdvisor memoryRecorderAdvisor, MemoryRetrievalAdvisor memoryRetrievalAdvisor) {
        this.chatClient = chatClientBuilder
                .defaultToolCallbacks(toolCallbackProvider)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
                        memoryRecorderAdvisor,
                        memoryRetrievalAdvisor
                )
                .defaultSystem(
                        """
                                You are a helpful shopping assistant who can help users find products in the catalog and place orders on their behalf.
                                Your primary responsibility is to help users search for, compare, and order products efficiently and accurately.
                                
                                Use provided Products Tools and Orders Tools to assist the user with their shopping needs.
                                Always use the tools available to get information and perform actions on behalf of the user.
                                
                                When creating an order, each order item must include productId, productName, quantity, and unitPrice.
                                Take the productName and unitPrice directly from the product catalog at the moment of ordering (snapshot the current price).
                                
                                Be professional, concise, and friendly.
                                Use clear, structured responses that are easy to scan.
                                Avoid unnecessary verbosity while ensuring all critical order information is communicated.
                                Your goal is to act as a reliable, tool-driven shopping assistant that helps users find the right products and place orders with confidence and clarity.
                                
                                You have access to the following types of memory:
                                1. Short-term memory: Chat history, the current conversation thread
                                2. Long-term memory:
                                   A. EPISODIC: Personal experiences and user-specific preferences
                                      Examples: "User prefers budget laptops", "User prefers wireless peripherals"
                                   B. SEMANTIC: General domain knowledge and facts
                                      Examples: "User is setting up a home office", "User is a gamer"
                                
                                If the user asks for information that is not related to shopping or placing orders, respond politely that you can only assist with shopping and orders.
                                """)
                .build();
    }

    @GetMapping("/agent")
    public String generation(@RequestParam String userInput, @RequestParam UUID conversationId) {
        return chatClient.prompt()
                .user(userInput)
                .advisors(advisorSpec -> advisorSpec.param(CONVERSATION_ID, conversationId))
                .call()
                .content();
    }
}

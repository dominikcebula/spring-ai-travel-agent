package com.dominikcebula.spring.ai.agenttests;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;

import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class AgentIntegrationTest {

    private static final String AGENT_BASE_URL = "http://localhost:8050";

    private final TestRestTemplate restTemplate = new TestRestTemplate(
            new RestTemplateBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .readTimeout(Duration.ofMinutes(2)));

    @Test
    void shouldReturnAtLeastOneProductWhenAskedToListAllProducts() {
        // given
        UUID conversationId = UUID.randomUUID();
        String userInput = "List all products available in the catalog";
        String url = AGENT_BASE_URL + "/api/v1/agent?userInput={userInput}&conversationId={conversationId}";

        // when
        ResponseEntity<String> response = restTemplate.getForEntity(
                url, String.class, userInput, conversationId.toString());

        // then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        String body = response.getBody();
        assertThat(body).isNotBlank();
        assertThat(body).containsAnyOf(
                "Wireless Gaming Mouse",
                "Apex Pro",
                "Ultrabook",
                "Smartphone",
                "Smartwatch",
                "Tablet",
                "Laptop",
                "Headphones",
                "Keyboard",
                "Mouse"
        );
    }
}

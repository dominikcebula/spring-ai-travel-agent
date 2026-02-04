# Persistent and Isolated Chat History using Spring AI

![logo_02_persistent_chat_history.png](docs/logo_02_persistent_chat_history.png)

## Introduction

In this article, I will describe how to implement a persistent and isolated chat history for an AI travel agent
using Spring AI. I will describe the importance of maintaining chat history, the reasons for persisting it, and the need
for isolating conversation history per user. Additionally, I will provide a demo, architecture overview, and
implementation details.

This article builds on the previous one, [AI Travel Agent using Spring AI](article.md), have a look at it first, if you
haven't already.

## Why Chat History is Important?

Large Language Models (LLMs) are stateless by default. Each request is processed independently, with no built-in memory
of previous interactions. It means that context must be explicitly provided on every request if we want the model to
behave as if it "remembers" a conversation.

For a conversational application such as an AI travel agent, this context is not optional. Chat history is what allows
the system to move beyond isolated question-answering and into a goal-oriented dialogue.

Chat history enables the AI to understand references to earlier messages, such as:

- Pronouns and implicit context ("Book that flight instead")
- Follow-up questions ("What about cheaper options?")
- Progressive refinement ("Actually, make it a direct flight")

Without chat history, each user message must restate all prior context, leading to repetitive promptsa and poor user
experience.

Travel planning is inherently iterative. A user might:

- Ask for destination ideas
- Narrow down dates and budget
- Compare flights
- Select hotels
- Adjust plans based on constraints

Persisted chat history allows the AI to reason across these steps and maintain a shared mental model of the trip as it
evolves. This is critical for producing consistent, relevant, and personalized responses.

By keeping chat history, the AI can adapt to user preferences over the course of a conversation:

- Preferred destinations
- Preferences for dates
- Travel style (luxury vs. budget, solo vs. family)
- Preferences for flights and hotels
- Budget sensitivity

Even within a single session, this significantly improves response quality and makes the interaction feel natural rather
than mechanical.

Below is an example conversation between a user and an AI travel agent when the chat history is not maintained:

![Screenshot from 2026-02-03 20-51-30.png](docs/article02_persistent_chat_history/Screenshot%20from%202026-02-03%2020-51-30.png)

![Screenshot from 2026-02-03 20-53-40.png](docs/article02_persistent_chat_history/Screenshot%20from%202026-02-03%2020-53-40.png)

Without any chat history, each user message is processed independently, without any contextual information,
as a result, in the provided example Agent is unable to remember car rental preferences, and it is unable to book cars
that are matching the user preferences.

Here is an example conversation between a user and an AI travel agent when the chat history is maintained:

![Screenshot from 2026-02-03 20-51-30.png](docs/article02_persistent_chat_history/Screenshot%20from%202026-02-03%2020-51-30.png)

![Screenshot from 2026-02-03 21-10-25.png](docs/article02_persistent_chat_history/Screenshot%20from%202026-02-03%2021-10-25.png)

![Screenshot from 2026-02-03 21-12-29.png](docs/article02_persistent_chat_history/Screenshot%20from%202026-02-03%2021-12-29.png)

With chat history, the AI is able to remember user preferences and book cars that are matching the user preferences.

## Short-term memory vs. long-term memory

In this article I will focus on Chat History, which is a short-term memory. It stores conversation history for a
single user, as a series of events. Each question and answer is stored. This allows the agent to access the context of
the current conversation and provide relevant, contextual responses.

Long-term memory is information extracted from the conversation and stored in a structured format, it contains key
information, such as user preferences, facts, knowledge. Long-term memory usually involves extraction and consolidation
of information from the conversation.

Long-term memory is outside the scope of this article. If time allows, I will cover it in the next article. Keep in mind
that user preference should be extracted from Chat History and stored for future use.

## Why persist Chat History?

By default, Spring AI will store Chat History in-memory using `InMemoryChatMemoryRepository`. This is ok for
development, but it is not suitable for production.

We need to persist the Chat History to a persistent storage so that it can be shared across multiple instances of the
Agent Chat Service, and it can survive restarts.

## How to persist Chat History?

Chat History will be prested and accessed using `ChatMemoryRepository`.

`ChatMemory` abstraction will manage chat memory and make decisions on which messages to keep and which to remove.

In my case I used MongoDB as a persistent storage with `MongoChatMemoryRepository`.

`ChatClient` creation was not changed and it looks like this:

```java
public AgentController(ChatClient.Builder chatClientBuilder, ToolCallbackProvider toolCallbackProvider, ChatMemory chatMemory) {
    this.chatClient = chatClientBuilder
            .defaultToolCallbacks(toolCallbackProvider)
            .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
            // ...
            .build();
}
```

I have added a dependency to MongoDB Chat Memory in `pom.xml` which autoconfigures `MongoChatMemoryRepository` and uses
it as the implementation of `ChatMemoryRepository` which is then used by `ChatClient`.

Added Maven dependency looks like below:

```xml

<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-model-chat-memory-repository-mongodb</artifactId>
</dependency>
```

To be able to run the application locally, I have configured `docker-compose.yaml` which looks like below:

```yaml
services:
  mongo:
    image: 'mongo:8.2.4-noble'
    restart: always
    ports:
      - "27017:27017"
    environment:
      MONGO_INITDB_DATABASE: travel-agent-chat
      MONGO_INITDB_ROOT_USERNAME: admin
      MONGO_INITDB_ROOT_PASSWORD: secret
```

To start MongoDB as a Docker container using Docker Compose automatically, I have also added dependency to
`spring-boot-docker-compose`:

```xml

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-docker-compose</artifactId>
</dependency>
```

Whenever a user interacts with the Agent, chat history is persisted in MongoDB.

Let's have a look at the MongoDB collection where chat history is stored:

```bash
$ mongosh -u admin -p secret localhost:27017
...
test> use travel-agent-chat
travel-agent-chat> db.ai_chat_memory.find()
[
  {
    _id: ObjectId('698262d6ae98b4a088ba03ae'),
    conversationId: '859bdab7-deef-4cef-90a6-3addda92c072',
    message: {
      content: 'My preference are economy cars.',
      type: 'USER',
      metadata: { messageType: 'USER' }
    },
    timestamp: ISODate('2026-02-03T21:04:22.833Z'),
    _class: 'org.springframework.ai.chat.memory.repository.mongo.Conversation'
  },
  {
    _id: ObjectId('698262d6ae98b4a088ba03af'),
    conversationId: '859bdab7-deef-4cef-90a6-3addda92c072',
    message: {
      content: "Thanks for letting me know your preference for economy cars!"
      type: 'ASSISTANT',
      metadata: { messageType: 'ASSISTANT' }
    },
    timestamp: ISODate('2026-02-03T21:04:22.833Z'),
    _class: 'org.springframework.ai.chat.memory.repository.mongo.Conversation'
  },
  ...
]
```

The full source code of the agent is available on
GitHub: https://github.com/dominikcebula/spring-ai-travel-agent/tree/main/agent

## Why isolate the conversation history per user?

Without isolation, all users would share the same chat history. This would lead to a single conversation history
containing all user messages. The result would be conversation context spilling over into unrelated conversations.

As a result, AI would, for example, confuse users about their preferences, which would lead to incorrect bookings and
a poor user experience.

## How to implement conversation history isolation?

Conversation history isolation implementation requires changes both in the frontend and on the agent side.

In my case, on the frontend, I am keeping `conversationId` as `UUID` in the local storage. Whenever a user opens the
app, I check if there is a `conversationId` in the local storage. If not, I generate a new one as a random UUID and
store it in the local storage.

Whenever `/api/v1/agent` endpoint is called, I pass the `conversationId` as a query parameter.

The below code snippet shows how this is implemented on the frontend:

```typescript
const CONVERSATION_ID_KEY = 'travel_agent_conversation_id';

function getOrCreateConversationId(): string {
    let conversationId = localStorage.getItem(CONVERSATION_ID_KEY);
    if (!conversationId) {
        conversationId = crypto.randomUUID();
        localStorage.setItem(CONVERSATION_ID_KEY, conversationId);
    }
    return conversationId;
}

const conversationId = getOrCreateConversationId();

async function callAgent(userInput: string): Promise<string> {
    const response = await fetch(
        `${API_BASE_URL}/api/v1/agent?userInput=${encodeURIComponent(userInput)}&conversationId=${encodeURIComponent(conversationId)}`
    );
    if (!response.ok) {
        throw new Error(`API error: ${response.status}`);
    }
    return response.text();
}
```

On the agent side, I am using `conversationId` as a query parameter, along with the user input.

`conversationId` is then used by `advisorSpec` to attach `CONVERSATION_ID` when using `chatClient`.

Below is the code snippet showing how this is implemented on the agent side:

```java

@GetMapping("/agent")
public String generation(@RequestParam String userInput, @RequestParam UUID conversationId) {
    return chatClient.prompt()
            .user(userInput)
            .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, conversationId))
            .call()
            .content();
}
```

## Architecture

The below diagram shows the architecture of the application updated with MongoDB used to persist chat history.

![architecture.drawio.png](docs/article02_persistent_chat_history/architecture.drawio.png)

## Summary

TBD

## References

- [Source Code on GitHub](https://github.com/dominikcebula/spring-ai-travel-agent/)
- [Spring AI Documentation](https://docs.spring.io/spring-ai/reference/)

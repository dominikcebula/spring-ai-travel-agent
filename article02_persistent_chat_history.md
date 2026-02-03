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

## Why persist Chat History?

TBD

## Why isolate the conversation history per user?

TBD

## How to implement conversation history isolation?

TBD

## Demo

TBD

## Architecture

TBD

## Implementation

### Agent Chat Web UI

TBD

### Agent Chat Service

TBD

## Summary

TBD

## References

- [Source Code on GitHub](https://github.com/dominikcebula/spring-ai-travel-agent/)
- [Spring AI Documentation](https://docs.spring.io/spring-ai/reference/)

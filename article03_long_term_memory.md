# AI Agent Long-Term Memory (LTM) using Spring AI

LOGO - TBD

## Introduction

In this article I will show how I implemented long-term memory (LTM) in AI agents using Spring AI and MongoDB.

This article builds on the previous articles, [AI Travel Agent using Spring AI](article.md)
and [Persistent and Isolated Chat History using Spring AI](article02_persistent_chat_history.md), if you haven't read
them yet, I recommend getting familiar with them as well.

## Why do we need long-term memory (LTM)?

Large Language Models (LLMs) are stateless by default, each prompt is processed independently. This means that for the
agent to understand the context, it needs to be provided with each request to Large Language Models (LLMs).

One of the solutions is to use Chat History (Short-Term Memory). The challenge with this approach is that it does not
scale.

Chat History usually can contain 10–20 last messages. Extending it with more messages will lead to increased cost due to
high tokens consumption and will also cause challenges with hitting the max token limit. This is because Chat History
adds all of those messages to each user prompt, so from the user perspective it looks like only a single message, but
what happens under the hood is that all previous messages are added as well. So what looks like a short message from the
user point of view is actually a user message plus all last 20 messages.

At the same time Agent needs to have access to user preferences and facts from the previous conversations.

The solution is to use Long-Term Memory (LTM) to store important information about the user preferences and provide
facts.

## How does long-term memory work?

Long-Term Memory (LTM) is a storage for information that agent should remember over time about the user.

Compared to Short-Term Memory (STM), it does not contain all messages, instead information is extracted from messages
and stored in a structured way. This allows storing much more selective and targeted information in a more compact form.

Long-Term Memory (LTM) entries are extracted using Large Language Models (LLMs) and stored in a vector database. Each
entry is associated with a vector embedding, which allows for efficient retrieval based on similarity using semantic
search.

Adding Long-Term Memory (LTM) means that Large Language Models (LLMs) will be used not only to answer questions, but
also to extract information from the user's messages. Additionally, an embedding model will be used to create embeddings
for each entry.

When the agent needs to access long-term memory, it can query the vector database using the current context to retrieve
relevant entries. This way, the agent can access important information about the user preferences and facts from
previous conversations without having to include all past messages in the prompt.

When the agent needs to store information in long-term memory, it checks if the vector database already contains an
entry similar to the currently extracted information, and if not, it creates a new entry.

## How will AI agent use long-term memory?

TBD

## Types of long-term memory

TBD

## How to implement long-term memory in AI agents?

### High-Level Architecture

TBD

### Vector Storage

TBD + props + pom.xml changes

### Embedding Model

TBD

### Recording Memories

TBD

### Retrieving Memories

TBD

### Agent System Prompt

TBD

### Memory Service

TBD - Memory Service + Memory record

## Summary

TBD

## References

- De Lio, R. (2025, July 16). Agent Long-term Memory with Spring AI & Redis. Retrieved February 16, 2026,
  from https://medium.com/redis-with-raphael-de-lio/agent-memory-with-spring-ai-redis-af26dc7368bd
- [Source Code on GitHub](https://github.com/dominikcebula/spring-ai-travel-agent/)
- [Spring AI Documentation](https://docs.spring.io/spring-ai/reference/)

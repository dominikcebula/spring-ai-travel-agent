# AI Agent Long-Term Memory (LTM) using Spring AI

![logo_article03_long_term_memory.png](docs/article03_long_term_memory/logo_article03_long_term_memory.png)

## Introduction

In this article I will show how I implemented long-term memory (LTM) in AI agent using Spring AI and MongoDB.

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

## Prompting for long-term memory extraction

Agent will use LLM to extract information from the user's messages. Below is an example prompt for extracting long-term
memory from a dialog with the user:

```text
USER SAID:
My preference are economy cars when renting a car.

ASSISTANT REPLIED:
Thank you for letting me know! I've noted that you prefer economy cars when renting a car.
I'll keep this preference in mind for any future car rental searches or bookings
to help you find the most suitable options.

YOUR TASK:
Extract up to 5 memories.
```

Here is the system prompt for extracting long-term memory:

```text
Extract long-term memories from a dialog with the user.

A memory is either:

1. EPISODIC: Personal experiences and user-specific preferences
   Examples: "User prefers economy cars", "User prefers budget hotels"

2. SEMANTIC: General domain knowledge and facts
   Examples: "User needs a Schengen visa", "Berlin has comprehensive bike lanes"

Limit extraction to clear, factual information. Do not infer information that was not explicitly stated.
Return an empty array, if no memories can be extracted.

The instance must conform to this JSON schema:
{
  "$schema" : "https://json-schema.org/draft/2020-12/schema",
  "type" : "object",
  "properties" : {
    "memories" : {
      "type" : "array",
      "items" : {
        "type" : "object",
        "properties" : {
          "content" : {
            "type" : "string"
          },
          "memoryType" : {
            "type" : "string",
            "enum" : [ "EPISODIC", "SEMANTIC" ]
          }
        },
        "required" : [ "content", "memoryType" ],
        "additionalProperties" : false
      }
    }
  },
  "required" : [ "memories" ],
  "additionalProperties" : false
}

Do not include code fences, schema, or properties. Output a single-line JSON object.
```

As a result, Long-Term Memory (LTM) entry is extracted by LLM and stored with the vector created by embedding model:

```text
  {
    _id: 'd6440f1a-15d6-4253-a748-f5b68e5013bc',
    content: 'User prefers economy cars when renting a car',
    metadata: {
      createdAt: ISODate('2026-02-17T20:45:18.584Z'),
      memoryType: 'EPISODIC',
      conversationId: '859bdab7-deef-4cef-90a6-3addda92c072'
    },
    embedding: [
            -0.034637451171875,  -0.0021152496337890625,       0.062103271484375,
            0.0277252197265625,        -0.0360107421875, -0.00009626150131225586,
          0.002040863037109375,     -0.0180816650390625,    0.005931854248046875,
          ...
          -0.0011997222900390625,     -0.0261383056640625,        -0.017456054687
    ],
    _class: 'org.springframework.ai.vectorstore.mongodb.atlas.MongoDBAtlasVectorStore$MongoDBDocument'
  }
]
```

## How will AI agent use long-term memory?

Agent will search for relevant long-term memory entries using semantic search before answering questions, if entries are
found, they will be added to the context of the prompt.

Here is the example of a prompt with long-term memories extracted:

```text
Use the Long-term MEMORY below if relevant. Keep answers factual and concise.

----- MEMORY -----
1. Memory Type: EPISODIC, Memory Content: User prefers economy cars when renting a car
2. Memory Type: EPISODIC, Memory Content: User prefers to travel business class whenever available
3. Memory Type: EPISODIC, Memory Content: User prefers to avoid hotel rooms close to the elevator
------------------
```

## Types of long-term memory

I have implemented two types of long-term memory:

EPISODIC: Personal experiences and user-specific preferences
Examples: "User prefers economy cars", "User prefers budget hotels".

SEMANTIC: General domain knowledge and facts
Examples: "User needs a Schengen visa", "Berlin has comprehensive bike lanes".

## How to implement long-term memory in AI agents?

### High-Level Architecture

TBD

![agent_long_term_memory.png](docs/article03_long_term_memory/agent_long_term_memory.png)

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

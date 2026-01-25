# AI Travel Agent using Spring AI

## Introduction

In this article, I'll describe how I created a simplified travel agent using Spring AI.

The end result is an AI-powered travel agent that can help you search, and book flights, hotels, and rental cars through
natural language interactions.

Agent will be accessible through a chat web interface built using ReactJS. Looking like this:

![agent-chat-ui.png](docs/agent-chat-ui.png)

Full source code is available on GitHub: https://github.com/dominikcebula/spring-ai-travel-agent

## Demo

TBD

## Architecture

The diagram below depicts the architecture of the solution.

TBD - architecture diagram

Project is divided into 3 domains:

* Flights
* Hotels
* Rental Cars

Each domain consists of MCP Server, MCP Tools, API, and Microservice that implements the API.

Under each domain it's possible to execute search operations as well as manage created bookings, using REST API or using
MCP protocol, which is a solution well suited for AI agents.

## Tech Stack

Below is a list of technologies used in the solution.

| Layer    | Technology                                     |
|----------|------------------------------------------------|
| AI/LLM   | Spring AI 1.1.2                                |
| Backend  | Java 21, Spring Boot 3.5.9, Spring AI MCP      |
| Frontend | React 19, TypeScript, react-chatbotify         |
| Protocol | Model Context Protocol (MCP) - Streamable HTTP |
| Build    | Maven (multi-module), npm                      |

## Implementation

### Agent

Agent is created using Spring AI. It is using the Amazon Nova Micro model hosted in Amazon Bedrock.

Agent accesses MCP Tools for Flights, Hotels, and Rental Cars using MCP Client. MCP Server for each domain is accessing
a backing Microservice via REST API to execute business logic for search and bookings operations for each domain.

Agent is using an in-memory chat history to remember conversations between user and agent.

To narrow down the agent interactions with the user to "helpful travel assistant who can help with booking flights,
hotels, and rental cars", system prompt is used to control agent behavior.

Agent code looks like this:

```java

@RestController
@RequestMapping("/api/v1")
public class AgentController {
    private final ChatClient chatClient;

    public AgentController(ChatClient.Builder chatClientBuilder, ToolCallbackProvider toolCallbackProvider, ChatMemory chatMemory) {
        this.chatClient = chatClientBuilder
                .defaultToolCallbacks(toolCallbackProvider)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
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
    public String generation(String userInput) {
        return chatClient.prompt()
                .user(userInput)
                .call()
                .content();
    }
}

```

Agent accesses MCP Tools using MCP Client defined under `application.yml`:

```yaml
spring:
  ai:
    bedrock:
      aws:
        region: eu-central-1
      converse:
        chat:
          options:
            model: eu.amazon.nova-micro-v1:0
    mcp:
      client:
        streamable-http:
          connections:
            cars-mcp-server:
              url: ${CARS_MCP_URL:http://localhost:8011}
            flights-mcp-server:
              url: ${FLIGHTS_MCP_URL:http://localhost:8021}
            hotels-mcp-server:
              url: ${HOTELS_MCP_URL:http://localhost:8031}
server:
  port: 8050
```

Agent can be used without Web UI by executing reqeusts against `/api/v1/agent` endpoint with user input, for example:

```bash
$ curl http://localhost:8050/api/v1/agent?userInput=Show%20me%20all%20Hotels%20available%20at%20Krakow
Here are the hotels available in Krakow with rooms available:

1. **Krakow Main Square Hotel**
   - Address: Rynek Glowny 28, 31-010 Krakow
   - Star Rating: 5
   - Available Rooms:
     - Comfortable single room with city view
     - Cozy single room with modern amenities
     - Spacious double room with king-size bed
     - Elegant double room with garden view
     - Twin room with two comfortable beds
     - Deluxe room with premium furnishings and minibar
     - Family room with space for up to 4 guests
     - Luxury suite with separate living area and premium amenities

2. **Kazimierz Historic Inn**
   - Address: Szeroka 18, 31-053 Krakow
   - Star Rating: 4
   - Available Rooms:
     - Comfortable single room with city view
     - Cozy single room with modern amenities
     - Spacious double room with king-size bed
     - Elegant double room with garden view
     - Twin room with two comfortable beds
     - Deluxe room with premium furnishings and minibar
     - Family room with space for up to 4 guests
     - Luxury suite with separate living area and premium amenities

...
```

Full agent source code is available on GitHub: https://github.com/dominikcebula/spring-ai-travel-agent/tree/main/agent

### Agent Chat Web UI

Agent Chat Web UI is built using ReactJS and [react-chatbotify](https://react-chatbotify.com/). It's using
`/api/v1/agent` endpoint to communicate with the agent.

Chatbot UI source code looks like this:

```typescript jsx
    <ChatBot flow={flow}
             settings={{
                 general: {
                     embedded: true,
                     primaryColor: "#3b82f6",
                     secondaryColor: "#1e40af"
                 },
                 header: {
                     title: "Travel Assistant",
                     avatar: "/travel-agent-icon.svg"
                 },
                 chatHistory: {
                     storageKey: "travel_agent_chat"
                 },
                 chatWindow: {
                     showScrollbar: true
                 },
                 botBubble: {
                     showAvatar: true
                 }
             }}/>
```

The code that calls Agent API is below:

```typescript jsx
async function callAgent(userInput: string): Promise<string> {
    const response = await fetch(
        `${API_BASE_URL}/api/v1/agent?userInput=${encodeURIComponent(userInput)}`
    );
    if (!response.ok) {
        throw new Error(`API error: ${response.status}`);
    }
    return response.text();
}

const flow: Flow = {
    start: {
        message: "Hello! I'm your travel assistant. I can help you search and book flights, hotels, and rental cars. How can I assist you today?",
        path: "chat"
    },
    chat: {
        message: async (params: Params) => {
            try {
                return await callAgent(params.userInput);
            } catch (error) {
                return "Sorry, I'm having trouble connecting to the server. Please try again later.";
            }
        },
        path: "chat"
    }
};
```

Then end results allow the user to interact with the agent like this:

![agent-chat-ui-02.png](docs/agent-chat-ui-02.png)

Full source code is available on GitHub: https://github.com/dominikcebula/spring-ai-travel-agent/tree/main/agent-chat-ui

### MCP Servers and MCP Tools

MCP Servers are hosting MCP Tools for each domain giving the agent the ability to access and manager booking data.

Here are some examples of MCP Tools created:

#### Flights MCP Tools

| Tool                     | Description                                                                        |
|--------------------------|------------------------------------------------------------------------------------|
| `getAllAvailableFlights` | Get all available flights, optionally filtered by departure and/or arrival airport |
| `getFlightByNumber`      | Get a flight by its flight number                                                  |
| `getAllFlightsBookings`  | Get all flight bookings                                                            |
| `getFlightBooking`       | Get a flight booking by its reference number                                       |
| `createFlightBooking`    | Create a new flight booking with passengers and flight numbers                     |
| `updateFlightBooking`    | Update an existing flight booking                                                  |
| `cancelFlightBooking`    | Cancel an existing flight booking                                                  |

#### Hotels MCP Tools

| Tool                                 | Description                                                  |
|--------------------------------------|--------------------------------------------------------------|
| `getAllAvailableHotels`              | Get all available hotels                                     |
| `getHotelById`                       | Get a hotel by its ID                                        |
| `getRoomsByHotelId`                  | Get all rooms available at a specific hotel                  |
| `searchForAvailableRooms`            | Search for available hotel rooms by airport code and/or city |
| `getAllHotelsBookingsByHotelId`      | Get all hotel bookings, optionally filtered by hotel         |
| `getHotelBookingsByBookingReference` | Get a hotel booking by its reference number                  |
| `createHotelBooking`                 | Create a new hotel booking with guests and room details      |
| `updateHotelBooking`                 | Update an existing hotel booking                             |
| `cancelHotelBooking`                 | Cancel an existing hotel booking                             |

#### Cars MCP Tools

| Tool                            | Description                                                  |
|---------------------------------|--------------------------------------------------------------|
| `getAllCarRentalLocations`      | Get all locations where cars for rental are available        |
| `getCarRentalLocationById`      | Get location of car rental service by its ID                 |
| `getCarsByCarRentalLocationId`  | Get all cars available for rental at a given location        |
| `getAllCarsAvailableForRent`    | Get all cars available for rental in all locations           |
| `getCarAvailableForRentById`    | Get car by its ID                                            |
| `searchForAvailableCarsForRent` | Search for available cars by airport code and/or city        |
| `getAllCarRentalBookings`       | Get all car rental bookings, optionally filtered by location |
| `getCarRentalBooking`           | Get a car rental booking by its reference number             |
| `createCarRentalBooking`        | Create a new car rental booking with drivers and dates       |
| `updateCarRentalBooking`        | Update an existing car rental booking                        |
| `cancelCarRentalBooking`        | Cancel an existing car rental booking                        |

### Microservices

TBD

### Running the project Locally

TBD

## Further Enhancements

- Memory of conversations between user and agent should be persisted in a database.
- Conversations should be isolated between users.
- Flights / Hotels / Rental Cars Microservices should be persisted in a database.
- Validation of user input should be performed on the Microservices side.

## Summary

TBD

## References

TBD

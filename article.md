# AI Travel Agent using Spring AI

## Introduction

In this article, I'll describe how I created a simplified travel agent using Spring AI.

The end result is an AI-powered travel agent that can help you search, and book flights, hotels, and rental cars through
natural language interactions.

Agent will be accessible through a chat web interface built using ReactJS. Looking like this:

![agent-chat-ui.png](docs/agent-chat-ui.png)

Full source code is available on GitHub: https://github.com/dominikcebula/spring-ai-travel-agent

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

## Summary

TBD

## References

TBD

# 📦 Spring AI Travel Agent

## 📝 Overview

This repository contains a full-stack AI-powered travel booking assistant built with **Spring AI** and the **Model
Context Protocol (MCP)**. This project demonstrates how to build a conversational agent that can search,
compare, and book flights, hotels, and rental cars through natural language interactions.

The system showcases a microservices architecture where each travel domain (flights, hotels, cars) is implemented as an
independent service, with **MCP servers** exposing their functionality as **AI-callable tools**. The central agent
orchestrates these tools using AWS Bedrock (Amazon Nova) to provide a seamless booking experience.

![agent-chat-ui.png](docs/agent-chat-ui.png)

### Key Features

- **Conversational Booking** — Natural language interface for searching and booking travel services
- **Multi-Domain Support** — Integrated flights, hotels, and car rental booking capabilities
- **MCP Integration** — Tool-based AI architecture using Spring AI's MCP implementation
- **Microservices Architecture** — Loosely coupled services with clean API contracts
- **Modern React UI** — travel-themed chat interface built with react-chatbotify

## 🛠️ Tech Stack

| Layer    | Technology                                     |
|----------|------------------------------------------------|
| AI/LLM   | Spring AI 1.1.2                                |
| Backend  | Java 21, Spring Boot 3.5.9, Spring AI MCP      |
| Frontend | React 19, TypeScript, react-chatbotify         |
| Protocol | Model Context Protocol (MCP) - Streamable HTTP |
| Build    | Maven (multi-module), npm                      |

## 🏗️ Architecture

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│  agent-chat-ui  │────▶│      agent      │────▶│   AWS Bedrock   │
│   (React UI)    │     │  (Spring AI)    │     │  (Amazon Nova)  │
└─────────────────┘     └────────┬────────┘     └─────────────────┘
                                 │
                    ┌────────────┼────────────┐
                    ▼            ▼            ▼
            ┌──────────┐  ┌──────────┐  ┌──────────┐
            │  Cars    │  │ Flights  │  │  Hotels  │
            │   MCP    │  │   MCP    │  │   MCP    │
            │  Server  │  │  Server  │  │  Server  │
            └────┬─────┘  └────┬─────┘  └────┬─────┘
                 │             │             │
                 ▼             ▼             ▼
            ┌──────────┐  ┌──────────┐  ┌──────────┐
            │  Cars    │  │ Flights  │  │  Hotels  │
            │ Service  │  │ Service  │  │ Service  │
            └──────────┘  └──────────┘  └──────────┘
```

### Component Ports

| Component            | Port | Description         |
|----------------------|------|---------------------|
| agent-chat-ui        | 3000 | React frontend      |
| agent                | 8050 | Central AI agent    |
| cars-microservice    | 8010 | Car rental REST API |
| cars-mcp-server      | 8011 | Car tools for AI    |
| flights-microservice | 8020 | Flights REST API    |
| flights-mcp-server   | 8021 | Flight tools for AI |
| hotels-microservice  | 8030 | Hotels REST API     |
| hotels-mcp-server    | 8031 | Hotel tools for AI  |

## 🚀 Usage

### Prerequisites

- Java 21+
- Maven 3.9+
- Node.js 18+
- AWS account with Bedrock access (Amazon Nova model enabled)
- AWS credentials configured (`~/.aws/credentials` or environment variables)

### Running Locally

1. **Build the project**
   ```bash
   mvn clean install
   ```

2. **Start all backend services** (from project root, in separate terminals or use IDE run configurations)
   ```bash
   # Microservices
   cd cars/cars-microserivce && mvn spring-boot:run
   cd flights/flights-microserivce && mvn spring-boot:run
   cd hotels/hotels-microserivce && mvn spring-boot:run

   # MCP Servers
   cd cars/cars-mcp-server && mvn spring-boot:run
   cd flights/flights-mcp-server && mvn spring-boot:run
   cd hotels/hotels-mcp-server && mvn spring-boot:run

   # Agent
   cd agent && mvn spring-boot:run
   ```

3. **Start the frontend**
   ```bash
   cd agent-chat-ui
   npm install
   npm start
   ```

4. **Open the application** at http://localhost:3000

## ✍ Author

Dominik Cebula

- https://dominikcebula.com/
- https://blog.dominikcebula.com/
- https://www.udemy.com/user/dominik-cebula/

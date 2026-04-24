# 📦 Spring AI Shopping Agent

![logo.png](docs/logo.png)

## 📝 Overview

This repository contains a full-stack AI-powered shopping assistant built with **Spring AI** and the **Model
Context Protocol (MCP)**. The project demonstrates how to build a conversational agent that can search a product
catalog, compare products, and place orders on the user's behalf through natural language interactions.

The system showcases a microservices architecture where each domain (products, orders) is implemented as an
independent service, with **MCP servers** exposing their functionality as **AI-callable tools**. The central agent
orchestrates these tools using AWS Bedrock (Claude Opus 4.5) to deliver a seamless shopping experience.

![agent-chat-ui.png](docs/agent-chat-ui.png)

### Example Interaction

The agent helps users find the right products and create orders using prompts like:

```text
I would like to buy a budget laptop for daily usage with at least 8GB RAM and 512 GB of storage.
Please also include a monitor, mouse and a keyboard.
Select products that match my criteria and create the order.
```

The agent processes the user's request using an LLM and MCP Tools and reports the created order back:

```text
Your order has been created successfully! Here are the details:

- **CoreBook 14 Everyday Laptop** - Price: $699.99
- **Wireless Gaming Mouse** - Price: $59.99
- **Mechanical Gaming Keyboard** - Price: $129.99
- **27-inch 4K Monitor** - Price: $349.99

**Order ID:** 9f992c3d-2af9-4de4-b8e2-1f939237866f

**Total Value:** $1239.96
```

### Key Features

- **Conversational Shopping** — Natural language interface for searching the catalog and placing orders
- **Product Catalog** — Browse, filter by category, and search products by name, SKU, or tags
- **Order Management** — Create, list, update, and cancel orders on the user's behalf
- **MCP Integration** — Tool-based AI architecture using Spring AI's MCP implementation
- **Microservices Architecture** — Loosely coupled services with clean API contracts
- **Modern React UI** — Chat interface built with react-chatbotify

## 🛠️ Tech Stack

| Layer    | Technology                                     |
|----------|------------------------------------------------|
| AI/LLM   | Spring AI 1.1.2                                |
| Backend  | Java 25, Spring Boot 3.5.12, Spring AI MCP     |
| Frontend | React 19, TypeScript                           |
| Protocol | Model Context Protocol (MCP) - Streamable HTTP |
| Build    | Maven (multi-module), npm                      |

## 🏗️ Architecture

![architecture.drawio.png](docs/architecture.drawio.png)

### Component Ports

| Component             | Port | Description               |
|-----------------------|------|---------------------------|
| agent-chat-ui         | 3000 | React frontend            |
| agent                 | 8050 | Central AI agent          |
| products-microservice | 8020 | Products catalog REST API |
| products-mcp-server   | 8021 | Product tools for AI      |
| orders-microservice   | 8030 | Orders REST API           |
| orders-mcp-server     | 8031 | Order tools for AI        |

### Data Storage

For simplicity, **all catalog and order data is kept in-memory only** — no real database is used for the business
domains. Each microservice maintains its own in-memory data store with pre-populated sample data (products, orders).
Data is reset when services restart. The agent uses MongoDB only to persist chat history and long-term memory.

### MCP Server to Microservice Communication

Each MCP Server connects to its corresponding Microservice via REST API using Spring's **declarative HTTP Service
Client** (`HttpServiceProxyFactory`). The API contracts are defined as interfaces with `@HttpExchange` annotations in
the `*-microservice-api` modules, which are shared between the microservice (server) and the client.

![MCP_Server_to_Microservice_Communication.drawio.png](docs/MCP_Server_to_Microservice_Communication.drawio.png)

## 🛍️ Product Catalog

The in-memory product catalog contains items across the following categories: **Laptops**, **Monitors**, **Keyboards**,
**Mice**, **Headsets**, **Tablets**, **Smartphones**, **Smartwatches**, **Cameras**, **Audio**, and **Accessories**.

Each product includes the following attributes:

| Field            | Example                                       |
|------------------|-----------------------------------------------|
| id               | `26`                                          |
| name             | `CoreBook 14 Everyday Laptop`                 |
| price            | `699.99`                                      |
| category         | `Laptops`                                     |
| stock            | `50`                                          |
| sku              | `APX-PRO15-16-512`                            |
| rating           | `4.6`                                         |
| popularity       | `1500` (number of purchases)                  |
| tags             | `["gaming", "ultrabook", "high-performance"]` |
| warehouseCountry | `Poland`, `USA`, `China`, `Germany`           |

## 🧾 Order Management

The Order Management Service supports:

- Creating orders
- Listing all orders
- Getting order details by ID
- Updating existing orders
- Cancelling orders

Each order contains the following information:

| Field         | Description                                                       |
|---------------|-------------------------------------------------------------------|
| orderId       | Unique identifier (UUID)                                          |
| orderNumber   | Human-readable number, e.g. `ORD-20240615-0001`                   |
| orderDate     | Date and time the order was placed                                |
| customerName  | Customer name                                                     |
| customerEmail | Customer email                                                    |
| status        | `CREATED`, `UPDATED`, `CANCELLED`, `COMPLETED`                    |
| totalValue    | Total order value                                                 |
| items         | Line items, each with productId, productName, quantity, unitPrice |

## 🔧 MCP Tools

The AI agent has access to the following tools exposed by MCP servers:

### 🛍️ Products MCP Tools

| Tool             | Description                                                                                                                 |
|------------------|-----------------------------------------------------------------------------------------------------------------------------|
| `getAllProducts` | Get all products from the catalog, optionally filtered by category and/or a search term matching product name, SKU, or tags |
| `getProductById` | Get a product by its numeric identifier                                                                                     |

### 🧾 Orders MCP Tools

| Tool           | Description                                                                                      |
|----------------|--------------------------------------------------------------------------------------------------|
| `getAllOrders` | Get all orders                                                                                   |
| `getOrder`     | Get an order by its identifier (UUID)                                                            |
| `createOrder`  | Create a new order with customer details and items (productId, productName, quantity, unitPrice) |
| `updateOrder`  | Update an existing order with new customer details and/or items                                  |
| `cancelOrder`  | Cancel an existing order by its identifier                                                       |

## 🚀 Usage

### Prerequisites

- Java 25
- Maven 3.9+
- Node.js 18+
- Docker / Docker Compose
- AWS account with Bedrock access (Claude Opus 4.5 model enabled)
- AWS credentials configured (`~/.aws/credentials` or environment variables)

### Running Locally

#### Using Docker Compose

1. **Build the project**
   ```bash
   mvn clean install
   ```

2. **Start all services with Docker Compose**
   ```bash
   docker compose up
   ```

3. **Open the application** at http://localhost:8080

#### Using Local Services

1. **Build the project**
   ```bash
   mvn clean install
   ```

2. **Start all backend services** (from project root, in separate terminals or use IDE run configurations)
   ```bash
   # Microservices
   cd products/products-microservice && mvn spring-boot:run
   cd orders/orders-microservice && mvn spring-boot:run

   # MCP Servers
   cd products/products-mcp-server && mvn spring-boot:run
   cd orders/orders-mcp-server && mvn spring-boot:run

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

# Refactor AI Travel Agent to AI Shopping Agent

## Goal

This repository contains AI Travel Agent.

Your goal is to refactor this code to AI Shopping Agent.

## Tasks outside the scope of this project

Do not change the UI implemented under `agent-chat-ui`.

UI will be changed as a separate task.

## Tech Stack

| Layer    | Technology                                     |
|----------|------------------------------------------------|
| AI/LLM   | Spring AI 1.1.2                                |
| Backend  | Java 25, Spring Boot 3.5.12, Spring AI MCP     |
| Frontend | React 19, TypeScript, react-chatbotify         |
| Protocol | Model Context Protocol (MCP) - Streamable HTTP |
| Build    | Maven (multi-module), npm                      |

## Current State

Repository contains AI Travel Agent.

AI Travel Agent is divided into three domains:

- flights
- hotels
- cars

Each domain is used by agent.

Agent is implemented under agent microservice.

├── agent
── cars
│ ├── cars-mcp-server
│ ├── cars-microservice
│ ├── cars-microservice-api
│ ├── cars-microservice-client
│ └── pom.xml
├── flights
│ ├── flights-mcp-server
│ ├── flights-microservice
│ ├── flights-microservice-api
│ ├── flights-microservice-client
│ └── pom.xml
├── hotels
│ ├── hotels-mcp-server
│ ├── hotels-microservice
│ ├── hotels-microservice-api
│ ├── hotels-microservice-client
│ └── pom.xml

Get familiar with the code and understand how it works.

## Desired Outcome

### Desired Outcome – Overview

Repository should contain AI Shopping Agent.

The agent will have access to a simplified Products Catalog and Order Management System, allowing it to create orders on
the user's behalf using products from the catalog.

The result is a functional agent that helps users find the right products for their needs and create orders using
prompts like:

```text
I would like to buy a budget laptop for daily usage with at least 8GB RAM and 512 GB of storage.
Please also include a monitor, mouse and a keyboard.
Select product that match my criteria and create the order.
```

The agent processes the user's request using an LLM and MCP Tools. The result is a created order reported by the agent:

```text
Your order has been created successfully! Here are the details:

- **Dell Inspiron 14 Laptop** - Price: $699.99
- **Wireless Gaming Mouse** - Price: $59.99
- **Mechanical Gaming Keyboard** - Price: $129.99
- **27-inch 4K Monitor** - Price: $349.99

**Order ID:** 9f992c3d-2af9-4de4-b8e2-1f939237866f

**Total Value:** $1239.96
```

### Desired Outcome – Structure

AI Shopping Agent should have the following structure:

- products – product catalog domain
    - products-microservice - product catalog microservice, holds the product catalog data in-memory, allows reading all
      products, get products by ID, query for products by category, search for products
    - products-mcp-server - product catalog MCP server exposing tools for the agent
    - products-microservice-api - shared API interfaces for product microservice, used by MCP Server to invoke product
      microservice via products-microservice-client, products-microservice implements products-microservice-api
    - products-microservice-client - products microservice client used by the MCP Server to call product microservice
      via
      REST API
- orders – order management microservice
    - orders-microservice - order management microservice, holds orders data in-memory, allows creating orders, getting
      order
      details by ID, listing all orders, cancelling orders, updating orders
    - orders-mcp-server - order management MCP server exposing tools for the agent
    - orders-microservice-api - shared API interfaces for order management microservice, used by MCP Server to invoke
      order management microservice via orders-microservice-client, orders-microservice
      implements orders-microservice-api
    - orders-microservice-client - order management microservice client used by the MCP Server to call order management
      microservice via REST API, orders-microservice-client is used by the MCP Server to invoke order management
      microservice
- agent – AI Shopping Agent Microsevice – the agent microservice, which contains the agent implementation, connects to
  products and orders MCP servers to get access to the tools for products and order management, processes user requests
  and creates orders on behalf of the user

├── agent
── orders
│ ├── orders-mcp-server
│ ├── orders-microservice
│ ├── orders-microservice-api
│ ├── orders-microservice-client
│ └── pom.xml
├── products
│ ├── products-mcp-server
│ ├── products-microservice
│ ├── products-microservice-api
│ ├── products-microservice-client
│ └── pom.xml

### Desired Outcome – Products Catalog

Product Catalog should contain the following products:

```text
Product(id=1, name="Wireless Gaming Mouse", price=59.99, category="Mice", stock=150),
Product(id=2, name="Mechanical Gaming Keyboard", price=129.99, category="Keyboards", stock=80),
Product(id=3, name="27-inch 4K Monitor", price=349.99, category="Monitors", stock=45),
Product(id=4, name="Noise-Cancelling Headset", price=89.99, category="Headsets", stock=120),
Product(id=5, name="Ergonomic Vertical Mouse", price=39.99, category="Mice", stock=95),
Product(id=6, name="Compact Wireless Keyboard", price=49.99, category="Keyboards", stock=110),
Product(id=7, name="34-inch Ultrawide Monitor", price=599.99, category="Monitors", stock=30),
Product(id=8, name="USB-C Docking Station", price=149.99, category="Accessories", stock=75),
Product(id=9, name="RGB Gaming Headset", price=69.99, category="Headsets", stock=100),
Product(id=10, name="Trackball Mouse", price=44.99, category="Mice", stock=60),
Product(id=11, name="Mechanical Numpad", price=34.99, category="Keyboards", stock=85),
Product(id=12, name="Portable USB Monitor", price=199.99, category="Monitors", stock=55),
Product(id=13, name="Webcam 1080p HD", price=79.99, category="Cameras", stock=140),
Product(id=14, name="USB Microphone", price=99.99, category="Audio", stock=90),
Product(id=15, name="Large Gaming Mouse Pad", price=24.99, category="Accessories", stock=200),
Product(id=16, name="Monitor Stand with USB Hub", price=59.99, category="Accessories", stock=70),
Product(id=17, name="Wireless Earbuds", price=79.99, category="Headsets", stock=130),
Product(id=18, name="Bluetooth Keyboard and Mouse Combo", price=69.99, category="Keyboards", stock=65),
Product(id=19, name="4K Webcam with Ring Light", price=129.99, category="Cameras", stock=50),
Product(id=20, name="USB Hub 7-Port", price=29.99, category="Accessories", stock=180),
Product(id=21, name="Apex Pro 15 Ultrabook", price=1499.99, category="Laptops", stock=25),
Product(id=22, name="FlexEdge 14 Convertible Laptop", price=1299.99, category="Laptops", stock=30),
Product(id=23, name="Nebula G14 Performance Laptop", price=1599.99, category="Laptops", stock=20),
Product(id=24, name="CarbonLite X1 Business Laptop", price=1449.99, category="Laptops", stock=35),
Product(id=25, name="AirLite 13 Ultra-Thin Laptop", price=1099.99, category="Laptops", stock=40),
Product(id=26, name="CoreBook 14 Everyday Laptop", price=699.99, category="Laptops", stock=50),
Product(id=27, name="HomePro 15 Standard Laptop", price=649.99, category="Laptops", stock=45),
Product(id=28, name="ZenCore 14 Slim Laptop", price=899.99, category="Laptops", stock=35),
Product(id=29, name="WorkMate T14 Business Laptop", price=1199.99, category="Laptops", stock=28),
Product(id=30, name="ProEdge 14 High-Performance Laptop", price=1999.99, category="Laptops", stock=22),

Product(id=31, name="NovaTab S10 Tablet", price=499.99, category="Tablets", stock=60),
Product(id=32, name="Orion Slate X Tablet", price=649.99, category="Tablets", stock=45),
Product(id=33, name="VeloTab Air 11 Tablet", price=399.99, category="Tablets", stock=70),
Product(id=34, name="ZenithPad Pro 12 Tablet", price=799.99, category="Tablets", stock=35),
Product(id=35, name="AstraTab Lite 10 Tablet", price=299.99, category="Tablets", stock=80),
Product(id=36, name="CoreSlate Mini 8 Tablet", price=249.99, category="Tablets", stock=90),

Product(id=37, name="Orion X12 Smartphone", price=999.99, category="Smartphones", stock=50),
Product(id=38, name="NovaPhone Z Pro Smartphone", price=899.99, category="Smartphones", stock=65),
Product(id=39, name="VeloCore S9 Smartphone", price=749.99, category="Smartphones", stock=70),
Product(id=40, name="Astra One Max Smartphone", price=1099.99, category="Smartphones", stock=40),
Product(id=41, name="Zenith Edge 5G Smartphone", price=849.99, category="Smartphones", stock=55),
Product(id=42, name="CoreLink Lite 5 Smartphone", price=599.99, category="Smartphones", stock=85),

Product(id=43, name="NovaWatch Pro Smartwatch", price=299.99, category="Smartwatches", stock=75),
Product(id=44, name="Orion Fit X Smartwatch", price=249.99, category="Smartwatches", stock=80),
Product(id=45, name="VeloPulse S Smartwatch", price=199.99, category="Smartwatches", stock=95),
Product(id=46, name="Zenith Time Pro Smartwatch", price=349.99, category="Smartwatches", stock=50),
Product(id=47, name="AstraFit Core Smartwatch", price=179.99, category="Smartwatches", stock=110),
Product(id=48, name="CoreWear Lite Smartwatch", price=149.99, category="Smartwatches", stock=120),
```

To the above list, add the following features:

- sku - example: APX-PRO15-16-512
- rating - example: 4.6
- popularity - example: 1500 (number of purchases)
- tags – example: ["gaming", "ultrabook", "high-performance"]
- warehouse country location – example: "Poland", "USA", "China", "Germany"

### Desired Outcome – Order Management System

Order Management System allows:

- creating orders
- listing all orders
- cancelling orders
- updating orders

Each order holds the following information:

- order ID
- order number – example: ORD-20240615-0001
- order date
- customer name
- customer email
- order status
- order total value
- order products

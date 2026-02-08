# Körber Java Microservices Assignment

## 📋 Overview
This project implements a scalable e-commerce backend system consisting of two communicating microservices: **Inventory Service** and **Order Service**. The system is designed to handle product inventory batches with expiry dates and process customer orders with real-time stock checks.

### Architecture
The solution follows a microservices architecture built with **Spring Boot** and **Java 17**.
* **Inventory Service (Port 8081):** Manages product stock and expiry dates. Implements the **Factory Design Pattern** for extensible logic.
* **Order Service (Port 8082):** Processes orders and communicates synchronously with the Inventory Service via REST.

Both services use **H2 In-Memory Database** for storage and **Liquibase** for automatic schema migration and data loading.

---

## 📂 Repository Structure
```text
├── inventory-service/      # Spring Boot application for Inventory management
│   ├── src/main/java...    # Source code (Factory Pattern implementation)
│   └── src/main/resources  # Liquibase changelogs & CSV data
├── order-service/          # Spring Boot application for Order processing
│   ├── src/main/java...    # Source code (RestTemplate client)
│   └── src/main/resources  # Liquibase changelogs & CSV data
└── README.md               # Project documentation
```

---

## 🚀 Getting Started

### Prerequisites
- Java 17
- Maven

### Build Instructions
Since this is a multi-project repository, you must build each service individually.

#### Clone the repository:
```bash
git clone <repository-url>
cd <repository-folder>
```

#### Build Inventory Service:
```bash
cd InventoryService
mvn clean install
cd ..
```

#### Build Order Service:
```bash
cd OrderService
mvn clean install
cd ..
```

---

## 🏃‍♂️ Running the Applications
You need to run both services simultaneously. Open two terminal windows:

### Terminal 1: Start Inventory Service
```bash
cd InventoryService
mvn spring-boot:run
```

**Server Details:**
- Port: 8081
- Swagger UI: http://localhost:8081/swagger-ui
- H2 Console: http://localhost:8081/h2-console
- JDBC URL: `jdbc:h2:mem:inventorydb`
- User/Pass: `sa` / `password`

### Terminal 2: Start Order Service
```bash
cd OrderService
mvn spring-boot:run
```

**Server Details:**
- Port: 8082
- Swagger UI: http://localhost:8082/swagger-ui
- H2 Console: http://localhost:8082/h2-console
- JDBC URL: `jdbc:h2:mem:orderdb`
- User/Pass: `sa` / `password`

---

## 📖 API Documentation

### 📦 Inventory Service (Port 8081)
Responsible for maintaining inventory batches. It uses a Factory Pattern (InventoryProcessorFactory) to select the logic for retrieving and updating stock.

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/inventory/{productId}` | Returns product details and batches sorted by expiry date (FEFO). |
| POST | `/api/v1/inventory/update` | Updates stock quantity. Used internally by Order Service. |

**Example Response (GET /api/v1/inventory/1001):**
```json
{
  "productId": 1001,
  "productName": "Product A",
  "batches": [
    {
      "batchId": 1,
      "quantity": 68,
      "expiryDate": "2026-06-25"
    },
    {
      "batchId": 2,
      "quantity": 50,
      "expiryDate": "2026-12-31"
    }
  ]
}
```

### 🛒 Order Service (Port 8082)
Accepts orders and communicates with the Inventory Service to reserve stock.

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/order` | Places an order. Fails if inventory is insufficient. |

**Example Request (POST /order):**
```json
{
  "productId": 1001,
  "quantity": 2
}
```

**Example Response (POST /order):**
```json
{
  "orderId": 1,
  "productId": 1001,
  "productName": "Product A",
  "quantity": 2,
  "status": "PLACED",
  "orderDate": "2026-02-08"
}
```

---

## 🧪 Testing Instructions

Both services include Unit Tests (Mockito) and Integration Tests (@SpringBootTest with H2).

To run tests for a specific service:
```bash
# In either InventoryService or OrderService directory:
mvn test
```

### Key Test Scenarios Covered

**Inventory Service:**
- Verifies that batches are returned sorted by expiry date (Ascending)
- Ensures stock is deducted from the batch expiring soonest (FEFO logic)

**Order Service:**
- Mocks the Inventory Service to simulate successful and failed stock updates
- Ensures orders are strictly not saved if the inventory update fails

---

## 🛠️ Technology Stack
- **Language:** Java 17
- **Framework:** Spring Boot 3
- **Database:** H2 In-Memory Database
- **Migration:** Liquibase (Schema creation & CSV Data loading)
- **Testing:** JUnit 5, Mockito
- **Documentation:** SpringDoc OpenAPI (Swagger UI)
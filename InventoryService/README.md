# Inventory Microservice

## Overview

The **Inventory Service** is a Spring Boot application responsible for managing product inventory batches and their expiry dates. It is designed with the **Factory Design Pattern** to allow for future extensibility of inventory handling logic.

**Key Features:**

- **Batch Management:** Tracks multiple batches per product with specific expiry dates.
- **Retrieval Logic:** Retrieves batches sorted by expiry date.
- **Factory Pattern:** Decouples business logic execution from the controller to support multiple inventory strategies.
- **Liquibase:** Automated database schema creation and CSV data loading on startup.

---

## 🛠️ Project Setup Instructions

### Prerequisites

- **Java 17**
- **Maven**

### Installation & Running

1.  **Clone the repository:**

    ```bash
    git clone <repository-url>
    cd inventory-service
    ```

2.  **Build the project:**

    ```bash
    mvn clean install
    ```

3.  **Run the application:**

    ```bash
    mvn spring-boot:run
    ```

    _Alternatively, run the generated JAR:_

    ```bash
    java -jar target/inventory-service-0.0.1-SNAPSHOT.jar
    ```

4.  **Verify Status:**
    The service will start on **Port 8081**.
    - **Swagger UI:** [http://localhost:8081/swagger-ui/index.html](http://localhost:8081/swagger-ui/index.html) [cite: 40]
    - **H2 Console:** [http://localhost:8081/h2-console](http://localhost:8081/h2-console)
      - **JDBC URL:** `jdbc:h2:mem:inventorydb`
      - **User:** `sa`
      - **Password:** `password`

---

## 📖 API Documentation

### 1. Get Inventory Batches

Retrieves inventory information for a given product with all batches sorted by expiry date (ascending)[cite: 15].

- **Endpoint:** `GET /inventory/{productId}`
- **Example Request:** `GET http://localhost:8081/inventory/1001`
- **Response:**
  ```json
  {
    "productId": 1001,
    "productName": "Laptop",
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

### 2. Update Inventory

Updates the stock quantity for a product. Used internally by the Order Service.

- **Endpoint:** `POST /inventory/update`
- **Request Body:**
  ```json
  {
    "productId": 1001,
    "quantity": 5
  }
  ```
- **Response:** `200 OK` or `400 Bad Request` (if insufficient stock).

---

## 🧪 Testing Instructions

The project includes both Unit Tests (using Mockito) and Integration Tests (using H2)[cite: 33, 34].

### Running Tests

To execute all tests, run the following command in the project root:

```bash
mvn test
```

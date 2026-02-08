# Order Microservice

## Overview

The **Order Service** is responsible for accepting customer orders and processing them. It communicates synchronously with the **Inventory Service** to check stock availability and reserve items before confirming an order.

**Key Features:**

- **Order Placement:** Validates stock and creates orders.
- **Inter-service Communication:** Uses `RestTemplate` to sync with Inventory Service.
- **Database Management:** Uses Liquibase for schema management and initial data loading.
- **Error Handling:** Prevents order creation if inventory is insufficient.

---

## 🛠️ Project Setup Instructions

### Prerequisites

- **Java 17**
- **Maven**
- **Inventory Service** must be running on Port 8081.

### Installation & Running

1.  **Clone the repository:**

    ```bash
    git clone <repository-url>
    cd order-service
    ```

2.  **Build the project:**

    ```bash
    mvn clean install
    ```

3.  **Run the application:**

    ```bash
    mvn spring-boot:run
    ```

4.  **Verify Status:**
    The service will start on **Port 8082**.
    - **Swagger UI:** [http://localhost:8082/swagger-ui/index.html](http://localhost:8082/swagger-ui/index.html)
    - **H2 Console:** [http://localhost:8082/h2-console](http://localhost:8082/h2-console)
      - **JDBC URL:** `jdbc:h2:mem:orderdb`
      - **User:** `sa`
      - **Password:** `password`

---

## 📖 API Documentation

### 1. Place Order

Creates a new order. This triggers a call to the Inventory Service to deduct the specified quantity.

- **Endpoint:** `POST /order`
- **Request Body:**
  ```json
  {
    "productId": 1001,
    "quantity": 2
  }
  ```
- **Success Response (200 OK):**
  ```json
  {
    "orderId": 11,
    "productId": 1001,
    "productName": "Product 1001",
    "quantity": 2,
    "status": "PLACED",
    "orderDate": "2026-02-08"
  }
  ```
- **Error Response (400 Bad Request):**
  ```json
  "Failed to place order: Insufficient Stock"
  ```

---

## 🧪 Testing Instructions

The project includes Unit Tests for business logic and Integration Tests for API endpoints.

### Running Tests

To execute all tests, run:

```bash
mvn test
```

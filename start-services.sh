#!/bin/bash

echo "Starting Koerber Microservices..."
echo ""

# Check if we're in the correct directory
if [ ! -d "InventoryService" ]; then
    echo "Error: InventoryService directory not found."
    echo "Please run this script from the root of the KoerberAssignment project."
    exit 1
fi

if [ ! -d "OrderService" ]; then
    echo "Error: OrderService directory not found."
    echo "Please run this script from the root of the KoerberAssignment project."
    exit 1
fi

echo "Starting Inventory Service on port 8081..."
cd InventoryService
./mvnw spring-boot:run &
INVENTORY_PID=$!

echo "Waiting for Inventory Service to start..."
# Check if service is ready by polling the health endpoint
while ! curl -s http://localhost:8081/actuator/health > /dev/null 2>&1; do
    echo "Waiting for Inventory Service to be ready..."
    sleep 2
done

echo "Inventory Service is ready! Starting Order Service on port 8082..."
cd ../OrderService
./mvnw spring-boot:run &
ORDER_PID=$!

cd ..

echo ""
echo "Both services are starting..."
echo ""
echo "Inventory Service: http://localhost:8081/swagger-ui"
echo "Order Service: http://localhost:8082/swagger-ui"
echo ""
echo "Process IDs: Inventory=$INVENTORY_PID, Order=$ORDER_PID"
echo "To stop services, run: kill $INVENTORY_PID $ORDER_PID"
echo ""

wait

@echo off
setlocal enabledelayedexpansion

echo Starting Koerber Microservices...
echo.

REM Check if we're in the correct directory
if not exist "InventoryService" (
    echo Error: InventoryService directory not found.
    echo Please run this script from the root of the KoerberAssignment project.
    pause
    exit /b 1
)

if not exist "OrderService" (
    echo Error: OrderService directory not found.
    echo Please run this script from the root of the KoerberAssignment project.
    pause
    exit /b 1
)

echo Starting Inventory Service on port 8081...
start "Inventory Service" cmd /k "cd /d InventoryService && mvnw.cmd spring-boot:run"

echo Waiting 5 seconds before starting Order Service...
timeout /t 5 /nobreak

echo Starting Order Service on port 8082...
start "Order Service" cmd /k "cd /d OrderService && mvnw.cmd spring-boot:run"

echo.
echo Both services are starting...
echo.
echo Inventory Service: http://localhost:8081/swagger-ui
echo Order Service: http://localhost:8082/swagger-ui
echo.
echo Press any key to continue or close this window.
pause

#!/bin/bash

# 색상 정의
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

BASE_PATH="/Users/dosang-won/Documents/msa-물류/PathFinder"

echo -e "${BLUE}=====================================${NC}"
echo -e "${BLUE}   MSA Services Build & Start${NC}"
echo -e "${BLUE}=====================================${NC}"

# 빌드 함수
build_service() {
    local service_name=$1
    local service_path=$2

    echo -e "\n${YELLOW}📦 Building $service_name...${NC}"

    # 경로 존재 확인
    if [ ! -d "$service_path" ]; then
        echo -e "${RED}❌ Service path not found: $service_path${NC}"
        return 1
    fi

    cd "$service_path"

    # gradlew 존재 및 실행 권한 확인
    if [ ! -x "./gradlew" ]; then
        echo -e "${RED}❌ gradlew not found or not executable in $service_path${NC}"
        return 1
    fi

    ./gradlew clean build -x test

    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✅ $service_name build success${NC}"
        return 0
    else
        echo -e "${RED}❌ $service_name build failed${NC}"
        return 1
    fi
}

# 서비스 실행 함수
start_service() {
    local service_name=$1
    local service_path=$2
    local port=$3
    local wait_time=$4
    
    echo -e "\n${YELLOW}🚀 Starting $service_name on port $port...${NC}"
    cd "$service_path"
    ./gradlew bootRun > "/tmp/${service_name}.log" 2>&1 &
    local pid=$!
    
    echo "Waiting ${wait_time}s for $service_name to start..."
    sleep $wait_time
    
    # Health check
    if curl -s "http://localhost:${port}/actuator/health" > /dev/null 2>&1; then
        echo -e "${GREEN}✅ $service_name is UP (PID: $pid)${NC}"
        echo "$pid" > "/tmp/${service_name}.pid"
        return 0
    else
        echo -e "${YELLOW}⚠️  $service_name started but health check pending (PID: $pid)${NC}"
        echo "$pid" > "/tmp/${service_name}.pid"
        return 0
    fi
}

# Health check 함수
check_health() {
    local service_name=$1
    local port=$2
    local max_attempts=30
    local attempt=0
    
    echo -e "${YELLOW}🔍 Checking $service_name health...${NC}"
    
    while [ $attempt -lt $max_attempts ]; do
        if curl -s "http://localhost:${port}/actuator/health" | grep -q "UP"; then
            echo -e "${GREEN}✅ $service_name is healthy${NC}"
            return 0
        fi
        attempt=$((attempt + 1))
        echo -n "."
        sleep 2
    done
    
    echo -e "\n${RED}❌ $service_name health check timeout${NC}"
    return 1
}

# 정리 함수
cleanup() {
    echo -e "\n${RED}🛑 Stopping all services...${NC}"
    
    for pidfile in /tmp/*.pid; do
        if [ -f "$pidfile" ]; then
            pid=$(cat "$pidfile")
            service_name=$(basename "$pidfile" .pid)
            echo "Stopping $service_name (PID: $pid)"
            kill $pid 2>/dev/null
            rm "$pidfile"
        fi
    done
    
    exit 0
}

# Ctrl+C 핸들러
trap cleanup SIGINT SIGTERM

echo -e "\n${BLUE}================================================${NC}"
echo -e "${BLUE}   STEP 1: Building All Services${NC}"
echo -e "${BLUE}================================================${NC}"

# 모든 서비스 빌드
build_service "Config Server" "$BASE_PATH/config" || exit 1
build_service "Eureka Server" "$BASE_PATH/eureka" || exit 1
build_service "Gateway Service" "$BASE_PATH/gateway" || exit 1
build_service "User Service" "$BASE_PATH/user" || exit 1
build_service "Hub Service" "$BASE_PATH/com.hub-service" || exit 1
build_service "Company Service" "$BASE_PATH/company" || exit 1
build_service "Product Service" "$BASE_PATH/product" || exit 1
build_service "Order Service" "$BASE_PATH/order" || exit 1
build_service "Delivery Service" "$BASE_PATH/delivery" || exit 1
build_service "Delivery Manager Service" "$BASE_PATH/delivery-manager" || exit 1

echo -e "\n${GREEN}✅ All services built successfully!${NC}"

echo -e "\n${BLUE}================================================${NC}"
echo -e "${BLUE}   STEP 2: Starting Services${NC}"
echo -e "${BLUE}================================================${NC}"

# 1. Config Server 시작
start_service "config-server" "$BASE_PATH/config" "19000" "15"
check_health "Config Server" "19000"

# Config Server 설정 확인
echo -e "\n${YELLOW}🔍 Verifying Config Server...${NC}"
if curl -s "http://localhost:19000/application/dev" > /dev/null; then
    echo -e "${GREEN}✅ Config Server serving configurations${NC}"
else
    echo -e "${RED}❌ Config Server not serving configurations${NC}"
    cleanup
fi

# 2. Eureka Server 시작
start_service "eureka-server" "$BASE_PATH/eureka" "19100" "15"
check_health "Eureka Server" "19100"

# 3. Gateway 시작
start_service "gateway-service" "$BASE_PATH/gateway" "19200" "15"
check_health "Gateway Service" "19200"

# 4. 비즈니스 서비스들 시작 (도메인 순서로)
start_service "user-service" "$BASE_PATH/user" "8081" "10"
start_service "hub-service" "$BASE_PATH/com.hub-service" "8084" "10"
start_service "company-service" "$BASE_PATH/company" "8086" "10"
start_service "product-service" "$BASE_PATH/product" "8085" "10"
start_service "order-service" "$BASE_PATH/order" "8087" "10"
start_service "delivery-service" "$BASE_PATH/delivery" "8082" "10"
start_service "delivery-manager-service" "$BASE_PATH/delivery-manager" "8083" "10"

echo -e "\n${BLUE}================================================${NC}"
echo -e "${BLUE}   STEP 3: Service Status${NC}"
echo -e "${BLUE}================================================${NC}"

# 최종 상태 확인
echo -e "\n${GREEN}🎉 All services started!${NC}\n"

echo -e "${BLUE}Service Status:${NC}"
echo -e "  Config Server:        http://localhost:19000"
echo -e "  Eureka Dashboard:     http://localhost:19100"
echo -e "  Gateway:              http://localhost:19200"
echo -e "  Swagger UI:           http://localhost:19200/docs"
echo -e "  User Service:         http://localhost:8081"
echo -e "  Delivery Service:     http://localhost:8082"
echo -e "  Delivery Manager:     http://localhost:8083"
echo -e "  Hub Service:          http://localhost:8084"
echo -e "  Product Service:      http://localhost:8085"
echo -e "  Company Service:      http://localhost:8086"
echo -e "  Order Service:        http://localhost:8087"

echo -e "\n${YELLOW}📋 Log files:${NC}"
for logfile in /tmp/*-service*.log /tmp/*-server.log; do
    if [ -f "$logfile" ]; then
        echo "  $(basename "$logfile")"
    fi
done

echo -e "\n${YELLOW}💡 Tips:${NC}"
echo -e "  - View logs: tail -f /tmp/<service-name>.log"
echo -e "  - Stop all: Press Ctrl+C"
echo -e "  - Eureka Dashboard: Check registered services"
echo -e "  - Gateway Routes: curl http://localhost:19200/actuator/gateway/routes"

echo -e "\n${GREEN}Press Ctrl+C to stop all services${NC}\n"

# 무한 대기 (Ctrl+C로 종료)
while true; do
    sleep 1
done


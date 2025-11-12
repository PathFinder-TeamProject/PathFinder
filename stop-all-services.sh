#!/bin/bash

# 색상 정의
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}🛑 Stopping all MSA services...${NC}\n"

# PID 파일로 서비스 종료
stopped_count=0
for pidfile in /tmp/*.pid; do
    if [ -f "$pidfile" ]; then
        pid=$(cat "$pidfile")
        service_name=$(basename "$pidfile" .pid)
        
        if ps -p $pid > /dev/null 2>&1; then
            echo -e "Stopping ${GREEN}$service_name${NC} (PID: $pid)"
            kill $pid 2>/dev/null
            sleep 2
            
            # 강제 종료가 필요한 경우
            if ps -p $pid > /dev/null 2>&1; then
                echo -e "  Force killing $service_name"
                kill -9 $pid 2>/dev/null
            fi
            
            stopped_count=$((stopped_count + 1))
        else
            echo -e "${YELLOW}$service_name${NC} is not running"
        fi
        
        rm "$pidfile"
    fi
done

# 포트로 프로세스 찾아서 종료
echo -e "\n${YELLOW}Checking for services on known ports...${NC}"
ports=(19000 19100 19200 8081 8082 8083 8084 8085 8086 8087)

for port in "${ports[@]}"; do
    pid=$(lsof -ti:$port 2>/dev/null)
    if [ -n "$pid" ]; then
        echo -e "Stopping service on port ${GREEN}$port${NC} (PID: $pid)"
        kill $pid 2>/dev/null
        sleep 1
        
        # 강제 종료가 필요한 경우
        if ps -p $pid > /dev/null 2>&1; then
            kill -9 $pid 2>/dev/null
        fi
        stopped_count=$((stopped_count + 1))
    fi
done

# 로그 파일 정리 옵션
echo -e "\n${YELLOW}Clean up log files? (y/n)${NC}"
read -t 5 -n 1 cleanup_logs
echo

if [ "$cleanup_logs" = "y" ] || [ "$cleanup_logs" = "Y" ]; then
    rm -f /tmp/*-service*.log /tmp/*-server.log
    echo -e "${GREEN}✅ Log files cleaned${NC}"
fi

if [ $stopped_count -gt 0 ]; then
    echo -e "\n${GREEN}✅ Stopped $stopped_count service(s)${NC}"
else
    echo -e "\n${YELLOW}No services were running${NC}"
fi


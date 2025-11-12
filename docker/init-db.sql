-- MSA Database per Service 패턴
-- 각 마이크로서비스별 독립된 데이터베이스 생성

-- User Service Database
CREATE DATABASE user_db;

-- Delivery Service Database
CREATE DATABASE delivery_db;

-- Hub Service Database
CREATE DATABASE hub_db;

-- Order Service Database
CREATE DATABASE order_db;

-- Delivery Manager Service Database
CREATE DATABASE delivery_manager_db;

-- Product Service Database
CREATE DATABASE product_db;

-- Company Service Database
CREATE DATABASE company_db;

-- 권한 부여 (필요시)
GRANT ALL PRIVILEGES ON DATABASE user_db TO pathfinder;
GRANT ALL PRIVILEGES ON DATABASE delivery_db TO pathfinder;
GRANT ALL PRIVILEGES ON DATABASE hub_db TO pathfinder;
GRANT ALL PRIVILEGES ON DATABASE order_db TO pathfinder;
GRANT ALL PRIVILEGES ON DATABASE delivery_manager_db TO pathfinder;
GRANT ALL PRIVILEGES ON DATABASE product_db TO pathfinder;
GRANT ALL PRIVILEGES ON DATABASE company_db TO pathfinder;


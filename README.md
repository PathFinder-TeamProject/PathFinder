# PathFinder
<div>
  <h1>🚚 물류 배송 관리 플랫폼 PathFinder</h1>
  <h3>MSA 기반 전국 물류 허브 배송 관리 시스템</h3>
</div>

<br>

## 목차
- [1. 프로젝트 개요](#1-프로젝트-개요)
- [2. 프로젝트 관리](#2-프로젝트-관리)
- [3. 프로젝트 구상도](#3-프로젝트-구상도)
- [4. 기능 구현](#4-기능-구현)
- [5. 기술 문서](#5-기술-문서)
- [6. 트러블슈팅](#6-트러블슈팅)

<br>

---

## 1. 프로젝트 개요
전국 17개 물류 허브를 기반으로 한 **B2B 물류 관리 및 배송 플랫폼**으로,  
허브 간 배송 효율화와 경로 최적화를 목표로 설계된 **MSA(Microservices Architecture)** 기반 시스템입니다.

### ✨ 주요 기능
- MSA 구조 기반의 **독립적 서비스 운영**
- **JWT 인증** 기반 사용자 권한 관리
- **P2P + Hub-to-Hub Relay** 방식의 효율적인 배송 경로 관리
- **Slack API**를 통한 주문/배송 실시간 알림
- **논리적 삭제(Soft Delete)** 및 **Audit 필드 관리**
- **FeignClient**를 통한 마이크로서비스 간 통신

### 🛠️ 개발 환경
![Java](https://img.shields.io/badge/Java-17-007396?logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.2-6DB33F?logo=springboot&logoColor=white)
![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-6DB33F?logo=spring&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring%20Security-6DB33F?logo=springsecurity&logoColor=white)
![JPA](https://img.shields.io/badge/Spring%20Data%20JPA-6DB33F?logo=spring&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-02303A?logo=gradle&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring%20Security-6DB33F?logo=springsecurity\&logoColor=white)
![Spring Data JPA](https://img.shields.io/badge/Spring%20Data%20JPA-6DB33F?logo=spring\&logoColor=white)
![QueryDSL](https://img.shields.io/badge/QueryDSL-0769AD?logo=java\&logoColor=white)
![Slack API](https://img.shields.io/badge/Slack%20API-4A154B?logo=slack\&logoColor=white)

![Spring Cloud Gateway](https://img.shields.io/badge/Spring%20Cloud%20Gateway-0288D1?logo=spring\&logoColor=white)
![Spring Cloud Config](https://img.shields.io/badge/Spring%20Cloud%20Config-43A047?logo=spring\&logoColor=white)
![Eureka](https://img.shields.io/badge/Eureka%20Server-FF6B6B?logo=spring\&logoColor=white)
![FeignClient](https://img.shields.io/badge/FeignClient-F5A623?logo=spring\&logoColor=white)

![Docker](https://img.shields.io/badge/Docker-2496ED?logo=docker&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?logo=postgresql&logoColor=white)
![Swagger](https://img.shields.io/badge/Swagger-85EA2D?logo=swagger&logoColor=black)
![Eureka](https://img.shields.io/badge/Eureka%20Server-FF6B6B?logo=spring&logoColor=white)
![GitHub](https://img.shields.io/badge/GitHub-181717?logo=github&logoColor=white)
![IntelliJ IDEA](https://img.shields.io/badge/IntelliJ%20IDEA-000000?logo=intellijidea&logoColor=white)

### 👥 팀 구성
| 이름  | 담당 서비스                               |                                                                       Github                                                                       |
|-----|--------------------------------------|--------------|
| 김민식 | 허브(Hub), <br> 허브 간 이동 경로(HubRoute)   | <a href="https://github.com/minsik0"><img src="https://img.shields.io/badge/Github-181717?style=for-the-badge&logo=Github&logoColor=white"></a> |
| 도상원 | 배송(Delivery)                         | <a href="github.com/dark2138"><img src="https://img.shields.io/badge/Github-181717?style=for-the-badge&logo=Github&logoColor=white"></a> |
| 박래현 | Company, Product                     | <a href="https://github.com/Raebagi"><img src="https://img.shields.io/badge/Github-181717?style=for-the-badge&logo=Github&logoColor=white"></a> |
| 오상경 | 사용자(User),<br>배송담당자(DeliveryManager) | <a href="https://github.com/osk0521"><img src="https://img.shields.io/badge/Github-181717?style=for-the-badge&logo=Github&logoColor=white"></a> |
| 한규원 | 주문(Order), 메시지(Message)              | <a href="https://github.com/akaneblue"><img src="https://img.shields.io/badge/Github-181717?style=for-the-badge&logo=Github&logoColor=white"></a> |

---

## 2. 프로젝트 관리

### 📅 개발 기간
2025.09.29 ~ 2025.10.31

<details>
<summary><strong>협업 라이프사이클</strong></summary>

1. 브랜치 생성
2. 코드 작성
3. PR 생성
4. 리뷰 요청
5. `dev` → `main` Merge
</details>

<details>
<summary><strong>컨벤션</strong></summary>


- **Branch**
    - **전략**

      | Branch Type | Description |
            |-------------|-------------|
      | `dev`       | 주요 개발 branch, `main`으로 merge 전 거치는 branch |
      | `feature`   | 각자 개발할 branch, 기능 단위로 생성하기, 할 일 issue 등록 후 branch 생성 및 작업 |

    - **네이밍**
        - `{header}/#{issue number}`
        - 예) `feat/#1`

- **커밋 메시지 규칙**
    ```bash
    > type: 기능 요약 (: 뒤 한 칸 뛰고)

      - chore: 내부 파일 수정
      - feat: 새로운 기능 구현
      - add: feat 이외의 부수적인 코드 추가, 라이브러리 추가, 새로운 파일 생성 시
      - fix: 코드 수정, 버그, 오류 해결
      - del: 쓸모없는 코드 삭제
      - docs: README나 WIKI 등의 문서 개정
      - move: 프로젝트 내 파일이나 코드의 이동
      - rename: 파일 이름의 변경
      - merge: 다른 브랜치를 merge하는 경우
      - style: 코드가 아닌 스타일 변경을 하는 경우
      - init: Initial commit을 하는 경우
      - refactor: 로직은 변경 없는 클린 코드를 위한 코드 수정
      
      ex) feat: 게시글 목록 조회 API 구현
    ```

- **Issue**
    ```bash
    📱 Description
    <!-- 진행할 작업을 설명해주세요 -->
    
    📱 To-do
    <!-- 작업을 수행하기 위해 해야할 태스크를 작성해주세요 -->
    [ ] todo1
    
    📱 ETC
    <!-- 특이사항 및 예정 개발 일정을 작성해주세요 -->
    ```

- **PR**
    - **규칙**
        - 기능 branch 작업 완료 후 PR 보내기
        - PR 후 슬랙에 공유하기
        - 최소 1명 이상의 동의를 받으면 merge
        - review 반영 후, 본인이 merge

    - **Template**
      ```bash
      📒 Issue Number
      <!-- 작업한 이슈 번호를 명시해주세요 -->
      
      📒 Description
      <!-- 작업 내용에 대한 설명을 적어주세요 -->
      
      📒 Test Result
      <!-- local에서 postman으로 요청한 결과를 첨부합니다 -->
      
      📒 To Reviewer
      <!-- 리뷰 받고 싶은 포인트를 작성합니다 -->
      
      ```
</details>

<br>

## 3. 프로젝트 구상도
 이미지 삽입 예정
### 🛠️ 아키텍처
<p align="center">
  <img src="images/아키택쳐.png" width="1200" alt="아키택쳐.png">
</p>
### 🔗 ERD

ERD 이미지 삽입 예정

<p align="center">
  <img src="images/erd.png" width="1200" alt="erd.png">
</p>


## 4. 기능 구현
✨ 주요 서비스 구성

| 서비스                  | 역할                                  |
|----------------------| ----------------------------------- |
| **Gateway Service**  | 클라이언트 요청 라우팅 및 필터링                  |
| **Eureka Server**    | 서비스 디스커버리 및 등록 관리                   |
| **User Service**     | 회원가입, 로그인, 권한 승인 (JWT 기반)           |
| **Hub Service**      | 전국 허브 CRUD 및 캐싱 관리                  |
| **Hub Route Service** | 허브 간 경로 관리 (P2P + Hub-to-Hub Relay) |
| **Order Service**    | 주문 생성/취소, 배송 자동 생성 연동               |
| **Delivery Service** | 배송 상태 추적 및 경로 이력 관리                 |
| **Company Service**  | 업체 정보 및 허브 소속 관리                    |
| **Product Service**  | 상품 정보 CRUD                          |
| **Slack Service**    | 주문·배송 이벤트 실시간 알림 전송                 |

🚚 허브 간 이동 방식
PathFinder는 P2P + Hub-to-Hub Relay 알고리즘을 적용합니다.
<li>200km 이하: 인접 허브 간 P2P 직접 배송</li>
<li>200km 초과: 중간 허브를 통한 Relay 배송 경로 계산</li>
<li>허브 간 최단 거리 계산 및 경유 허브 자동 결정</li>
<li>Redis 캐싱을 통한 경로 조회 성능 최적화</li>

🗂️ 주요 도메인 설계

| 도메인              | 설명                                  |
|----------------------| ----------------------------------- |
| **Hub**          | 허브명, 주소, 좌표 정보                  |
| **Route**        | 허브 간 경로 및 거리                  |
| **User**         | 권한(Role) 기반 접근 제어                  |
| **Company**      | 업체 등록 및 소속 허브 관리                  |
| **Product**      | 상품 및 재고 관리                  
| **Order**        | 주문 요청, 상태 변경, 배송 연동                  |
| **Delivery**     | 배송 추적, 진행 단계 관리                  |
| **SlackMessage** | 알림 메시지 로그 관리                  |

🔐 사용자 권한 구조

| 역할                  | 설명                                  |
|----------------------| ----------------------------------- |
| **MASTER**     | 전체 관리 권한                  |
| **HUB_MANAGER**      | 허브 및 배송 관리                  |
| **DELIVERY_MANAGER** | 자신의 배송만 접근 가능                  |
| **COMPANY_MANAGER**  | 업체별 상품 및 주문 관리                  |

## 5. 기술 문서

### 📄 Swagger
<details>
    <summary><strong>Swagger</strong></summary>
    <img width="1306" height="925" alt="Pathfinder swagger" src="https://github.com/user-attachments/assets/f4a9610a-b826-4a65-97e3-69a9e77f5e58" />
</details>

### 🔗 디렉토리 구조
<details>
    <summary><strong>디렉토리 구조</strong></summary>

```
    com.pathfinder
    ├─ gateway-service
    │  ├─ config
    │  │   ├─ GatewayConfig.java
    │  │   ├─ RedisConfig.java
    │  │   └─ SecurityConfig.java
    │  ├─ security
    │  │   ├─ JwtAuthenticationFilter.java
    │  │   └─ JwtUtil.java
    │  ├─ service
    │  │   └─ UserCacheService.java
    │  └─ dto
    │      └─ UserResponseDto.java
    
    ├─ eureka-server
    │  └─ EurekaApplication.java
    
    ├─ config-server
    │  ├─ ConfigApplication.java
    │  └─ resources/config-repository/
    │      ├─ user-service-dev.yml
    │      ├─ order-service-dev.yml
    │      ├─ delivery-service-dev.yml
    │      ├─ hub-service-dev.yml
    │      ├─ message-service-dev.yml
    │      └─ gateway-service-dev.yml
    
    ├─ user-service
    │  ├─ application
    │  │   ├─ UserServiceV1.java
    │  │   ├─ UserDetailsServiceImpl.java
    │  │   ├─ dto/request/
    │  │   ├─ exception/
    │  │   └─ response/
    │  ├─ domain
    │  │   ├─ entity/
    │  │   ├─ enums/
    │  │   └─ repository/
    │  ├─ infrastructure
    │  │   ├─ client/
    │  │   ├─ config/
    │  │   │   ├─ redis/
    │  │   │   └─ security/
    │  │   └─ repository/
    │  ├─ jwt/
    │  └─ presentation
    │      ├─ controller/
    │      └─ dto/response/
    
    ├─ delivery-manager-service
    │  ├─ application/
    │  │   ├─ DeliveryManagerServiceV1.java
    │  │   ├─ DeliveryManagerInternalServiceV1.java
    │  │   └─ exception/
    │  ├─ domain/
    │  │   ├─ entity/
    │  │   ├─ enums/
    │  │   └─ repository/
    │  ├─ infrastructure/
    │  │   ├─ cache/
    │  │   ├─ client/
    │  │   ├─ config/
    │  │   │   └─ security/
    │  │   └─ repository/
    │  └─ presentation/
    │      ├─ controller/
    │      └─ dto/response/
    
    ├─ hub-service
    │  ├─ application/
    │  │   ├─ HubServiceV1.java
    │  │   ├─ HubRouteService.java
    │  │   └─ HubManagerService.java
    │  ├─ domain/
    │  │   ├─ model/
    │  │   ├─ enums/
    │  │   └─ repository/
    │  ├─ infrastructure/
    │  │   ├─ client/
    │  │   ├─ config/
    │  │   └─ cache/
    │  └─ presentation/
    │      ├─ controller/
    │      ├─ advice/
    │      └─ dto/
    
    ├─ delivery-service
    │  ├─ application/
    │  │   ├─ command/
    │  │   │   └─ service/
    │  │   ├─ query/
    │  │   │   └─ service/
    │  │   ├─ outbox/
    │  │   ├─ dto/
    │  │   └─ exception/
    │  ├─ domain/
    │  │   ├─ entity/
    │  │   ├─ enums/
    │  │   ├─ repository/
    │  │   ├─ service/
    │  │   └─ value/
    │  ├─ infrastructure/
    │  │   ├─ external/
    │  │   │   ├─ client/
    │  │   │   ├─ fallback/
    │  │   │   ├─ dto/
    │  │   │   └─ security/
    │  │   ├─ config/
    │  │   ├─ messaging/
    │  │   └─ repository/
    │  └─ presentation/
    │      └─ controller/
    
    ├─ order-service
    │  ├─ application/
    │  │   ├─ OrderServiceV1.java
    │  │   ├─ dto/
    │  │   └─ exception/
    │  ├─ domain/
    │  │   ├─ entity/
    │  │   ├─ enums/
    │  │   └─ repository/
    │  ├─ infrastructure/
    │  │   ├─ global/
    │  │   │   ├─ client/
    │  │   │   ├─ fallback/
    │  │   │   ├─ dto/
    │  │   │   └─ security/
    │  │   ├─ config/
    │  │   └─ repository/
    │  └─ presentation/
    │      ├─ controller/
    │      └─ dto/
    
    ├─ product-service
    │  ├─ application/
    │  │   ├─ ProductService.java
    │  │   ├─ dto/
    │  │   └─ exception/
    │  ├─ domain/
    │  │   └─ entity/
    │  ├─ infrastructure/
    │  │   ├─ global/
    │  │   │   ├─ client/
    │  │   │   ├─ fallback/
    │  │   │   ├─ dto/
    │  │   │   └─ security/
    │  │   ├─ config/
    │  │   └─ repository/
    │  └─ presentation/
    │      ├─ controller/
    │      └─ dto/response/
    
    ├─ company-service
    │  ├─ application/
    │  │   ├─ CompanyService.java
    │  │   ├─ dto/
    │  │   └─ exception/
    │  ├─ domain/
    │  │   └─ entity/
    │  ├─ infrastructure/
    │  │   ├─ config/
    │  │   └─ repository/
    │  └─ presentation/
    │      ├─ controller/
    │      └─ dto/response/
    
    ├─ message-service
    │  ├─ application/
    │  │   └─ MessageService.java
    │  ├─ domain/
    │  │   ├─ entity/
    │  │   ├─ enums/
    │  │   └─ repository/
    │  ├─ infrastructure/
    │  │   ├─ config/
    │  │   ├─ global/
    │  │   │   └─ security/
    │  │   └─ repository/
    │  └─ presentation/
    │      ├─ controller/
    │      └─ dto/
    
    └─ docker/
       ├─ docker-compose.yml
       ├─ docker-compose.infrastructure.yml
       └─ init-db.sql

```
</details>
---

## 6. 트러블슈팅
순환 의존성 → FeignClient 인터페이스 분리로 해결

허브 거리 계산 오류 → 캐싱 및 ID 매핑 로직 개선

허브 배송 담당자(허브와 허브간의 이동)의 hubId 값 → 중앙 허브 한 곳을 지정하고, 해당 허브의 매니저를 MASTER으로 지정

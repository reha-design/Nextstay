# 🐰 RabbitMQ 중심 비동기 아키텍처 구조도

이 문서는 Nextstay 프로젝트의 ** 최전선(Main API)과 RabbitMQ를 활용한 비동기 처리 흐름 ** 을 시각화한 문서입니다.


## 🏗️ 전체 시스템 구조

```mermaid
graph TD
    %% 사용자 영역
    User([👤 게스트 / 호스트])

    %% 최전선 보호막 (API Gateway & Auth)
    subgraph Frontline ["The Front-line (최전선)"]
        API[🌱 Main API Server]
        Auth[🔑 Security & JWT]
    end

    %% 메시지 브로커 (완충 지대)
    subgraph Messaging ["Messaging Layer (완충 및 배달)"]
        MQ[[🐰 RabbitMQ]]
        DLQ(📥 Dead Letter Queue)
    end

    %% 백그라운드 처리 영역
    subgraph Workers ["Background Workers (전문 작업자)"]
        Consumer[👷 Reservation Consumer]
        GoWorker[⚙️ Notification Worker]
        Analytics[📊 Analytics Service]
    end

    %% 데이터 저장소
    DB[(🗄️ Main Database)]

    %% 흐름 연결
    User -- "1. HTTP Req (예약/결제)" --> API
    API --- Auth
    API -- "2. ACID 트랜잭션 저장" --> DB
    
    %% RabbitMQ 발행
    API -- "3. Event Publish (비동기)" --> MQ
    
    %% RabbitMQ 구독 및 소비
    MQ -- "4a. Consume" --> Consumer
    MQ -- "4b. Consume" --> GoWorker
    MQ -. "4c. Error Redirection" .-> DLQ
    
    %% 후속 작업
    Consumer -- "5. 상태 업데이트" --> DB
    GoWorker -- "6. 외부 연동" --> SMS["📱 알림톡 / 푸시"]
    API -. "7. 통계 데이터 전송 (gRPC)" .-> Analytics

    %% 스타일링
    classDef frontline fill:#e1f5fe,stroke:#01579b,stroke-width:2px;
    classDef broker fill:#fff3e0,stroke:#e65100,stroke-width:2px;
    classDef worker fill:#f1f8e9,stroke:#33691e,stroke-width:2px;
    classDef storage fill:#eceff1,stroke:#455a64,stroke-width:2px;
    
    class API,Auth frontline;
    class MQ,DLQ broker;
    class Consumer,GoWorker,Analytics worker;
    class DB storage;
```

---

## 🔍 기술적 핵심 개념

### 1. 최전선 (The Front-line)
- **역할**: 모든 사용자 요청의 진입점입니다.
- **특징**: 보안 검증(JWT) 및 핵심 트랜잭션(DB 저장)만 즉시 처리하고, 시간이 오래 걸리는 작업은 RabbitMQ로 위임하여 **사용자 응답 속도를 극대화**합니다.

### 2. 완충 지대 (Messaging Layer)
- **RabbitMQ**: 시스템 간의 결합도를 낮추는 버퍼 역할을 합니다. 트래픽이 몰려도 메시지를 안전하게 보관하여 후속 워커들이 순차적으로 처리하게 돕습니다.
- **DLQ (Dead Letter Queue)**: 처리에 실패한 메시지를 별도로 격리하여 데이터 유실 없이 사후 분석 및 재처리가 가능하게 합니다.

### 3. 전문 작업자 (Background Workers)
- **분업화**: 각 작업의 성격에 맞는 기술 스택(Kotlin, Golang, Bun)을 사용하여 시스템 전체의 효율성과 확장성(Scalability)을 높였습니다.

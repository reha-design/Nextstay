# 📍 금일 작업 분야별 커밋 계획

오늘 진행한 아키텍처 설계, 분석 서비스 연동, 그리고 비동기 처리 고도화 내역을 아래와 같이 분야별로 나누어 커밋할 계획입니다.

---

### 1. [kotlin] 비동기 메시징 고도화
*   **주요 내용**: RabbitMQ DLQ/DLX 설정 추가 및 예약 이벤트 컨슈머 로직 강화.
*   **대상 파일**: 
    *   `backend/src/main/kotlin/com/mrmention/nextstay/global/config/RabbitMqConfig.kt`
    *   `backend/src/main/kotlin/com/mrmention/nextstay/domain/booking/consumer/ReservationEventConsumer.kt`

### 2. [Elysia] 분석 서비스 영속성 및 보안 강화
*   **주요 내용**: SQLite WAL 모드 도입, CORS 설정, Rate Limit(도배 방지) 추가 및 로깅 API 구현.
*   **대상 파일**:
    *   `backend-analytics/src/index.ts`
    *   `backend-analytics/package.json`
    *   `backend-analytics/bun.lock`

### 3. [Nuxt] 프론트엔드 분석 연동 및 타입 안전성 확보
*   **주요 내용**: `useAnalytics` 컴포저블, 자동 추적 플러그인, `bun-types` 설정 및 Eden Treaty 연동.
*   **대상 파일**:
    *   `frontend-guest/composables/useAnalytics.ts`
    *   `frontend-guest/plugins/analytics.client.ts`
    *   `frontend-guest/nuxt.config.ts`
    *   `frontend-guest/tsconfig.json`
    *   `frontend-guest/package.json`
    *   `frontend-guest/bun.lock`

### 4. [infra] 서비스 가용성 및 기동 자동화
*   **주요 내용**: 분석 서비스 전용 스타터 스크립트(`run_analytics.py`) 추가.
*   **대상 파일**:
    *   `python/run_analytics.py`

### 5. [docs] 시스템 설계 및 테스트 가이드 문서화
*   **주요 내용**: RabbitMQ 아키텍처 수정, SSR 캐싱 전략, 분석 테스트 시나리오 작성.
*   **대상 파일**:
    *   `docs/flow/architecture_rabbitmq.md`
    *   `docs/decisions/03_ssr_caching_strategy.md`
    *   `docs/test/analytics_test_scenario.md`

---

**💡 위 계획대로 커밋을 진행해도 괜찮을까요? 확인해 주시면 바로 실행 후 오늘 작업을 마무리하겠습니다!**

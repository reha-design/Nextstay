# 🛠️ 트러블슈팅 및 아키텍처 결정 로그 (Troubleshooting & Decisions)

이 문서는 Nextstay 개발 과정에서 마주한 핵심 기술적 도전 과제와 이를 해결하기 위한 아키텍처적 의사결정 과정을 기록합니다. 단순한 코드 수정을 넘어, **AI 에이전트를 전략적으로 지휘(Directing)**하여 도출한 최적화 사례들입니다.

---

## 1. 프론트엔드 상태 불일치 (JWT Dynamic Update)

### 🔴 문제 상황
- **현상**: 호스트 온보딩 완료 후 DB 상태는 `COMPLETED`로 변경되지만, 프론트엔드의 JWT 토큰 내 `onboardingStatus`는 이전 상태인 `PENDING`을 유지함.
- **영향**: 유저가 수동으로 로그아웃 후 재로그인하기 전까지 온보딩 완료 대시보드로 진입할 수 없는 UX 결함 발생.

### 🛡️ 해결 전략 및 의사결정
- **기존 방식**: 로그아웃 강제 유도 (비효율적)
- **개선 방식**: **온보딩 완료 API 성공 시 새로운 Access Token 즉시 발급**.
- **구체적 조치**:
    - `AuthService`에서 상태 변경 후 새로운 Claims를 포함한 JWT를 생성하여 응답에 포함.
    - 프론트엔드(`onboarding.vue`)에서 API 응답으로 받은 새 토큰을 스토어에 즉시 업데이트.
- **결과**: 페이지 새로고침 없이 호스트 대시보드로 즉시 전환되는 매끄러운 UX 구현.

## 2. 비동기 환경에서의 블로킹 I/O (Coroutine Context)

### 🔴 문제 상황
- **현상**: 비동기 워커(RabbitMQ Consumer)와 메시지 발행(Publisher) 로직에서 전통적인 `Thread.sleep()`이 사용되어, 비동기 엔진의 이점을 살리지 못하고 스레드가 점유됨.
- **영향**: 고부하 상황에서 스레드 고갈(Thread Starvation) 및 응답 지연 발생 가능성.

### 🛡️ 해결 전략 및 의사결정
- **의사결정**: **Pure Suspend-based Non-blocking Flow**로 전환.
- **구체적 조치**:
    - `ReservationEventConsumer`: `Thread.sleep()`을 `suspend delay()`로 교체하여 이벤트 루프 스레드를 자유롭게 함.
    - `BookingService`: 메시지 발행 로직을 `CoroutineScope(Dispatchers.IO).launch`로 감싸 데이터베이스 트랜잭션을 차단하지 않고 배경에서 처리하도록 분리.
- **결과**: 이벤트 기반 핸들러가 온전히 작동하는 고성능 비동기 파이프라인 완성.

## 3. JPA 데이터 조회 성능 최적화 (N+1 정복)

### 🔴 문제 상황
- **현상**: 숙소 목록 및 상세 조회 시 연관된 `Host`, `DiscountPolicies`, `SeasonPrices`를 조회하기 위해 숙소 개수(N)만큼 추가 쿼리가 발생하는 **1+3N 문제** 확인.
- **영향**: 숙소 리스트가 10개인 경우 총 31번의 SQL 쿼리가 발생하여 DB 부하 폭증.

### 🛡️ 해결 전략 및 의사결정
- **의사결정**: **Fetch Join(ManyToOne) + Batch Size(OneToMany) 하이브리드 전략**.
- **구체적 조치**:
    - `StayRepository`: `@EntityGraph(attributePaths = ["host"])`를 적용하여 1:1 관계는 단 한 번의 JOIN으로 묶음 배송.
    - `application.yml`: `default_batch_fetch_size: 100` 설정을 통해 1:N 관계의 데이터를 `IN` 절로 통합 조회.
- **결과**: 쿼리 발생 횟수를 **31회에서 단 3회로 90% 이상 대폭 절감**.

---

> [!TIP]
> **AI-Native Insight**: 위 문제들은 제가 AI 에이전트와 페어 프로그래밍을 하며, 에이전트의 초안에서 발생할 수 있는 잠재적 결함(N+1, Blocking I/O 등)을 명확히 식별하고 **"이 부분은 리액티브하게, 저 부분은 배치로 처리해줘"**라고 정교하게 지시하여 얻어낸 결과물입니다.

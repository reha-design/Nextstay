# 🧪 분석 서비스(Analytics) 테스트 시나리오

분석 서비스(Bun + Elysia + SQLite WAL)와 프론트엔드(Nuxt 3) 연동이 정상적으로 작동하는지 확인하기 위한 ** 단계별 테스트 시나리오 ** 입니다.


---

## 🏗️ 사전 준비 (Prerequisites)
1.  **분석 서버 실행**: `backend-analytics` 디렉토리에서 `bun dev` 실행 (4000 포트)
2.  **프론트엔드 실행**: `frontend-guest` 디렉토리에서 `bun dev` 실행 (3000 포트)
3.  **DB 확인**: 루트에 `analytics.sqlite` 파일이 생성되었는지 확인.

---

## 📋 테스트 시나리오 리스트

### 시나리오 1: 서버 헬스 체크 및 DB 초기화
*   **목적**  :
 서버가 정상 가동 중이며 SQLite 테이블이 생성되었는지 확인.
*   **방법**  :

    ```bash
    curl http://localhost:4000/
    ```
*   **기대 결과**  :
 `{"status":"ok", "engine":"Bun + Elysia + SQLite (WAL)"}` 응답 확인.

### 시나리오 2: 직접 API 호출 (데이터 저장 검증)
*   **목적**  :
 API 요청 시 SQLite에 실제 데이터가 정합성 있게 저장되는지 확인.
*   **방법**  :

    ```bash
    curl -X POST http://localhost:4000/stats/v1/visits \
      -H "Content-Type: application/json" \
      -d '{"path": "/test/manual", "userId": "user_999", "userAgent": "Curl-Test"}'
    ```
*   **기대 결과**  :
 `{"success":true, "message":"Visit logged to SQLite (WAL)"}` 응답 확인.

### 시나리오 3: 프론트엔드 자동 페이지 추적 (Client-side)
*   **목적**  :
 사용자가 페이지를 이동할 때 자동으로 로그가 전송되는지 확인.
*   **방법**  :

    1. 브라우저에서 `http://localhost:3000` 접속.
    2. 개발자 도구(F12)의 ** Network 탭 ** 열기.

    3. 메인 -> 로그인 -> 상세 페이지 등으로 메뉴 이동.
*   **기대 결과**  :
 이동할 때마다 `4000`번 포트의 `/stats/v1/visits`로 POST 요청이 가는지 확인.

### 시나리오 4: 타입 안전성(Eden) 검증 (개발 단계)
*   ** 목적 ** :
 잘못된 데이터 형식을 보낼 때 빌드/IDE 레벨에서 차단되는지 확인.
*   ** 방법 ** :

    `useAnalytics.ts`에서 존재하지 않는 필드를 `client.stats.v1.visits.post({ invalidField: 1 })`와 같이 추가해보기.
*   ** 기대 결과 ** :
 IDE에서 즉시 빨간 줄(Type Error)이 발생하고 빌드가 실패해야 함.

### 시나리오 5: 실시간 통계 집계 검증
*   ** 목적 ** :
 수집된 데이터가 통계 API에 즉시 반영되는지 확인.
*   ** 방법 ** :

    ```bash
    curl http://localhost:4000/stats/v1/visits/today
    ```
*   ** 기대 결과 ** :
 `totalVisits` 숫자가 실제 클릭/이동 횟수와 일치하고 `popularPaths`에 가장 많이 들어간 경로가 1위로 나오는지 확인.

---

## 🚨 문제 발생 시 체크리스트
1.  **CORS 에러**: 분석 서버에 CORS 설정이 누락되었는지 확인 (현재 설정 필요 여부 검토).
2.  **SQLite Lock**: WAL 모드가 꺼져있어 쓰기 작업 중 조회가 막히는지 확인.
3.  **포트 충돌**: 4000 포트가 이미 사용 중인지 확인.

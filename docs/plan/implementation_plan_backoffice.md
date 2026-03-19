# 📋 백오피스(B2B/Admin) 인프라 구축 및 API 상세 계획

이 문서는 Nextstay 프로젝트의 사내 관리자(Admin) 및 호스트(Owner)를 위한 백오피스 핵심 도메인 구축을 위한 상세 설계 및 구현 계획을 담고 있습니다.

## 🏗️ 1. 데이터베이스 스키마 설계 (JPA Entity)

새롭게 추가될 핵심 테이블 간의 관계와 속성 정의입니다.

### 🎫 프로모션 및 쿠폰 (Promotion & Coupon) - [Lazy Issue 적용]
- **`PromoRule`**: 쿠폰 발급 규칙 (마스터 데이터)
  - `id`, `name`, `discount_type` (FIXED/RATE), `amount`, `min_order_price`, `start_at`, `end_at`, `max_per_user` (인당 발급 제한)
- **`Coupon`**: 유저에게 발급된 개별 쿠폰 (사용자의 '쿠폰함' 개념)
  - `id`, `promo_rule_id`, `member_id`, `is_used`, `used_at`, `issued_at`
  - **작동 방식**: 모든 유저에게 미리 데이터를 생성(Pre-issue)하지 않고, 유저가 쿠폰을 '다운로드'하거나 '결제 시점'에 조건을 만족할 때 비동기적으로 `Coupon` 레코드를 생성하여 DB 부하를 최소화합니다.
  - **노출 로직**: 유저의 쿠폰함에서는 `is_used = false`인 항목만 보여주며, 사용 완료 처리 시 즉시 목록에서 제외하거나 '사용 완료' 탭으로 이동시킵니다.

### 📝 호스트 특례 신청 (Host Application)
- **`HostApplication`**: 사장님 가입 및 승인 서류 관리
  - `id`, `member_id`, `stay_name`, `business_no`, `status` (PENDING, AUDIT, REJECTED, APPROVED)
  - `document_urls`: S3에 업로드된 증빙 서류 경로 (JSON/String List)

### 💰 정산 관리 (Settlement)
- **`Settlement`**: 월별 매출 및 수수료 정산
  - `id`, `host_id`, `settle_month` (YYYY-MM), `total_amount`, `fee_amount`, `net_amount`, `status` (PENDING, COMPLETED)

### ⚖️ 운영 및 CS (Compliance & CS)
- **`CsTicket`**: 고객 민원 및 분쟁 관리
  - `id`, `booking_id`, `member_id`, `subject`, `content`, `priority_score`, `status` (OPEN, RESOLVED)

---

## 🚀 2. 백엔드 API 구현 (Spring Boot)

관리자 권한(`ROLE_ADMIN`) 및 호스트 권한(`ROLE_HOST`) 기반의 엔드포인트를 구축합니다.

### Phase A: 호스트 온보딩 및 숙소 관리 고도화
- `[POST] /admin/v1/applications`: 특례 신청 서류 접수
- `[GET] /admin/v1/applications/me`: 본인의 신청 상태 조회
- `[PUT] /admin/v1/applications/{id}/status`: [Admin] 신청 건 승인/반려 처리

### Phase B: 정산 및 쿠폰 시스템 구축
- `[POST] /admin/v1/coupons`: 쿠폰 마스터 룰 생성
- `[GET] /admin/v1/hosts/{id}/settlements`: 호스트별 정산 내역 조회

### Phase C: 규제 및 운영 대시보드
- `[GET] /admin/v1/hosts/{id}/compliance`: 180일 규제 준수 여부(숙박 일수) 조회
- `[GET] /admin/v1/tickets`: 민원 티켓 목록 관리

---

## 🛠️ 3. 기술적 세부 사항

1.  **보안 (Security)**: `SecurityConfig`에 `hasRole('ADMIN')` 및 `hasRole('HOST')` 기반의 경로별 인가 로직 추가.
2.  **데이터 정합성**: 정산 및 쿠폰 사용 시 트랜잭션 격리 수준 및 동시성 제어 고려.
3.  **협업 가이드**: 프론트엔드(`frontend-backoffice`)와의 연동을 위해 Swagger(SpringDoc) 문서 자동 생성 유지.

---

## 📅 실천 로드맵

1.  **1단계**: 신규 엔티티 및 Repository 구현 (DB Migration)
2.  **2단계**: 호스트 특례 신청(Phase A) API 완성 및 통합 테스트
3.  **3단계**: 관리자용 쿠폰 및 정산(Phase B) API 연동
4.  **4단계**: 백오피스 프론트엔드(Vue 3) 대시보드 UI 연동

# 🎯 넥스트스테이(Nextstay) API 요구사항 명세서

이 문서는 **Kotlin + Spring Boot (핵심 비즈니스 및 B2B 백오피스)** 와 **Bun + Elysia.js (B2C 부가 기능)** 분리, 그리고 **Golang (비동기 워커)** 기반으로 설계된 초소형 숙박 예약 시스템의 API 엔드포인트 명세서입니다.

---

## 🏗️ 1. 핵심 커머스 로직 (Spring Boot + Kotlin)
숙소 조회, 예약 및 결제 트랜잭션 등 데이터 정합성이 중요한 핵심 B2C 로직입니다.

### 🏠 숙소 & 지도 검색 (Accommodations & Map)
| 도메인 | 메소드 | 엔드포인트 | 설명 | 권한 |
| :--- | :--- | :--- | :--- | :--- |
| `Store` | **GET** | `/api/v1/accommodations` | 전체/필터별 숙소 목록 조회 (페이징, 날짜 필터) | 누구나 |
| `Store` | **GET** | `/api/v1/accommodations/map` | 카카오 지도 화면 내 바운딩 박스(좌표) 안에 속한 숙소 핀 데이터 리스트 조회 (카카오맵 SDK 연동 필수) | 누구나 |
| `Store` | **GET** | `/api/v1/accommodations/{id}` | 특정 숙소 상세 정보 조회 및 방 뷰어 | 누구나 |
| `Store` | **GET** | `/api/v1/accommodations/{id}/calendar` | 예약 불가능/가능 날짜 인벤토리 조회 (달력 달력 렌더링용) | 누구나 |
| `Store` | **POST** | `/api/v1/accommodations` | 신규 숙소 등록 | 사장님(OWNER) |
| `Store` | **PUT** | `/api/v1/accommodations/{id}` | 숙소 정보 수정 | 사장님(OWNER) |

### 📅 예약 및 결제 (Reservations)
| 도메인 | 메소드 | 엔드포인트 | 설명 | 권한 |
| :--- | :--- | :--- | :--- | :--- |
| `Match` | **POST** | `/api/v1/reservations` | 숙소 예약 요청 (180일 규제 한도 체크 및 Lazy Issue 쿠폰 검증/소비 로직 포함) | 로그인 유저 |
| `Match` | **GET** | `/api/v1/reservations/me` | 내 예약 내역 조회 | 로그인 유저 |
| `Match` | **GET** | `/api/v1/reservations/store/{storeId}` | 내 숙소에 접수된 예약 대기 목록 조회 | 사장님(OWNER) |
| `Match` | **POST** | `/api/v1/reservations/{id}/accept` | 사장님이 예약 확정 | 사장님(OWNER) |
| `Match` | **POST** | `/api/v1/reservations/{id}/cancel` | 예약 취소 (고객/사장님 공통 기능) | 해당자 |

### ❤️ 커뮤니티 & 리텐션 (Reviews, Wishlist)
| 도메인 | 메소드 | 엔드포인트 | 설명 | 권한 |
| :--- | :--- | :--- | :--- | :--- |
| `Review`| **GET** | `/api/v1/accommodations/{id}/reviews` | 리뷰 목록 및 평균 별점 조회 (보안을 위해 벡엔드 DTO 레벨에서 작성자 이름 마스킹 처리 반환, 예: '변영*'. 사장님 답글 `host_content` 포함) | 누구나 |
| `Review`| **POST** | `/api/v1/reservations/{id}/reviews` | 확정된 지난 예약 건에 대한 숙박 후기 작성 | 해당 게스트 |
| `Review`| **POST** | `/admin/v1/reviews/{reviewId}/reply` | 게스트의 리뷰에 대한 사장님(호스트)의 답글(`host_content`) 작성 | 사장님(OWNER) |
| `Wish`  | **POST** | `/api/v1/wishlists/{accommodationId}` | 특정 숙소 찜하기 추가(Toggle 방식) | 로그인 유저 |
| `Wish`  | **GET** | `/api/v1/wishlists/me` | 내가 찜한 숙소 목록 리스트 | 로그인 유저 |

### 👤 유저 & 권한 (Users / Auth)
| 도메인 | 메소드 | 엔드포인트 | 설명 | 권한 |
| :--- | :--- | :--- | :--- | :--- |
| `Auth` | **POST** | `/api/v1/auth/signup` | 통합 회원가입 (Request Body에 `role` 파라미터로 'GUEST'/'HOST' 구분. *참고: 원본 서비스의 프론트엔드 라우터 주소는 `/social/register` 였음*) | 누구나 |
| `Auth` | **POST** | `/api/v1/auth/login` | 로그인 및 JWT 발급 (이메일/비밀번호 기반) | 누구나 |
| `User` | **GET** | `/api/v1/users/me` | 내 프로필 정보 및 권한(Role) 조회 | 로그인 유저 |

### 📁 파일 업로드 (AWS S3)
| 도메인 | 메소드 | 엔드포인트 | 설명 | 권한 |
| :--- | :--- | :--- | :--- | :--- |
| `File` | **POST** | `/api/v1/files/presigned-url` | 프론트엔드에서 S3로 이미지를 직접 올릴 수 있는 일회용 티켓(Pre-signed URL) 발급 | 로그인 유저 |

---

## 💼 2. B2B 백오피스 API (Spring Boot + Kotlin)
사내 관리자 및 호스트를 위한 전용 API입니다. 규제 대응(180일) 및 프로모션 관리 로직이 포함됩니다. (Vue 3 SPA 어드민 연동)

### 🎫 프로모션 (Promotions) - Lazy Issue 적용
| 도메인 | 메소드 | 엔드포인트 | 설명 | 권한 |
| :--- | :--- | :--- | :--- | :--- |
| `Promo` | **POST** | `/admin/v1/coupons` | 대규모(100만명) 쿠폰 마스터 룰셋 생성 | 사내 관리자 |
| `Promo` | **GET** | `/admin/v1/coupons` | 발급된 쿠폰 마스터 목록 및 사용 통계 조회 | 사내 관리자 |

### 💰 정산 (Settlements)
| 도메인 | 메소드 | 엔드포인트 | 설명 | 권한 |
| :--- | :--- | :--- | :--- | :--- |
| `Money` | **GET** | `/admin/v1/hosts/{id}/settlements` | 해당 월의 객실 판매 대금 및 수수료 정산 내역 조회 | 사장님(OWNER) |

### 📝 특례 신청 및 서류 업로드 (Host Registration)
| 도메인 | 메소드 | 엔드포인트 | 설명 | 권한 |
| :--- | :--- | :--- | :--- | :--- |
| `Apply` | **POST** | `/admin/v1/applications` | 공유숙박 특례 심사 신청 (A안/B안 타입 선택 및 S3 업로드된 증빙 서류 URL 목록 전송) | 사장님(OWNER) |
| `Apply` | **GET** | `/admin/v1/applications/me` | 내 특례 심사 진행 상태 조회 (서류접수/현장심사/교육이수/최종승인) | 사장님(OWNER) |
| `Apply` | **PUT** | `/admin/v1/applications/{id}/status` | [사내 관리자 전용] 신청 건의 상태(반려, 승인 등) 변경 처리 | 사내 관리자 |

### ⚖️ 운영 및 규제 (Compliance & CS)
| 도메인 | 메소드 | 엔드포인트 | 설명 | 권한 |
| :--- | :--- | :--- | :--- | :--- |
| `Limit` | **GET** | `/admin/v1/hosts/{id}/stay-days` | 호스트별 올해 총 숙박 일수 조회 (180일 영업 규제 체크용) | 사내 관리자 |
| `CS`    | **GET** | `/admin/v1/tickets` | 분쟁/민원 티켓 목록 조회 (ML 기반 긴급도 스코어링 포함) | 사내 관리자 |
| `CS`    | **PUT** | `/admin/v1/tickets/{id}/resolve` | 티켓 해결 및 보상(쿠폰) 즉시 지급 처리 | 사내 관리자 |
| `Churn` | **GET** | `/admin/v1/users/churn-risk` | ML 기반 이탈 예상 유저 리스트 조회 (타겟팅 프로모션용) | 사내 관리자 |

---

## ⚡ 3. 서브 & 통계 API (Bun + Elysia.js + Eden)
트랜잭션 부담이 적고 초고속 응답이 필요한 로직입니다. (Nuxt.js와 Eden E2E 타입 동기화)

### 📊 실시간 & 로그 (Analytics)
| 도메인 | 메소드 | 엔드포인트 | 설명 | 권한 |
| :--- | :--- | :--- | :--- | :--- |
| `Event` | **POST** | `/analytics/event` | [B2C 프론트엔드] 커스텀 이벤트 로깅 (이름, 페이로드, 타임스탬프) | 누구나 |
| `Visit` | **POST** | `/stats/v1/visits` | [B2C 프론트엔드] 방 조회수 및 방문자 수 로깅 (경로, 유저ID, UA) | 누구나 |
| `Visit` | **GET** | `/stats/v1/visits/today` | 오늘 전체 방문자 통계 및 인기 경로 TOP 5 조회 | 누구나 |
| `Health`| **GET** | `/` | 시스템 상태 라우팅 체크 (Bun + Elysia + SQLite WAL) | 누구나 |


---

## 🐹 4. 비동기 메시지 처리 워커 (Golang Auto-Scaled ECS)
직접적인 HTTP 엔드포인트는 아니지만 메인 서버(Spring Boot)가 RabbitMQ 큐에 이벤트를 Publish하면, KEDA/ECS 지표로 복제된 Golang 고루틴 워커가 대용량 트래픽을 비동기로 병렬 Consume 처리합니다.

1. **[알람 발송 워커]**: 예약 발생/확정/취소 시 유저 및 사장님에게 카카오 알림톡 및 앱 푸시 알림 발송
2. **[Lazy Issue 리마인더 워커]**: 미사용 쿠폰 만료 임박 시, 사용 이력이 없는(사용안한) 대상자만 DB에서 필터링하여 알림톡 발송
3. **[로그 파이프라인 워커]**: ELK 스택(Logstash) 연동을 위해 비즈니스 로그 및 유저 행동 로그를 전송 (추후 ML 이탈 방지 모델의 피드백 데이터 수집용)

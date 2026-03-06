# 🗄️ 넥스트스테이(Nextstay) 핵심 DB 스키마 설계안

이 문서는 넥스트스테이 프로젝트의 데이터베이스(MySQL) 핵심 엔티티 구조와 설계 의도를 담고 있습니다. 특히 보안과 확장성을 고려한 식별자(Identifier) 채번 전략을 명확히 정의합니다.

---

## 1. 👤 Users (회원/사용자 테이블)

**[설계 핵심 의도 - 비즈니스 키 분리]**
경쟁사에 일일 가입자 수 등 핵심 성장 지표가 노출되는 것을 막고, 백오피스(CS)에서 운영 편의성을 높이기 위해 **내부 DB용 식별자(PK)**와 **외부 노출용 식별자(비즈니스 키)**를 완벽하게 분리하여 설계했습니다. API 응답 및 JWT 토큰에는 오직 `user_no`만 노출됩니다.

| 컬럼명 | 원본 타입 | 길이/제약조건 | 널 허용 여부 | 설명 및 예시 |
| :--- | :--- | :--- | :--- | :--- |
| **`id`** | BigInt | Auto Increment, **PK** | NOT NULL | **[내부용]** DB 테이블 조인(Join) 및 인덱스 검색을 위한 순차 증가 식별자. (예: `58421`) (⚠️ **절대 외부 노출 금지**) |
| **`user_no`** | Varchar | 20, **Unique** | NOT NULL | **[외부용]** JWT 토큰 및 API 응답에 노출되는 비즈니스 키. 가입일자+순번 조합. (예: `m20260307-001` - 회원, `h20260307-002` - 호스트) |
| `email` | Varchar | 100, **Unique** | NOT NULL | 로그인 아이디 (로그인용) |
| `password` | Varchar | 255 | NOT NULL | BCrypt 등으로 단방향 암호화 처리된 해시 비밀번호 |
| `name` | Varchar | 30 | NOT NULL | 사용자의 진짜 실명 원본 (예: `김현수`) (⚠️ API 응답 시 DTO에서 `김현*` 로 마스킹 처리 필수) |
| `role` | Enum | 'GUEST', 'HOST', 'ADMIN' | NOT NULL | 백오피스와 게스트웹 접근 권한을 가르는 핵심 권한자 |
| `created_at`| DateTime | | NOT NULL | 가입일 (기본값: 현재 시간) |
| `deleted_at`| DateTime | | NULL | 회원 탈퇴 시 Soft-Delete 처리를 위한 삭제 일자 |

---

*(향후 `Accommodations`(숙소), `Reservations`(예약), `Coupons`(쿠폰) 등 핵심 테이블 구조가 도출되면 이 문서에 지속적으로 업데이트합니다.)*

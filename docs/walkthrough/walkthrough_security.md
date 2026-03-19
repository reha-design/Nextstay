# 🛡️ 보안 강화(Security Hardening) 워크스루

회원가입 및 로그인 기능에 대해 실무/대기업 수준의 보안 강화 작업을 완료하고 검증했습니다.

## 🛠️ 주요 구현 내용

### 1. 백엔드 DTO 유효성 검사 고도화
- **[SignupRequest.kt](file:///c:/portpolio/Nextstay/backend/src/main/kotlin/com/mrmention/nextstay/domain/member/dto/SignupRequest.kt)**:
  - `name`: 특수문자 차단을 위한 `@Pattern` 및 `@Size(2~100)` 적용.
  - `email`: RFC 5321 표준에 따른 `@Size(max=255)` 적용.
  - `password`: Bcrypt DoS 방지를 위한 `@Size(max=72)` 및 정밀 정규식 적용.
  - `phone`: 하이픈 포함 숫자만 허용하는 `@Pattern` 적용.
- **[AuthDto.kt](file:///c:/portpolio/Nextstay/backend/src/main/kotlin/com/mrmention/nextstay/domain/member/dto/AuthDto.kt)**:
  - 로그인 시에도 동일한 길이 제한을 적용하여 메모리 오염 공격 방어.

### 2. 프론트엔드 1차 방어선 구축
- **[signup.vue](file:///c:/portpolio/Nextstay/frontend-guest/pages/signup.vue)**:
  - 모든 입력 필드에 HTML `maxlength` 속성을 추가하여 불필요한 서버 요청 사전 차단.

## 🧪 검증 결과 (통합 테스트)

`MemberSecurityTest.kt`를 통해 다음 시나리오에 대한 자동화 검증을 완료했습니다.

| 테스트 케이스 | 공격 페이로드 | 결과 | 상태 |
| :--- | :--- | :--- | :--- |
| **SQL Injection** | `admin' --` | **400 Bad Request** | ✅ 통과 |
| **Hashing DoS** | PW (73자 이상) | **400 Bad Request** | ✅ 통과 |
| **Email Overflow** | Email (256자 이상) | **400 Bad Request** | ✅ 통과 |
| **Invalid Phone** | `010-ABCD-EFGH` | **400 Bad Request** | ✅ 통과 |

```bash
# 테스트 실행 결과
./gradlew test --tests com.mrmention.nextstay.domain.member.MemberSecurityTest
> :test > MemberSecurityTest > SQL 인젝션... PASSED
> :test > MemberSecurityTest > 너무 긴 비밀번호... PASSED
> :test > MemberSecurityTest > 비정상적인 전화번호... PASSED
> :test > MemberSecurityTest > 로그인 시에도 길이 제한... PASSED
BUILD SUCCESSFUL
```

---
> [!NOTE]
> 모든 보안 에러는 `GlobalExceptionHandler`를 통해 표준화된 에러 메시지로 응답하며, 시스템 내부 스택 트레이스는 외부로 노출되지 않습니다.

# 🍃 Kotlin + Spring Boot 기초 세팅 전략 (엔터프라이즈 아키텍처)

코틀린과 스프링 부트에 익숙하지 않으시더라도, 실무(특히 네카라쿠배와 같은 대형 IT 기업)에서 가장 표준적으로 사용하는 세팅과 폴더 구조를 제안해 드립니다. 
이 뼈대만 잘 구성해 두시면 면접관에게 **"최신 스프링 생태계의 트렌드와 안정적인 아키텍처를 정확히 이해하고 있다"**는 평가를 받을 수 있습니다.

---

## 1. 🛠️ 기술 스택 버저닝 (초기 세팅 시 선택)
Spring Initializr (https://start.spring.io/) 에서 프로젝트를 생성할 때 다음 스펙을 권장합니다.
* **Build Tool**: Gradle - Kotlin (Groovy 대신 Kotlin DSL을 사용하는 것이 최근 트렌드입니다)
* **Language**: Kotlin (Java 17 이상 호환)
* **Spring Boot Version**: 3.2.x 이상 (최신 안정 버전)
* **Packaging**: Jar
* **Dependencies (핵심 라이브러리)**:
  * `Spring Web`: REST API 구성
  * `Spring Data JPA`: DB ORM 다루기 (필수)
  * `Spring Security`: JWT 인증 및 Role 기반 권한 체크를 위한 방패
  * `Validation`: API DTO 검증용 (`@Valid`)
  * `MySQL Driver`: 실제 DB 연결용

---

## 2. 📂 가장 추천하는 폴더(패키지) 구조: Domain-Driven (도메인형)
초보자들은 흔히 모든 컨트롤러를 `controller` 폴더에, 모든 서비스를 `service` 폴더에 몰아넣는 '계층형(Layered)' 구조를 씁니다. 
하지만 대규모 포트폴리오를 어필하기 위해서는 기능(도메인)별로 묶는 **도메인형 구조**를 강력히 추천합니다.

```text
src/main/kotlin/com/nextstay/api
 ┣ 📂 global          // 프로젝트 전체에서 공통으로 쓰는 놈들
 ┃ ┣ 📂 config        // SecurityConfig, WebConfig 등 설정 파일
 ┃ ┣ 📂 exception     // GlobalExceptionHandler (에러 공통 처리)
 ┃ ┗ 📂 security      // JWT Provider, 필터 로직
 ┃
 ┣ 📂 domain          // 핵심 비즈니스 로직 (여기가 가장 중요)
 ┃ ┣ 📂 user          // 유저 & 호스트 관련 도메인
 ┃ ┃ ┣ 📂 controller  // 예: UserController.kt
 ┃ ┃ ┣ 📂 service     // 예: UserService.kt
 ┃ ┃ ┣ 📂 repository  // 예: UserRepository.kt (JPA)
 ┃ ┃ ┣ 📂 entity      // 예: User.kt (DB 테이블 매핑)
 ┃ ┃ ┗ 📂 dto         // Request, Response 객체들
 ┃ ┃
 ┃ ┣ 📂 accommodation // 숙소 도메인
 ┃ ┣ 📂 reservation   // 예약 도메인
 ┃ ┗ 📂 review        // 리뷰 도메인
 ┃
 ┗ 📜 NextstayApplication.kt // 실행 메인 클래스
```

### [이 구조가 좋은 이유]
* 예약 관련 로직을 고칠 때 `reservation` 폴더 하나만 열면 Controller부터 DB(Entity)까지 다 들어있어 유지보수가 압도적으로 편합니다.

---

## 3. 🛡️ 에러 처리 및 응답 포맷 (Global Exception Handler)
앱이나 웹(B2C/B2B 프론트엔드)이 백엔드와 통신할 때, 백엔드가 에러에 따라 제각각의 모양으로 응답을 주면 프론트엔드 개발자가 매우 힘들어합니다.
스프링 부트의 `@RestControllerAdvice`를 사용하여 모든 API의 성공/실패 응답 형태를 표준 JSON 포맷으로 고정하는 것이 시니어급의 첫걸음입니다.

**[표준 API 응답 JSON 제안]**
```json
// 성공 시
{
  "code": "SUCCESS",
  "message": "처리가 완료되었습니다.",
  "data": { ... } // 실제 반환 데이터 (DTO)
}

// 400 실패 시 (예: 비밀번호 틀림)
{
  "code": "AUTH_FAILED",
  "message": "이메일 또는 비밀번호가 일치하지 않습니다.",
  "data": null
}
```

---

## 4. 백엔드 진행 방향 요약
코틀린+스프링 부트는 문법이 생소할 수 있지만 패턴이 매우 정형화되어 있습니다.
1. `Entity` (DB 구조)를 먼저 코틀린 데이터 클래스로 정의합니다.
2. `Repository` (JPA) 인터페이스를 만들어 DB 접근 메서드를 뚫어둡니다.
3. `Service`에서 비즈니스 로직(180일 체크 등)을 짭니다.
4. `Controller`에서 프론트엔드의 요청을 받아 Service로 넘겨줍니다.

프론트엔드(Bun 기반 Nuxt/Vue)를 가장 먼저 화면단부터 띄워두고(초기화), 그 다음 이 스프링 부트 뼈대를 생성하는 순서로 가시면 완벽합니다!

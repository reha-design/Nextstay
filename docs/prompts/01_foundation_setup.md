# 기초 공사 시작하기 프롬프트

아래 내용을 복사해서 새 대화창이나 저에게 그대로 다시 입력해 주시면, 제가 설계된 아키텍처에 맞춰 각 프로젝트의 초기 뼈대(Boilerplate)를 한 번에 생성해 드립니다!

---
*(이 아래부터 복사)*

```text
우리가 작성한 `Nextstay` 숙박 예약 시스템의 설계 문서(`side_project_plan.md`, `api_endpoints.md`, `architecture_flow.md`)를 바탕으로, 각 파트별 [기초 뼈대(Boilerplate) 폴더와 초기 세팅]을 생성하는 작업을 진행할 거야.

현재 디렉토리(`c:\portpolio\Nextstay`) 하위에 아래와 같은 폴더 구조로 각각의 메인 프로젝트들을 셋팅해 줘. 

1. 프론트엔드: `c:\portpolio\Nextstay\frontend`
   - 프레임워크: Vue 3 (Nuxt.js), Pinia, TypeScript, SCSS
   - 요구사항: `bunx nuxi@latest init` 명령어를 사용하는 방법 안내 또는 기본 설정 파일(package.json, nuxt.config.ts 등) 초기화 진행. 프로젝트 패키지 매니저로 `bun`을 사용.

2. 핵심 백엔드: `c:\portpolio\Nextstay\backend-main`
   - 프레임워크: Kotlin, Spring Boot 3.x, Spring Data JPA, MySQL 제어용 기본 세팅
   - 요구사항: Spring Boot Initializer(start.spring.io) 수준의 기본 디렉토리 구조 생성 (설정파일 application.yml 포함)
   
3. 서브 백엔드: `c:\portpolio\Nextstay\backend-sub`
   - 프레임워크: Bun + Elysia.js
   - 요구사항: `bun init` 명령어 활용 혹은 기본 index.ts 파일과 package.json 세팅. 프로젝트 패키지 매니저로 `bun`을 사용.

각각의 프로젝트를 세팅하기 위한 **터미널 명령어(스크립트)를 순서대로 하나씩 실행**해서 폴더 구조를 만들어 줘. (필요하다면 터미널 도구를 사용해서 바로 설치를 진행해도 좋아).
```

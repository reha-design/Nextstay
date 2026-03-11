---
description: Nextstay 프로젝트 전용 커밋 메시지 작성 규칙
---

Nextstay 프로젝트에서 커밋 메시지를 작성할 때는 다음의 [ 분야 ] 머리말(Prefix) 규칙을 엄격히 준수합니다.

### 1. 머리말(Prefix) 규칙
- **[ Nuxt ]**: Guest 웹 (`frontend-guest`) 관련 작업
- **[ Vue ]**: Host 관리자 페이지 (`frontend-backoffice`) 관련 작업
- **[ Kotlin ]**: 메인 백엔드 API (`backend`) 관련 작업
- **[ Go ]**: 알림 워커 (`backend-worker-go`) 관련 작업
- **[ Elysia ]**: 분석 서비스 (`backend-analytics`) 관련 작업
- **[ Infra ]**: AWS, Docker, SST, Nginx, DB 등 인프라 설정 관련 작업
- **[ Docs ]**: README, 기술 문서, 작업 보고서 관련 작업
- **[ Refactor ]**: 기능 변경 없는 코드 정리 및 리팩토링

### 2. 작성 형식
`[ 분야 ] 작업_내용 (동사형으로 마무리)`

### 3. 예시
- `[ Nuxt ] API 보안 프록시 구현 및 회원가입 연동 완료`
- `[ Kotlin ] Stay 엔티티 내 동적 이미지 컬렉션 필드 추가`
- `[ Infra ] EC2 배포 최적화를 위한 Dockerfile 경량화 및 S3 연동`
- `[ Docs ] Phase 8 데이터 정교화 작업 보고서 작성`

---
**지시**: 이 워크플로우는 AI 에이전트가 `git commit` 명령을 제안하거나 수행할 때 항상 참조해야 하는 표준 가이드라인입니다.

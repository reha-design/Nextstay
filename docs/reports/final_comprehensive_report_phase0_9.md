# Nextstay 프로젝트 종합 작업 보고서 (Phase 0 ~ 9)

본 보고서는 **Nextstay** 프로젝트의 안정성 확보부터 AWS 클라우드 인프라 구축, 서비스 최적화 및 보안 강화까지의 모든 단계를 기록한 최종 보고서입니다.

---

## 🛠️ 작업 이력 및 성과 요약

### [Phase 0] 백엔드 안정성 및 동기식 리팩토링
- **R2DBC 제거 및 JPA 전환**: 데이터 일관성과 개발 속도 향상을 위해 안정적인 JPA로 전환.
- **동기식 전환**: 복잡한 비동기 로직(Coroutines)을 제거하여 백엔드 서버의 신뢰성 및 가독성 확보.

### [Phase 1~2] AWS 인프라 구축
- **Amazon EC2 (t3.micro)**: 메인 서버 환경 구축 및 2GB Swap 설정을 통한 저비용 서버 최적화.
- **RDS MySQL & Amazon MQ**: 완전 관리형 DB 및 메시지 브로커 도입으로 서비스 영속성 확보.
- **S3 & 이중 CloudFront**: 정적 자산(Host Web)과 게스트 SSR 웹의 글로벌 서빙 환경 구축.

### [Phase 3~4] 컨테이너화 및 배포 최적화
- **Docker 기반 배포**: 모든 마이크로서비스(Spring, Elysia, Go)를 컨테이너화하여 일관된 환경 유지.
- **JAR 직접 전송 방식**: EC2 리소스 한계를 고려하여 로컬 빌드 후 전송하는 경량화된 배포 프로세스 확립.

### [Phase 5~7] 프론트엔드 현대화 (SST & Proxy)
- **SST Ion 도입**: Nuxt 3 SSR을 AWS Lambda에 배포하여 운영 비용 극단적 축소.
- **보안 프록시 구축**: 브라우저의 Mixed Content(HTTPS ↔ HTTP) 차단 문제를 해결하기 위해 서버 사이드 프록시 도입.
- **CORS 및 인증 고도화**: 운영 환경에서의 403 Forbidden 및 회원가입 연동 이슈 완전 해결.

### [Phase 8] 데이터 정교화 및 비주얼 강화
- **지도 좌표 정밀화**: 실제 시 도심지 좌표 및 Jitter 적용으로 지도 가시성 및 현실성 향상.
- **이미지 갤러리 완성**: 모든 숙소에 6장 이상의 사진을 `picsum.photos` 시드 방식으로 제공하여 5분할 격자 UI 완성.

### [Phase 9] 보안 및 개발 워크플로우 최적화
- **루트 .gitignore 수립**: AWS 키(.pem) 및 비밀번호 포함 설정 파일 등 민감 정보 유출 방지.
- **커밋 가이드라인 구축**: 에이전트 전용 명세서(`.agents/workflows/commit.md`)를 통한 체계적인 히스토리 관리.

---

## 📅 프로젝트 최종 상태 (2026.03.12 기준)

| 구분 | 상태 |
| :--- | :--- |
| **인프라** | AWS RDS, MQ, EC2, CloudFront, Lambda(SST) 가동 중 |
| **프론트엔드 (Guest)** | [https://d384c7rwalwmeb.cloudfront.net](https://d384c7rwalwmeb.cloudfront.net) |
| **백엔드 API** | EC2 Docker 컨테이너 3종 (Spring, Analytics, Worker) 정상 작동 |
| **보안** | 전체 HTTPS 지원 및 서버 사이드 보안 프록시 가동 중 |

---

## 📄 참고 문서
- [전체 프로젝트 README](../../README.md)
- [일일 작업 보고서 (2026.03.12)](./work_report_2026_03_12.md)
- [기술 상세 가이드 (walkthrough)](../walkthrough/walkthrough_phase8_deployment.md)

**최종 보고서 작성 완료.** 본 프로젝트는 현재 포트폴리오로 즉시 활용 가능한 '전 구간 정상 가동' 상태입니다.

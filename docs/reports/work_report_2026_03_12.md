# 주간 작업 보고서 (2026.03.11 - 03.12)

## 📋 요약
어제와 오늘 이틀간 **Nextstay** 프로젝트의 프론트엔드 인프라를 전면 개편하고, 클라우드 환경에서의 서비스 안정성 및 보안 문제를 모두 해결했습니다. 특히 **SST(Serverless Stack)** 도입을 통해 운영 비용을 최적화하고, 브라우저 보안 정책(Mixed Content)으로 인한 기능 마비 문제를 **서버 사이드 프록시** 구축을 통해 완벽하게 정상화했습니다.

---

## 🚀 주요 성과

### 1. 프론트엔드 인프라 현대화 (SST 도입)
- **Guest SSR (Nuxt)**: 기존 EC2 실행 방식에서 **AWS Lambda** 서버리스 방식으로 전환하여 월 100만 회 무료 호출 범위를 확보하고 확장성을 극대화했습니다.
- **Host CSR (Vite)**: S3와 CloudFront를 결합한 정적 호스팅 구조로 안정적인 글로벌 서빙 환경을 구축했습니다.
- **비용 최적화**: API Gateway 대신 **Lambda Function URL**을 사용하여 추가 비용 없이 SSR 기능을 제공하도록 설계했습니다.

### 2. 백엔드 연동 및 인프라 최적화
- **EC2 리소스 효율화**: 게스트 SSR의 Lambda 위임으로 EC2 부하를 줄였으며, **2GB Swap** 설정을 통해 저사양(t3.micro) 인스턴스에서도 Docker 서비스(Spring, Elysia, Go)가 안정적으로 구동되도록 개선했습니다.
- **Nginx 게이트웨이 정교화**: 리버프 프록시 경로(`/api/`, `/analytics/`) 설정을 최적화하여 백엔드 내부 포트 노출을 방지하고 보안을 강화했습니다.

### 3. 데이터 정교화 및 비주얼 강화 (Phase 8)
- **정밀 좌표 적용**: 모든 숙소를 바다에서 육지 도심(서울, 부산 등)으로 이전 배치하고 Jitter를 적용하여 지도상 가시성을 높였습니다.
- **이미지 갤러리 완성**: 모든 숙소에 6장 이상의 이미지를 확보하여 5분할 격자 UI가 빈칸 없이 꽉 채워지도록 개선했습니다. (picsum.photos 안정성 확보)

---

## 🛠️ 핵심 기술 해결 (Problem Solving)

### 1. Lambda SSR 런타임 에러 해결
- **문제**: Lambda 환경에서 Nuxt 서버 실행 시 ESM 모듈 로딩 실패로 인한 500 에러 발생.
- **해결**: Nitro 엔진의 `externals.inline` 설정을 통해 의존성 파일을 패키징 내부에 강제 포함(Aggressive Inlining)하여 해결했습니다.

### 2. Mixed Content(혼합 콘텐츠) 보안 이슈 해결 (중요)
- **문제**: HTTPS(프론트엔드)에서 HTTP(백엔드 EC2) API를 직접 호출할 때 브라우저가 보안상의 이유로 요청을 차단함.
- **해결**: 
    - **분석 프록시**: `/api/analytics-proxy` 구축 (Elysia 연동성 확보).
    - **전역 API 프록시**: `/api/backend-proxy` 구축 및 `useApi` 인터셉터 업데이트.
    - **결과**: 브라우저 보안 경고 없이 **가격 계산, 숙소 조회, 로그 수집** 등 모든 동적 기능 정상화.

### 3. CORS 및 라우팅 이슈 완결
- CloudFront 도메인을 Spring Boot 보안 정책에 반영하여 403 Forbidden 문제 해결.
- Nginx `proxy_pass`의 경로 스트리핑 이슈(슬래시 하나 차이)를 교정하여 API 연동 정상화.

---

## 📊 결과 확인 (End-to-End Verification)

- [x] **분석 로그 전송**: `200 OK` 확인 (Mixed Content 해결 후).
- [x] **가격 계산 API**: `calculate-price` 호출 및 UI 업데이트 검증 완료.
- [x] **인프라 안정성**: Lambda SSR 평균 응답 시간 및 정적 자산 로딩 속도 최적화 확인.
- [x] **데이터 및 비주얼**: 모든 상세 페이지 5분할 갤러리 및 도심 지도 위치 정밀도 최종 검증 완료.

---

## 📂 관련 문서
- [상세 기술 가이드 및 검증 영상 (walkthrough.md)](file:///C:/Users/user/.gemini/antigravity/brain/c753ac03-05b4-444c-8d86-8fbb9dcd1e7e/walkthrough.md)
- [배포 인프라 구성도 (implementation_plan.md)](file:///C:/Users/user/.gemini/antigravity/brain/c753ac03-05b4-444c-8d86-8fbb9dcd1e7e/implementation_plan.md)

---
**보고서 작성 완료**. 모든 시스템이 정상 가동 중이며, 추가 보안 요구 사항(도메인/SSL) 발생 시 즉시 대응 가능합니다.

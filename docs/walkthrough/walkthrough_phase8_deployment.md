# Nuxt 3 SSR + SST (Serverless Stack) 배포 및 데이터 정교화 완료 보고서 (Phase 8)

SST Ion(v3)을 사용하여 프론트엔드 인프라를 AWS Lambda 기반으로 전환하고, 백엔드 데이터를 정교화하여 포트폴리오의 완성도를 높였습니다.

## 🚀 배포 정보

| 서비스 | 환경 | 배포 방식 | URL |
| :--- | :--- | :--- | :--- |
| **Guest Frontend** | SSR | AWS Lambda (Universal) | [https://d384c7rwalwmeb.cloudfront.net](https://d384c7rwalwmeb.cloudfront.net) |
| **Host Backoffice** | CSR | S3 + CloudFront | [https://d2jwfd8djegkc5.cloudfront.net](https://d2jwfd8djegkc5.cloudfront.net) |

---

## 🏗️ Phase 8: 데이터 정교화 (좌표 및 갤러리 이미지 보강)

숙소 데이터의 현실성을 높이고 상세 페이지의 비주얼을 완성하기 위해 좌표 정밀화 및 갤러리 이미지 보강 작업을 완료했습니다.

### 1. 지도 좌표 정밀화
- 각 도시(서울, 부산, 제주 등)의 실제 시청/랜드마크 좌표를 기준으로 변경하고 Jitter를 적용하여 지도상에 자연스럽게 배치했습니다.
- 이제 지도가 바다 한복판이 아닌 실제 육지 도심을 가리킵니다.

### 2. 갤러리 이미지 (1+4 격자) 완성
- 모든 숙소에 6장 이상의 이미지를 제공하여 상세 페이지 상단의 5분할 격자가 빈칸 없이 꽉 채워지도록 개선했습니다.
- `picsum.photos`의 시드(Seed) 방식을 활용하여 브라우저 차단(ORB) 없이 안정적인 로딩을 보장합니다.

### 3. 운영 환경 배포 최적화
- EC2(t3.micro)의 메모리 부족 문제를 해결하기 위해, 로컬에서 빌드한 JAR를 전송하고 도커 빌드 시 컴파일 과정을 생략하는 최적화된 배포 프로세스를 적용했습니다.

### 시각적 확인 (Proof of Work)

> **[상세 스크린샷 및 영상 증적]**
> - 숙소 상세 페이지 5분할 갤러리: `C:/Users/user/.gemini/antigravity/brain/c753ac03-05b4-444c-8d86-8fbb9dcd1e7e/accommodation_gallery_1773259235974.png`
> - 도시별 정밀 좌표 적용 지도: `C:/Users/user/.gemini/antigravity/brain/c753ac03-05b4-444c-8d86-8fbb9dcd1e7e/accommodation_map_1773259250232.png`

---

## 🛠️ 해결된 주요 이슈 요약

1.  **전역 API 보안 프록시 구축**: Mixed Content 이슈를 해결하고 `localhost` 하드코딩을 제거하여 운영 환경에서 회원가입 및 가격 계산이 정상 작동하도록 구현했습니다.
2.  **Elysia Analytics 서비스 복구**: 서버 사이드 프록시를 통해 로그 수집 기능을 HTTPS 환경에서 복구했습니다.
3.  **Docker 빌드 메모리 최적화**: EC2 성능 한계를 고려한 경량화된 배포 방식을 확립했습니다.

---

## 📄 참고 문서
- [전체 프로젝트 README](../../README.md)
- [트러블슈팅 및 아키텍처 결정 로그](../troubleshooting/strategic_decisions.md)

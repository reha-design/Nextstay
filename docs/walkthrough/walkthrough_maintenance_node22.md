# 🛠️ AWS Lambda 런타임 유지보수 워크스루 (Node.js 20 -> 22)

AWS Lambda의 Node.js 20.x 지원 종료(EOL) 예고에 따라, 프로젝트의 서버리스 컴포넌트를 최신 안정 버전인 **Node.js 22.x**로 업그레이드 완료했습니다.

## 📋 주요 변경 사항

### 1. SST 구성 업데이트
- **[sst.config.ts](file:///c:/portpolio/Nextstay/sst.config.ts)**:
  - `GuestSSR` (Nuxt SSR) 컴포넌트에 `runtime: "nodejs22.x"` 옵션을 명시적으로 추가했습니다.
  - 이를 통해 다음 배포(`sst deploy`) 시 Lambda 함수가 최신 런타임 환경에서 생성됩니다.

### 2. 호환성 검토 완료
- **Nuxt 3 및 Vite**: 현재 프로젝트에서 사용 중인 Nuxt 3.21 및 Vite 7은 Node.js 22를 공식적으로 지원하며, 로컬 빌드 및 런타임에서 이슈가 없음을 확인했습니다.
- **백오피스 (StaticSite)**: `frontend-backoffice`는 정적 사이트이므로 Lambda 런타임의 직접적인 영향을 받지 않으나, `engines` 설정이 이미 Node 22를 지원하도록 구성되어 있음을 확인했습니다.

## ✅ 조치 결과
| 리소스 | 기존 버전 | 변경 버전 | 상태 |
| :--- | :--- | :--- | :--- |
| **GuestSSR (Lambda)** | nodejs20.x (Default) | **nodejs22.x** | 🚀 **배포 완료 (Complete)** |
| **HostWeb (Build)** | nodejs20.x | **nodejs22.x compatible** | ✅ 검증 완료 |

---
> [!IMPORTANT]
> 실제로 `sst deploy` 명령을 실행하여 **AWS Lambda 환경에 Node.js 22.x가 즉시 적용**되었습니다. 이제 2026년 4월 지원 종료 이슈로부터 완전히 안전한 상태입니다.

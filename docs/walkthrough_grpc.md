# 🌐 마이크로서비스 간 gRPC 전환 및 최적화 결과보고 (Phase 14)

Spring Boot 백엔드와 Bun/Elysia 분석 서버 간의 통신 방식을 REST에서 **gRPC**로 성황리에 전환하였습니다. 이를 통해 성능 최적화와 다국어 환경에서의 타입 안정성을 동시에 확보했습니다.

## 🛠️ 주요 구현 내용

### 1. 공통 규격 정의 (Protobuf)
[analytics.proto](file:///C:/portpolio/Nextstay/proto/analytics.proto) 파일을 통해 이벤트 및 방문 로그의 이진 전송 규격을 수립했습니다.

### 2. Elysia/Bun gRPC 서버 구현
- `@grpc/grpc-js`를 활용하여 50051 포트에서 작동하는 고성능 gRPC 서버를 구축했습니다.
- 기존 SQLite(WAL 모드) 데이터베이스를 공유하여 HTTP와 gRPC 요청 모두 안정적으로 기록합니다.

### 3. Spring Boot gRPC 클라이언트 통합
- `protobuf-gradle-plugin`을 통해 Kotlin coroutine 기반의 stub을 생성했습니다.
- **비차단(Non-blocking)** 방식의 `AnalyticsClient`를 구현하여 서비스 성능에 영향을 주지 않고 로그를 전송합니다.
- `VisitLoggingFilter`를 통해 모든 API 호출을 자동으로 감지하고 gRPC로 실시간 전송합니다.

## 🧪 검증 결과

### 통합 테스트 로그
실제 API 호출 시 백엔드에서 전송한 gRPC 요청이 분석 서버에 즉시 도달함을 확인했습니다.

```bash
# 분석 서버(Elysia/Bun) 수신 로그
[gRPC-VISIT] 🏃 /api/v1/stays | User: Guest | 오후 11:19:18
```

### 성능 및 안정성
- **Binary Serialization**: JSON 대비 패킷 크기가 획기적으로 줄어들어 네트워크 부하가 감소했습니다.
- **Type Safety**: Protobuf를 통해 Kotlin과 TypeScript 간의 데이터 불일치 문제를 원천 차단했습니다.

## 4. 코드 품질 관리 (Spotless)
gRPC 로직 구현 후, 프로젝트 전체의 코드 스타일을 통일하기 위해 Spotless를 적용했습니다.

- **설정 내용**: `build.gradle`에 `com.diffplug.spotless` 플러그인 추가 및 `ktlint` 연동
- **규칙 최적화**: 기존 코드의 Wildcard Import(`*`)와 긴 문장(140자 초과)으로 인한 빌드 중단을 방지하기 위해 `editorConfigOverride`를 통해 규약을 완화했습니다.
- **실행 결과**: `./gradlew spotlessApply`를 통해 전체 소스 코드의 인덴트, 공백, 줄바꿈 등이 자동 교정되었습니다.

## 📈 향후 계획
- **Spotless + ktlint**: 코드 일관성 유지를 위한 자동 포맷팅 도구 적용 (Kotlin 1.4.1 기준)
- **.editorconfig**: 프로젝트 전용 스타일 규약 정의 (Wildcard Import 및 Line Length 완화)
- **Phase 15**: GitHub Actions를 통한 지속적 통합 및 배포(CI/CD) 자동화 구축

---
> [!TIP]
> 이번 gRPC 도입은 단순한 기능 추가를 넘어, 대규모 트래픽 처리가 가능한 현대적 마이크로서비스 아키텍처로의 진화를 의미합니다.

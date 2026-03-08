# 06_pricing_engine_refactoring.md

## 개요
실시간 가격 계산 모듈(`PriceService`)의 성능 최적화 및 정밀도 향상을 위한 리팩토링 계획입니다. 
이 문서는 실무 환경 대용량 트래픽 및 오차 없는 결제 금액 연산을 고려하여 작성되었습니다.

## 1. 연산 정밀도 향상 (Double -> BigDecimal 변환)

### 문제 상황
현재 `StayDiscountPolicy`의 `discountRate`와 `StaySeasonPrice`의 `multiplier`가 `Double` 타입으로 선언되어 있습니다. 부동소수점(`Floating Point`) 방식은 십진수 분수를 정확하게 표현할 수 없는 근본적인 한계가 있어, 복합적인 할인율과 가중치를 계산할 때 1원 단위의 오차가 발생할 위험이 있습니다. (예: 0.1 + 0.2 != 0.3)

### 해결 방안
금융 및 결제(Payment) 도메인의 표준에 맞게, 돈과 관련된 모든 비율 및 가중치 연산을 `java.math.BigDecimal`로 전환합니다.

**변경 대상:**
- `StayDiscountPolicy` 엔티티의 `discountRate` 필드 타입 변경
- `StaySeasonPrice` 엔티티의 `multiplier` 필드 타입 변경
- 연관된 DTO(`DiscountPolicyRequest`, `SeasonPriceRequest` 등) 타입 변경
- `PriceService` 내의 가격 계산 로직(`*`, `-` 연산자 대신 `.multiply()`, `.subtract()` 등 활용)

## 2. N+1 쿼리 문제 해결 (조회 성능 최적화)

### 문제 상황
현재 `PriceService`의 `calculatePrice()` 메서드는 특정 객실(`Room`)의 가격을 계산하기 위해 다음과 같은 지연 로딩(Lazy Loading) 쿼리들을 발생시킵니다.
1. `Room` 조회
2. `Room.stay` 접근 시 `Stay` 프록시 초기화 쿼리
3. `Stay.seasonPrices` 접근 시 컬렉션 초기화 쿼리
4. `Stay.discountPolicies` 접근 시 컬렉션 초기화 쿼리

이는 단일 객실 조회 시에는 큰 문제가 되지 않지만, 향후 숙소 목록 조회나 예약 가능한 여러 객실을 동시에 띄울 때 심각한 N+1 성능 저하로 이어집니다.

### 해결 방안
`RoomRepository`에 가격 계산에 필요한 연관 엔티티들을 사전에 한 번의 SQL(JOIN)로 가져오도록 커스텀 쿼리를 작성합니다.

**변경 대상:**
- `RoomRepository`에 `@EntityGraph` 또는 `JOIN FETCH`를 활용한 쿼리 추가
  ```kotlin
  @Query("SELECT r FROM Room r " +
         "JOIN FETCH r.stay s " +
         "LEFT JOIN FETCH s.seasonPrices " +
         "LEFT JOIN FETCH s.discountPolicies " +
         "WHERE r.roomNo = :roomNo")
  fun findRoomWithPricingData(@Param("roomNo") roomNo: String): Optional<Room>
  ```
- `PriceService`에서 해당 최적화된 메서드를 호출하여 불필요한 추가 쿼리 발생 차단

## 기대 효과
- **데이터 무결성 확보**: 할인 금액 및 최종 결제 금액의 1원 단위 오차 원천 차단.
- **응답 속도 개선**: DB I/O를 최소화하여 대량의 조회 요청에도 빠르고 안정적인 가격 계산 API 제공.

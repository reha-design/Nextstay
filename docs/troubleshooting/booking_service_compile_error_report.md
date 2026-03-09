# 예약 서비스 컴파일 오류 분석 및 수정 보고서

**작성일:** 2026-03-09
**대상 파일:** `com.mrmention.nextstay.domain.booking.service.BookingService.kt`

## 1. 발생 현상 (Symptom)

백엔드 컴파일 중 `BookingService.kt`에서 요금 정보를 참조할 수 없는 에러 발생:

```text
e: BookingService.kt:74:36 Unresolved reference 'finalTotalPrice'.
```

## 2. 원인 분석 (Root Cause Analysis)

### 2.1 요금 계산 DTO 구조 고도화

동적 가격 엔진(`PricingEngine`)을 도입하면서 기존의 단순한 요금 반환 방식에서, 요금 상세(`pricing`)와 전시용 정보(`display`)를 분리한 계층형 DTO(`PriceCalculationResponse`)로 구조가 고도화되었습니다.

### 2.2 누락된 종속 피드백 (Broken Dependency)

`BookingService`는 예약을 생성할 때 최종 요금 검증을 위해 `PriceService`를 호출합니다. `PriceService`가 반환하는 타입인 `PriceCalculationResponse`의 구조가 변경되었으나, 이를 사용하는 `BookingService`의 코드가 이전의 평면적(Flat) 구조를 그대로 참조하고 있어 `finalTotalPrice` 필드를 찾지 못하는 문제가 발생했습니다.

## 3. 수정 내용 (Solution)

### 3.1 계층적 참조 적용

`PriceCalculationResponse` 내의 `pricing` 객체를 거쳐 `finalTotalPrice`에 접근하도록 수정했습니다.

#### 수정 전 (오류 발생):

```kotlin
totalPrice = priceInfo.finalTotalPrice
```

#### 수정 후 (해결):

```kotlin
// pricing 객체 하위의 필드를 명시적으로 참조
totalPrice = priceInfo.pricing.finalTotalPrice
```

## 4. 수정 결과 (Result)

- **컴파일 정상화:** `BookingService`의 참조 오류가 해결되었습니다.
- **데이터 일관성:** 고도화된 가격 엔진의 계산 결과가 실제 예약 데이터 생성 시에도 정확하게 반영됩니다.

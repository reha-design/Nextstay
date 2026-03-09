# 백엔드 컴파일 오류 분석 및 수정 보고서

**작성일:** 2026-03-09
**대상 파일:** `com.mrmention.nextstay.domain.price.service.PricingEngine.kt`

## 1. 발생 현상 (Symptom)

Gradle 빌드(`compileKotlin`) 중 다음과 같은 유형의 에러가 다수 발생하며 컴파일이 중단됨:

```text
e: PricingEngine.kt:38:21 Smart cast to 'Int' is impossible, because 'weekendPrice' is a property that has an open or custom getter.
e: PricingEngine.kt:54:49 Smart cast to 'LocalDate' is impossible, because 'startDate' is a property that has an open or custom getter.
```

## 2. 원인 분석 (Root Cause Analysis)

### 2.1 코틀린의 스마트 캐스팅(Smart Cast) 제약

코틀린은 변수가 `null`이 아님을 확인하면 해당 블록 내에서 자동으로 Non-nullable 타입으로 변환해주는 "스마트 캐스팅" 기능을 제공합니다. 그러나 다음의 경우 스마트 캐스팅이 제한됩니다:

- **커스텀 게터가 있는 프로퍼티:** 게터가 매번 다른 값을 반환할 가능성이 있으므로 컴파일러는 `null` 체크 직후에도 값이 유지된다고 보장할 수 없습니다.
- **가변 프로퍼티 (var):** 다른 스레드나 로직에 의해 값이 변경될 수 있습니다.
- **Open 클래스의 프로퍼티:** 상속받은 하위 클래스에서 게터를 재정의할 수 있어 값이 고정되지 않습니다.

### 2.2 JPA 엔티티와 프록시 객체

현재 오류가 발생한 프로퍼티들(`weekendPrice`, `startDate` 등)은 JPA 엔티티(`RoomPriceSchedule`, `DiscountPolicy`)의 필드입니다.

- JPA(Hibernate)는 지연 로딩 등을 구현하기 위해 엔티티를 상속받은 **프록시 객체**를 생성합니다.
- 이 과정에서 모든 프로퍼티는 이론적으로 `open` 상태이거나 커스텀 로직(게터)을 포함할 수 있게 됩니다.
- 따라서 코틀린 컴파일러는 엔티티의 프로퍼티에 대해 `if (x != null)` 체크를 하더라도, 실제 사용 시점(`x.toLong()`)에 여전히 `null`이 아님을 확신할 수 없다고 판단합니다.

## 3. 수정 내용 (Solution)

### 3.1 로컬 변수 복사 기법 (Local Variable Snapshot)

가장 안전하고 권장되는 해결책은 **변경 불가능한(val) 로컬 변수에 값을 복사**하여 사용하는 것입니다. 로컬 변수는 스레드 안전성이 보장되며 외부에서 값이 바뀔 염려가 없으므로 스마트 캐스팅이 완벽하게 작동합니다.

#### 수정 전 (오류 발생):

```kotlin
if (schedule.weekendPrice != null) {
    // 오류: schedule.weekendPrice가 사용 시점에 null로 바뀔 수 있다고 판단함
    return schedule.weekendPrice.toLong()
}
```

#### 수정 후 (해결):

```kotlin
val weekendPrice = schedule.weekendPrice // 로컬 변수에 스냅샷 저장
if (weekendPrice != null) {
    // 이제 스마트 캐스팅이 정상 작동함 (Int? -> Int)
    return weekendPrice.toLong()
}
```

### 3.2 람다식 내 변수 캡처 활용

`filter`나 `find`와 같은 고차 함수 내에서도 프로퍼티를 직접 참조하는 대신 로컬 변수를 선언하여 비교 로직을 수행하도록 변경했습니다.

## 4. 수정 결과 (Result)

- **컴파일 성공:** 모든 스마트 캐스팅 관련 에러가 제거되었습니다.
- **안정성 향상:** 다중 스레드 환경 등 변수 값이 변할 수 있는 상황에서도 일관된 요금 계산 로직을 보장합니다.
- **성능 유지:** 불필요한 게터 호출을 줄이고 로컬 스택 변수를 활용함으로써 미세한 성능 최적화 효과도 기대할 수 있습니다.

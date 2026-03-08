# Nuxt 3 SSR 인증(Authentication) 아키텍처 가이드

본 문서는 Nextstay 프로젝트에 적용된 **Universal Cookie & Global Interceptor** 패턴 기반의 SSR 인증 처리 아키텍처의 설계 배경과 상세 구현 방식을 정리한 가이드입니다. 

---

## 1. 아키텍처 개요 및 설계 배경

Nuxt 3와 같은 SSR(Server-Side Rendering) 프레임워크는 코드가 브라우저(Client)뿐만 아니라 Node.js 기반의 Nuxt 서버(Server)에서도 실행된다는 **아이소모픽(Isomorphic)** 특성을 가집니다. 

기존 SPA(Single Page Application) 환경에서는 인증 토큰(JWT 등)을 브라우저의 `LocalStorage`에 저장하고 통신할 때 꺼내어 쓰는 것이 일반적이었습니다. 하지만 SSR 환경에서는 사용자가 페이지를 처음 방문하거나 '새로고침'을 할 때 **최초 렌더링을 Nuxt 서버가 수행**합니다. Node.js 환경인 Nuxt 서버에는 브라우저 전용 스토리지인 `LocalStorage`가 존재하지 않으므로, 토큰 값을 읽지 못해 백엔드 API 호출 시 401/403 (Unauthorized/Forbidden) 인증 에러가 발생하는 근본적인 문제가 존재합니다.

이를 해결하고 최적의 효율을 내기 위한 패턴이 **Universal Cookie & Global Interceptor** 아키텍처입니다.

---

## 2. 왜 이 패턴이 '최적의 효율'인가? (Architecture Rationale)

* **동기화 비용 제로 (Zero Synchronization Overhead):** 
  쿠키(Cookie)는 브라우저와 서버 사이를 오가는 물리적인 티켓과 같습니다. 개발자가 통신 로직에 명시적으로 코딩하지 않아도, 브라우저가 첫 요청을 보낼 때 HTTP 프로토콜 규격에 의해 해당 도메인의 쿠키를 자동으로 헤더에 싣고 서버로 전송합니다. 이로 인해 상태 동기화 누락이 원천 차단됩니다.
* **아이소모픽(Isomorphic) 일관성:** 
  Nuxt 3의 `useCookie` 컴포저블을 활용하면 동작 환경이 `process.server`인지 `process.client`인지 프레임워크가 알아서 판단하여 저장소(HTTP Header 또는 document.cookie)에 접근합니다. 즉, 개발자는 컨텍스트 분리를 고민할 필요 없이 단일 비즈니스 로직으로 대응할 수 있습니다.

---

## 3. 코드 레벨 상세 구현 (Refinement)

위 설계 철학을 반영하여 토큰 조회부터 인터셉터를 통한 헤더 주입까지 책임지는 전역 API 유틸리티(`useApi`) 구조입니다.

```typescript
// composables/useApi.ts
import { useRuntimeConfig, useFetch } from '#app'

export const useApi = <T = any>(url: string, options: any = {}) => {
  // 1. 유니버설 쿠키 호출 (서버/클라이언트 투명하게 토큰 획득)
  const token = useCookie<string | null>('auth_token')
  
  const defaults = {
    // baseURL 설정을 통해 상대 경로만으로 외부 API 호출 가능하게 설정
    // 예: useRuntimeConfig().public.apiBase
    baseURL: 'http://localhost:8080',
    
    // 2. 인터셉터: 요청이 외부 서버(Spring Boot 등)로 발송되기 직전 가로채기
    onRequest({ options }: any) {
      if (token.value) {
        options.headers = options.headers || {}
        
        // [중요 디테일] Nuxt 3 SSR의 fetch 인터셉터에서 options.headers는 
        // 자바스크립트의 기본 객체일 수도 있고, 네이티브 'Headers' 인스턴스일 수도 있습니다.
        // Headers 인스턴스인 경우 일반 객체처럼 '=' 로 할당하면 값이 무시(Silent Ignore)되므로 set을 씁니다.
        if (options.headers instanceof Headers) {
          options.headers.set('Authorization', `Bearer ${token.value}`)
        } else {
          options.headers.Authorization = `Bearer ${token.value}`
        }
      }
    },
    
    // 3. 에러 핸들링: 만료된 토큰 등에 대한 글로벌 가드 응답 처리
    onResponseError({ response }: any) {
      if (response.status === 401) {
        // 전역 상태 클리어 로직, 또는 login 리다이렉트 등 전략적 대응
        console.error('인증 토큰이 만료되었거나 유효하지 않습니다.')
      }
    }
  }
  
  // 최종적으로 사용자의 요청 옵션 스펙(options)과 글로벌 인터셉터(defaults)를 병합
  return useFetch<T>(url, { ...defaults, ...options })
}
```

---

## 4. 보안적 디테일 (Security Considerations)

완벽한 프로덕션 도입을 위해서는 저장된 쿠키(토큰) 환경에 대해 다음과 같은 추가 보안 설정을 챙기는 것이 바람직합니다.

1. **`HttpOnly: true` (XSS 방어)** 
   토큰을 굽고 저장할 때 옵션으로 부여하면 자바스크립트(`document.cookie`) 상에서 쿠키 탈취를 시도하는 XSS 형태의 공격을 막아낼 수 있습니다.
   *(단, 이 경우 클라이언트의 JS 계층(`useCookie`)을 거쳐 직접 읽는 것은 불가능해지며 오로지 브라우저에서 서버로만 전달되는 순수 SSR 목적으로 쓰입니다.)*
2. **`SameSite: 'Lax'` 또는 `'Strict'` (CSRF 방어)**
   서드파티 도메인에서 온 비정상적인 위조 요청을 막기 위해 브라우저 쿠키 정책을 설정합니다.

---

## 5. 결론

**"Universal Cookie & Global Interceptor"** 패턴의 구현 결과는 다음과 같습니다.
1. SSR 초기 렌더링 혹은 강제 새로고침 상황에서의 빈번한 네트워크 에러(Unauthorized)를 원천 차단합니다.
2. 클라이언트의 사용 상태 및 세션 유지를 안전하게 보장합니다.
3. 코드 중복(API 마다 토큰을 직접 세팅)을 극소화합니다.

이 설계는 **'Low Cost, High Impact'(최소 비용 고효율)**를 달성하는 최적의 모델이며, 프로젝트의 규모와 트래픽이 커지더라도 인증 로직 때문에 발생하는 부채비용을 크게 덜어줍니다.

import { defineNuxtRouteMiddleware, navigateTo } from '#app'
import { useAuthStore } from '~/stores/auth'

export default defineNuxtRouteMiddleware(async (to, from) => {
  const authStore = useAuthStore()
  
  // 1. 1차 검증: 쿠키(토큰) 존재 여부 확인
  if (!authStore.isAuthenticated) {
    return navigateTo({
      path: '/login',
      query: { redirect: to.fullPath }
    })
  }

  // 2. 2차 검증: 실제 백엔드 데이터와 대조 (SSR 시점에 서버에서 실행됨)
  // 매 페이지 이동마다 호출하지만, nuxtApp.payload 에 캐싱되거나 
  // 백엔드 세션 체크가 가벼울 경우 가장 확실한 동기화 방법입니다.
  const user = await authStore.fetchUser()
  
  if (!user) {
    // 백엔드에서 유효하지 않은 유저라고 판단한 경우
    console.error('[Middleware] 유효하지 않은 세션입니다. 로그인 페이지로 이동합니다.')
    return navigateTo({
      path: '/login',
      query: { redirect: to.fullPath }
    })
  }
})

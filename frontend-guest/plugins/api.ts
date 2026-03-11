import { useAuthStore } from '~/stores/auth'

/**
 * 전역 API 에러 인터셉터 플러그인
 * 
 * 모든 $fetch 요청의 응답을 가로채어 401(미인증) 또는 404(찾을 수 없음) 에러가 발생하면
 * 프론트엔드의 세션을 즉시 초기화하고 로그인 페이지로 리다이렉트합니다.
 */
export default defineNuxtPlugin((nuxtApp) => {
  const authStore = useAuthStore()

  // Nuxt 3 전역 fetch 인터셉터 설정 (ofetch 활용)
  // 모든 컴포넌트나 미들웨어에서 사용하는 $fetch 에 적용됩니다.
  const originalFetch = globalThis.$fetch

  globalThis.$fetch = originalFetch.create({
    onRequest({ request, options }) {
      const config = useRuntimeConfig()
      const baseURL = process.client ? '/api/backend-proxy' : (config.public.apiUrl as string)
      // string 이 아닐 경우 (Request 객체 등) url 속성 사용
      let url = typeof request === 'string' ? request : request.url

      // 1. /api/v1으로 시작하는 상대 경로인 경우 baseURL 자동 보완
      if (url.startsWith('/api/v1') && !url.startsWith('http')) {
        options.baseURL = baseURL
      }

      // 2. 백엔드(8080 등)로 향하는 요청에만 보안 옵션 및 토큰 적용
      const isBackend = url.includes(baseURL) || url.startsWith('/api/v1') || options.baseURL === baseURL
      
      if (isBackend) {
        options.credentials = 'include'
        
        if (authStore.token) {
          const headers = (options.headers || {}) as any
          if (headers instanceof Headers) {
            headers.set('Authorization', `Bearer ${authStore.token}`)
          } else if (Array.isArray(headers)) {
            const hasAuth = headers.some(([k]) => k.toLowerCase() === 'authorization')
            if (!hasAuth) (headers as any[]).push(['Authorization', `Bearer ${authStore.token}`])
          } else {
            headers.Authorization = `Bearer ${authStore.token}`
          }
          options.headers = headers
        }
      }
    },
    async onResponseError({ request, response, options }) {
      const url = typeof request === 'string' ? request : request.url
      
      // 401: 토큰 만료 
      if (response.status === 401) {
        if (url.includes('/api/v1/auth/refresh')) {
          console.error('[Auth Interceptor] 리프레시 토큰 만료. 세션을 초기화합니다.')
          authStore.clearAuth()
          return
        }

        try {
          console.log('[Auth Interceptor] 401 발생 (Unauthorized). 토큰 갱신 시도:', url)
          const config = useRuntimeConfig()
          const baseURL = config.public.apiUrl as string
          
          // 1. Refresh API 호출
          const refreshRes: any = await originalFetch(`${baseURL}/api/v1/auth/refresh`, {
            method: 'POST',
            credentials: 'include'
          })
          
          const newAccessToken = refreshRes.accessToken
          authStore.token = newAccessToken
          
          console.log('[Auth Interceptor] 토큰 갱신 성공. 원래 요청 재시도...')
          
          // 재시도 시 새 토큰 적용 (onRequest에서도 적용되지만 여기서 명시적으로 다시 한 번)
          const headers = (options.headers || {}) as any
          if (headers instanceof Headers) {
            headers.set('Authorization', `Bearer ${newAccessToken}`)
          } else {
            headers.Authorization = `Bearer ${newAccessToken}`
          }
          options.headers = headers
          
          return globalThis.$fetch(request, options as any)
          
        } catch (refreshErr) {
          console.error('[Auth Interceptor] 토큰 갱신 실패. 로그아웃 처리합니다.')
          authStore.clearAuth()
          
          // 클라이언트 사이드라면 리다이렉트 유도
          if (process.client) {
            const route = useRoute()
            const requiresAuth = route.meta.middleware === 'auth' || 
                                 (Array.isArray(route.meta.middleware) && route.meta.middleware.includes('auth'))

            if (requiresAuth && route.path !== '/login') {
              window.location.href = `/login?redirect=${encodeURIComponent(route.fullPath)}`
            }
          }
        }
        return
      }

      // 404 처리 (특수 경로만)
      if (response.status === 404 && (url.includes('/api/v1/users/me') || url.includes('/api/v1/auth'))) {
        authStore.clearAuth()
      }
    }
  })
})

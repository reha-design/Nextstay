import { useRuntimeConfig, useFetch } from '#app'
import { useAuthStore } from '~/stores/auth'

export const useApi = <T = any>(url: string, options: any = {}) => {
  const baseURL = 'http://localhost:8080'
  const token = useCookie<string | null>('auth_token')
  const authStore = useAuthStore()

  const defaults = {
    baseURL,
    onRequest({ options }: any) {
      if (token.value) {
        if (options.headers instanceof Headers) {
          options.headers.set('Authorization', `Bearer ${token.value}`)
        } else {
          options.headers = {
            ...options.headers,
            Authorization: `Bearer ${token.value}`
          }
        }
      }
    },
    async onResponseError({ request, response, options }: any) {
      if (response.status === 401) {
        try {
          // 1. Refresh endpoint 호출
          const refreshRes: any = await $fetch(`${baseURL}/api/v1/auth/refresh`, {
            method: 'POST'
          })
          
          const newAccessToken = refreshRes.accessToken
          
          // 2. 새로운 토큰을 쿠키에 갱신
          token.value = newAccessToken
          
          if (import.meta.server) {
             const { appendResponseHeader } = await import('h3')
             const nuxtApp = useNuxtApp()
             const event = nuxtApp.ssrContext?.event
             if (event) {
               // 브라우저 갱신을 위해 SSR 응답 헤더에 명시적으로 Set-Cookie 추가
               appendResponseHeader(event, 'Set-Cookie', `auth_token=${newAccessToken}; Path=/;`)
             }
          }

          // 3. 원래 실패했던 요청에 새로운 토큰 갱신
          options.headers = options.headers || {}
          if (options.headers instanceof Headers) {
            options.headers.set('Authorization', `Bearer ${newAccessToken}`)
          } else {
            options.headers = {
              ...options.headers,
              Authorization: `Bearer ${newAccessToken}`
            }
          }

          // 4. 원본 요청 재실행
          return $fetch(request, options)
          
        } catch (refreshErr) {
          // 리프레시 토큰까지 만료/유효하지 않은 경우
          console.error('[Refresh Failed] Token expired or invalid', refreshErr)
          authStore.clearAuth()
        }
      }
    }
  }

  const finalOptions = { ...defaults, ...options }
  
  return useFetch<T>(url, finalOptions)
}

import { useRuntimeConfig, useFetch } from '#app'
import { useAuthStore } from '~/stores/auth'

export const useApi = <T = any>(url: string, options: any = {}) => {
  const baseURL = 'http://localhost:8080'
  const token = useCookie<string | null>('auth_token')
  const authStore = useAuthStore()

  const defaults = {
    baseURL,
    credentials: 'include' as const, // Cross-Origin 쿠키 전송 허용
  }

  const finalOptions = { ...defaults, ...options }
  
  return useFetch<T>(url, finalOptions)
}

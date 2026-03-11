import { useRuntimeConfig, useFetch } from '#app'
import { useAuthStore } from '~/stores/auth'

export const useApi = <T = any>(url: string, options: any = {}) => {
  const config = useRuntimeConfig()
  const baseURL = process.client ? '/api/backend-proxy' : config.public.apiUrl
  const token = useCookie<string | null>('auth_token')
  const authStore = useAuthStore()

  if (process.server) {
    console.log(`[useApi SSR] Fetching: ${config.public.apiUrl}${url}`)
  }

  const defaults = {
    baseURL,
    credentials: 'include' as const, // Cross-Origin 쿠키 전송 허용
  }

  const finalOptions = { ...defaults, ...options }
  
  return useFetch<T>(url, finalOptions)
}

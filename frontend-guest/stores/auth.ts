import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  const token = useCookie<string | null>('auth_token', { maxAge: 60 * 60 * 24 * 7 })
  const user = useCookie<any | null>('auth_user', { maxAge: 60 * 60 * 24 * 7 })

  const isAuthenticated = computed(() => !!token.value)
  const isHost = computed(() => user.value?.role === 'HOST')

  function setAuth(newToken: string, userData: any) {
    token.value = newToken
    user.value = userData
  }

  function clearAuth() {
    token.value = null
    user.value = null
    // Nuxt 3 useCookie(name).value = null 이 가장 확실한 삭제 방법입니다.
  }

  /**
   * 백엔드에서 현재 로그인된 유저 정보를 가져와 세션 유효성을 검증합니다.
   * 유효하지 않은 세션(401, 404 등)일 경우 세션을 초기화합니다.
   */
  async function fetchUser() {
    if (!token.value) return null

    try {
      // $fetch 는 전역 플러그인에서 설정된 인터셉터를 사용하게 됩니다.
      const data = await $fetch<any>('/api/v1/users/me', {
        headers: {
          Authorization: `Bearer ${token.value}`
        },
        credentials: 'include'
      })
      user.value = data
      return data
    } catch (error: any) {
      // 401(권한없음) 또는 404(유저삭제) 시 클라이언트 세션 파기
      if (error.status === 401 || error.status === 404) {
        clearAuth()
      }
      return null
    }
  }

  function loadAuth() {
    // Nuxt 3 useCookie handles initial load automatically.
    // Provided for backward compatibility with app.vue and components.
  }

  return { token, user, isAuthenticated, isHost, setAuth, clearAuth, fetchUser, loadAuth }
})

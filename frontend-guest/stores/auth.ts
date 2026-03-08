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
  }

  function loadAuth() {
    // Already handled by useCookie reactivity in Nuxt 3
  }

  return { token, user, isAuthenticated, isHost, setAuth, clearAuth, loadAuth }
})

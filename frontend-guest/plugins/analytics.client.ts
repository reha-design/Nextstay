import { useAnalytics } from "~/composables/useAnalytics"

export default defineNuxtPlugin((nuxtApp) => {
  const { logVisit } = useAnalytics()
  const router = useRouter()

  // 클라이언트 사이드에서만 동작하도록 설정 (선택 사항, SSR에서도 로깅하려면 제거)
  if (process.client) {
    // 라우트 변경 시마다 방문 로직 실행
    router.afterEach((to) => {
      logVisit(to.fullPath)
    })

    // 초기 로드 시 실행
    logVisit(router.currentRoute.value.fullPath)
  }
})

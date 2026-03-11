import { edenTreaty } from '@elysiajs/eden'
import type { App } from '../../backend-analytics/src/index'

export const useAnalytics = () => {
  const config = useRuntimeConfig()
  
  // 분석 서버 URL (서버 사이드 프록시 사용)
  const analyticsUrl = '/api/analytics-proxy'
  
  // Eden Treaty 클라이언트 생성
  const client = edenTreaty<App>(analyticsUrl)

  /**
   * 단순 이벤트 로깅
   */
  const logEvent = async (eventName: string, payload: any = {}) => {
    try {
      const { data, error } = await client.analytics.event.post({
        eventName,
        payload,
        timestamp: Date.now()
      })
      
      if (error) {
        console.error('[Analytics] Event log failed:', error)
      }
      return { data, error }
    } catch (err) {
      console.error('[Analytics] Network error:', err)
    }
  }

  /**
   * 페이지 방문 로깅
   */
  const logVisit = async (path: string) => {
    const authStore = useAuthStore() // 인증 스토어에서 유저 정보 가져오기
    
    try {
      const { data, error } = await client.stats.v1.visits.post({
        path,
        userId: authStore.user?.userNo || undefined,
        userAgent: typeof window !== 'undefined' ? window.navigator.userAgent : 'SSR'
      })
      
      if (error) {
        console.warn('[Analytics] Visit log failed:', error)
      }
      return { data, error }
    } catch (err) {
      // 분석 서비스 장애가 메인 서비스에 영향을 주지 않도록 에러만 기록
      console.warn('[Analytics] Service unavailable')
    }
  }

  return {
    logEvent,
    logVisit,
    client
  }
}

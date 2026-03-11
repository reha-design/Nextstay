<template>
  <div class="container onboarding-container">
    <div class="onboarding-card">
      <div class="header">
        <h1>호스트 온보딩</h1>
        <p>넥스트스테이 호스트가 되신 것을 환영합니다! 서비스를 이용하시려면 특례 신청을 완료해야 합니다.</p>
      </div>

      <div class="steps">
        <div class="step active">
          <div class="step-icon">1</div>
          <div class="step-text">사업자 유형 선택</div>
        </div>
        <div class="step">
          <div class="step-icon">2</div>
          <div class="step-text">필수 서류 업로드</div>
        </div>
        <div class="step">
          <div class="step-icon">3</div>
          <div class="step-text">심사 대기</div>
        </div>
      </div>

      <div class="content">
        <div class="info-box">
          <h3>특례 신청이란?</h3>
          <p>미스터멘션(넥스트스테이)은 합법적인 숙박 운영을 지원합니다. 서류 제출 후 심사가 완료되면 정식 호스트 활동이 가능합니다.</p>
        </div>

        <div class="form-placeholder">
          <p>계획된 온보딩 폼이 여기에 위치합니다.</p>
          <button @click="handleCompleteOnboarding" class="complete-btn" :disabled="loading">
            {{ loading ? '처리 중...' : '임시 완료 처리 (테스트용)' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '~/stores/auth'

const router = useRouter()
const authStore = useAuthStore()
const loading = ref(false)

const handleCompleteOnboarding = async () => {
  loading.value = true
  try {
    // 백엔드 API 호출: 온보딩 완료 처리 및 새 토큰 수신
    // (현재는 fetch 직접 호출 대신 authStore나 커스텀 API 유틸 사용 가능)
    const response = await $fetch<any>('/api/v1/hosts/onboarding/complete', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${authStore.token}`
      }
    })

    if (response.accessToken) {
      // 새로운 토큰으로 스토어 갱신 (응답바디의 정보를 기반으로)
      authStore.setAuth(response.accessToken, response)
      alert('온보딩이 완료되었습니다! 대시보드로 이동합니다.')
      router.push('/')
    }
  } catch (err: any) {
    alert('오류가 발생했습니다: ' + (err.data?.message || err.message))
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.onboarding-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 80vh;
  padding: 2rem;
}

.onboarding-card {
  background: white;
  padding: 3rem;
  border-radius: 20px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.05);
  max-width: 600px;
  width: 100%;
  text-align: center;
}

.header {
  margin-bottom: 2rem;
  h1 { font-size: 2rem; margin-bottom: 0.5rem; color: #1a1a1a; }
  p { color: #666; }
}

.steps {
  display: flex;
  justify-content: space-between;
  margin-bottom: 3rem;
  position: relative;

  &::before {
    content: '';
    position: absolute;
    top: 20px;
    left: 10%;
    right: 10%;
    height: 2px;
    background: #eee;
    z-index: 1;
  }
}

.step {
  position: relative;
  z-index: 2;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.5rem;

  .step-icon {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    background: white;
    border: 2px solid #eee;
    display: flex;
    justify-content: center;
    align-items: center;
    font-weight: bold;
    color: #999;
  }

  &.active .step-icon {
    border-color: #006ce4;
    color: #006ce4;
    background: #f0f7ff;
  }

  .step-text { font-size: 0.8rem; color: #666; }
}

.info-box {
  background: #f8fbff;
  padding: 1.5rem;
  border-radius: 12px;
  text-align: left;
  margin-bottom: 2rem;
  border-left: 4px solid #006ce4;

  h3 { font-size: 1rem; margin-bottom: 0.5rem; color: #006ce4; }
  p { font-size: 0.9rem; color: #444; line-height: 1.5; }
}

.complete-btn {
  background: #006ce4;
  color: white;
  border: none;
  padding: 1rem 2rem;
  border-radius: 8px;
  font-weight: bold;
  cursor: pointer;
  width: 100%;
  transition: background 0.2s;

  &:hover { background: #0056b3; }
  &:disabled { background: #ccc; cursor: not-allowed; }
}
</style>

<template>
  <div class="container auth-container">
    <div class="auth-card">
      <h2>회원가입</h2>
      <p class="subtitle">넥스트스테이와 함께 새로운 여행을 시작하세요.</p>

      <!-- Role Selector -->
      <div class="role-selector">
        <div 
          class="role-card" 
          :class="{ active: form.role === 'GUEST' }"
          @click="form.role = 'GUEST'"
        >
          <div class="emoji">🧳</div>
          <div class="role-info">
            <span class="role-title">여행자 (GUEST)</span>
            <span class="role-desc">새로운 여행을 계획하는 게스트</span>
          </div>
          <div class="check-mark" v-if="form.role === 'GUEST'">✅</div>
        </div>
        <div 
          class="role-card" 
          :class="{ active: form.role === 'HOST' }"
          @click="form.role = 'HOST'"
        >
          <div class="emoji">🏠</div>
          <div class="role-info">
            <span class="role-title">호스트 (HOST)</span>
            <span class="role-desc">숙소를 운영하고 관리하는 호스트</span>
          </div>
          <div class="check-mark" v-if="form.role === 'HOST'">✅</div>
        </div>
      </div>
      
      <form class="auth-form" @submit.prevent="handleSignup">
        <div class="form-group">
          <label for="name">이름</label>
          <input type="text" id="name" v-model="form.name" placeholder="이름" required maxlength="100" />
        </div>

        <div class="form-group">
          <label for="email">이메일</label>
          <input type="email" id="email" v-model="form.email" placeholder="이메일 주소를 입력하세요" required maxlength="255" />
        </div>
        
        <div class="form-group">
          <label for="password">비밀번호</label>
          <input type="password" id="password" v-model="form.password" placeholder="비밀번호(8자리 이상)" required minlength="8" maxlength="72" />
        </div>

        <div class="form-group">
          <label for="passwordConfirm">비밀번호 확인</label>
          <input type="password" id="passwordConfirm" v-model="form.passwordConfirm" placeholder="비밀번호를 다시 입력하세요" required maxlength="72" />
        </div>

        <div class="form-group">
          <label for="phone">전화번호</label>
          <input type="tel" id="phone" v-model="form.phone" v-phone placeholder="010-0000-0000" required />
        </div>

        <div class="form-checkbox">
          <input type="checkbox" id="termsAgreed" v-model="form.termsAgreed" required />
          <label for="termsAgreed">이용약관 및 개인정보 수집 동의 (필수)</label>
        </div>

        <div class="form-checkbox">
          <input type="checkbox" id="marketingAgreed" v-model="form.marketingAgreed" />
          <label for="marketingAgreed">마케팅 정보 수신 동의 (선택)</label>
        </div>

        <div class="form-error" v-if="error">{{ error }}</div>
        
        <button type="submit" class="auth-button" :disabled="loading">
          {{ loading ? '가입 처리 중...' : '회원가입' }}
        </button>
      </form>
      
      <div class="auth-footer">
        이미 계정이 있으신가요? <NuxtLink to="/login">로그인</NuxtLink>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '~/stores/auth'

definePageMeta({
  middleware: ['guest']
})

const router = useRouter()
const authStore = useAuthStore()

const form = reactive({
  name: '',
  email: '',
  password: '',
  passwordConfirm: '',
  phone: '',
  termsAgreed: false,
  marketingAgreed: false,
  role: 'GUEST' // Default to guest
})

const loading = ref(false)
const error = ref('')

const handleSignup = async () => {
  if (form.password !== form.passwordConfirm) {
    error.value = '비밀번호가 일치하지 않습니다.'
    return
  }

  loading.value = true
  error.value = ''
  
  try {
    const data = await $fetch<any>('/api/v1/auth/signup', {
      method: 'POST',
      body: {
        role: form.role,
        name: form.name,
        email: form.email,
        password: form.password,
        passwordConfirm: form.passwordConfirm,
        phone: form.phone,
        termsAgreed: form.termsAgreed,
        marketingAgreed: form.marketingAgreed
      },
      credentials: 'include' // Cross-Origin 쿠키(Refresh Token) 수신 허용
    })

    if (data) {
      if (data.onboardingStatus === 'PENDING') {
        alert('회원가입이 완료되었습니다! 호스트 온보딩 페이지로 이동합니다.')
        router.push('/hosts/onboarding')
      } else {
        alert('회원가입이 완료되었습니다! 로그인해 주세요.')
        const route = useRoute()
        router.push({
          path: '/login',
          query: route.query
        })
      }
    }
  } catch (err: any) {
    if (err.status === 409) {
      error.value = '이미 가입된 이메일입니다.'
    } else if (err.status === 400) {
      const dataStr = err.data
      if (typeof dataStr === 'object' && dataStr.message) {
          error.value = dataStr.message
      } else if (typeof dataStr === 'object' && Object.keys(dataStr).length > 0) {
          error.value = Object.values(dataStr)[0] as string
      } else {
          error.value = '입력 항목을 다시 확인해주세요.'
      }
    } else {
      error.value = '회원가입 중 오류가 발생했습니다.'
    }
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.auth-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 70vh;
}

.auth-card {
  background: #ffffff;
  padding: 3rem 2.5rem;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
  width: 100%;
  max-width: 480px;

  h2 {
    font-size: 2rem;
    margin-bottom: 0.5rem;
    color: #1a1a1a;
    font-weight: 800;
  }

  .subtitle {
    margin-bottom: 2rem;
  }
}

.role-selector {
  display: flex;
  gap: 1rem;
  margin-bottom: 2rem;

  @media (max-width: 480px) {
    flex-direction: column;
  }
}

.role-card {
  flex: 1;
  padding: 1.5rem 1rem;
  border: 2px solid #eee;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  position: relative;
  background: #fdfdfd;

  &:hover {
    transform: translateY(-5px);
    border-color: #a0c4f8;
    background: #f8fbff;
  }

  &.active {
    border-color: #006ce4;
    background: #f0f7ff;
    box-shadow: 0 4px 12px rgba(0, 108, 228, 0.1);

    .role-title {
      color: #006ce4;
    }
  }

  .emoji {
    font-size: 3.5rem;
    margin-bottom: 1rem;
    line-height: 1;
    font-family: "Apple Color Emoji", "Segoe UI Emoji", "Noto Color Emoji", sans-serif;
  }

  .role-info {
    display: flex;
    flex-direction: column;
    gap: 0.25rem;
  }

  .role-title {
    font-size: 1rem;
    font-weight: 700;
    color: #333;
  }

  .role-desc {
    font-size: 0.8rem;
    color: #777;
    word-break: keep-all;
  }

  .check-mark {
    position: absolute;
    top: 8px;
    right: 8px;
    font-size: 1.2rem;
  }
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;

  label {
    font-size: 0.9rem;
    font-weight: 600;
    color: #333;
  }

  input {
    padding: 0.75rem 1rem;
    border: 1px solid #ccc;
    border-radius: 6px;
    font-size: 1rem;
    outline: none;
    transition: border-color 0.2s;

    &:focus {
      border-color: #006ce4;
    }
  }
}

.form-checkbox {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-top: 0.25rem;

  input[type="checkbox"] {
    width: 1.1rem;
    height: 1.1rem;
    cursor: pointer;
  }

  label {
    font-size: 0.9rem;
    color: #444;
    cursor: pointer;
  }
}

.form-error {
  color: #d32f2f;
  font-size: 0.9rem;
  background: #fdeceb;
  padding: 0.75rem;
  border-radius: 4px;
}

.auth-button {
  background-color: #006ce4;
  color: white;
  border: none;
  padding: 1rem;
  font-size: 1.1rem;
  font-weight: bold;
  border-radius: 6px;
  cursor: pointer;
  margin-top: 1rem;
  transition: background-color 0.2s;

  &:hover:not(:disabled) {
    background-color: #0056b3;
  }

  &:disabled {
    background-color: #a0c4f8;
    cursor: not-allowed;
  }
}

.auth-footer {
  text-align: center;
  margin-top: 2rem;
  font-size: 0.95rem;
  color: #666;

  a {
    color: #006ce4;
    text-decoration: none;
    font-weight: 600;

    &:hover {
      text-decoration: underline;
    }
  }
}
</style>

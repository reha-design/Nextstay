<template>
  <div class="container auth-container">
    <div class="auth-card">
      <h2>로그인</h2>
      <p class="subtitle">넥스트스테이에 다시 오신 것을 환영합니다.</p>
      
      <form class="auth-form" @submit.prevent="handleLogin">
        <div class="form-group">
          <label for="email">이메일</label>
          <input type="email" id="email" v-model="form.email" placeholder="이메일 주소를 입력하세요" required />
        </div>
        
        <div class="form-group">
          <label for="password">비밀번호</label>
          <input type="password" id="password" v-model="form.password" placeholder="비밀번호를 입력하세요" required />
        </div>

        <div class="form-error" v-if="error">{{ error }}</div>
        
        <button type="submit" class="auth-button" :disabled="loading">
          {{ loading ? '로그인 중...' : '로그인' }}
        </button>
      </form>
      
      <div class="auth-footer">
        계정이 없으신가요? <NuxtLink :to="{ path: '/signup', query: $route.query }">회원가입</NuxtLink>
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
  email: '',
  password: ''
})

const loading = ref(false)
const error = ref('')

const handleLogin = async () => {
  loading.value = true
  error.value = ''
  
  try {
    const data = await $fetch('/api/v1/auth/login', {
      method: 'POST',
      body: { email: form.email, password: form.password },
      credentials: 'include'
    })

    if (data) {
      const responseData = data as any
      // Store token and user data in Pinia
      authStore.setAuth(responseData.accessToken, {
        userNo: responseData.userNo,
        email: responseData.email,
        name: responseData.name,
        phone: responseData.phone,
        role: responseData.role
      })
      
      const route = useRoute()
      const redirectPath = (route.query.redirect as string) || '/'
      router.push(redirectPath)
    }
  } catch (err: any) {
    if (err.status === 401) {
      error.value = '이메일 또는 비밀번호가 올바르지 않습니다.'
    } else {
      error.value = err.data?.message || '로그인 중 오류가 발생했습니다.'
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
  min-height: 60vh;
}

.auth-card {
  background: #ffffff;
  padding: 3rem 2.5rem;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
  width: 100%;
  max-width: 440px;

  h2 {
    font-size: 2rem;
    margin-bottom: 0.5rem;
    color: #1a1a1a;
    font-weight: 800;
  }

  .subtitle {
    color: #666;
    margin-bottom: 2rem;
  }
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
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
  margin-top: 0.5rem;
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

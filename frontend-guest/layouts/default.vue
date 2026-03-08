<template>
  <div class="app-layout">
    <header class="app-header">
      <div class="container">
        <div class="logo">
          <NuxtLink to="/">Nextstay</NuxtLink>
        </div>
        <nav class="auth-nav">
          <template v-if="authStore.isAuthenticated">
            <span class="welcome-text">환영합니다, <strong>{{ authStore.user?.name || '고객' }}</strong>님</span>
            <button @click="handleLogout" class="nav-link logout-btn">로그아웃</button>
          </template>
          <template v-else>
            <NuxtLink to="/login" class="nav-link">로그인</NuxtLink>
            <NuxtLink to="/signup" class="nav-link">회원가입</NuxtLink>
          </template>
        </nav>
      </div>
    </header>
    
    <main class="app-main">
      <slot />
    </main>
    
    <footer class="app-footer">
      <div class="container">
        <p>&copy; 2026 Nextstay. All rights reserved.</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { useAuthStore } from '~/stores/auth'
import { useRouter } from 'vue-router'

const authStore = useAuthStore()
const router = useRouter()

const handleLogout = () => {
  authStore.clearAuth()
  router.push('/login')
}
</script>

<style lang="scss">
body {
  margin: 0;
  font-family: 'Inter', 'Noto Sans KR', sans-serif;
  background-color: #f8f9fa;
  color: #333;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 1.5rem;
  width: 100%;
  box-sizing: border-box;
}

.app-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.app-header {
  background-color: #ffffff;
  border-bottom: 1px solid #eaeaea;
  padding: 1rem 0;

  .container {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .logo a {
    font-size: 1.5rem;
    font-weight: 800;
    color: #006ce4; // Booking.com like blue
    text-decoration: none;
    letter-spacing: -0.5px;
  }

  .auth-nav {
    display: flex;
    gap: 0.5rem;
    align-items: center;

    .welcome-text {
      font-size: 0.95rem;
      color: #333;
      margin-right: 0.5rem;
    }

    .nav-link {
      text-decoration: none;
      color: #1a1a1a;
      font-weight: 600;
      font-size: 0.95rem;
      padding: 0.5rem 1rem;
      border-radius: 4px;
      transition: background-color 0.2s, color 0.2s;
      background: none;
      border: none;
      cursor: pointer;

      &:hover {
        background-color: #f0f2f5;
        color: #006ce4;
      }
    }
    
    .logout-btn {
      color: #d32f2f;
      &:hover {
        color: #d32f2f;
        background-color: #fdeceb;
      }
    }
  }
}

.app-main {
  flex: 1;
  padding: 2rem 0;
}

.app-footer {
  background-color: #ffffff;
  border-top: 1px solid #eaeaea;
  padding: 2rem 0;
  text-align: center;
  color: #666;
  font-size: 0.875rem;
}
</style>

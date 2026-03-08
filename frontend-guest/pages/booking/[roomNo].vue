<template>
  <div class="booking-page container">
    <header class="booking-header">
      <h1>예약 확인 및 결제</h1>
      <p>선택하신 객실의 정보를 확인하고 예약을 완료해 주세요.</p>
    </header>

    <div v-if="pending" class="loading-state">
      <div class="loader"></div>
      <p>정보를 불러오고 있습니다...</p>
    </div>

    <div v-else-if="error" class="error-state">
      <p>정보를 불러오는 중 오류가 발생했습니다.</p>
      <button @click="$router.back()" class="back-btn">뒤로 가기</button>
    </div>

    <div v-else class="booking-layout">
      <!-- Main Content -->
      <main class="booking-main">
        <!-- 1. Room Summary -->
        <section class="booking-card room-summary">
          <div v-if="roomInfo" class="room-preview">
            <img :src="roomInfo.imageUrls?.[0] || 'https://picsum.photos/seed/room/800/600'" :alt="roomInfo.name" />
            <div class="room-text">
              <span class="category-tag">객실 정보</span>
              <h2>{{ roomInfo.name }}</h2>
              <p class="stay-name">{{ roomInfo.stayName }}</p>
            </div>
          </div>
          <div class="schedule-info">
            <div class="info-item">
              <span class="label">체크인</span>
              <span class="date">{{ checkIn }} (15:00)</span>
            </div>
            <div class="info-item">
              <span class="label">체크아웃</span>
              <span class="date">{{ checkOut }} (11:00)</span>
            </div>
            <div class="info-item">
              <span class="label">숙박 기간</span>
              <span class="value">{{ priceInfo?.totalNights }}박</span>
            </div>
          </div>
        </section>

        <!-- 2. Guest Info -->
        <section class="booking-card guest-info">
          <h3 class="card-title">예약자 정보</h3>
          <div class="form-group">
            <label>성함</label>
            <input type="text" :value="authStore.user?.name" disabled />
          </div>
          <div class="form-group">
            <label>연락처</label>
            <input type="text" v-model="guestPhone" v-phone placeholder="'-' 없이 입력" />
          </div>
          <p class="notice">입력하신 연락처로 예약 확정 메시지가 발송됩니다.</p>
        </section>

        <!-- 3. Payment Method -->
        <section class="booking-card payment-method">
          <h3 class="card-title">결제 수단 선택</h3>
          <div class="method-options">
            <label class="option active">
              <input type="radio" name="payment" checked />
              <span class="label-text">신용/체크카드</span>
            </label>
            <label class="option">
              <input type="radio" name="payment" />
              <span class="label-text">간편 결제</span>
            </label>
            <label class="option">
              <input type="radio" name="payment" />
              <span class="label-text">무통장 입금</span>
            </label>
          </div>
        </section>
      </main>

      <!-- Sidebar: Price Summary -->
      <aside class="booking-sidebar">
        <div class="price-sticky-card">
          <h3 class="card-title">결제 상세</h3>
          <div class="price-rows">
            <div class="row">
              <span class="label">객실 요금 ({{ priceInfo?.totalNights }}박)</span>
              <span class="val">₩{{ priceInfo?.totalBasePrice?.toLocaleString() }}</span>
            </div>
            <div v-if="priceInfo?.discountAmount > 0" class="row discount">
              <span class="label">연박 할인</span>
              <span class="val">-₩{{ priceInfo?.discountAmount?.toLocaleString() }}</span>
            </div>
            <hr />
            <div class="row total">
              <span class="label">최종 결제 금액</span>
              <span class="val">₩{{ priceInfo?.finalTotalPrice?.toLocaleString() }}</span>
            </div>
          </div>
          
          <div class="terms-agreements">
            <label class="checkbox-label">
              <input type="checkbox" v-model="agreed" />
              <span>취소 규정 및 이용 약관에 동의합니다. (필수)</span>
            </label>
          </div>

          <button 
            class="pay-submit-btn" 
            :disabled="!agreed || submintting"
            @click="handleBooking"
          >
            {{ submintting ? '처리 중...' : `₩${priceInfo?.finalTotalPrice?.toLocaleString()} 결제하기` }}
          </button>
        </div>
      </aside>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '~/stores/auth'
import { useApi } from '~/composables/useApi'

definePageMeta({
  middleware: 'auth'
})

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const roomNo = route.params.roomNo as string
const checkIn = route.query.checkIn as string
const checkOut = route.query.checkOut as string

const agreed = ref(false)
const submintting = ref(false)
const guestPhone = ref(authStore.user?.phone || '')

// 1. Fetch Price Info
const { data: priceInfo, pending: pricePending, error: priceError } = await useApi(`/api/v1/rooms/${roomNo}/calculate-price`, {
  method: 'POST',
  body: { checkInDate: checkIn, checkOutDate: checkOut }
})

// 2. Fetch Room Detail
const { data: roomInfo, pending: roomPending } = await useApi(`/api/v1/rooms/${roomNo}`)

const pending = computed(() => pricePending.value || roomPending.value)
const error = computed(() => priceError.value)

const handleBooking = async () => {
  if (!agreed.value) return
  
  submintting.value = true
  try {
    await $fetch(`/api/v1/rooms/${roomNo}/bookings`, {
      baseURL: 'http://localhost:8080',
      method: 'POST',
      headers: {
        Authorization: `Bearer ${authStore.token}`
      },
      body: {
        roomNo,
        checkInDate: checkIn,
        checkOutDate: checkOut
      }
    })

    alert('예약이 성공적으로 완료되었습니다!')
    router.push('/mypage/bookings')
  } catch (e: any) {
    console.error(e)
    alert('예약 처리 중 오류가 발생했습니다: ' + (e.response?._data?.message || '잠시 후 다시 시도해 주세요.'))
  } finally {
    submintting.value = false
  }
}
</script>

<style lang="scss" scoped>
$primary: #7575ff;
$bg-light: #f8faff;
$border: #eee;

.booking-page {
  padding-top: 40px;
  padding-bottom: 80px;
}

.booking-header {
  margin-bottom: 40px;
  h1 { font-size: 2rem; font-weight: 800; margin-bottom: 10px; }
  p { color: #666; font-size: 1.1rem; }
}

.booking-layout {
  display: grid;
  grid-template-columns: 1fr 400px;
  gap: 40px;

  @media (max-width: 1024px) {
    grid-template-columns: 1fr;
  }
}

.booking-card {
  background: white;
  border: 1px solid $border;
  border-radius: 16px;
  padding: 30px;
  margin-bottom: 24px;
}

.card-title {
  font-size: 1.3rem;
  font-weight: 800;
  margin-bottom: 24px;
  color: #1a1a1a;
}

// 1. Room Summary
.room-preview {
  display: flex;
  gap: 20px;
  margin-bottom: 30px;
  img { width: 120px; height: 120px; border-radius: 12px; object-fit: cover; }
  .room-text {
    .category-tag { font-size: 0.8rem; color: $primary; font-weight: 700; background: $bg-light; padding: 4px 8px; border-radius: 4px; }
    h2 { font-size: 1.5rem; font-weight: 800; margin: 8px 0; }
    .stay-name { color: #666; font-weight: 500; }
  }
}

.schedule-info {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  background: $bg-light;
  padding: 20px;
  border-radius: 12px;
  .info-item {
    display: flex;
    flex-direction: column;
    .label { font-size: 0.8rem; color: #888; margin-bottom: 4px; }
    .date, .value { font-weight: 700; font-size: 1rem; color: #333; }
  }
}

// 2. Guest Info Form
.form-group {
  margin-bottom: 20px;
  label { display: block; font-size: 0.9rem; font-weight: 700; margin-bottom: 8px; color: #444; }
  input {
    width: 100%;
    padding: 14px;
    border: 1px solid #ddd;
    border-radius: 8px;
    font-size: 1rem;
    &:disabled { background: #f5f5f5; color: #888; }
  }
}
.notice { font-size: 0.85rem; color: $primary; font-weight: 600; margin-top: 10px; }

// 3. Payment Method
.method-options {
  display: flex;
  gap: 12px;
  .option {
    flex: 1;
    border: 1px solid #ddd;
    border-radius: 8px;
    padding: 16px;
    text-align: center;
    cursor: pointer;
    transition: all 0.2s;
    input { display: none; }
    .label-text { font-weight: 700; color: #666; }
    &:hover { border-color: $primary; }
    &.active {
      border-color: $primary;
      background: $bg-light;
      .label-text { color: $primary; }
    }
  }
}

// Sidebar Price Card
.price-sticky-card {
  position: sticky;
  top: 100px;
  background: white;
  border: 1px solid $border;
  padding: 30px;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.05);

  .price-rows {
    .row {
      display: flex;
      justify-content: space-between;
      margin-bottom: 12px;
      font-weight: 600;
      color: #555;
      &.discount { color: #ff5a5f; }
      &.total {
        margin-top: 16px;
        font-size: 1.4rem;
        font-weight: 900;
        color: #1a1a1a;
      }
    }
    hr { border: none; border-top: 1px solid $border; margin: 16px 0; }
  }
}

.terms-agreements {
  margin: 24px 0;
  .checkbox-label {
    display: flex;
    gap: 10px;
    align-items: center;
    font-size: 0.9rem;
    color: #666;
    cursor: pointer;
    input { width: 18px; height: 18px; }
  }
}

.pay-submit-btn {
  width: 100%;
  background: $primary;
  color: white;
  border: none;
  padding: 18px;
  border-radius: 12px;
  font-size: 1.2rem;
  font-weight: 800;
  cursor: pointer;
  transition: opacity 0.2s;
  &:disabled { background: #ccc; cursor: not-allowed; }
  &:hover:not(:disabled) { opacity: 0.9; }
}

.loading-state, .error-state {
  text-align: center;
  padding: 100px 0;
  .loader {
    width: 40px;
    height: 40px;
    border: 4px solid #eee;
    border-top-color: $primary;
    border-radius: 50%;
    animation: spin 1s linear infinite;
    margin: 0 auto 20px;
  }
}

@keyframes spin { to { transform: rotate(360deg); } }
</style>

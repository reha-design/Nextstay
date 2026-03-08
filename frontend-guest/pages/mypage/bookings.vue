<template>
  <div class="mypage-bookings container">
    <header class="page-header">
      <h1>내 예약 내역</h1>
      <p>예약하신 숙소 목록을 확인하실 수 있습니다.</p>
    </header>

    <div v-if="pending" class="loading-state">
      <div class="loader"></div>
    </div>

    <div v-else-if="error" class="error-state">
      <p>내역을 불러오는 중에 문제가 발생했습니다.</p>
    </div>

    <div v-else-if="!bookings || bookings.length === 0" class="empty-state">
      <div class="empty-icon">📅</div>
      <h3>아직 예약된 내역이 없습니다.</h3>
      <p>Nextstay와 함께 멋진 여행을 시작해 보세요!</p>
      <NuxtLink to="/" class="go-home-btn">숙소 둘러보기</NuxtLink>
    </div>

    <div v-else class="booking-list">
      <div v-for="booking in bookings" :key="booking.bookingNo" class="booking-item-card">
        <div class="status-badge" :class="booking.status.toLowerCase()">
          {{ getStatusLabel(booking.status) }}
        </div>
        
        <div class="card-body">
          <div class="booking-info">
            <span class="booking-no">예약번호: {{ booking.bookingNo }}</span>
            <h2 class="stay-name">{{ booking.stayName }}</h2>
            <p class="room-name">{{ booking.roomName }}</p>
            
            <div class="schedule">
              <div class="date-box">
                <span class="lab">체크인</span>
                <span class="val">{{ formatDate(booking.checkInDate) }}</span>
              </div>
              <div class="date-sep">→</div>
              <div class="date-box">
                <span class="lab">체크아웃</span>
                <span class="val">{{ formatDate(booking.checkOutDate) }}</span>
              </div>
            </div>
          </div>

          <div class="price-info">
            <span class="lab">총 결제 금액</span>
            <span class="price">₩{{ booking.totalPrice.toLocaleString() }}</span>
            <button class="detail-btn">상세 보기</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useApi } from '~/composables/useApi'

definePageMeta({
  middleware: 'auth'
})

const { data: bookings, pending, error } = await useApi('/api/v1/members/me/bookings')

const getStatusLabel = (status: string) => {
  const labels: Record<string, string> = {
    'CONFIRMED': '예약 확정',
    'PENDING': '예약 대기',
    'CANCELLED': '취소 완료',
    'COMPLETED': '이용 완료'
  }
  return labels[status] || status
}

const formatDate = (dateStr: string) => {
  const d = new Date(dateStr)
  return `${d.getMonth() + 1}월 ${d.getDate()}일 (${['일','월','화','수','목','금','토'][d.getDay()]})`
}
</script>

<style lang="scss" scoped>
$primary: #7575ff;
$border: #eee;

.mypage-bookings {
  padding-top: 40px;
  padding-bottom: 80px;
}

.page-header {
  margin-bottom: 40px;
  h1 { font-size: 2rem; font-weight: 800; margin-bottom: 8px; }
  p { color: #666; }
}

.booking-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.booking-item-card {
  position: relative;
  background: white;
  border: 1px solid $border;
  border-radius: 16px;
  overflow: hidden;
  transition: transform 0.2s, box-shadow 0.2s;
  &:hover { transform: translateY(-4px); box-shadow: 0 10px 25px rgba(0,0,0,0.05); }

  .status-badge {
    position: absolute;
    top: 20px;
    right: 30px;
    padding: 6px 12px;
    border-radius: 6px;
    font-size: 0.85rem;
    font-weight: 700;
    
    &.confirmed { background: #e8f5e9; color: #2e7d32; }
    &.pending { background: #fff3e0; color: #ef6c00; }
    &.cancelled { background: #ffebee; color: #c62828; }
    &.completed { background: #f5f5f5; color: #616161; }
  }
}

.card-body {
  padding: 30px;
  display: flex;
  justify-content: space-between;
  align-items: flex-end;

  @media (max-width: 768px) {
    flex-direction: column;
    align-items: flex-start;
    gap: 24px;
  }
}

.booking-info {
  .booking-no { font-size: 0.85rem; color: #999; display: block; margin-bottom: 10px; }
  .stay-name { font-size: 1.5rem; font-weight: 800; margin-bottom: 6px; }
  .room-name { color: #666; font-weight: 500; margin-bottom: 24px; }
}

.schedule {
  display: flex;
  align-items: center;
  gap: 20px;
  .date-sep { color: #ccc; font-weight: 300; font-size: 1.2rem; }
  .date-box {
    display: flex;
    flex-direction: column;
    .lab { font-size: 0.75rem; color: #aaa; margin-bottom: 4px; }
    .val { font-size: 1.1rem; font-weight: 700; color: #333; }
  }
}

.price-info {
  text-align: right;
  .lab { display: block; font-size: 0.85rem; color: #888; margin-bottom: 4px; }
  .price { display: block; font-size: 1.4rem; font-weight: 900; color: $primary; margin-bottom: 16px; }
  .detail-btn {
    background: transparent;
    border: 1px solid #ddd;
    color: #444;
    padding: 8px 16px;
    border-radius: 8px;
    font-weight: 700;
    font-size: 0.9rem;
    cursor: pointer;
    &:hover { background: #f5f5f5; }
  }

  @media (max-width: 768px) {
    width: 100%;
    text-align: left;
    padding-top: 20px;
    border-top: 1px solid $border;
    display: flex;
    justify-content: space-between;
    align-items: center;
    .price { margin-bottom: 0; }
  }
}

.empty-state {
  text-align: center;
  padding: 100px 0;
  .empty-icon { font-size: 4rem; margin-bottom: 20px; }
  h3 { font-size: 1.5rem; font-weight: 800; margin-bottom: 10px; }
  p { color: #666; margin-bottom: 30px; }
  .go-home-btn {
    display: inline-block;
    background: $primary;
    color: white;
    padding: 14px 40px;
    border-radius: 12px;
    text-decoration: none;
    font-weight: 800;
  }
}

.loading-state {
  text-align: center;
  padding: 100px 0;
  .loader {
    width: 40px;
    height: 40px;
    border: 4px solid #eee;
    border-top-color: $primary;
    border-radius: 50%;
    animation: spin 1s linear infinite;
    margin: 0 auto;
  }
}

@keyframes spin { to { transform: rotate(360deg); } }
</style>

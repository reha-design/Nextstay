<template>
  <div class="room-card">
    <div class="room-image">
      <img :src="room.imageUrls[0]" :alt="room.name" />
      <div class="capacity-tag">최대 {{ room.capacity }}인</div>
    </div>
    
    <div class="room-details">
      <div class="room-header">
        <div class="title-row">
          <h3>{{ room.name }}</h3>
          <!-- ⚡ 프로모션 배지 -->
          <div class="promotion-badge" v-if="room.badgeText || room.discountRate">
            <span class="icon">⚡</span>
            <span class="text">{{ room.badgeText || `단 1일만 ${room.discountRate}% 세일` }}</span>
          </div>
        </div>
        <p class="room-desc">{{ room.description }}</p>
      </div>

      <div class="room-info-points">
        <div class="point">
          <span class="label">체크인/아웃</span>
          <span class="value">15:00 / 11:00</span>
        </div>
      </div>

      <div class="room-footer">
        <div class="price-container">
          <div class="nightly-price">
            <span class="label">1박 요금</span>
            <span class="amount">₩{{ (room.basePrice || 0).toLocaleString() }}</span>
          </div>
          
          <div class="monthly-price" v-if="room.monthlyPrice">
            <span class="label">한달살기 최저가</span>
            <span class="amount">₩{{ room.monthlyPrice.toLocaleString() }}</span>
            <span class="unit">원</span>
          </div>
        </div>
        
        <button class="select-btn" @click="$emit('select', room)">
          예약하기
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
interface Room {
  roomNo: string
  name: string
  description: string
  type: string
  basePrice: number
  capacity: number
  imageUrls: string[]
  monthlyPrice?: number
  discountRate?: number
  badgeText?: string
}

defineProps<{
  room: Room
}>()

defineEmits<{
  (e: 'select', room: Room): void
}>()
</script>

<style lang="scss" scoped>
.room-card {
  display: flex;
  background: white;
  border: 1px solid #ebebeb;
  border-radius: 16px;
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  margin-bottom: 20px;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 24px rgba(0, 0, 0, 0.1);
    border-color: #d1d1d1;
  }

  @media (max-width: 768px) {
    flex-direction: column;
  }
}

.room-image {
  width: 280px;
  position: relative;
  flex-shrink: 0;
  min-height: 200px; // 최소 높이만 지정하고 나머지는 스트레칭

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .capacity-tag {
    position: absolute;
    bottom: 12px;
    left: 12px;
    background: rgba(0, 0, 0, 0.6);
    backdrop-filter: blur(4px);
    color: white;
    padding: 4px 10px;
    border-radius: 6px;
    font-size: 12px;
    font-weight: 600;
  }

  @media (max-width: 768px) {
    width: 100%;
    height: 220px;
  }
}

.room-details {
  flex: 1;
  padding: 24px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.room-header {
  .title-row {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 8px;
    gap: 12px;

    h3 {
      font-size: 20px;
      font-weight: 800;
      color: #222;
      margin: 0;
    }
  }

  .room-desc {
    font-size: 14px;
    color: #717171;
    margin: 0;
    line-height: 1.5;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }
}

.promotion-badge {
  flex-shrink: 0;
  background: #e6fffb;
  border: 1px solid #87e8de;
  color: #006d75;
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 11px;
  font-weight: 800;
  display: flex;
  align-items: center;
  gap: 4px;

  .icon {
    font-size: 12px;
    color: #ff9d4d;
  }
}

.room-info-points {
  margin-top: 16px;
  .point {
    display: flex;
    gap: 8px;
    font-size: 13px;
    .label { color: #888; font-weight: 500; }
    .value { color: #222; font-weight: 600; }
  }
}

.room-footer {
  margin-top: 24px;
  display: flex;
  justify-content: space-between;
  align-items: flex-end;

  .price-container {
    display: flex;
    flex-direction: column;
    gap: 4px;
  }

  .nightly-price {
    .label { font-size: 12px; color: #717171; margin-right: 6px; }
    .amount { font-size: 14px; font-weight: 700; color: #222; }
  }

  .monthly-price {
    display: flex;
    align-items: baseline;
    gap: 2px;
    
    .label {
      font-size: 12px;
      color: #717171;
      margin-right: 8px;
    }
    .amount {
      font-size: 22px;
      font-weight: 900;
      color: #222;
    }
    .unit {
      font-size: 14px;
      font-weight: 700;
      color: #222;
    }
  }

  .select-btn {
    background: #222;
    color: white;
    border: none;
    padding: 12px 24px;
    border-radius: 10px;
    font-size: 15px;
    font-weight: 700;
    cursor: pointer;
    transition: all 0.2s;

    &:hover {
      background: #000;
      transform: scale(1.03);
    }

    &:active {
      transform: scale(0.97);
    }
  }
}
</style>

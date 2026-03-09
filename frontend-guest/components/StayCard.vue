<template>
  <div class="room-card">
    <!-- 이미지 영역 (Thumbnail) -->
    <div class="thumbnail">
      <img :src="stay.thumbnailUrl" :alt="stay.name" loading="lazy" />
      
      <!-- 상단 그라데이션 오버레이 (가독성 확보용) -->
      <div class="top-overlay"></div>

      <!-- ⚡ 프로모션 배지 -->
      <div class="promotion-badge" v-if="bestTier && bestTier.discountRate > 0">
        <span class="icon">⚡</span>
        <span class="text">단 10일만 {{ bestTier.discountRate }}% 세일</span>
      </div>

      <!-- 위시리스트 버튼 (Glassmorphism) -->
      <button class="wishlist-btn" @click.stop="toggleWishlist">
        <div class="glass-circle">
          <span class="heart-icon">♡</span>
        </div>
      </button>
    </div>

    <!-- 정보 영역 (Information) -->
    <div class="info">
      <!-- 주소 (상단 연하게) -->
      <div class="address">{{ stay.address }}</div>
      
      <!-- 제목 및 별점 -->
      <div class="header">
        <h3 class="name">{{ stay.name }}</h3>
        <span class="rating">★ {{ stay.rating }}</span>
      </div>
      
      <!-- 가격 블록 (우측 정렬 적층형) -->
      <div class="pricing-block" v-if="bestTier">
        <!-- 1박 요금 -->
        <div class="nightly-price">
          ₩ {{ Math.round(bestTier.price / bestTier.nights).toLocaleString() }} / 1박
        </div>

        <!-- 할인 정보 (원가 + 할인율) -->
        <div class="discount-info" v-if="bestTier.discountRate > 0">
          <span class="original">₩ {{ bestTier.originalPrice.toLocaleString() }}</span>
          <span class="rate">{{ bestTier.discountRate }}%</span>
        </div>

        <!-- 최종 가치 (최대 강조) -->
        <div class="final-price">
          <span class="amount">₩ {{ bestTier.price.toLocaleString() }}</span>
          <span class="unit">/ 1달</span>
        </div>
      </div>
      
      <!-- 기본가 (티어 없을 때) -->
      <div class="simple-price" v-else>
        <span class="amount">₩ {{ formattedPrice }}</span>
        <span class="unit">/ 1박</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  stay: {
    stayNo: string
    name: string
    address: string
    category: string
    minPrice: number
    thumbnailUrl: string
    rating: number
    priceTiers?: {
      nights: number
      price: number
      originalPrice: number
      discountRate: number
    }[]
  }
}>()

const formattedPrice = computed(() => {
  return props.stay.minPrice.toLocaleString()
})

const bestTier = computed(() => {
  if (!props.stay.priceTiers || props.stay.priceTiers.length === 0) return null
  // 29박(한달살기)이 있으면 우선적으로 선택, 없으면 가장 긴 박수 선택
  const monthly = props.stay.priceTiers.find(t => t.nights >= 29)
  if (monthly) return monthly
  return props.stay.priceTiers.reduce((prev, current) => (prev.nights > current.nights) ? prev : current)
})

const toggleWishlist = () => {
  console.log('Toggle wishlist for', props.stay.stayNo)
}
</script>

<style lang="scss" scoped>
.room-card {
  display: flex;
  flex-direction: column;
  background: #ffffff;
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.3s cubic-bezier(0.2, 0, 0, 1), box-shadow 0.3s ease;
  border: 1px solid #f0f0f0;

  &:hover {
    transform: translateY(-6px);
    box-shadow: 0 16px 32px rgba(0, 0, 0, 0.08);
    
    .thumbnail img {
      transform: scale(1.05);
    }
  }
}

.thumbnail {
  position: relative;
  width: 100%;
  padding-top: 66.66%; // 3:2 Aspect Ratio
  background-color: #f5f5f5;
  overflow: hidden;

  img {
    position: absolute;
    top: 0; left: 0;
    width: 100%; height: 100%;
    object-fit: cover;
    transition: transform 0.5s ease;
  }

  .top-overlay {
    position: absolute;
    top: 0; left: 0; width: 100%; height: 40%;
    background: linear-gradient(to bottom, rgba(0,0,0,0.3) 0%, transparent 100%);
    pointer-events: none;
  }

  .promotion-badge {
    position: absolute;
    top: 14px; left: 14px;
    background: linear-gradient(90deg, #6c5ce7 0%, #a29bfe 100%);
    color: white;
    padding: 5px 12px;
    border-radius: 50px;
    font-size: 11px;
    font-weight: 800;
    display: flex;
    align-items: center;
    gap: 4px;
    box-shadow: 0 4px 10px rgba(108, 92, 231, 0.3);
    z-index: 2;

    .icon { font-size: 12px; }
  }

  .wishlist-btn {
    position: absolute;
    top: 10px; right: 10px;
    background: none; border: none;
    cursor: pointer;
    z-index: 2;

    .glass-circle {
      width: 32px; height: 32px;
      background: rgba(255, 255, 255, 0.2);
      backdrop-filter: blur(8px);
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      border: 1px solid rgba(255, 255, 255, 0.3);
      transition: background 0.2s;

      &:hover {
        background: rgba(255, 255, 255, 0.3);
      }

      .heart-icon {
        color: white;
        font-size: 20px;
        filter: drop-shadow(0 2px 4px rgba(0,0,0,0.2));
      }
    }
  }
}

.info {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 4px;

  .address {
    font-size: 13px;
    color: #999;
    font-weight: 500;
  }

  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 2px;

    .name {
      font-size: 17px;
      font-weight: 800;
      color: #222;
      margin: 0;
      line-height: 1.3;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }

    .rating {
      flex-shrink: 0;
      font-size: 13px;
      font-weight: 700;
      color: #222;
      margin-left: 8px;
    }
  }

  // 우측 정렬 적층형 가격 블록
  .pricing-block {
    margin-top: 12px;
    display: flex;
    flex-direction: column;
    align-items: flex-end;
    border-top: 1px solid #f8f8f8;
    padding-top: 12px;

    .nightly-price {
      font-size: 13px;
      color: #717171;
      font-weight: 500;
      margin-bottom: 4px;
    }

    .discount-info {
      display: flex;
      align-items: center;
      gap: 6px;
      margin-bottom: 2px;
      
      .original {
        font-size: 13px;
        color: #b0b0b0;
        text-decoration: line-through;
      }
      .rate {
        font-size: 18px;
        font-weight: 800;
        color: #ff385c;
      }
    }

    .final-price {
      display: flex;
      align-items: baseline;
      gap: 2px;

      .amount {
        font-size: 22px;
        font-weight: 900;
        color: #222;
        letter-spacing: -0.5px;
      }
      .unit {
        font-size: 14px;
        font-weight: 700;
        color: #222;
      }
    }
  }

  .simple-price {
    margin-top: 10px;
    text-align: right;
    .amount { font-size: 18px; font-weight: 800; color: #222; }
    .unit { font-size: 14px; color: #717171; }
  }
}
</style>

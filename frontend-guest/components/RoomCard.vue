<template>
  <div class="room-card">
    <div class="thumbnail">
      <img :src="stay.thumbnailUrl" :alt="stay.name" loading="lazy" />
      <div class="category-badge">{{ stay.category }}</div>
      <button class="wishlist-btn" @click.stop="toggleWishlist">
        <span class="heart-icon">♡</span>
      </button>
    </div>
    <div class="info">
      <div class="header">
        <h3 class="name">{{ stay.name }}</h3>
        <span class="rating">★ {{ stay.rating }}</span>
      </div>
      <div class="address">{{ stay.address }}</div>
      <div class="price-container">
        <span class="price">₩ {{ formattedPrice }}</span>
        <span class="unit">/ 1박 최저가</span>
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
  }
}>()

const formattedPrice = computed(() => {
  return props.stay.minPrice.toLocaleString()
})

const toggleWishlist = () => {
  // TODO: 하트 찜하기 로직 연동
  console.log('Toggle wishlist for', props.stay.stayNo)
}
</script>

<style lang="scss" scoped>
.room-card {
  display: flex;
  flex-direction: column;
  background: #ffffff;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  border: 1px solid #eee;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 24px rgba(0, 0, 0, 0.1);
  }
}

.thumbnail {
  position: relative;
  width: 100%;
  padding-top: 66.66%; // 3:2 Aspect Ratio
  background-color: #f5f5f5;

  img {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .category-badge {
    position: absolute;
    top: 12px;
    left: 12px;
    background: rgba(0, 0, 0, 0.6);
    color: white;
    padding: 4px 8px;
    border-radius: 4px;
    font-size: 0.8rem;
    font-weight: bold;
    backdrop-filter: blur(4px);
  }

  .wishlist-btn {
    position: absolute;
    top: 12px;
    right: 12px;
    background: none;
    border: none;
    color: white;
    font-size: 1.5rem;
    cursor: pointer;
    text-shadow: 0 2px 4px rgba(0,0,0,0.5);
    transition: transform 0.2s;

    &:hover {
      transform: scale(1.1);
    }
  }
}

.info {
  padding: 1rem;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;

  .header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;

    .name {
      font-size: 1.1rem;
      font-weight: 700;
      color: #333;
      margin: 0;
      line-height: 1.3;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }

    .rating {
      font-size: 0.9rem;
      font-weight: 600;
      color: #ffb400;
      white-space: nowrap;
      margin-left: 8px;
    }
  }

  .address {
    font-size: 0.9rem;
    color: #666;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .price-container {
    margin-top: 0.5rem;
    display: flex;
    align-items: baseline;
    gap: 4px;

    .price {
      font-size: 1.2rem;
      font-weight: 800;
      color: #1a1a1a;
    }

    .unit {
      font-size: 0.85rem;
      color: #666;
    }
  }
}
</style>

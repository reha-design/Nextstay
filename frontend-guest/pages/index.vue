<template>
  <div class="container main-page">
    <section class="hero-section">
      <h1>어디로 떠나시나요?</h1>
      <p>넥스트스테이에서 완벽한 숙소를 찾아보세요.</p>
      
      <div class="search-box">
        <input type="text" placeholder="여행지, 구, 동을 입력하세요" class="search-input" />
        <button class="search-button">검색</button>
      </div>
    </section>

    <section class="featured-section">
      <h2>인기 숙소</h2>

      <!-- Loading State -->
      <div v-if="pending" class="loading-state">
        <div class="spinner"></div>
        <p>숙소 정보를 불러오는 중입니다...</p>
      </div>

      <!-- Error State -->
      <div v-else-if="error" class="error-state">
        <p>데이터를 불러오는 데 실패했습니다.</p>
        <button @click="handleRefresh" class="retry-btn">다시 시도</button>
      </div>

      <!-- Empty State -->
      <div v-else-if="!stays || stays.length === 0" class="empty-state">
        <p>현재 등록된 인기 숙소가 없습니다.</p>
      </div>

      <!-- Data State -->
      <div v-else class="room-grid">
        <RoomCard 
          v-for="stay in stays" 
          :key="stay.stayNo" 
          :stay="stay" 
          @click="goToDetail(stay.stayNo)"
        />
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useApi } from '~/composables/useApi'
import RoomCard from '~/components/RoomCard.vue'

const router = useRouter()

export interface PriceTierDto {
  nights: number
  price: number
  originalPrice: number
  discountRate: number
}

export interface MainPageStayResponse {
  stayNo: string
  name: string
  address: string
  category: string
  minPrice: number
  thumbnailUrl: string
  rating: number
  priceTiers: PriceTierDto[]
}

// Fetch main page accommodations
const { data: stays, pending, error, refresh } = await useApi<MainPageStayResponse[]>('/api/v1/stays/main')

const handleRefresh = () => {
  refresh()
}

const goToDetail = (stayNo: string) => {
  router.push(`/accommodations/${stayNo}`)
}
</script>

<style lang="scss" scoped>
.main-page {
  display: flex;
  flex-direction: column;
  gap: 3rem;
  padding-bottom: 4rem;
}

.hero-section {
  text-align: center;
  padding: 5rem 2rem;
  background: linear-gradient(135deg, #006ce4 0%, #004cb8 100%);
  color: white;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0, 108, 228, 0.2);

  h1 {
    font-size: 2.5rem;
    margin-bottom: 1rem;
    font-weight: 800;
  }

  p {
    font-size: 1.2rem;
    margin-bottom: 2.5rem;
    opacity: 0.9;
  }
}

.search-box {
  display: flex;
  max-width: 600px;
  margin: 0 auto;
  background: white;
  padding: 0.5rem;
  border-radius: 50px; // Pill shape
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);

  .search-input {
    flex: 1;
    border: none;
    padding: 1rem 1.5rem;
    font-size: 1.1rem;
    outline: none;
    color: #333;
    border-radius: 50px 0 0 50px;
    background: transparent;

    &::placeholder {
      color: #999;
    }
  }

  .search-button {
    background-color: #ffb400; // Accent color
    color: #1a1a1a;
    border: none;
    padding: 0 2.5rem;
    font-size: 1.1rem;
    font-weight: bold;
    border-radius: 50px;
    cursor: pointer;
    transition: background-color 0.2s, transform 0.1s;

    &:hover {
      background-color: #f0a800;
    }

    &:active {
      transform: scale(0.98);
    }
  }
}

.featured-section {
  h2 {
    font-size: 1.8rem;
    margin-bottom: 1.5rem;
    color: #1a1a1a;
    font-weight: 700;
  }
}

// Grid Layout for Room Cards (Responsive)
.room-grid {
  display: grid;
  gap: 1.5rem;
  
  // Default (Mobile Webview): 1 column
  grid-template-columns: 1fr;

  // Tablet
  @media (min-width: 640px) {
    grid-template-columns: repeat(2, 1fr);
  }

  // Small PC
  @media (min-width: 960px) {
    grid-template-columns: repeat(3, 1fr);
  }

  // Large PC
  @media (min-width: 1200px) {
    grid-template-columns: repeat(4, 1fr);
    gap: 2rem;
  }
}

// States styling
.loading-state, .empty-state, .error-state {
  text-align: center;
  padding: 5rem 2rem;
  background-color: #f9f9f9;
  border-radius: 12px;
  color: #666;
  font-size: 1.1rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
}

.error-state {
  color: #d32f2f;
  background-color: #fff5f5;

  .retry-btn {
    padding: 0.5rem 1.5rem;
    background-color: #d32f2f;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-weight: bold;

    &:hover {
      background-color: #b71c1c;
    }
  }
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid rgba(0, 108, 228, 0.1);
  border-left-color: #006ce4;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>

<template>
  <div v-if="stay" class="detail-page">
    <!-- 1. Hero Image Section -->
    <header class="hero-section">
      <div class="container">
        <div class="image-grid">
          <div class="main-image">
            <img :src="stay.images[0]" :alt="stay.name" @click="openGallery(0)" />
          </div>
          <div class="sub-images">
            <img v-for="(img, idx) in stay.images.slice(1, 5)" :key="idx" :src="img" :alt="stay.name" @click="openGallery(idx + 1)" />
            <button v-if="stay.images.length > 5" class="view-all-btn" @click="openGallery(0)">
              <span class="icon">🔍</span> 사진 전체보기
            </button>
          </div>
        </div>
      </div>
    </header>

    <!-- 2. Sticky Tab Navigation -->
    <nav class="sticky-tabs" :class="{ 'is-sticky': isSticky }">
      <div class="container flex justify-between items-center">
        <ul class="tabs">
          <li v-for="tab in tabs" :key="tab.id" :class="{ active: activeTab === tab.id }" @click="scrollToTab(tab.id)">
            {{ tab.label }}
          </li>
        </ul>
        <div v-if="isSticky" class="sticky-price-info">
          <span class="price">₩{{ minPrice.toLocaleString() }}~</span>
          <button class="book-now-btn" @click="scrollToTab('rooms')">객실예약</button>
        </div>
      </div>
    </nav>

    <div class="container content-layout">
      <!-- Main Content -->
      <main class="main-content">
        <!-- 3. Stay Header & Basic Info -->
        <section id="info" class="section stay-header-section">
          <div class="header-top">
            <span class="category-chip">{{ stay.category }}</span>
            <div class="rating-box">
              <span class="star">★</span>
              <span class="score">{{ stay.rating }}</span>
              <span class="count">(리뷰 128)</span>
            </div>
          </div>
          <h1 class="stay-title">{{ stay.name }}</h1>
          <p class="address">
            <span class="icon">📍</span> {{ stay.address }}
            <a href="#location" class="map-link">지도보기</a>
          </p>
          
          <div class="tags-row">
            <span v-for="tag in ['#세스코관리', '#오션뷰', '#가족맞춤', '#인기숙소']" :key="tag" class="tag">{{ tag }}</span>
          </div>
        </section>

        <hr class="divider" />

        <!-- 4. Amenities / Options -->
        <section class="section amenities-section">
          <h2 class="section-title">편의시설 및 서비스</h2>
          <div class="amenity-grid">
            <div v-for="amenity in amenities" :key="amenity.name" class="amenity-item">
              <span class="icon">{{ amenity.icon }}</span>
              <span class="name">{{ amenity.name }}</span>
            </div>
          </div>
        </section>

        <hr class="divider" />

        <!-- 5. Room Selection (Core Function) -->
        <section id="rooms" class="section rooms-section">
          <h2 class="section-title">객실 선택</h2>
          <div class="room-cards">
            <div v-for="room in stay.rooms" :key="room.roomNo" class="room-card">
              <div class="room-image">
                <img :src="room.imageUrls[0]" :alt="room.name" />
                <span class="capacity-tag">최대 {{ room.capacity }}인</span>
              </div>
              <div class="room-details">
                <div class="room-header">
                  <h3>{{ room.name }}</h3>
                  <p class="room-desc">{{ room.description }}</p>
                </div>
                <div class="room-info-points">
                  <div class="point">
                    <span class="label">체크인</span>
                    <span class="value">15:00</span>
                  </div>
                  <div class="point">
                    <span class="label">체크아웃</span>
                    <span class="value">11:00</span>
                  </div>
                </div>
                <div class="room-footer">
                  <div class="price-box">
                    <span class="price-label">숙박 (1박 기준)</span>
                    <span class="amount">₩{{ room.basePrice.toLocaleString() }}</span>
                  </div>
                  <button class="select-btn" @click="startBooking(room)">예약하기</button>
                </div>
              </div>
            </div>
          </div>
        </section>

        <hr class="divider" />

        <!-- 6. Detailed Description -->
        <section id="description" class="section description-section">
          <h2 class="section-title">숙소 정보</h2>
          <div class="html-content" v-html="formattedDescription"></div>
          
          <div class="usage-guide">
            <h3>이용 안내</h3>
            <ul>
              <li>입실 시간 : 오후 3시 이후 / 퇴실 시간 : 오전 11시 이전</li>
              <li>반려동물 동반 시 사전 문의 및 추가 요금이 발생할 수 있습니다.</li>
              <li>전 객실 금연 구역입니다.</li>
            </ul>
          </div>
        </section>

        <hr class="divider" />

        <!-- 7. Location -->
        <section id="location" class="section map-section">
          <h2 class="section-title">위치 안내</h2>
          <p class="map-address">{{ stay.address }}</p>
          <div id="map" class="kakao-map"></div>
        </section>
      </main>

      <!-- Sidebar (Desktop only) -->
      <aside class="sidebar-wrapper">
        <div class="sticky-sidebar">
          <div class="reservation-card">
            <h3>방문 일정 선택</h3>
            <div class="date-selector">
              <div class="date-group">
                <label>체크인</label>
                <input type="date" v-model="checkInDate" />
              </div>
              <div class="date-group">
                <label>체크아웃</label>
                <input type="date" v-model="checkOutDate" />
              </div>
            </div>
            <div class="price-summary">
              <div class="total-row">
                <span>최저가</span>
                <span class="amount">₩{{ minPrice.toLocaleString() }}~</span>
              </div>
            </div>
            <button class="primary-btn" @click="scrollToTab('rooms')">객실 확인하기</button>
          </div>
          
          <div class="host-profile">
            <div class="avatar">H</div>
            <div class="info">
              <p class="host-name">호스트: {{ stay.hostName }}님</p>
              <button class="contact-btn">호스트에게 문의</button>
            </div>
          </div>
        </div>
      </aside>
    </div>

    <!-- 8. Mobile Bottom Bar -->
    <div class="mobile-bottom-bar">
      <div class="m-price-info">
        <span class="label">1박 최저가</span>
        <span class="amount">₩{{ minPrice.toLocaleString() }}~</span>
      </div>
      <button class="m-book-btn" @click="scrollToTab('rooms')">객실 선택</button>
    </div>
  </div>

  <div v-else-if="pending" class="loading-state">
    <div class="loader"></div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useApi } from '~/composables/useApi'

// TypeScript declaration for Kakao Maps
declare global {
  interface Window {
    kakao: any
  }
}

interface RoomDetail {
  roomNo: string
  name: string
  description: string
  type: string
  basePrice: number
  capacity: number
  imageUrls: string[]
}

interface StayDetail {
  stayNo: string
  name: string
  description: string
  address: string
  city: string
  category: string
  hostName: string
  latitude: number | null
  longitude: number | null
  rating: number
  images: string[]
  rooms: RoomDetail[]
}

const route = useRoute()
const router = useRouter()
const stayNo = route.params.id as string

const { data: stay, pending } = await useApi<StayDetail>(`/api/v1/stays/${stayNo}`)

// Tabs configuration
const tabs = [
  { id: 'rooms', label: '객실선택' },
  { id: 'description', label: '숙소안내' },
  { id: 'location', label: '위치' },
  { id: 'reviews', label: '리뷰' }
]
const activeTab = ref('rooms')
const isSticky = ref(false)

// Amenities dummy data
const amenities = [
  { name: '주차장', icon: '🅿️' },
  { name: '무선 인터넷', icon: '📶' },
  { name: '주방', icon: '🍳' },
  { name: '에어컨', icon: '❄️' },
  { name: '세탁기', icon: '🧺' },
  { name: 'TV', icon: '📺' }
]

// Computed
const minPrice = computed(() => {
  if (!stay.value || stay.value.rooms.length === 0) return 0
  return Math.min(...stay.value.rooms.map(r => r.basePrice))
})

const formattedDescription = computed(() => {
  if (!stay.value) return ''
  return stay.value.description.replace(/\n/g, '<br>')
})

// Scroll handlers
const handleScroll = () => {
  isSticky.value = window.scrollY > 500
  
  const sections = ['info', 'rooms', 'description', 'location']
  for (const sectionId of sections) {
    const el = document.getElementById(sectionId)
    if (el) {
      const rect = el.getBoundingClientRect()
      if (rect.top <= 150 && rect.bottom >= 150) {
        activeTab.value = sectionId === 'info' ? 'rooms' : sectionId
        break
      }
    }
  }
}

const scrollToTab = (tabId: string) => {
  const el = document.getElementById(tabId)
  if (el) {
    const top = el.offsetTop - 120
    window.scrollTo({ top, behavior: 'smooth' })
  }
}

const checkInDate = ref(new Date().toISOString().split('T')[0])
const checkOutDate = ref(new Date(Date.now() + 86400000).toISOString().split('T')[0])

const startBooking = (room: RoomDetail) => {
  router.push({
    path: `/booking/${room.roomNo}`,
    query: {
      checkIn: checkInDate.value,
      checkOut: checkOutDate.value
    }
  })
}

const openGallery = (startIndex: number) => {
  console.log('Open Gallery', startIndex)
}

// Map Initialization
const initMap = () => {
  if (typeof window.kakao === 'undefined' || !stay.value) return
  
  const container = document.getElementById('map')
  
  // Use coordinates if available, otherwise default to a known location
  const centerPosition = stay.value.latitude && stay.value.longitude 
    ? new window.kakao.maps.LatLng(stay.value.latitude, stay.value.longitude)
    : new window.kakao.maps.LatLng(33.450701, 126.570667)

  const options = {
    center: centerPosition,
    level: 3
  }
  const map = new window.kakao.maps.Map(container, options)
  
  if (stay.value.latitude && stay.value.longitude) {
    // If we have coordinates, just add the marker
    new window.kakao.maps.Marker({
      map: map,
      position: centerPosition
    })
  } else {
    // Fallback to Geocoding if coordinates are missing
    const geocoder = new window.kakao.maps.services.Geocoder()
    geocoder.addressSearch(stay.value.address, (result: any[], status: string) => {
      if (status === window.kakao.maps.services.Status.OK) {
        const coords = new window.kakao.maps.LatLng(result[0].y, result[0].x)
        map.relayout()
        map.setCenter(coords)
        new window.kakao.maps.Marker({
          map: map,
          position: coords
        })
      }
    })
  }
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
  
  if (!window.kakao) {
    const script = document.createElement('script')
    script.src = `//dapi.kakao.com/v2/maps/sdk.js?appkey=bbce3c69d524c8f1dfec521842c8608d&libraries=services&autoload=false`
    script.onload = () => {
      window.kakao.maps.load(initMap)
    }
    document.head.appendChild(script)
  } else {
    window.kakao.maps.load(initMap)
  }
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style lang="scss" scoped>
// Design System Variables
$primary-color: #7575ff;
$secondary-color: #8496ba;
$text-dark: #1a1a1a;
$text-light: #666;
$bg-faded: #f8faff;
$border-color: #eee;
$shadow-soft: 0 4px 20px rgba(0,0,0,0.08);

.detail-page {
  background: white;
  padding-bottom: 80px;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

// 1. Hero Image Section
.hero-section {
  padding: 24px 0;
  .image-grid {
    display: grid;
    grid-template-columns: 2fr 1fr;
    gap: 12px;
    height: 500px;
    border-radius: 16px;
    overflow: hidden;
    position: relative;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      cursor: pointer;
      transition: transform 0.4s ease, filter 0.3s;
      &:hover {
        transform: scale(1.02);
        filter: brightness(0.9);
      }
    }

    .sub-images {
      display: grid;
      grid-template-rows: 1fr 1fr;
      gap: 12px;
      position: relative;
    }

    .view-all-btn {
      position: absolute;
      right: 20px;
      bottom: 20px;
      background: white;
      border: 1px solid $text-dark;
      padding: 8px 16px;
      border-radius: 8px;
      font-weight: 600;
      cursor: pointer;
      box-shadow: $shadow-soft;
      &:hover { background: #f5f5f5; }
    }
  }
}

// 2. Sticky Tab Navigation
.sticky-tabs {
  background: white;
  border-bottom: 1px solid $border-color;
  z-index: 100;
  position: sticky;
  top: 0;
  transition: box-shadow 0.3s;

  &.is-sticky {
    box-shadow: 0 2px 10px rgba(0,0,0,0.1);
  }

  .tabs {
    display: flex;
    list-style: none;
    padding: 0;
    margin: 0;

    li {
      padding: 20px 24px;
      font-weight: 600;
      color: $secondary-color;
      cursor: pointer;
      border-bottom: 3px solid transparent;
      transition: all 0.2s;

      &:hover { color: $primary-color; }
      &.active {
        color: $primary-color;
        border-bottom-color: $primary-color;
      }
    }
  }

  .sticky-price-info {
    display: flex;
    align-items: center;
    gap: 16px;
    .price {
      font-weight: 800;
      font-size: 1.2rem;
    }
    .book-now-btn {
      background: $primary-color;
      color: white;
      border: none;
      padding: 10px 24px;
      border-radius: 8px;
      font-weight: 700;
      cursor: pointer;
    }
  }
}

// 3. Layout Structure
.content-layout {
  display: grid;
  grid-template-columns: 1fr 380px;
  gap: 60px;
  margin-top: 40px;

  @media (max-width: 1024px) {
    grid-template-columns: 1fr;
  }
}

.section {
  padding: 40px 0;
}

.section-title {
  font-size: 1.6rem;
  font-weight: 800;
  margin-bottom: 24px;
  color: $text-dark;
}

.divider {
  border: none;
  border-top: 1px solid $border-color;
  margin: 0;
}

// 4. Header Section
.stay-header-section {
  .header-top {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;
  }

  .category-chip {
    background: $bg-faded;
    color: $primary-color;
    padding: 4px 12px;
    border-radius: 20px;
    font-size: 0.85rem;
    font-weight: 700;
  }

  .rating-box {
    display: flex;
    align-items: center;
    gap: 4px;
    .star { color: #ffb400; font-size: 1.1rem; }
    .score { font-weight: 800; font-size: 1rem; }
    .count { color: $text-light; font-size: 0.9rem; }
  }

  .stay-title {
    font-size: 2.4rem;
    font-weight: 900;
    margin-bottom: 12px;
    letter-spacing: -1px;
  }

  .address {
    font-size: 1.1rem;
    color: $text-light;
    margin-bottom: 20px;
    .map-link {
      color: $primary-color;
      text-decoration: underline;
      margin-left: 12px;
      font-weight: 600;
      font-size: 0.95rem;
    }
  }

  .tags-row {
    display: flex;
    gap: 10px;
    .tag {
      color: $secondary-color;
      font-size: 0.9rem;
      background: #f1f3f7;
      padding: 6px 12px;
      border-radius: 4px;
    }
  }
}

// 5. Amenities
.amenity-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  
  .amenity-item {
    display: flex;
    align-items: center;
    gap: 12px;
    .icon { font-size: 1.4rem; }
    .name { color: #444; font-size: 1rem; }
  }
}

// 6. Room Selection
.room-cards {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.room-card {
  display: flex;
  border: 1px solid $border-color;
  border-radius: 12px;
  overflow: hidden;
  transition: transform 0.2s, box-shadow 0.2s;
  
  &:hover {
    box-shadow: $shadow-soft;
    border-color: $primary-color;
  }

  .room-image {
    width: 280px;
    height: 220px;
    flex-shrink: 0;
    position: relative;
    img { width: 100%; height: 100%; object-fit: cover; }
    .capacity-tag {
      position: absolute;
      top: 12px;
      left: 12px;
      background: rgba(0,0,0,0.6);
      color: white;
      padding: 4px 10px;
      border-radius: 4px;
      font-size: 0.8rem;
    }
  }

  .room-details {
    flex: 1;
    padding: 24px;
    display: flex;
    flex-direction: column;

    h3 { font-size: 1.4rem; font-weight: 800; margin-bottom: 8px; }
    .room-desc { color: $text-light; font-size: 0.95rem; margin-bottom: 16px; }

    .room-info-points {
      display: flex;
      gap: 30px;
      margin-bottom: auto;
      .point {
        display: flex;
        flex-direction: column;
        .label { font-size: 0.75rem; color: $secondary-color; margin-bottom: 2px; }
        .value { font-weight: 700; font-size: 0.95rem; }
      }
    }

    .room-footer {
      display: flex;
      justify-content: space-between;
      align-items: flex-end;
      margin-top: 20px;

      .price-box {
        .price-label { display: block; font-size: 0.85rem; color: $text-light; margin-bottom: 4px; }
        .amount { font-size: 1.5rem; font-weight: 900; color: $primary-color; }
      }

      .select-btn {
        background: $primary-color;
        color: white;
        border: none;
        padding: 12px 32px;
        border-radius: 8px;
        font-weight: 800;
        cursor: pointer;
        transition: background 0.2s;
        &:hover { background: #5a5aff; }
      }
    }
  }

  @media (max-width: 640px) {
    flex-direction: column;
    .room-image { width: 100%; height: 200px; }
  }
}

// 7. Sidebar
.sidebar-wrapper {
  .sticky-sidebar {
    position: sticky;
    top: 100px;
    display: flex;
    flex-direction: column;
    gap: 20px;
  }

  .reservation-card {
    background: white;
    border: 1px solid $border-color;
    border-radius: 16px;
    padding: 24px;
    box-shadow: $shadow-soft;

    h3 { font-size: 1.2rem; font-weight: 800; margin-bottom: 20px; }

    .date-selector {
      border: 1px solid #ddd;
      border-radius: 8px;
      padding: 16px;
      margin-bottom: 20px;
      display: flex;
      flex-direction: column;
      gap: 12px;

      .date-group {
        display: flex;
        flex-direction: column;
        gap: 4px;
        label { font-size: 0.75rem; color: $secondary-color; font-weight: 700; }
        input {
          border: none;
          font-size: 0.95rem;
          font-weight: 600;
          color: #333;
          outline: none;
          background: transparent;
          cursor: pointer;
        }
      }
    }

    .price-summary {
      padding-top: 16px;
      border-top: 1px solid $border-color;
      margin-bottom: 20px;
      .total-row {
        display: flex;
        justify-content: space-between;
        align-items: center;
        span { font-weight: 700; color: $text-light; }
        .amount { font-size: 1.4rem; color: $text-dark; font-weight: 900; }
      }
    }

    .primary-btn {
      width: 100%;
      background: $primary-color;
      color: white;
      border: none;
      padding: 16px;
      border-radius: 12px;
      font-size: 1.1rem;
      font-weight: 800;
      cursor: pointer;
      &:hover { opacity: 0.9; }
    }
  }

  .host-profile {
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 20px;
    background: $bg-faded;
    border-radius: 16px;

    .avatar {
      width: 48px;
      height: 48px;
      background: $primary-color;
      color: white;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      font-weight: 800;
      font-size: 1.2rem;
    }

    .host-name { font-weight: 700; font-size: 0.95rem; margin-bottom: 4px; }
    .contact-btn {
      border: none;
      background: none;
      color: $primary-color;
      padding: 0;
      font-weight: 700;
      font-size: 0.85rem;
      text-decoration: underline;
      cursor: pointer;
    }
  }
}

// 8. Map
.kakao-map {
  width: 100%;
  height: 400px;
  background: #f1f1f1;
  border-radius: 12px;
  margin-top: 16px;
}

// 9. Mobile Elements
.mobile-bottom-bar {
  display: none;
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: white;
  padding: 16px 20px;
  border-top: 1px solid $border-color;
  justify-content: space-between;
  align-items: center;
  z-index: 1000;

  @media (max-width: 1024px) {
    display: flex;
  }

  .m-price-info {
    .label { font-size: 0.75rem; color: $text-light; display: block; }
    .amount { font-size: 1.2rem; font-weight: 800; color: $text-dark; }
  }

  .m-book-btn {
    background: $primary-color;
    color: white;
    border: none;
    padding: 12px 32px;
    border-radius: 8px;
    font-weight: 800;
    font-size: 1rem;
  }
}

// Loading state
.loading-state {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  .loader {
    width: 48px;
    height: 48px;
    border: 5px solid #eee;
    border-bottom-color: $primary-color;
    border-radius: 50%;
    animation: rotation 1s linear infinite;
  }
}

@keyframes rotation {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>

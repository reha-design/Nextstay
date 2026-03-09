<template>
  <div v-if="stay" class="detail-page">
    <!-- 1. Hero Image Section -->
    <header class="hero-section">
      <div class="container">
        <div class="image-grid">
          <div class="grid-main">
            <img :src="stay.images[0]" :alt="stay.name" @click="openGallery(0)" />
          </div>
          <div class="grid-sub">
            <div v-for="(img, idx) in stay.images.slice(1, 5)" :key="idx" class="sub-img-wrapper">
              <img :src="img" :alt="stay.name" @click="openGallery(idx + 1)" />
            </div>
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
        <div v-if="isSticky" class="sticky-cta">
          <div class="cta-info">
            <div class="price-row">
              <span class="label">총액</span>
              <span class="amount">₩{{ (minPrice * totalNights).toLocaleString() }}</span>
            </div>
            <div class="rating-row">
              <span class="star">★</span>
              <span class="score">{{ stay.rating }}</span>
              <span class="count"> · 후기 128개</span>
            </div>
          </div>
          <button class="book-now-btn" @click="scrollToTab('rooms')">예약하기</button>
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
              <span class="count"> · 리뷰 128개</span>
            </div>
          </div>
          <h1 class="stay-title">{{ stay.name }}</h1>
          <p class="address-row">
            {{ stay.address }}
          </p>
          
          <div class="tags-row">
            <span v-for="tag in ['#세스코관리', '#오션뷰', '#가족맞춤', '#인기숙소']" :key="tag" class="tag">{{ tag }}</span>
          </div>
        </section>

        <hr class="divider" />

        <!-- 4. Amenities -->
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

        <!-- 5. Room Selection -->
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
                    <span class="label">체크인/아웃</span>
                    <span class="value">15:00 / 11:00</span>
                  </div>
                </div>
                <div class="room-footer">
                  <div class="price-box">
                    <div class="nightly-price">
                      <span class="price-label">1박 요금</span>
                      <span class="amount">₩{{ room.basePrice.toLocaleString() }}</span>
                    </div>
                    <div class="monthly-special" v-if="room.monthlyPrice">
                      <span class="badge" v-if="room.badgeText">{{ room.badgeText }}</span>
                      <div class="price-detail">
                        <span class="label">한달살기 최저가</span>
                        <span class="amount">₩{{ room.monthlyPrice.toLocaleString() }}원</span>
                      </div>
                    </div>
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
        </section>

        <hr class="divider" />

        <!-- 7. Location -->
        <section id="location" class="section map-section">
          <h2 class="section-title">위치</h2>
          <p class="map-address">{{ stay.address }}</p>
          <div id="map" class="kakao-map"></div>
        </section>
      </main>

      <!-- Sidebar (Desktop only) -->
      <aside class="sidebar-wrapper">
        <div class="sticky-sidebar">
          <div class="reservation-card">
            <div class="card-header">
              <div class="price-info">
                <template v-if="calculatedPricing">
                  <div class="final-price-main">
                    <span class="price">{{ calculatedPricing.display.formattedFinalPrice }}</span>
                    <span class="unit"> / {{ totalNights }}박</span>
                  </div>
                  <div class="discount-badge-row" v-if="calculatedPricing.display.badgeText">
                    <span class="badge">{{ calculatedPricing.display.badgeText }}</span>
                  </div>
                </template>
                <template v-else>
                  <span class="price">₩{{ minPrice.toLocaleString() }}</span>
                  <span class="unit"> / 박</span>
                </template>
              </div>
              <div class="rating-info">
                <span class="star">★</span>
                <span class="score">{{ stay.rating }}</span>
                <span class="count"> · 리뷰 128개</span>
              </div>
            </div>

            <div class="booking-inputs">
              <div class="date-row" @click="showDatePicker = true">
                <div class="input-item checkin">
                  <label>체크인</label>
                  <div class="value">{{ checkInDate }}</div>
                </div>
                <div class="input-item checkout">
                  <label>체크아웃</label>
                  <div class="value">{{ checkOutDate }}</div>
                </div>
              </div>
              <div class="guest-row">
                <label>인원</label>
                <div class="guest-selector">게스트 2명</div>
              </div>
            </div>

            <button class="reserve-btn" @click="scrollToTab('rooms')">객실 확인하기</button>
            <p class="price-notice">{{ calculatedPricing?.display?.discountNotice || '예약 확정 전에는 요금이 청구되지 않습니다.' }}</p>

            <div class="price-breakdown">
              <template v-if="calculatedPricing">
                <div class="row">
                  <span class="label">기본 요금 합계</span>
                  <span class="val">₩{{ calculatedPricing.pricing.totalOriginalPrice.toLocaleString() }}</span>
                </div>
                <div class="row discount-row" v-if="calculatedPricing.pricing.totalDiscountAmount > 0">
                  <span class="label">연박 할인 ({{ calculatedPricing.pricing.totalDiscountRate }}%)</span>
                  <span class="val">-₩{{ calculatedPricing.pricing.totalDiscountAmount.toLocaleString() }}</span>
                </div>
                <div class="row service-fee">
                  <span class="label">서비스 수수료</span>
                  <span class="val">₩0</span>
                </div>
                <hr />
                <div class="row total">
                  <span class="label">총 합계</span>
                  <span class="val">₩{{ calculatedPricing.pricing.finalTotalPrice.toLocaleString() }}</span>
                </div>
              </template>
              <template v-else>
                <div class="row">
                  <span class="label">₩{{ minPrice.toLocaleString() }} x {{ totalNights }}박</span>
                  <span class="val">₩{{ (minPrice * totalNights).toLocaleString() }}</span>
                </div>
                <div class="row service-fee">
                  <span class="label">서비스 수수료</span>
                  <span class="val">₩0</span>
                </div>
                <hr />
                <div class="row total">
                  <span class="label">총 합계</span>
                  <span class="val">₩{{ (minPrice * totalNights).toLocaleString() }}</span>
                </div>
              </template>
            </div>
          </div>
          
          <div class="report-box">
             <span class="icon">🏳️</span> 숙소 신고하기
          </div>
        </div>
      </aside>
    </div>

    <!-- 8. Mobile Bottom Bar -->
    <div class="mobile-bottom-bar">
      <div class="m-price-info">
        <span class="amount">₩{{ (minPrice * totalNights).toLocaleString() }}</span>
        <span class="unit">/ {{ totalNights }}박</span>
      </div>
      <button class="m-book-btn" @click="scrollToTab('rooms')">객실 선택</button>
    </div>

    <!-- 9. Date Picker Overlay -->
    <div v-if="showDatePicker" class="datepicker-overlay" @click.self="showDatePicker = false">
      <DateRangePicker 
        :initial-check-in="checkInDate" 
        :initial-check-out="checkOutDate"
        @change="onDateChange"
        @close="showDatePicker = false"
      />
    </div>
  </div>

  <div v-else-if="pending" class="loading-state">
    <div class="loader"></div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
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
  monthlyPrice?: number
  discountRate?: number
  badgeText?: string
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
  { id: 'info', label: '개요' },
  { id: 'rooms', label: '객실선택' },
  { id: 'description', label: '설명' },
  { id: 'location', label: '위치' }
]
const activeTab = ref('info')
const isSticky = ref(false)
const showDatePicker = ref(false)
const totalNights = ref(1)

// Dates
const checkInDate = ref(new Date().toISOString().split('T')[0])
const checkOutDate = ref(new Date(Date.now() + 86400000).toISOString().split('T')[0])

const onDateChange = (data: { checkIn: string, checkOut: string, nights: number }) => {
  if (data.checkIn) checkInDate.value = data.checkIn
  if (data.checkOut) checkOutDate.value = data.checkOut
  if (data.nights) totalNights.value = data.nights
}

// Amenities dummy data
const amenities = [
  { name: '주차장', icon: '🅿️' },
  { name: '무선 인터넷', icon: '📶' },
  { name: '주방', icon: '🍳' },
  { name: '에어컨', icon: '❄️' },
  { name: '세탁기', icon: '🧺' },
  { name: 'TV', icon: '📺' }
]

// New pricing interfaces for dynamic calculation
interface PricingDetail {
  nightlyBasePrice: number
  totalNights: number
  totalOriginalPrice: number
  finalTotalPrice: number
  totalDiscountAmount: number
  totalDiscountRate: number
  pricePerNightDiscounted: number
}

interface DisplayInfo {
  badgeText: string | null
  discountNotice: string | null
  isLongStayDiscount: boolean
  formattedOriginalPrice: string
  formattedFinalPrice: string
}

interface PriceCalculationResponse {
  pricing: PricingDetail
  display: DisplayInfo
  appliedPolicies: any[]
}

const calculatedPricing = ref<PriceCalculationResponse | null>(null)

const calculateBestPrice = async () => {
  if (!stay.value || stay.value.rooms.length === 0) return
  
  const cheapestRoom = stay.value.rooms.reduce((prev, curr) => 
    prev.basePrice < curr.basePrice ? prev : curr
  )
  
  try {
    const response = await $fetch<PriceCalculationResponse>(
      `/api/v1/rooms/${cheapestRoom.roomNo}/calculate-price`,
      {
        method: 'POST',
        body: {
          checkInDate: checkInDate.value,
          checkOutDate: checkOutDate.value
        }
      }
    )
    calculatedPricing.value = response
  } catch (e) {
    console.error('Price calculation failed', e)
  }
}

watch([checkInDate, checkOutDate, stay], () => {
  calculateBestPrice()
}, { immediate: true })

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
  isSticky.value = window.scrollY > 550
  
  const sections = ['info', 'rooms', 'description', 'location']
  for (const sectionId of sections) {
    const el = document.getElementById(sectionId)
    if (el) {
      const rect = el.getBoundingClientRect()
      // Use offset to trigger active state earlier
      if (rect.top <= 140 && rect.bottom >= 140) {
        activeTab.value = sectionId
        break
      }
    }
  }
}

const scrollToTab = (tabId: string) => {
  const el = document.getElementById(tabId)
  if (el) {
    const top = el.offsetTop - 100
    window.scrollTo({ top, behavior: 'smooth' })
  }
}

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
  
  const centerPosition = stay.value.latitude && stay.value.longitude 
    ? new window.kakao.maps.LatLng(stay.value.latitude, stay.value.longitude)
    : new window.kakao.maps.LatLng(33.450701, 126.570667)

  const options = {
    center: centerPosition,
    level: 3
  }
  const map = new window.kakao.maps.Map(container, options)
  
  if (stay.value.latitude && stay.value.longitude) {
    new window.kakao.maps.Marker({
      map: map,
      position: centerPosition
    })
  } else {
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

  // Ensure map fits container
  setTimeout(() => {
    map.relayout()
    map.setCenter(centerPosition)
  }, 100)
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
  
  if (!window.kakao) {
    const script = document.createElement('script')
    script.src = `//dapi.kakao.com/v2/maps/sdk.js?appkey=7a651a84732bad2cb8d0025c67e0385a&libraries=services&autoload=false`
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
$primary-color: #7575ff; // 기존 브랜드 블루
$text-dark: #222222;
$text-light: #717171;
$border-color: #DDDDDD;
$shadow: 0 6px 16px rgba(0,0,0,0.12);

.detail-page {
  background: white;
  padding-bottom: 80px;
  color: $text-dark;
  font-family: Circular, -apple-system, BlinkMacSystemFont, Roboto, "Helvetica Neue", sans-serif;
}

.container {
  max-width: 1120px;
  margin: 0 auto;
  padding: 0 24px;
}

// 1. Image Grid
.hero-section {
  padding-top: 24px;
  .image-grid {
    display: grid;
    grid-template-columns: 2fr 1fr;
    gap: 8px;
    height: 480px;
    border-radius: 12px;
    overflow: hidden;
    
    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      cursor: pointer;
      transition: filter 0.3s;
      &:hover { filter: brightness(0.85); }
    }

    .grid-main { height: 100%; }
    .grid-sub {
      display: grid;
      grid-template-columns: 1fr 1fr;
      grid-template-rows: 1fr 1fr;
      gap: 8px;
      position: relative;
    }

    .view-all-btn {
      position: absolute;
      right: 24px;
      bottom: 24px;
      background: white;
      border: 1px solid $text-dark;
      padding: 7px 15px;
      border-radius: 8px;
      font-size: 14px;
      font-weight: 600;
      cursor: pointer;
      box-shadow: 0 1px 2px rgba(0,0,0,0.1);
      &:hover { background: #f7f7f7; }
    }
  }
}

// 2. Sticky Tab Nav
.sticky-tabs {
  background: white;
  z-index: 100;
  position: sticky;
  top: 0;
  border-bottom: 1px solid transparent;

  &.is-sticky {
    border-bottom: 1px solid $border-color;
  }

  .container {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .tabs {
    display: flex;
    list-style: none;
    padding: 0;
    margin: 0;

    li {
      padding: 24px 0;
      margin-right: 24px;
      font-weight: 600;
      font-size: 14px;
      color: $text-dark;
      cursor: pointer;
      border-bottom: 4px solid transparent;
      transition: all 0.2s;

      &:hover { color: black; }
      &.active {
        border-bottom-color: black;
      }
    }
  }

  .sticky-cta {
    display: flex;
    align-items: center;
    gap: 16px;
    .cta-info {
      text-align: left;
      .price-row {
        .label { font-size: 14px; font-weight: 600; margin-right: 4px; }
        .amount { font-weight: 800; font-size: 18px; text-decoration: underline; }
      }
      .rating-row {
        display: flex;
        align-items: center;
        font-size: 12px;
        margin-top: 2px;
        font-weight: 600;
        .star { font-size: 10px; margin-right: 2px; }
        .count { color: $text-light; font-weight: 400; margin-left: 2px; }
      }
    }
    .book-now-btn {
      background: $primary-color;
      color: white;
      border: none;
      padding: 12px 32px;
      border-radius: 999px;
      font-weight: 700;
      font-size: 16px;
      cursor: pointer;
      transition: all 0.2s;
      &:hover { background: darken($primary-color, 10%); transform: scale(1.02); }
    }
  }
}

// 3. Layout
.content-layout {
  display: grid;
  grid-template-columns: 1fr 340px;
  gap: 80px;
  margin-top: 32px;

  @media (max-width: 1024px) {
    grid-template-columns: 1fr;
    gap: 48px;
  }
}

.section { padding: 48px 0; }
.section-title { font-size: 22px; font-weight: 700; margin-bottom: 24px; }
.divider { border: none; border-top: 1px solid $border-color; margin: 0; }

// Header
.stay-header-section {
  .header-top { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 8px; }
  .category-chip { font-weight: 600; font-size: 14px; }
  .rating-box {
    display: flex; align-items: center; gap: 4px; font-size: 14px;
    .star { color: black; }
    .score { font-weight: 600; }
    .count { color: $text-light; }
  }
  .stay-title { font-size: 32px; font-weight: 700; margin-bottom: 12px; line-height: 1.1; }
  .address-row { font-size: 16px; font-weight: 600; text-decoration: underline; margin-bottom: 24px; }
  
  .tags-row {
    display: flex; flex-wrap: wrap; gap: 8px;
    .tag { font-size: 13px; color: $text-light; border: 1px solid #ddd; padding: 4px 10px; border-radius: 100px; }
  }
}

// Amino
.amenity-grid {
  display: grid; grid-template-columns: 1fr 1fr; gap: 16px;
  .amenity-item { display: flex; align-items: center; gap: 16px; font-size: 16px; .icon { font-size: 20px; } }
}

// Rooms
.room-card {
  display: flex; border: 1px solid $border-color; border-radius: 12px; margin-bottom: 16px; overflow: hidden;
  &:hover { box-shadow: 0 6px 16px rgba(0,0,0,0.1); }
  .room-image { width: 220px; height: 180px; position: relative; img { width: 100%; height: 100%; object-fit: cover; } }
  .room-details { flex: 1; padding: 20px; display: flex; flex-direction: column; }
  .room-header h3 { font-size: 18px; margin-bottom: 4px; }
  .room-desc { font-size: 14px; color: $text-light; margin-bottom: 12px; }
  .room-footer { display: flex; justify-content: space-between; align-items: flex-end; margin-top: auto; }
  .amount { font-size: 18px; font-weight: 800; }
  .select-btn { background: $text-dark; color: white; border: none; padding: 10px 20px; border-radius: 8px; font-weight: 600; cursor: pointer; }
}

// Map
.map-section {
  .kakao-map {
    width: 100%;
    height: 480px;
    border-radius: 12px;
    background: #f7f7f7;
    margin-top: 16px;
    border: 1px solid $border-color;
  }
}

// Sidebar
.sidebar-wrapper {
  .sticky-sidebar { position: sticky; top: 110px; }
  .reservation-card {
    border: 1px solid $border-color; border-radius: 12px; padding: 24px; box-shadow: $shadow;
    .card-header { display: flex; justify-content: space-between; align-items: flex-end; margin-bottom: 24px; }
    .price { font-size: 22px; font-weight: 700; }
    .unit { font-size: 16px; color: #444; }
    .rating-info { font-size: 14px; .star { font-size: 12px; } .score { font-weight: 600; } }

    .booking-inputs {
      border: 1px solid #B0B0B0; border-radius: 8px; margin-bottom: 16px;
      .date-row { display: flex; border-bottom: 1px solid #B0B0B0; cursor: pointer; }
      .input-item { flex: 1; padding: 10px 12px; &.checkin { border-right: 1px solid #B0B0B0; } }
      label { display: block; font-size: 10px; font-weight: 800; text-transform: uppercase; }
      .value { font-size: 14px; padding-top: 2px; }
      .guest-row { padding: 10px 12px; label { margin-bottom: 2px; } .guest-selector { font-size: 14px; } }
    }

    .reserve-btn {
      width: 100%; 
      background: $primary-color;
      color: white; 
      border: none; 
      padding: 14px; 
      border-radius: 8px; 
      font-size: 16px; 
      font-weight: 600; 
      cursor: pointer; 
      margin-bottom: 8px;
      transition: background 0.2s;
      &:hover { background: darken($primary-color, 10%); }
    }
    .price-notice { font-size: 14px; text-align: center; color: $text-light; margin-bottom: 24px; }

    .price-breakdown {
      .row { display: flex; justify-content: space-between; font-size: 16px; margin-bottom: 12px; 
        .label { text-decoration: underline; color: $text-light; }
      }
      hr { border: none; border-top: 1px solid $border-color; margin: 16px 0; }
      .total { font-weight: 700; .label { text-decoration: none; color: black; } }
    }
  }
}

.report-box { margin-top: 24px; text-align: center; font-size: 14px; color: $text-light; cursor: pointer; text-decoration: underline; font-weight: 500; }

// Mobile
.mobile-bottom-bar {
  display: none; position: fixed; bottom: 0; left: 0; right: 0; background: white; border-top: 1px solid $border-color; padding: 16px 24px;
  justify-content: space-between; align-items: center; z-index: 101;
  .amount { font-size: 18px; font-weight: 800; }
  .m-book-btn { background: $primary-color; color: white; border: none; padding: 12px 24px; border-radius: 8px; font-weight: 700; }
  
  @media (max-width: 1024px) { display: flex; }
}

@media (max-width: 768px) {
  .hero-section .image-grid { grid-template-columns: 1fr; .grid-sub { display: none; } }
  .stay-header-section .stay-title { font-size: 26px; }
  .amenity-grid { grid-template-columns: 1fr; }
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
// Picker Overlay
.datepicker-overlay {
  position: fixed; top: 0; left: 0; width: 100%; height: 100%;
  background: rgba(0,0,0,0.3);
  display: flex; align-items: center; justify-content: center;
  z-index: 2000;
}

@keyframes rotation {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

// Additional Pricing Styles
.final-price-main { display: flex; align-items: baseline; gap: 4px; }
.discount-badge-row {
  margin-top: 4px;
  .badge {
    background: #ff385c;
    color: white;
    padding: 2px 8px;
    border-radius: 4px;
    font-size: 11px;
    font-weight: 800;
  }
}

.discount-row {
  .val { color: #ff385c; font-weight: 600; }
}

.monthly-special {
  margin-top: 8px;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;

  .badge {
    background: #008489;
    color: white;
    padding: 2px 6px;
    border-radius: 3px;
    font-size: 10px;
    font-weight: 700;
  }

  .price-detail {
    text-align: right;
    .label { font-size: 11px; color: #717171; display: block; }
    .amount { font-size: 16px; font-weight: 800; color: #222; }
  }
}
</style>

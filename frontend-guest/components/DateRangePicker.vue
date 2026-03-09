<template>
  <div class="calendar-container">
    <div class="calendar-header">
      <div class="nights-display">
        <h2>{{ totalNights > 0 ? `${totalNights}박` : '날짜 선택' }}</h2>
        <p v-if="checkIn && checkOut" class="date-range-text">
          {{ formatDateRange(checkIn, checkOut) }}
        </p>
        <p v-else class="instruction">여행 날짜를 입력하여 정확한 숙박 요금을 확인하세요.</p>
      </div>
      
      <div class="input-boxes">
        <div class="input-box" :class="{ active: selecting === 'checkIn' }" @click="selecting = 'checkIn'">
          <span class="label">체크인</span>
          <span class="value">{{ checkIn ? formatDate(checkIn) : '날짜 추가' }}</span>
          <span v-if="checkIn" class="clear-icon" @click.stop="clearDates">×</span>
        </div>
        <div class="input-box" :class="{ active: selecting === 'checkOut' }" @click="selecting = 'checkOut'">
          <span class="label">체크아웃</span>
          <span class="value">{{ checkOut ? formatDate(checkOut) : '날짜 추가' }}</span>
          <span v-if="checkOut" class="clear-icon" @click.stop="clearDates">×</span>
        </div>
      </div>
    </div>

    <div class="calendar-grid-wrapper">
      <button class="nav-btn prev" @click="changeMonth(-1)">⟨</button>
      <button class="nav-btn next" @click="changeMonth(1)">⟩</button>

      <div class="dual-calendars">
        <!-- Month 1 -->
        <div class="month-view">
          <h3>{{ formatYearMonth(currentMonth) }}</h3>
          <div class="days-header">
            <span v-for="d in weekDays" :key="d">{{ d }}</span>
          </div>
          <div class="days-grid">
            <div v-for="blank in firstDayOffset(currentMonth)" :key="`b1-${blank}`" class="day empty"></div>
            <div 
              v-for="day in daysInMonth(currentMonth)" 
              :key="`d1-${day}`" 
              class="day"
              :class="getDayClass(currentMonth, day)"
              @click="handleDayClick(currentMonth, day)"
              @mouseenter="handleMouseEnter(currentMonth, day)"
            >
              {{ day }}
            </div>
          </div>
        </div>

        <!-- Month 2 -->
        <div class="month-view">
          <h3>{{ formatYearMonth(nextMonth) }}</h3>
          <div class="days-header">
            <span v-for="d in weekDays" :key="d">{{ d }}</span>
          </div>
          <div class="days-grid">
            <div v-for="blank in firstDayOffset(nextMonth)" :key="`b2-${blank}`" class="day empty"></div>
            <div 
              v-for="day in daysInMonth(nextMonth)" 
              :key="`d2-${day}`" 
              class="day"
              :class="getDayClass(nextMonth, day)"
              @click="handleDayClick(nextMonth, day)"
              @mouseenter="handleMouseEnter(nextMonth, day)"
            >
              {{ day }}
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="calendar-footer">
      <button class="clear-btn" @click="clearDates">날짜 지우기</button>
      <button class="close-btn" @click="$emit('close')">닫기</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'

const props = defineProps<{
  initialCheckIn?: string
  initialCheckOut?: string
}>()

const emit = defineEmits(['change', 'close'])

const weekDays = ['일', '월', '화', '수', '목', '금', '토']
const checkIn = ref<Date | null>(props.initialCheckIn ? new Date(props.initialCheckIn) : null)
const checkOut = ref<Date | null>(props.initialCheckOut ? new Date(props.initialCheckOut) : null)
const selecting = ref<'checkIn' | 'checkOut'>('checkIn')
const hoverDate = ref<Date | null>(null)

const currentMonth = ref(new Date())
currentMonth.value.setDate(1)

const nextMonth = computed(() => {
  const d = new Date(currentMonth.value)
  d.setMonth(d.getMonth() + 1)
  return d
})

const totalNights = computed(() => {
  if (!checkIn.value || !checkOut.value) return 0
  const diff = checkOut.value.getTime() - checkIn.value.getTime()
  return Math.max(0, Math.ceil(diff / (1000 * 60 * 60 * 24)))
})

// Helper functions
const daysInMonth = (date: Date) => new Date(date.getFullYear(), date.getMonth() + 1, 0).getDate()
const firstDayOffset = (date: Date) => new Date(date.getFullYear(), date.getMonth(), 1).getDay()

const formatYearMonth = (date: Date) => `${date.getFullYear()}년 ${date.getMonth() + 1}월`
const formatDate = (date: Date) => {
  return `${date.getFullYear()}. ${date.getMonth() + 1}. ${date.getDate()}.`
}
const formatYearMonthDate = (date: Date) => {
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

const formatDateRange = (start: Date, end: Date) => {
  return `${formatDate(start)} - ${formatDate(end)}`
}

const changeMonth = (delta: number) => {
  const d = new Date(currentMonth.value)
  d.setMonth(d.getMonth() + delta)
  currentMonth.value = d
}

const handleDayClick = (month: Date, day: number) => {
  const clickedDate = new Date(month.getFullYear(), month.getMonth(), day)
  
  // Prevent selecting past dates
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  if (clickedDate < today) return

  if (selecting.value === 'checkIn') {
    checkIn.value = clickedDate
    checkOut.value = null
    selecting.value = 'checkOut'
  } else {
    if (checkIn.value && clickedDate > checkIn.value) {
      checkOut.value = clickedDate
      selecting.value = 'checkIn'
      emitUpdate()
    } else {
      // If clicked date is before check-in, set it as new check-in
      checkIn.value = clickedDate
      checkOut.value = null
      selecting.value = 'checkOut'
    }
  }
}

const handleMouseEnter = (month: Date, day: number) => {
  if (selecting.value === 'checkOut' && checkIn.value) {
    hoverDate.value = new Date(month.getFullYear(), month.getMonth(), day)
  } else {
    hoverDate.value = null
  }
}

const getDayClass = (month: Date, day: number) => {
  const date = new Date(month.getFullYear(), month.getMonth(), day)
  const today = new Date()
  today.setHours(0, 0, 0, 0)

  const isSelected = (d: Date | null) => d && d.getTime() === date.getTime()
  const isCheckIn = isSelected(checkIn.value)
  const isCheckOut = isSelected(checkOut.value)
  
  let inRange = false
  if (checkIn.value && checkOut.value) {
    inRange = date > checkIn.value && date < checkOut.value
  } else if (checkIn.value && hoverDate.value && selecting.value === 'checkOut') {
    inRange = date > checkIn.value && date < hoverDate.value
  }

  return {
    'selected': isCheckIn || isCheckOut,
    'in-range': inRange,
    'range-start': isCheckIn && (checkOut.value || hoverDate.value),
    'range-end': isCheckOut,
    'past': date < today,
    'today': date.getTime() === today.getTime()
  }
}

const clearDates = () => {
  checkIn.value = null
  checkOut.value = null
  selecting.value = 'checkIn'
  emitUpdate()
}

const emitUpdate = () => {
  emit('change', {
    checkIn: checkIn.value ? formatYearMonthDate(checkIn.value) : '',
    checkOut: checkOut.value ? formatYearMonthDate(checkOut.value) : '',
    nights: totalNights.value
  })
}
</script>

<style lang="scss" scoped>
$text-dark: #222222;
$text-light: #717171;
$bg-gray: #F7F7F7;
$primary-color: #7575ff; // Using existing brand blue

.calendar-container {
  background: white;
  padding: 32px;
  border-radius: 16px;
  box-shadow: 0 16px 32px rgba(0,0,0,0.15);
  width: 100%;
  max-width: 850px;
  user-select: none;
}

.calendar-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 24px;
  
  .nights-display {
    h2 { font-size: 22px; font-weight: 700; margin-bottom: 4px; }
    p { font-size: 14px; color: $text-light; }
  }

  .input-boxes {
    display: flex;
    gap: 8px;
    
    .input-box {
      border: 1px solid #ddd;
      border-radius: 8px;
      padding: 10px 16px;
      min-width: 140px;
      cursor: pointer;
      position: relative;
      transition: border-color 0.2s;
      
      &.active { border: 2px solid black; padding: 9px 15px; }
      
      .label { display: block; font-size: 10px; font-weight: 800; color: $text-dark; }
      .value { font-size: 14px; color: $text-dark; }
      .clear-icon { position: absolute; right: 8px; top: 50%; transform: translateY(-50%); font-size: 18px; color: $text-light; }
    }
  }
}

.calendar-grid-wrapper {
  position: relative;
  
  .nav-btn {
    position: absolute;
    top: 0;
    background: none;
    border: none;
    font-size: 18px;
    padding: 8px;
    cursor: pointer;
    border-radius: 50%;
    &:hover { background: $bg-gray; }
    &.prev { left: -10px; }
    &.next { right: -10px; }
  }
}

.dual-calendars {
  display: flex;
  gap: 40px;
}

.month-view {
  flex: 1;
  h3 { font-size: 16px; font-weight: 700; text-align: center; margin-bottom: 20px; }
  
  .days-header {
    display: grid;
    grid-template-columns: repeat(7, 1fr);
    margin-bottom: 10px;
    span { font-size: 12px; color: $text-light; text-align: center; }
  }
  
  .days-grid {
    display: grid;
    grid-template-columns: repeat(7, 1fr);
  }
}

.day {
  aspect-ratio: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  border-radius: 50%;
  position: relative;
  transition: all 0.2s;
  z-index: 1;

  &:hover:not(.empty):not(.past):not(.selected) {
    background: $bg-gray;
  }

  &.past { color: #ddd; cursor: default; text-decoration: line-through; }
  &.today { border-bottom: 2px solid $primary-color; border-radius: 0; }
  
  &.selected {
    color: white;
    &::before {
      content: '';
      position: absolute;
      top: 0; left: 0; right: 0; bottom: 0;
      background: black;
      border-radius: 50%;
      z-index: -1;
    }
  }

  &.in-range::after, &.range-start::after, &.range-end::after {
    content: '';
    position: absolute;
    top: 0; bottom: 0;
    background: #F7F7F7;
    z-index: -2;
  }

  &.in-range::after {
    left: 0; right: 0;
  }
  
  &.range-start::after {
    left: 50%; right: 0;
  }

  &.range-end::after {
    left: 0; right: 50%;
  }
}

.calendar-footer {
  display: flex;
  justify-content: space-between;
  margin-top: 24px;
  
  .clear-btn {
    background: none; border: none; text-decoration: underline; font-weight: 600; cursor: pointer;
    &:hover { background: $bg-gray; border-radius: 4px; }
  }
  
  .close-btn {
    background: #222; color: white; border: none; padding: 8px 16px; border-radius: 8px; font-weight: 600; cursor: pointer;
    &:hover { background: black; }
  }
}

@media (max-width: 768px) {
  .dual-calendars { flex-direction: column; gap: 20px; }
  .calendar-container { padding: 16px; max-width: 100%; }
}
</style>

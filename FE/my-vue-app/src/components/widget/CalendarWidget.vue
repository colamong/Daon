<template>
  <div class="p-6 rounded-xl border shadow-md bg-white w-full max-w-lg">
    <!-- 헤더 -->
    <div class="flex items-center justify-between mb-4">
      <!-- 왼쪽: 월 및 네비게이션 -->
      <div class="flex items-center space-x-3">
        <button @click="prevMonth" aria-label="이전 달" class="text-lg text-gray-600 hover:text-black">
          ‹
        </button>
        <span class="text-xl font-paperBold">{{ currentMonthLabel }}</span>
        <button @click="nextMonth" aria-label="다음 달" class="text-lg text-gray-600 hover:text-black">
          ›
        </button>
      </div>

      <!-- 오른쪽: 연도 -->
      <div class="text-lg font-paper text-gray-700">{{ currentYear }}</div>
    </div>

    <!-- 요일 헤더 -->
    <div class="grid grid-cols-7 text-center text-sm font-paper text-gray-500 mb-2">
      <div
        v-for="day in weekdays"
        :key="day"
        class="w-12 h-12 flex items-center justify-center"
      >
        {{ day }}
      </div>
    </div>

    <!-- 날짜들 -->
    <div class="grid grid-cols-7 gap-y-2">
      <div
        v-for="(day, index) in calendarDays"
        :key="index"
        class="w-12 h-12 flex items-center justify-center"
      >
        <div
          v-if="day.isCurrentMonth"
          :class="[
            'w-full h-full flex items-center justify-center rounded-full text-base font-paper cursor-pointer',
            day.isToday ? 'bg-blue-100' : '',
            hasSchedule(day.date) ? 'ring-2 ring-blue-200' : ''
          ]"
          @click="selectDate(day.date)"
        >
          {{ day.date.getDate() }}
        </div>

        <div
          v-else-if="day.isNextMonth"
          class="w-full h-full flex items-center justify-center text-gray-300 text-base font-paper"
        >
          {{ day.date.getDate() }}
        </div>

        <div
          v-else-if="day.isPrevMonth"
          class="w-full h-full"
        >
          <!-- 이전달 빈칸 -->
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import dayjs from 'dayjs'

const today = dayjs()
const current = ref(today.startOf('month'))
const selected = ref(null)

const weekdays = ['S', 'M', 'T', 'W', 'T', 'F', 'S']

const currentYear = computed(() => current.value.year())
const currentMonthLabel = computed(() => `${current.value.month() + 1}`)

const calendarDays = computed(() => {
  const start = current.value.startOf('month').startOf('week')
  const end = current.value.endOf('month').endOf('week')
  const days = []
  let cursor = start.clone()
  while (cursor.isBefore(end) || cursor.isSame(end)) {
    const cursorMonth = cursor.month()
    const currentMonth = current.value.month()

    const isCurrentMonth = cursorMonth === currentMonth
    const isNextMonth = cursorMonth === (currentMonth + 1) % 12
    const isPrevMonth = cursorMonth === (currentMonth + 11) % 12

    days.push({
      date: cursor.toDate(),
      isCurrentMonth,
      isNextMonth,
      isPrevMonth,
      isToday: cursor.isSame(today, 'day')
    })
    cursor = cursor.add(1, 'day')
  }
  return days
})

function prevMonth() {
  current.value = current.value.subtract(1, 'month')
}
function nextMonth() {
  current.value = current.value.add(1, 'month')
}
function selectDate(date) {
  selected.value = date
  console.log('선택된 날짜:', date)
}

const schedules = [
  dayjs().date(6).toDate(),
  dayjs().date(18).toDate(),
  dayjs().date(22).toDate(),
]
function hasSchedule(date) {
  return schedules.some(d => dayjs(d).isSame(date, 'day'))
}
</script>

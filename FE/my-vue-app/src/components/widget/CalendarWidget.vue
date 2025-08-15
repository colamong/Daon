<!-- src/components/widget/CalendarWidget.vue -->
<template>
  <div
    class="p-6 rounded-xl border shadow-md bg-white w-full h-full overflow-hidden"
  >
    <!-- 헤더 -->
    <div class="flex items-center justify-between mb-4">
      <div class="flex items-center space-x-3">
        <button
          @click="prevMonth"
          aria-label="이전 달"
          class="text-lg text-gray-600 hover:text-black"
        >
          ‹
        </button>
        <span class="text-2xl font-paperBold">{{ currentMonthLabel }}</span>
        <button
          @click="nextMonth"
          aria-label="다음 달"
          class="text-lg text-gray-600 hover:text-black"
        >
          ›
        </button>
      </div>
      <div class="text-lg font-paper text-gray-700">{{ currentYear }}</div>
    </div>

    <!-- 요일 -->
    <div
      class="grid grid-cols-7 text-center text-m font-paper text-gray-500 mb-2"
    >
      <div
        v-for="day in weekdays"
        :key="day"
        class="w-12 h-12 flex items-center justify-center"
      >
        {{ day }}
      </div>
    </div>

    <!-- 날짜 -->
    <div class="grid grid-cols-7 gap-y-2">
      <div
        v-for="(day, idx) in calendarDays"
        :key="idx"
        class="w-12 h-12 flex items-center justify-center"
      >
        <!-- 이번 달 날짜 -->
        <div
          v-if="day.isCurrentMonth"
          @click="selectDate(day.date)"
          class="cursor-pointer flex flex-col items-center justify-center relative group"
        >
          <!-- 기본 날짜 표시만 유지 -->
          <div>
            <div
              class="rounded-full flex items-center justify-center transition-all duration-200"
              :class="day.isToday ? 'bg-blue-100' : ''"
              :style="hoverStyle(day.date)"
            >
              <span class="text-base font-paper relative z-10">{{ day.date.getDate() }}</span>
            </div>
          </div>
        </div>

        <!-- 다음 달 날짜 -->
        <div
          v-else-if="day.isNextMonth"
          class="w-full h-full flex items-center justify-center text-gray-400 text-base font-paper"
        >
          {{ day.date.getDate() }}
        </div>

        <!-- 이전 달(빈칸) -->
        <div v-else class="w-full h-full"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import dayjs from "dayjs";
import { cardColors } from "@/data/cardColors.js";

const emit = defineEmits(["update-month", "date-selected"]);
const props = defineProps({
  events: { type: Array, required: true },
});

const today = dayjs();
const current = ref(today.startOf("month"));
const weekdays = ["S", "M", "T", "W", "T", "F", "S"];


const currentYear = computed(() => current.value.year());
const currentMonthLabel = computed(() => `${current.value.month() + 1}`);

const calendarDays = computed(() => {
  const start = current.value.startOf("month").startOf("week");
  const end = current.value.endOf("month").endOf("week");
  const days = [];
  let cursor = start.clone();

  while (cursor.isBefore(end) || cursor.isSame(end)) {
    const m = cursor.month();
    const cm = current.value.month();
    days.push({
      date: cursor.toDate(),
      isCurrentMonth: m === cm,
      isNextMonth: m === (cm + 1) % 12,
      isToday: cursor.isSame(today, "day"),
    });
    cursor = cursor.add(1, "day");
  }
  return days;
});


function prevMonth() {
  current.value = current.value.subtract(1, "month");
  emit("update-month", current.value.format("YYYY-MM"));
}
function nextMonth() {
  current.value = current.value.add(1, "month");
  emit("update-month", current.value.format("YYYY-MM"));
}
function selectDate(date) {
  emit("date-selected", date);
}

function getScheduleColor(date) {
  const ev = props.events.find((e) => dayjs(e.date).isSame(date, "day"));
  if (!ev) return null;
  const idx = dayjs(date).date() - 1;
  return cardColors[idx % cardColors.length];
}


// 원 테두리 및 호버 효과
function hoverStyle(date) {
  const c = getScheduleColor(date);
  const isMobile = window.innerWidth < 768;
  const size = isMobile ? "2rem" : "2.5rem";
  
  if (c) {
    return {
      width: size,
      height: size,
      boxShadow: `0 0 0 2px ${c}`,
      '--hover-color': c,
      '--hover-text-color': 'white'
    };
  }
  return {
    width: size,
    height: size,
    '--hover-color': '#e5e7eb',
    '--hover-text-color': 'black'
  };
}

</script>

<style scoped>
.group:hover .rounded-full {
  background-color: var(--hover-color) !important;
  transform: scale(1.1);
}

.group:hover .rounded-full span {
  color: var(--hover-text-color);
  font-weight: 600;
}
</style>

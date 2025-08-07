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
          class="cursor-pointer flex flex-col items-center justify-center relative"
        >
          <!-- 다이어리 이미지가 있으면 이미지 표시 -->
          <div v-if="getDiaryForDate(day.date)" class="relative">
            <img 
              :src="getDiaryForDate(day.date).imageUrl" 
              :alt="`${day.date.getDate()}일 그림일기`"
              class="w-10 h-10 rounded-lg object-cover shadow-sm"
              @error="(e) => e.target.style.display = 'none'"
            />
            <div class="absolute -bottom-1 -right-1 bg-white rounded-full w-4 h-4 flex items-center justify-center shadow-sm">
              <span class="text-xs font-semibold text-gray-800">{{ day.date.getDate() }}</span>
            </div>
          </div>
          <!-- 다이어리 이미지가 없으면 기본 날짜 표시 -->
          <div v-else>
            <div
              class="rounded-full flex items-center justify-center"
              :class="day.isToday ? 'bg-blue-100' : ''"
              :style="ringStyle(day.date)"
            >
              <span class="text-base font-paper">{{ day.date.getDate() }}</span>
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
import { ref, computed, onMounted, watch } from "vue";
import dayjs from "dayjs";
import { childService } from "@/services/childService.js";

const emit = defineEmits(["update-month"]);
const props = defineProps({
  events: { type: Array, required: true },
});

const today = dayjs();
const current = ref(today.startOf("month"));
const weekdays = ["S", "M", "T", "W", "T", "F", "S"];

// 다이어리 데이터 상태
const diaries = ref([]);
const childId = 3; // 고정 childId

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

// 월별 다이어리 조회
async function fetchMonthlyDiaries() {
  try {
    const year = current.value.year();
    const month = current.value.month() + 1; // dayjs month는 0부터 시작하므로 +1
    
    console.log('다이어리 조회:', { childId, year, month });
    const response = await childService.getMonthlyDiaries(childId, year, month);
    diaries.value = response || [];
    console.log('다이어리 조회 결과:', diaries.value);
  } catch (error) {
    console.error('다이어리 조회 오류:', error);
    diaries.value = [];
  }
}

function prevMonth() {
  current.value = current.value.subtract(1, "month");
  emit("update-month", current.value.format("YYYY-MM"));
  fetchMonthlyDiaries(); // 월 변경 시 다이어리 재조회
}
function nextMonth() {
  current.value = current.value.add(1, "month");
  emit("update-month", current.value.format("YYYY-MM"));
  fetchMonthlyDiaries(); // 월 변경 시 다이어리 재조회
}
function selectDate(date) {
  /* 필요한 경우 */
}

// 일정 색과 맞추기 위한 컬러 리스트
const colorList = [
  "#FF8A80",
  "#FFB74D",
  "#FFD54F",
  "#81C784",
  "#4FC3F7",
  "#BA68C8",
  "#90A4AE",
  "#F06292",
  "#A1887F",
  "#7986CB",
  "#AED581",
  "#4DB6AC",
  "#FFF176",
  "#E57373",
  "#64B5F6",
  "#DCE775",
];

function getScheduleColor(date) {
  const ev = props.events.find((e) => dayjs(e.date).isSame(date, "day"));
  if (!ev) return null;
  const idx = dayjs(date).date() - 1;
  return colorList[idx % colorList.length];
}

// 해당 날짜의 다이어리 찾기
function getDiaryForDate(date) {
  const dateString = dayjs(date).format('YYYY-MM-DD');
  return diaries.value.find(diary => {
    if (diary.createdAt) {
      return dayjs(diary.createdAt).format('YYYY-MM-DD') === dateString;
    }
    return false;
  });
}

// 원 테두리 및 크기 조정
function ringStyle(date) {
  const c = getScheduleColor(date);
  if (c) {
    return {
      width: "2.5rem", // 48px
      height: "2.5rem",
      boxShadow: `0 0 0 2px ${c}`,
    };
  }
  return {
    width: "2.5rem", // 40px
    height: "2.5rem",
  };
}

// 컴포넌트 마운트 시 초기 다이어리 조회
onMounted(() => {
  fetchMonthlyDiaries();
});
</script>

<style scoped>
/* 필요시 추가 */
</style>

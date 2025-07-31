<template>
  <div
    :style="{ backgroundImage: `url(${bgImg})` }"
    class="h-screen relative bg-cover bg-center"
  >
    <!-- 1) 고정 헤더 (맨 위, z-30) -->
    <header class="fixed top-4 left-4 z-30">
      <button
        @click="goBack"
        class="w-20 h-20 bg-white rounded-lg shadow flex items-center justify-center"
      >
        <img
          :src="HomeIcon"
          alt="뒤로가기"
          class="w-full h-full object-contain"
        />
      </button>
    </header>

    <!-- 2) 월 네비게이션 (z-30) -->
    <div class="flex justify-center items-center mb-2 space-x-4 z-30">
      <button
        @click="prevMonth"
        class="px-3 py-1 bg-gray-200 rounded hover:bg-gray-300"
      >
        ◀
      </button>
      <div class="relative cloud-header w-64 h-32">
        <img
          :src="cloudImg"
          alt="cloud"
          class="absolute inset-0 w-full h-full object-contain"
        />
        <span
          class="absolute inset-0 flex items-center justify-center text-4xl font-shark text-gray-800"
        >
          {{ formattedMonth }}
        </span>
      </div>
      <button
        @click="nextMonth"
        class="px-3 py-1 bg-gray-200 rounded hover:bg-gray-300"
      >
        ▶
      </button>
    </div>

    <!-- 3) 달력 컨테이너 (z-0) -->
    <div class="calendar-container relative z-0 font-shark">
      <FullCalendar ref="calendarRef" :options="calendarOptions" />
    </div>

    <!-- 달력 오른쪽 상단 연주 펭귄 -->
    <img
      :src="playPenImg"
      alt="playing penguin"
      class="fixed bottom-4 right-4 w-28 h-28 pointer-events-none z-20"
    />
  </div>
</template>

<script setup>
import { ref, computed, reactive, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import axios from "axios";

import FullCalendar from "@fullcalendar/vue3";
import dayGridPlugin from "@fullcalendar/daygrid";

import HomeIcon from "../assets/images/Home.png";
import cloudImg from "../assets/images/cloud.png";
import snowImg from "../assets/images/snow.png";
import playPenImg from "../assets/images/play_pen.png";
import bgImg from "../assets/images/child_calender.png";

// ————————————————
// 1) 레퍼런스 & 라우터
// ————————————————
const calendarRef = ref(null);
const route = useRoute();
const router = useRouter();
const childId = route.params.id;
function goBack() {
  router.back();
}

// ————————————————
// 2) 연·월 상태 & formatted
// ————————————————
const today = new Date();
const currentYear = ref(today.getFullYear());
const currentMonth = ref(today.getMonth() + 1);
const formattedMonth = computed(
  () => `${currentMonth.value}월 ${currentYear.value}`
);

// ————————————————
// 3) 그림일기 로드
// ————————————————
const diaries = ref([]);
async function fetchDiaries() {
  try {
    const res = await axios.get(`/api/children/${childId}/diaries`, {
      params: { year: currentYear.value, month: currentMonth.value },
    });
    diaries.value = res.data;
  } catch (e) {
    console.error("그림일기 로드 실패:", e);
  }
}
const diariesMap = computed(() =>
  diaries.value.reduce((acc, { date, imageUrl }) => {
    acc[date] = imageUrl;
    return acc;
  }, {})
);

// ————————————————
// 4) FullCalendar 옵션
// ————————————————
const calendarOptions = reactive({
  plugins: [dayGridPlugin],
  initialView: "dayGridMonth",
  headerToolbar: false,

  // 높이 고정
  height: 500,
  contentHeight: 500,

  views: {
    dayGridMonth: {
      fixedWeekCount: false,
      showNonCurrentDates: false,
    },
  },

  initialDate: today,

  dayCellDidMount(info) {
    // 숫자 뒤 구름 + font-shark
    const numEl = info.el.querySelector(".fc-daygrid-day-number");
    if (numEl) {
      numEl.classList.add("font-shark");
      Object.assign(numEl.style, {
        display: "inline-flex",
        width: "26px",
        height: "26px",
        alignItems: "center",
        justifyContent: "center",
        backgroundImage: `url(${cloudImg})`,
        backgroundSize: "contain",
        backgroundRepeat: "no-repeat",
        fontSize: "1rem",
        color: "#000",
      });
    }
    // 그림일기 이미지 삽입
    const url = diariesMap.value[info.dateStr];
    if (url) {
      const wrap = document.createElement("div");
      Object.assign(wrap.style, {
        marginTop: "2.5rem",
        height: "5rem",
        overflow: "hidden",
        borderRadius: "0.375rem",
      });
      const img = document.createElement("img");
      img.src = url;
      img.alt = "Diary";
      Object.assign(img.style, {
        width: "100%",
        height: "100%",
        objectFit: "cover",
      });
      wrap.appendChild(img);
      info.el.appendChild(wrap);
    }
  },
});

// ————————————————
// 5) 이전/다음 달
// ————————————————
function prevMonth() {
  if (currentMonth.value === 1) {
    currentYear.value--;
    currentMonth.value = 12;
  } else {
    currentMonth.value--;
  }
}
function nextMonth() {
  if (currentMonth.value === 12) {
    currentYear.value++;
    currentMonth.value = 1;
  } else {
    currentMonth.value++;
  }
}

// ————————————————
// 6) 연·월 변경 감지 → 데이터와 뷰 갱신
// ————————————————
watch(
  [currentYear, currentMonth],
  async ([y, m]) => {
    // 1) 그림일기 다시 불러오기
    await fetchDiaries();
    // 2) 캘린더 이동
    const api = calendarRef.value.getApi();
    api.gotoDate(`${y}-${String(m).padStart(2, "0")}-01`);
    // 3) 강제 렌더 (dayCellDidMount 재실행)
    api.render();
  },
  { immediate: true }
);
</script>

<style>
/* 달력 너비 제한 및 흰 배경 */
.calendar-container {
  max-width: 1200px;
  margin: 0 auto;
}
.calendar-container .fc,
.calendar-container .fc .fc-daygrid-day-frame {
  background-color: #fff;
}

/* 숫자 뒤에 구름 */
.calendar-container .fc-daygrid-day-number {
  position: relative;
  display: inline-flex !important;
  align-items: center;
  justify-content: center;
  width: 1.5rem;
  height: 1.5rem;
  background: url("../assets/images/cloud.png") center/cover no-repeat !important;
  color: #000;
}
</style>

<template>
  <div class="h-screen relative bg-amber-50 overflow-hidden p-4 pt-2">
    <!-- 1) 고정 헤더 -->
    <header class="fixed top-4 left-4 z-20">
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

    <!-- 2) 구름 위에 월 표시 -->
    <div class="flex justify-center items-center mb-6 space-x-4">
      <button
        @click="prevMonth"
        class="px-3 py-1 bg-gray-200 rounded hover:bg-gray-300"
      >
        ◀
      </button>

      <div class="relative cloud-header w-64 h-32">
        <!-- 구름 이미지 -->
        <img
          :src="cloudImg"
          alt="cloud background"
          class="absolute inset-0 w-full h-full object-contain"
        />
        <!-- 구름 위 텍스트 -->
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

    <!-- 1) 캘린더 컨테이너로 너비 제한 -->
    <div class="calendar-container font-shark">
      <!-- 2) FullCalendar 렌더 -->
      <FullCalendar ref="calendarRef" :options="calendarOptions" />
    </div>
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

// 1) 레퍼런스 & 라우터
const calendarRef = ref(null);
const route = useRoute();
const router = useRouter();
const childId = route.params.id;
function goBack() {
  router.back();
}

// 2) 연·월 상태 & 포맷
const today = new Date();
const currentYear = ref(today.getFullYear());
const currentMonth = ref(today.getMonth() + 1);
const formattedMonth = computed(
  () => `${currentMonth.value}월 ${currentYear.value}`
);

// 3) 그림일기 데이터 로드
const diaries = ref([]);
async function fetchDiaries() {
  try {
    const res = await axios.get(`/api/children/${childId}/diaries`, {
      params: { year: currentYear.value, month: currentMonth.value },
    });
    diaries.value = res.data;
  } catch (err) {
    console.error("그림일기 로드 실패:", err);
  }
}
const diariesMap = computed(() =>
  diaries.value.reduce((acc, { date, imageUrl }) => {
    acc[date] = imageUrl;
    return acc;
  }, {})
);

// 4) FullCalendar 옵션
const calendarOptions = reactive({
  plugins: [dayGridPlugin],
  initialView: "dayGridMonth",
  headerToolbar: false,
  initialDate: today,
  dayCellDidMount(info) {
    // 날짜 숫자 뒤 구름 + 폰트
    const numEl = info.el.querySelector(".fc-daygrid-day-number");
    if (numEl) {
      numEl.classList.add("font-shark");
      Object.assign(numEl.style, {
        display: "inline-flex",
        width: "32px",
        height: "32px",
        alignItems: "center",
        justifyContent: "center",
        backgroundImage: `url(${cloudImg})`,
        backgroundSize: "contain",
        backgroundRepeat: "no-repeat",
        fontSize: "1.25rem",
        fontWeight: "bold",
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

// 5) 버튼 핸들러: 연·월 상태만 바꾸기
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

// 6) 연·월 변경 감지: ①데이터 로드 → ②뷰 이동 → ③강제 렌더
watch(
  [currentYear, currentMonth],
  async ([y, m]) => {
    // ① 새 달 그림일기 불러오기
    await fetchDiaries();

    // ② FullCalendar 뷰 이동
    const api = calendarRef.value.getApi();
    api.gotoDate(`${y}-${String(m).padStart(2, "0")}-01`);

    // ③ 강제 렌더링 (dayCellDidMount 훅 재실행)
    api.render();
  },
  { immediate: true }
);
</script>

<style scoped>
/* 2) 캘린더를 이 영역 안으로 강제 축소 */
.calendar-container {
  max-width: 1200px;
  margin: 0 auto;
}
</style>

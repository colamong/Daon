<template>
  <div class="h-screen relative overflow-hidden pt-6">
    <!-- background -->
    <img
      :src="bgImage"
      alt="background"
      class="absolute inset-0 w-full h-full object-cover -z-10"
    />
    <!-- 1) 고정 헤더 -->
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

    <!-- 2) 달력 컨테이너 (FullCalendar 기본 네비 포함) -->
    <div
      class="calendar-container relative z-0 font-shark pt-6 mt-6 mx-auto w-[900px]"
    >
      <FullCalendar ref="calendarRef" :options="calendarOptions" />
    </div>

    <!-- 3) 장식 이미지 (하단 우측 펭귄) -->
    <img
      :src="playPenImg"
      alt="playing penguin"
      class="fixed bottom-4 right-4 w-40 h-40 pointer-events-none z-20"
    />
  </div>
</template>

<script setup>
import { ref, computed, reactive, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import axios from "axios";

import FullCalendar from "@fullcalendar/vue3";
import koLocale from "@fullcalendar/core/locales/ko";
import dayGridPlugin from "@fullcalendar/daygrid";

import bgImage from "../assets/images/child_calender.png";
import HomeIcon from "../assets/images/Home.png";
import cloudImg from "../assets/images/cloud.png";
import playPenImg from "../assets/images/play_pen.png";

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

// 3) 그림일기 데이터 로드 (배열 보장)
const diaries = ref([]);
async function fetchDiaries() {
  try {
    const res = await axios.get(`/api/children/${childId}/diaries`, {
      params: { year: currentYear.value, month: currentMonth.value },
    });
    // 배열인지, 객체 안 배열인지 판별
    let arr = [];
    if (Array.isArray(res.data)) {
      arr = res.data;
    } else if (Array.isArray(res.data.diaries)) {
      arr = res.data.diaries;
    }
    diaries.value = arr;
  } catch (e) {
    console.error("그림일기 로드 실패:", e);
    diaries.value = []; // 실패 시 빈 배열
  }
}

// 3.5) 컴포넌트 최초 마운트 시 데이터 로드
onMounted(fetchDiaries);

// 4) 날짜→이미지 URL 맵 (diaries.value가 항상 배열임을 가정)
const diariesMap = computed(() => {
  const arr = Array.isArray(diaries.value) ? diaries.value : [];
  return arr.reduce((map, { date, imageUrl }) => {
    map[date] = imageUrl;
    return map;
  }, {});
});

// 5) FullCalendar 옵션
const calendarOptions = reactive({
  plugins: [dayGridPlugin],
  initialView: "dayGridMonth",
  locale: koLocale,
  headerToolbar: {
    left: "",
    center: "title",
    right: "today prev next",
  },
  titleFormat: {
    year: "numeric", // → "2025"
    month: "long", // → "8월"
  },
  height: 500,
  contentHeight: 500,
  views: {
    dayGridMonth: {
      fixedWeekCount: false,
      showNonCurrentDates: false,
    },
  },
  initialDate: today,

  // 뷰(prev/next/today 포함)가 바뀔 때마다 연·월 state 갱신 + 다이어리 재로드
  datesSet: async (info) => {
    const d = info.start;
    currentYear.value = d.getFullYear();
    currentMonth.value = d.getMonth() + 1;
    await fetchDiaries();
    // 강제 렌더 호출하여 dayCellDidMount 재실행
    calendarRef.value.getApi().render();
  },
  dayCellContent: (arg) => {
    const dayNum = arg.date.getDate();
    return {
      html: `
      <div class="inline-flex w-6 h-6 items-center justify-center
                      bg-[url(${cloudImg})] bg-contain bg-no-repeat
                      font-shark text-sm text-black">
        ${dayNum}
      </div>
    `,
    };
  },

  // 날짜 셀 마운트 시: 구름 숫자 + 그림일기 삽입
  dayCellDidMount(info) {
    const numEl = info.el.querySelector(".fc-daygrid-day-number");
    if (numEl) {
      numEl.textContent = String(info.date.getDate());
      numEl.classList.add("font-shark");
      Object.assign(numEl.style, {
        display: "inline-flex",
        width: "20px",
        height: "20px",
        alignItems: "center",
        justifyContent: "center",
        backgroundImage: `url(${cloudImg})`,
        backgroundSize: "contain",

        fontSize: "1rem",
        color: "#000",
      });
    }
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
</script>

<style scoped>
/* 1) 캘린더 컨테이너 너비/높이 제한 */
.calendar-container {
  max-width: 900px;
  margin: 30 auto;
  /* 원하시는 높이로 조절하세요 */
  height: 700px;
}

:deep(.fc .fc-col-header) {
  background-color: #fff !important;
}
/* 2) 그리드(날짜 셀) 영역은 하얀 배경 유지 */
:deep(.fc .fc-daygrid-day-frame) {
  background-color: #fff !important;
}

/* 3) 툴바 래퍼 자체 (header-toolbar, toolbar-chunk) 투명 처리 */
:deep(.fc .fc-header-toolbar),
:deep(.fc .fc-header-toolbar .fc-toolbar-chunk) {
  background: transparent !important;
  border: none !important;
  box-shadow: none !important;
}

/* 4) 툴바 버튼도 투명으로 (원하면 살짝 반투명) */

/* 5) 툴바 가운데 title 폰트 크기 좀 줄이고 위치 조정 */
:deep(.fc-toolbar-title) {
  font-size: 1.75rem !important;
  /* margin/padding 조절이 필요하면 여기에 */
}
</style>

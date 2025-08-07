<!-- src/views/ChildDrawing.vue -->
<template>
  <div class="h-screen relative overflow-hidden pt-6">
    <!-- 배경 -->
    <img
      :src="bgImage"
      alt="background"
      class="absolute inset-0 w-full h-full object-cover -z-10"
    />

    <!-- 헤더 -->
    <header
      class="fixed top-4 left-4 right-4 z-30 flex items-center justify-between"
    >
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

    <!-- 달력 -->
    <div
      class="calendar-container relative z-0 font-shark pt-6 mt-6 mx-auto w-[1200px]"
    >
      <FullCalendar ref="calendarRef" :options="calendarOptions" />
    </div>

    <!-- 우측 장식 펭귄 -->
    <img
      :src="playPenImg"
      alt="playing penguin"
      class="fixed bottom-4 right-4 w-40 h-40 pointer-events-none z-20"
    />

    <!-- 그림일기 모달 -->
    <BaseModal v-model="showModal">
      <template #header>{{ selectedDiary.date }}</template>
      <div class="p-4 flex items-start">
        <div class="flex-1 text-center space-y-2">
          <img
            :src="selectedDiary.imageUrl"
            alt="Diary Detail"
            class="mx-auto rounded max-h-[400px]"
          />
          <p class="text-2xl font-shark">{{ selectedDiary.text }}</p>
        </div>
      </div>
    </BaseModal>

    <!-- 이전 버튼: showModal && hasPrev 인 경우에만 렌더링 -->
    <IconButton
      v-if="showModal && hasPrev"
      variant="left-arrow"
      label="이전 그림일기"
      @click="prevDiary"
      class="fixed top-1/2 -translate-y-1/2 z-50"
      :class="leftPosition"
    />

    <!-- 다음 버튼: showModal && hasNext 인 경우에만 렌더링 -->
    <IconButton
      v-if="showModal && hasNext"
      variant="right-arrow"
      label="다음 그림일기"
      @click="nextDiary"
      class="bg-blue-600/80 fixed top-1/2 -translate-y-1/2 z-50"
      :class="rightPosition"
    />
  </div>
</template>

<script setup>
import { ref, computed, reactive, onMounted, watch } from "vue";
import { useRouter } from "vue-router";
import { useChildStore } from "@/store/child";
import { childService } from "@/services/childService.js";

import FullCalendar from "@fullcalendar/vue3";
import dayGridPlugin from "@fullcalendar/daygrid";
import interactionPlugin from "@fullcalendar/interaction";
import koLocale from "@fullcalendar/core/locales/ko";

import BaseModal from "@/components/modal/BaseModal.vue";
import IconButton from "@/components/button/IconButton.vue";

import bgImage from "@/assets/images/child_calender.png";
import HomeIcon from "@/assets/images/Home.png";
import cloudImg from "@/assets/images/cloud.png";
import playPenImg from "@/assets/images/play_pen.png";

// props 정의
const props = defineProps({
  childId: {
    type: [String, Number],
    default: null,
  },
});

const router = useRouter();
const childStore = useChildStore();

// 선택된 아이 정보
const selectedChild = computed(() => childStore.selectedChild);

// 컴포넌트 마운트 시 실행
onMounted(() => {
  childStore.initialize();

  // URL에서 childId가 전달된 경우 해당 아이를 선택
  if (props.childId) {
    const childId = parseInt(props.childId);
    if (childStore.children.find((child) => child.id === childId)) {
      childStore.selectChild(childId);
    }
  }

  // 현재 달의 그림일기 로드
  const today = new Date();
  loadMonthlyDiaries(today.getFullYear(), today.getMonth() + 1);
});

// 선택된 아이가 바뀌면 그림일기 다시 로드
watch(selectedChild, (newChild) => {
  if (newChild?.id) {
    const today = new Date();
    loadMonthlyDiaries(today.getFullYear(), today.getMonth() + 1);
  }
});

// 뒤로가기
function goBack() {
  router.back();
}

// 모달 상태 & 선택된 일기
const showModal = ref(false);
const selectedDiary = ref({ date: "", imageUrl: "", text: "" });

// 그림일기 데이터
const diaries = ref([]);
const isLoading = ref(false);

// 월별 그림일기 로드 함수
async function loadMonthlyDiaries(year, month) {
  if (!selectedChild.value?.id) return;
  
  isLoading.value = true;
  try {
    const response = await childService.getMonthlyDiaries(selectedChild.value.id, year, month);
    
    // API 응답을 컴포넌트에서 사용하는 형태로 변환
    diaries.value = (response.diaries || []).map(diary => ({
      date: diary.date,
      imageUrl: diary.imageUrl,
      text: diary.text || '' // 아이용이므로 텍스트 없음
    }));
  } catch (error) {
    console.error('월별 그림일기 로드 실패:', error);
    diaries.value = [];
  } finally {
    isLoading.value = false;
  }
}

// 날짜 문자열 → diary 객체 매핑
const diariesMap = computed(() =>
  diaries.value.reduce((map, d) => {
    map[d.date] = d;
    return map;
  }, {})
);

// 현재 선택된 일기의 배열 내 인덱스
const currentIndex = computed(() =>
  diaries.value.findIndex((d) => d.date === selectedDiary.value.date)
);

// 이전/다음 존재 여부
const hasPrev = computed(() => currentIndex.value > 0);
const hasNext = computed(() => currentIndex.value < diaries.value.length - 1);

// 이전/다음 이동 함수
function prevDiary() {
  const idx = currentIndex.value;
  if (idx > 0) selectedDiary.value = diaries.value[idx - 1];
}
function nextDiary() {
  const idx = currentIndex.value;
  if (idx < diaries.value.length - 1)
    selectedDiary.value = diaries.value[idx + 1];
}

// 모달 바깥 버튼 좌/우 위치 계산
// (카드 너비 900px → 절반 450px, 버튼 여유 8px)
const leftPosition = computed(() => "left-[calc(50%-450px-8px)]");
const rightPosition = computed(() => "left-[calc(50%+400px+8px)]");

// FullCalendar 세팅
const today = new Date();
const calendarOptions = reactive({
  plugins: [dayGridPlugin, interactionPlugin],
  initialView: "dayGridMonth",
  locale: koLocale,
  headerToolbar: { left: "", center: "title", right: "today prev next" },
  titleFormat: { year: "numeric", month: "long" },
  height: 580,
  contentHeight: 530,
  initialDate: today,

  // 달력 뷰 변경 시 호출 (이전/다음 버튼 클릭 시)
  datesSet: (dateInfo) => {
    const viewDate = new Date(dateInfo.start);
    const year = viewDate.getFullYear();
    const month = viewDate.getMonth() + 1;
    loadMonthlyDiaries(year, month);
  },

  // 날짜 클릭 시 모달 열기
  dateClick: (info) => {
    const d = diariesMap.value[info.dateStr];
    if (d) {
      selectedDiary.value = d;
      showModal.value = true;
    }
  },

  // 날짜 숫자 커스텀
  dayCellContent: (arg) => {
    const n = arg.date.getDate();
    return {
      html: `
        <div
          class="inline-flex w-6 h-6 items-center justify-center font-shark text-sm text-black"
          style="
            background-image: url('${cloudImg}');
            background-size: contain;
        
          "
        >${n}</div>
      `,
    };
  },

  // 썸네일 + 클릭 리스너
  dayCellDidMount: (info) => {
    info.el.style.position = "relative";
    const key = info.date.toISOString().slice(0, 10);
    const d = diariesMap.value[key];
    if (!d) return;

    const wrap = document.createElement("div");
    Object.assign(wrap.style, {
      position: "absolute",
      top: "0.5rem",
      left: "50%",
      transform: "translateX(-50%)",
      width: "140px",
      height: "65px",
      overflow: "hidden",
      borderRadius: "0.25rem",
      cursor: "pointer",
    });

    const img = document.createElement("img");
    img.src = d.imageUrl;
    img.alt = "Diary";
    Object.assign(img.style, {
      width: "100%",
      height: "100%",
      objectFit: "cover",
    });

    wrap.appendChild(img);
    wrap.addEventListener("click", (e) => {
      e.stopPropagation();
      selectedDiary.value = d;
      showModal.value = true;
    });
    info.el.appendChild(wrap);
  },
});
</script>

<style scoped>
.calendar-container {
  max-width: 1200px;
  margin: 20 auto;
  height: 700px;
}

/* FullCalendar 헤더/캘린더 커스터마이징 */
:deep(.fc .fc-col-header) {
  background-color: #fff !important;
}
:deep(.fc .fc-daygrid-day-frame) {
  background-color: #fff !important;
}
:deep(.fc .fc-header-toolbar),
:deep(.fc .fc-header-toolbar .fc-toolbar-chunk) {
  background: transparent !important;
  border: none !important;
  box-shadow: none !important;
}
:deep(.fc-toolbar-title) {
  font-size: 1.75rem !important;
}
:deep(.fc .fc-daygrid-day) {
  height: 80px !important;
  min-width: 100px !important;
}
</style>

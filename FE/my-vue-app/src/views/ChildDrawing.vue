<!-- src/views/ChildDrawing.vue -->
<template>
  <div class="h-screen relative overflow-hidden pt-4 md:pt-6">
    <!-- 배경 -->
    <img
      :src="bgImage"
      alt="background"
      class="absolute inset-0 w-full h-full object-cover -z-10"
    />

    <!-- 헤더 -->
    <header
      class="fixed top-2 md:top-4 left-2 md:left-4 right-2 md:right-4 z-30 flex items-center justify-between"
    >
      <button
        @click="goBack"
        class="w-12 h-12 md:w-20 md:h-20 flex items-center justify-center transition-transform duration-400 hover:scale-x-[-1]"
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
      class="calendar-container relative z-0 font-shark pt-4 md:pt-6 mt-4 md:mt-6 mx-auto w-full px-2 md:px-0 md:w-[1200px]"
    >
      <FullCalendar ref="calendarRef" :options="calendarOptions" />
    </div>

    <!-- 우측 장식 펭귄 -->
    <img
      :src="playPenImg"
      alt="playing penguin"
      class="fixed bottom-2 md:bottom-4 right-2 md:right-4 w-24 h-24 md:w-40 md:h-40 pointer-events-none z-20"
    />

    <!-- 그림일기 모달 -->
    <BaseModal v-model="showModal">
      <template #header>{{ selectedDiary.date }}</template>
      <div class="p-2 md:p-4 flex items-start">
        <div class="flex-1 text-center space-y-2">
          <img
            :src="selectedDiary.imageUrl"
            alt="Diary Detail"
            class="mx-auto rounded max-h-[300px] md:max-h-[400px] max-w-full"
          />
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
import { ref, computed, reactive, onMounted, watch, onUnmounted } from "vue";
import { useRouter, useRoute } from "vue-router";
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
import bgmSound from "@/assets/effects/bgm.mp3";

// props 정의
const props = defineProps({
  childId: {
    type: [String, Number],
    default: null,
  },
});

const router = useRouter();
const route = useRoute();
const childStore = useChildStore();

// 선택된 아이 정보
const selectedChild = computed(() => childStore.selectedChild);

// BGM 관리
const bgmAudio = ref(null);
const isBGMPlaying = ref(false);

// route params에서 childId 받아오기 (fallback으로 selectedChild 사용)
const currentChildId = computed(() => {
  const routeChildId = route.params.childId;
  if (routeChildId) {
    return parseInt(routeChildId);
  }
  // props에서 받은 childId 사용
  if (props.childId) {
    return parseInt(props.childId);
  }
  // selectedChild에서 id 사용
  return selectedChild.value?.id || null;
});

// 컴포넌트 마운트 시 실행
onMounted(async () => {
  childStore.initialize();

  // URL에서 childId가 전달된 경우 해당 아이를 선택
  if (props.childId) {
    const childId = parseInt(props.childId);
    if (childStore.children.find((child) => child.id === childId)) {
      childStore.selectChild(childId);
    }
  }

  // 초기 다이어리 조회
  fetchMonthlyDiaries();

  // BGM 자동 재생 시도
  await playBGM();
});

// 컴포넌트 언마운트 시 BGM 정지
onUnmounted(() => {
  stopBGM();
});

// childId가 변경될 때마다 다이어리 재조회
watch(currentChildId, (newChildId) => {
  if (newChildId) {
    fetchMonthlyDiaries();
  }
});

// BGM 재생
async function playBGM() {
  if (!isBGMPlaying.value) {
    try {
      console.log("BGM 재생 시도 중...");
      const bgmModule = await import("@/assets/effects/bgm.mp3");
      bgmAudio.value = new Audio(bgmModule.default);
      bgmAudio.value.loop = true;
      bgmAudio.value.volume = 0.3;
      bgmAudio.value.preload = "auto";

      // 강제 재생 시도
      const playPromise = bgmAudio.value.play();

      if (playPromise !== undefined) {
        playPromise
          .then(() => {
            isBGMPlaying.value = true;
            console.log("BGM 재생 성공");
          })
          .catch((error) => {
            console.log("자동 재생 차단됨, 사용자 상호작용 대기 중:", error);
            // 차단된 경우 첫 번째 클릭/터치 이벤트에서 재생
            document.addEventListener("click", playBGM, { once: true });
            document.addEventListener("touchstart", playBGM, { once: true });
          });
      }
    } catch (error) {
      console.error("BGM 생성 실패:", error);
    }
  }
}

// BGM 정지
function stopBGM() {
  if (bgmAudio.value) {
    bgmAudio.value.pause();
    bgmAudio.value.currentTime = 0;
    bgmAudio.value = null;
    isBGMPlaying.value = false;
  }
}

// 뒤로가기
function goBack() {
  router.back();
}

// 모달 상태 & 선택된 일기
const showModal = ref(false);
const selectedDiary = ref({ date: "", imageUrl: "", text: "" });
const calendarRef = ref(null);

// 다이어리 데이터 상태
const diaries = ref([]);
const currentDate = ref(new Date());

// 월별 다이어리 조회
async function fetchMonthlyDiaries() {
  try {
    const childId = currentChildId.value;

    if (!childId) {
      console.warn("childId가 없어서 다이어리 조회를 건너뜁니다.");
      diaries.value = [];
      return;
    }

    const year = currentDate.value.getFullYear();
    const month = currentDate.value.getMonth() + 1;

    console.log("요청 파라미터:", { childId, year, month });
    const response = await childService.getMonthlyDiaries(childId, year, month);
    console.log("응답 결과:", response);
    console.log(
      "response 타입:",
      typeof response,
      "isArray:",
      Array.isArray(response)
    );

    // API 응답을 diaries 형태로 변환
    // response가 배열이 아니라 단일 객체일 수도 있으므로 처리
    const responseArray = Array.isArray(response)
      ? response
      : response
      ? [response]
      : [];
    console.log("responseArray:", responseArray);

    diaries.value = responseArray
      .map((diary) => {
        console.log("변환 중인 diary:", diary);
        return {
          date: diary.createdAt ? diary.createdAt.split("T")[0] : diary.date, // YYYY-MM-DD 형식으로 변환
          imageUrl: diary.imageUrl,
          text: diary.diaryText || diary.text || diary.content || "",
        };
      })
      .sort((a, b) => new Date(a.date) - new Date(b.date)); // 날짜순 정렬

    console.log("최종 변환된 diaries:", diaries.value);
  } catch (error) {
    console.error("다이어리 조회 오류:", error);
    diaries.value = [];
  }
}

// 날짜 문자열 → diary 객체 매핑
const diariesMap = computed(() => {
  const map = diaries.value.reduce((acc, d) => {
    acc[d.date] = d;
    return acc;
  }, {});
  console.log("diariesMap 계산됨:", map);
  return map;
});

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
  height: 'auto',
  aspectRatio: window.innerWidth < 768 ? 1.2 : 1.35,
  initialDate: today,

  // 월 변경 시 다이어리 재조회
  datesSet: async (info) => {
    // info.start는 달력 첫 주의 시작일(보통 이전달)이므로
    // 실제 달력의 중간 날짜를 사용해서 정확한 월을 가져옴
    const viewStart = new Date(info.start);
    const viewEnd = new Date(info.end);
    const middleDate = new Date((viewStart.getTime() + viewEnd.getTime()) / 2);

    currentDate.value = middleDate;
    await fetchMonthlyDiaries();

    // 데이터 로드 후 강제로 다시 렌더링
    setTimeout(() => {
      if (calendarRef.value) {
        calendarRef.value.getApi().render();
      }
    }, 600);
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
    const isMobile = window.innerWidth < 768;
    return {
      html: `
        <div
          class="inline-flex ${isMobile ? 'w-5 h-5' : 'w-6 h-6'} items-center justify-center font-shark ${isMobile ? 'text-xs' : 'text-sm'} text-black"
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

    // 시간대 문제 해결: 로컬 날짜로 변환
    const year = info.date.getFullYear();
    const month = String(info.date.getMonth() + 1).padStart(2, "0");
    const day = String(info.date.getDate()).padStart(2, "0");
    const key = `${year}-${month}-${day}`;

    // 더 긴 딜레이와 반복 체크로 확실히 데이터 로드 대기
    let attempts = 0;
    const maxAttempts = 10;

    const checkAndRender = () => {
      attempts++;
      const d = diaries.value.find((diary) => diary.date === key);

      if (d && d.imageUrl) {
        // 데이터를 찾았으면 이미지 렌더링
        const wrap = document.createElement("div");
        const isMobile = window.innerWidth < 768;
        Object.assign(wrap.style, {
          position: "absolute",
          top: isMobile ? "1.5rem" : "0.5rem",
          left: "50%",
          transform: "translateX(-50%)",
          width: isMobile ? "45px" : "140px",
          height: isMobile ? "35px" : "65px",
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

        console.log(`${key} 이미지 렌더링 완료 (${attempts}번째 시도)`);
      } else if (attempts < maxAttempts) {
        // 데이터가 없으면 100ms 후 다시 시도
        setTimeout(checkAndRender, 100);
      }
    };

    // 첫 시도는 700ms 후
    setTimeout(checkAndRender, 700);
  },
});
</script>

<style scoped>
.calendar-container {
  max-width: 1200px;
  margin: 20 auto;
  height: 500px;
}

@media (min-width: 768px) {
  .calendar-container {
    height: 700px;
  }
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
  font-size: 1.25rem !important;
}
:deep(.fc .fc-daygrid-day) {
  height: 70px !important;
  min-width: 40px !important;
  max-width: calc(100vw / 7 - 4px) !important;
}

:deep(.fc-daygrid-day-frame) {
  overflow: hidden !important;
}

:deep(.fc .fc-col-header-cell) {
  font-size: 0.75rem !important;
  padding: 0.25rem !important;
}

@media (min-width: 768px) {
  :deep(.fc-toolbar-title) {
    font-size: 1.75rem !important;
  }
  :deep(.fc .fc-daygrid-day) {
    height: 80px !important;
    min-width: 100px !important;
    max-width: none !important;
  }
  :deep(.fc .fc-col-header-cell) {
    font-size: 1rem !important;
    padding: 0.5rem !important;
  }
}
</style>

<template>
  <div
    :style="{ backgroundImage: `url(${bgImage})` }"
    class="h-screen relative bg-cover bg-center"
  >
    <header class="relative px-2 md:px-4 py-2 flex items-center justify-between">
      <button
        @click="goDashboard"
        @mouseenter="isBackButtonHovered = true"
        @mouseleave="isBackButtonHovered = false"
        aria-label="뒤로가기"
        class="w-20 h-20 md:!w-36 md:!h-36 flex items-center justify-center rounded-lg"
      >
        <img
          :src="isBackButtonHovered ? outHoverIcon : outIcon"
          alt="뒤로가기"
          class="w-16 h-16 md:w-28 md:h-28 object-contain transition-all duration-200"
        />
      </button>

      <!-- 선택된 아이 이름 표시 (네임태그) -->
      <div
        v-if="currentChild && currentChild.name"
        class="fixed top-2 md:top-4 right-2 md:right-4 z-10"
      >
        <!-- 네임태그 배경 이미지 -->
        <img
          src="@/assets/images/name_tag.png"
          alt="네임태그"
          class="w-32 h-16 md:w-56 md:h-28 object-contain"
        />
        <!-- 프로필과 이름 - fixed 위치로 정확히 배치 -->
        <div class="fixed top-5 right-6 md:top-10 md:right-16 flex items-center gap-1 md:gap-2">
          <img
            :src="currentChild.profileImage || 'https://placehold.co/40x40'"
            :alt="`${currentChild.name} 프로필`"
            class="w-8 h-8 md:w-14 md:h-14 rounded-full object-cover border-2 border-white"
          />
          <p class="text-sm md:text-2xl text-gray-800 font-shark">
            {{ currentChild.name }}
          </p>
        </div>
      </div>
    </header>

    <main
      class="absolute inset-0 flex items-center justify-center pointer-events-none"
    >
      <div
        class="pointer-events-auto grid grid-cols-1 md:grid-cols-2 gap-y-6 md:gap-y-8 gap-x-6 md:gap-x-24 w-full max-w-sm md:max-w-[1100px] px-4"
      >
        <div
          @click="goToPenguin"
          @mouseenter="playHoverSound"
          class="aspect-square bg-purple-300 rounded-xl md:rounded-2xl overflow-hidden shadow hover:shadow-xl hover:scale-105 transition-all duration-300 cursor-pointer hover-glow"
        >
          <div class="p-2 md:p-4 h-full flex flex-col">
            <h2
              class="text-2xl md:text-4xl mb-1 md:mb-2 text-black text-center font-shark text-outline-white"
            >
              <span class="text-blue-600 font-shark">펭구</span>랑 대화하기
            </h2>
            <div class="flex-1 flex items-center justify-center p-2 md:p-5">
              <img
                src="../assets/images/penguin.png"
                alt="펭구랑 대화하기"
                class="w-full h-auto object-contain"
                style="max-height: calc(100% - 1rem)"
              />
            </div>
          </div>
        </div>

        <div
          @click="goToDrawing"
          @mouseenter="playHoverSound"
          class="aspect-square bg-blue-400 rounded-xl md:rounded-2xl overflow-hidden shadow hover:shadow-xl hover:scale-105 transition-all duration-300 cursor-pointer hover-glow"
        >
          <div class="p-2 md:p-4 h-full flex flex-col">
            <h2 class="text-2xl md:text-4xl font-shark mb-1 md:mb-2 text-center text-outline-white">
              나의 <span class="font-shark text-red-500">그림</span> 일기
            </h2>
            <div class="flex-1 flex items-start justify-center pt-1 md:pt-2 pb-2 md:pb-5 px-2 md:px-5">
              <img
                src="../assets/images/drawing.png"
                alt="나의 그림 일기"
                class="w-full h-auto object-contain"
                style="max-height: calc(100% - 1rem)"
              />
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, onUnmounted, onBeforeUnmount } from "vue";
import { useRouter } from "vue-router";
import { useChildStore } from "@/store/child";
import { childService } from "@/services/childService.js";
import CloudShape from "@/components/widget/CloudShape.vue";
import audioManager from "@/utils/audioManager";
import outIcon from "../assets/images/out.png";
import outHoverIcon from "../assets/images/out_1.png";
import bgImage from "../assets/images/child_main.png";

// props 정의
const props = defineProps({
  childId: {
    type: [String, Number],
    default: null,
  },
});

const router = useRouter();
const childStore = useChildStore();

// 현재 선택된 아이 정보
const currentChild = computed(() => childStore.selectedChild);

// 페이지 식별자
const PAGE_ID = 'ChildMain';

// 뒤로가기 버튼 호버 상태
const isBackButtonHovered = ref(false);

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

  // 당일 그림일기 상태 미리 확인하여 localStorage 업데이트
  const currentChildId = currentChild.value?.id;
  if (currentChildId) {
    await checkAndUpdateTodayDiary(currentChildId);
  }

  // BGM 자동 재생 시도 (오디오 매니저 사용)
  const bgmModule = await import("@/assets/effects/bgm.mp3");
  await audioManager.playBGM(PAGE_ID, bgmModule.default);
});

// 컴포넌트 언마운트 시 BGM 정지
onUnmounted(() => {
  audioManager.onPageLeave(PAGE_ID);
});

// 페이지 떠날 때 BGM 정지 (브라우저 뒤로가기, 새로고침 등)
onBeforeUnmount(() => {
  audioManager.onPageLeave(PAGE_ID);
});




// 펭귀과 대화하기 클릭 시 바로 이동 (이미 선택된 아이 정보가 있음)
function goToPenguin() {
  // 현재 선택된 아이가 있으면 바로 펭귄 페이지로
  if (currentChild.value) {
    // 페이지 이동 전 BGM 정지
    audioManager.onPageLeave(PAGE_ID);
    router.push({
      name: "ChildPet",
      params: { childId: currentChild.value.id },
    });
  }
}

// 그림일기 클릭 시 바로 이동 (이미 선택된 아이 정보가 있음)
function goToDrawing() {
  // 현재 선택된 아이가 있으면 바로 그림일기 페이지로
  if (currentChild.value) {
    // 페이지 이동 전 BGM 정지
    audioManager.onPageLeave(PAGE_ID);
    router.push({
      name: "ChildDrawing",
      params: { childId: currentChild.value.id },
    });
  }
}

// 서버에서 당일 그림일기 확인하여 localStorage 업데이트
async function checkAndUpdateTodayDiary(currentChildId) {
  try {
    // 로컬 시간 기준으로 날짜 생성
    const today = new Date();
    const year = today.getFullYear();
    const month = today.getMonth() + 1;
    const day = today.getDate();
    const todayStr = `${year}-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}`;
    
    // 현재 시간 정보 로깅
    const currentTime = new Date();
    const localTime = currentTime.toLocaleString('ko-KR', { timeZone: 'Asia/Seoul' });
    const utcTime = currentTime.toISOString();
    const timezone = Intl.DateTimeFormat().resolvedOptions().timeZone;
    
    
    const response = await childService.getMonthlyDiaries(currentChildId, year, month);
    const diaries = Array.isArray(response) ? response : (response ? [response] : []);
    
    // 오늘 날짜의 그림일기가 있는지 확인
    const todayDiary = diaries.find(diary => {
      const diaryDate = diary.createdAt ? diary.createdAt.split('T')[0] : diary.date;
      return diaryDate === todayStr;
    });
    
    if (todayDiary) {
      const conversationResultId = todayDiary.conversationResultId || todayDiary.id;
      childStore.setChildTodayDiary(currentChildId, true, conversationResultId);
    }
    
  } catch (error) {
    // 당일 그림일기 확인 실패 시 무시
  }
}

const goDashboard = () => {
  // 페이지 이동 전 BGM 정지
  audioManager.onPageLeave(PAGE_ID);
  router.push({ name: "Dashboard" });
};

// 호버 시 효과음 재생
async function playHoverSound() {
  try {
    const decideModule = await import("@/assets/effects/decide.mp3");
    audioManager.playEffect(decideModule.default, 0.4);
  } catch (error) {
    // 호버 효과음 로드 실패 시 무시
  }
}
</script>

<style scoped>
.hover-glow:hover {
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.3), 0 0 20px rgba(255, 255, 255, 0.5),
    inset 0 0 20px rgba(255, 255, 255, 0.2);
  transform: translateY(-5px) scale(1.05);
}

/* 펭귀 버튼 전용 호버 효과 */
.bg-purple-300.hover-glow:hover {
  box-shadow: 0 20px 40px rgba(147, 51, 234, 0.4),
    0 0 25px rgba(196, 181, 253, 0.6), inset 0 0 20px rgba(255, 255, 255, 0.3);
}

/* 그림일기 버튼 전용 호버 효과 */
.bg-blue-400.hover-glow:hover {
  box-shadow: 0 20px 40px rgba(59, 130, 246, 0.4),
    0 0 25px rgba(147, 197, 253, 0.6), inset 0 0 20px rgba(255, 255, 255, 0.3);
}
</style>

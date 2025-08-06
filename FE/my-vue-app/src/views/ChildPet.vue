<template>
  <div class="relative w-screen h-screen overflow-hidden">
    <!-- background -->
    <img
      :src="bgImage"
      alt="background"
      class="absolute inset-0 w-full h-full object-cover -z-10"
    />

    <!-- header -->
    <header
      class="fixed top-4 left-4 right-4 z-20 flex items-center justify-between"
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

      <!-- 선택된 아이 이름 표시 -->
      <div
        v-if="selectedChild && selectedChild.name"
        class="bg-white/70 backdrop-blur-sm rounded-2xl px-6 py-3 shadow-lg"
      >
        <div class="flex items-center gap-3">
          <img
            :src="selectedChild.profileImage || 'https://placehold.co/40x40'"
            :alt="`${selectedChild.name} 프로필`"
            class="w-12 h-12 rounded-full object-cover border-2 border-white"
          />
          <p class="text-4xl text-gray-800 font-shark">
            {{ selectedChild.name }}
          </p>
        </div>
      </div>
    </header>

    <!-- main (펭귄 + 아래에 붙는 게이지) -->
    <main
      class="absolute left-1/2 top-[63%] -translate-x-1/2 -translate-y-1/2 transform z-10 flex flex-col items-center relative"
    >
      <span class="mb-4 text-black text-4xl text-outline-white font-shark">
        {{ "펭구" }}
      </span>
      <img
        :src="penguinSrc"
        alt="펭귄 단계 이미지"
        class="object-contain w-[140px] sm:w-[160px] lg:w-[200px] xl:w-[250px]"
      />

      <!-- 펭귄 바로 아래에 붙이는 게이지 바 -->
      <div
        class="mt-2 w-[clamp(200px,80vw,600px)] h-8 bg-white border-4 border-rose-600 rounded-full overflow-hidden relative"
      >
        <div
          class="h-full bg-emerald-300 rounded-full"
          :style="{ width: expRatio + '%' }"
        ></div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from "vue";
import { useRouter } from "vue-router";
import { useChildStore } from "@/store/child";
import { getChildPenguinData, incrementConversation } from "@/data/penguinData.js";

// 이미지
import HomeIcon from "../assets/images/Home.png";
import bgImage from "../assets/images/pet_bg.png";
import lvl1 from "../assets/images/lv_1.png";
import lvl2 from "../assets/images/lv_2.png";
import lvl3 from "../assets/images/lv_3.png";
import lvl4 from "../assets/images/lv_4.png";
import lvl5 from "../assets/images/lv_5.png";
import lvl6 from "../assets/images/lv_6.png";
import lvl7 from "../assets/images/lv_7.png";

const router = useRouter();
const childStore = useChildStore();

// 선택된 아이 정보
const selectedChild = computed(() => childStore.selectedChild);

// 펭귄 데이터 상태
const currentStage = ref(1);
const conversationCnt = ref(0);
const dinosaur = { max_stage: 7 }; // 최대 단계

// 선택된 아이의 펭귄 데이터 로드
function loadPenguinData() {
  if (selectedChild.value && selectedChild.value.name) {
    const penguinData = getChildPenguinData(selectedChild.value.name);
    currentStage.value = penguinData.currentStage;
    conversationCnt.value = penguinData.conversationCount;
  }
}

// 선택된 아이가 변경될 때마다 펭귄 데이터 다시 로드
watch(selectedChild, (newChild) => {
  if (newChild && newChild.name) {
    loadPenguinData();
  }
}, { immediate: true });

function goBack() {
  router.back();
}

// 컴포넌트 마운트 시 초기화
onMounted(() => {
  childStore.initialize();
  loadPenguinData();
});

// 2) 각 레벨별 다음 단계까지 필요한 대화 횟수 매핑
const levelRequirements = {
  1: 2,   // 1→2레벨: 2번 대화
  2: 4,   // 2→3레벨: 4번 대화  
  3: 6,   // 3→4레벨: 6번 대화
  4: 8,   // 4→5레벨: 8번 대화
  5: 10,  // 5→6레벨: 10번 대화
  6: 12,  // 6→7레벨: 12번 대화
  7: 0    // 7레벨은 최대 레벨
};

// 3) 현재 레벨에서 다음 단계까지 필요한 대화 횟수
const thresholdConvs = computed(() => {
  return levelRequirements[currentStage.value] || 0;
});

// 4) 단계별 펭귄 이미지
const penguinImgs = {
  1: lvl1,
  2: lvl2,
  3: lvl3,
  4: lvl4,
  5: lvl5,
  6: lvl6,
  7: lvl7,
};
const penguinSrc = computed(() => {
  const stage = Math.min(currentStage.value, dinosaur.max_stage);
  return penguinImgs[stage] || lvl1;
});

// 5) 경험치(대화) 비율 0~100%
const expRatio = computed(() => {
  // 7레벨(최대 레벨) 달성 시 항상 100%
  if (currentStage.value >= dinosaur.max_stage) {
    return 100;
  }
  
  // 현재 레벨에서 다음 레벨까지 필요한 대화 횟수
  const requiredConversations = thresholdConvs.value;
  
  // 필요 대화 횟수가 0이면 100% (에러 방지)
  if (requiredConversations === 0) {
    return 100;
  }
  
  // 경험치 비율 계산: (현재 대화 횟수 / 필요 대화 횟수) * 100
  const ratio = (conversationCnt.value / requiredConversations) * 100;
  
  // 100%를 넘지 않도록 제한
  return Math.min(100, Math.max(0, ratio));
});
</script>

<style scoped>
/* TailwindCSS 유틸만 사용 */
</style>

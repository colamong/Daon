<template>
  <div class="relative w-screen h-screen overflow-hidden">
    <!-- background -->
    <img
      :src="bgImage"
      alt="background"
      class="absolute inset-0 w-full h-full object-cover -z-10"
    />

    <!-- header -->
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
import { ref, computed } from "vue";
import { useRouter } from "vue-router";

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
function goBack() {
  router.back();
}

// -- 예시 API 데이터 --
const userDinosaur = { current_stage: 5 }; // 현재 단계
const conversationReward = { conversation_count: 5 }; // 대화 횟수
const dinosaur = { max_stage: 7 }; // 최대 단계

// 1) reactive state
const currentStage = ref(userDinosaur.current_stage);
const conversationCnt = ref(conversationReward.conversation_count);

// 2) 다음 단계까지 필요한 대화 횟수
const thresholdConvs = computed(() => currentStage.value * 2);

// 3) 단계별 펭귄 이미지
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

// 4) 경험치(대화) 비율 0~100%
const expRatio = computed(() => {
  if (currentStage.value >= dinosaur.max_stage) return 100;
  return Math.min(100, (conversationCnt.value / thresholdConvs.value) * 100);
});
</script>

<style scoped>
/* TailwindCSS 유틸만 사용 */
</style>

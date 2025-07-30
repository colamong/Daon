<template>
<div class="relative w-screen h-screen">
    <img
      :src="bgImage"
      class="absolute inset-0 w-full h-full object-fill z-[-1]"
      alt="background"
    />
  
    <header class="px-4 py-4 flex items-center justify-between z-[1]">
      <button
        @click="goBack"
        aria-label="뒤로가기"
        class="w-20 h-20 flex items-center justify-center bg-white rounded-lg shadow"
      >
       <img
          :src="HomeIcon"
          alt="뒤로가기"
          class="w-20 h-20 object-contain"
        />
    </img>
      </button>
    </header>
    <div
      class="
        absolute left-1/2    /* 수평 중앙 */
        top-[63%]            /* 세로 위치 */
        -translate-x-1/2     /* 정확히 중앙 정렬 */
        transform
        z-10
      "
    >
    <span
        class="
          absolute inset-0     /* 이미지 위 전체 영역 */
          flex items-center justify-center
          text-white           /* 글자색 */
          text-lg sm:text-xl md:text-2xl
          font-bold
          pointer-events-none  /* 텍스트가 클릭 방해 안 하도록 */
        "
      >
        {{ labelText }}
      </span>
    <img
      :src="penguinImage"
      alt="펭귄"
      class="absolute left-1/2 -translate-x-1/2   /* 수평 중앙 고정 */
        object-contain  "
  
      style="top: 55%; left: 50%; transform: translate(-50%, -50%); width: clamp(200px, 18vw, 300px); height: clamp(150px, 18vw, 300px);
        height: auto;"
    />
    </div>
</div>
</template>

<script setup>
import { useRouter } from "vue-router";
import HomeIcon from "../assets/images/Home.png";
import bgImage from "../assets/images/pet_bg.png";
import penguinImage from "../assets/images/lv_1.png";
const router = useRouter();
function goBack() {
  router.back();
}

</script>

<style scoped>
/* 필요 시 스타일 작성 */
</style>

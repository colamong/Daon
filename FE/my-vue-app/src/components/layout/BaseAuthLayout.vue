<!-- src/components/layout/BaseAuthLayout.vue -->

<template>
  <!-- 우측 하단 배경 이미지 (페이지별로 다른 이미지) -->
  <div class="fixed bottom-0 right-10" style="z-index: 10 !important">
    <img
      :src="landingImg"
      alt="페이지 배경 이미지"
      class="opacity-90 w-64 h-auto"
    />
  </div>

  <div
    class="relative min-h-screen w-full bg-blue-50 overflow-hidden flex items-center justify-center"
  >
    <!-- 그라데이션 배경 (전체 화면) -->
    <div
      class="w-[1920px] h-[1080px] left-[-2.15px] top-0 absolute bg-gradient-to-b from-blue-400/50 via-blue-100/50 to-blue-50/50 backdrop-blur-[1.50px]"
    ></div>

    <!-- 컨텐츠 -->
    <div class="relative z-10">
      <router-view />
    </div>
  </div>
</template>

<script setup>
import landingImg from "@/assets/images/landing_img.png"; // 모든 페이지에서 통일
</script>

<style scoped>
/* 필요 시 폼 중앙 정렬 등의 커스텀 */
</style>

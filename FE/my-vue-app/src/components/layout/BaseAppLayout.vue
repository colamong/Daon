<!-- src/components/layout/BaseAppLayout.vue -->
<template>
  <div class="min-h-screen flex flex-col">
    <Header />

    <!-- 
      1) bg-blue-50: 전체 배경색 
      2) inline style: 상단 파도 이미지를 full-width, auto-height로 고정 
    -->
    <main
      class="flex-grow"
      :style="{
        backgroundColor: '#EFF6FF', // bg-blue-50
        backgroundImage: `url(${bgTop})`,
        backgroundRepeat: 'no-repeat',
        backgroundPosition: 'center top',
        backgroundSize: '100% auto',
      }"
    >
      <router-view />
    </main>

    <Footer />
  </div>
</template>

<script setup>
import Header from "@/components/layout/Header.vue";
import Footer from "@/components/layout/Footer.vue";

// assets/images/bg_top.png 파일 경로
import bgTop from "@/assets/images/bg_top.png";
</script>

<style scoped>
/* 만약 main 전체가 화면을 덮지 않는다면, 아래처럼 강제 높이 지정해 보세요. */
/*
main {
  min-height: calc(100vh - var(--header-height) - var(--footer-height));
}
*/
</style>

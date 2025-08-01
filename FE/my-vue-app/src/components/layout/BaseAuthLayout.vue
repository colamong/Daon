<!-- src/components/layout/BaseAuthLayout.vue -->
<template>
  <div class="min-h-screen w-full bg-cover bg-center flex items-center justify-center"
       style="background-image: url('@/assets/images/login-bg.jpg');">
    <router-view />
  </div>
</template>

<script setup>
// 배경 이미지 경로는 실제 프로젝트 기준으로 조정
</script>

<style scoped>
/* 필요 시 폼 중앙 정렬 등의 커스텀 */
</style>

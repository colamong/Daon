<!-- src/components/layout/BaseAuthLayout.vue -->
<template>
  <div
    class="min-h-screen w-full bg-cover bg-center flex items-center justify-center"
    :style="{ backgroundImage: `url(${bgImage})` }"
  >
    <router-view />
  </div>
</template>

<script setup>
import bgImage from "../../assets/images/login-bg.png";
</script>

<style scoped>
/* 필요 시 폼 중앙 정렬 등의 커스텀 */
</style>

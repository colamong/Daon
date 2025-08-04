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
import { computed } from "vue";
import { useRoute } from "vue-router";

import signupBg from "../../assets/images/login-bg.png";
import loginBg from "../../assets/images/signup-bg.png";

// 현재 라우트 정보 가져오기
const route = useRoute();

// route.name 에 따라 사용할 배경 이미지를 결정
const bgImage = computed(() => {
  switch (route.name) {
    case "login": // 라우터 설정에서 name: 'Login' 으로 되어 있는 경우
      return loginBg;
    case "signup": // 라우터 설정에서 name: 'Signup' 으로 되어 있는 경우
      return signupBg;
    default:
      return loginBg; // 기본 이미지
  }
});
</script>

<style scoped>
/* 필요 시 폼 중앙 정렬 등의 커스텀 */
</style>

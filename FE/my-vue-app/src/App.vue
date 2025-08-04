<!-- src/App.vue -->
<template>
  <!-- router-view만 두고, 가드에서 로그인 여부에 따라 이동 -->
  <router-view />
</template>

<script>
export default {
  name: "App",
};
</script>

<style>
/* 글로벌 스타일, Tailwind가 이미 들어가 있다면 따로 스타일 불필요 */
</style>

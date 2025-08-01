<template>
  <div
    v-if="route.name === 'Home'"
    class="bg-white shadow p-4 flex items-center space-x-4"
  >
    <router-link
      to="/child"
      class="text-blue-600 font-semibold hover:underline"
    >
      아이 메인 화면
    </router-link>
    <router-view />
    <p class="font-paper text-xl">Paperlogy Regular</p>
    <p class="font-paperSemi text-xl">Paperlogy SemiBold</p>
    <p class="font-paperBold text-xl">Paperlogy Bold</p>
    <p class="font-shark text-xl">Baby Shark 폰트</p>
    <p class="text-xl">Baby Shark 폰트</p>
  </div>
  <router-view />
</template>

<script setup>
import { useRoute } from "vue-router";
const route = useRoute();
</script>

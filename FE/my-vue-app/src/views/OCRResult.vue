<template>
  <section class="px-6 py-8 max-w-4xl mx-auto">
    <h1 class="text-2xl font-bold mb-4">번역 결과</h1>

    <div v-if="text">
      <h2 class="font-semibold">추출된 텍스트</h2>
      <p class="whitespace-pre-wrap my-2">{{ text }}</p>
    </div>
    <div v-else class="text-gray-500">표시할 텍스트가 없습니다.</div>

    <button @click="goBack" class="mt-4 px-4 py-2 bg-gray-200 rounded">
      뒤로 가기
    </button>
  </section>
</template>

<script setup>
import { useRoute, useRouter } from "vue-router";

const route = useRoute();
const router = useRouter();

// 쿼리로 넘긴 값 꺼내기
const text = route.query.text || "";

function goBack() {
  router.back();
}
</script>

<style scoped>
/* 필요 시 추가 스타일 작성 */
</style>

<template>
  <div class="relative bg-white rounded-xl p-4 shadow">
    <!-- 공부하러가기 버튼 -->
    <router-link
      :to="link"
      class="absolute top-3 right-3 text-xs text-gray-500 hover:text-gray-800 font-paper"
    >
      공부하러가기
    </router-link>

    <!-- 챕터 및 제목 -->
    <h3 class="text-base font-paperBold mb-2">
      Ch.{{ chapter }} {{ title }}
    </h3>

    <!-- 본문 -->
    <p class="text-sm font-paper text-gray-700 leading-relaxed">
      {{ content }}
    </p>
  </div>
</template>

<script setup>
const props = defineProps({
  chapter: Number,
  title: String,
  content: String,
  link: {
    type: String,
    default: '#'
  }
})
</script>

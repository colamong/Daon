<!-- 한글공부 상황선택 카드 -->
<template>
  <router-link
    :to="link"
    class="block w-72 rounded-2xl overflow-hidden shadow-md hover:shadow-lg transition-shadow bg-white"
  >
    <!-- 이미지 영역 -->
    <div class="h-40 overflow-hidden">
      <img
        :src="resolvedImage"
        alt="카드 배경 이미지"
        class="w-full h-full object-cover"
      />
    </div>

    <!-- 제목 영역 -->
    <div class="px-4 py-4 text-center">
      <h3 class="text-xl font-paperBold text-black">
        {{ title }}
      </h3>
    </div>
  </router-link>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  image: String,
  title: {
    type: String,
    required: true
  },
  description: {
    type: String,
    required: true
  },
  link: {
    type: String,
    required: true
  }
})

const resolvedImage = computed(() => {
  return props.image
    ? props.image
    : new URL('@/assets/icons/image-placeholder.svg', import.meta.url).href
})
</script>

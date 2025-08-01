<!-- 한글공부 상황선택 카드 -->
<template>
  <router-link
    :to="link"
    class="relative w-72 h-44 rounded-2xl overflow-hidden shadow-md hover:shadow-lg transition-shadow group"
  >
    <!-- 배경 이미지 -->
    <img
      :src="resolvedImage"
      alt="카드 배경 이미지"
      class="absolute inset-0 w-full h-full object-cover"
    />

    <!-- 흐림 효과 있는 텍스트 박스 -->
    <div class="absolute bottom-4 left-4 right-4">
      <div class="bg-white/50 backdrop-blur-sm px-4 py-3 rounded-md text-center text-black">
        <h3 class="text-lg font-paperBold mb-1">
          {{ title }}
        </h3>
        <p class="text-sm font-paper leading-snug line-clamp-2">
          {{ description }}
        </p>
      </div>
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

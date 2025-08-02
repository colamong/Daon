<!-- 커뮤니티에서 지역검색할때 나올 카드 -->
<template>
  <router-link
    :to="link"
    class="flex items-center gap-4 w-full px-4 py-3 rounded-xl transition-colors hover:bg-hover-blue/20"
  >
    <!-- 이미지 -->
    <img
      :src="resolvedImage"
      alt="지역 이미지"
      class="w-12 h-12 rounded-md object-cover"
    />

    <!-- 텍스트 -->
    <div class="flex-1">
      <div class="text-base font-paperBold text-black">
        {{ title }}
      </div>
      <div class="text-sm font-paper text-gray-600">
        {{ description }}
      </div>
    </div>

    <!-- 오른쪽 화살표 -->
    <div class="text-gray-400 text-xl font-paperSemiBold ml-2">→</div>
  </router-link>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  title: String,
  description: String,
  image: String,
  link: String
})

const resolvedImage = computed(() =>
  props.image || new URL('@/assets/icons/image-placeholder.svg', import.meta.url).href
)
</script>

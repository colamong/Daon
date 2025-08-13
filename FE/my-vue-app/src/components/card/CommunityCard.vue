<!-- 커뮤니티 자체에 6칸씩 보여줄 카드 -->
<template>
  <router-link
    :to="link"
    class="bg-white rounded-xl shadow-md w-64 h-64 flex flex-col justify-between hover:shadow-lg transition-shadow"
  >
    <!-- 이미지 영역 -->
    <div class="flex justify-center items-center flex-1 p-6">
      <img
        :src="resolvedImage"
        alt="대표 이미지"
        class="w-40 h-40 object-contain"
      />
    </div>

    <!-- 텍스트 영역 -->
    <div class="bg-gray-50 px-4 py-3 rounded-b-xl">
      <div class="text-xs text-gray-400 flex justify-between mb-1">
        <span>{{ participantText }}</span>
      </div>
      <div class="text-sm text-gray-800 font-paper">{{ location }}</div>
    </div>
  </router-link>
</template>

<script setup>
import { computed } from 'vue'
import { useAttrs } from 'vue'

const attrs = useAttrs()

const location = attrs.location || ''
const favorites = attrs.favorites || 0
const resolvedImage = computed(() =>
  attrs.image || new URL('@/assets/icons/image-placeholder.svg', import.meta.url).href
)
const link = computed(() => attrs.link || '#')

// 참여자 수 텍스트
const participantText = computed(() => {
  return `${favorites}명 참여중`
})
</script>

<template>
  <div
    class="flex items-start gap-3 mb-4 font-paper"
    :class="isMine ? 'justify-end' : 'justify-start'"
  >
    <!-- 프로필 이미지: 내 메시지는 안 보임 -->
    <img
      v-if="!isMine"
      :src="imagePlaceholder"
      alt="user"
      class="w-10 h-10 rounded-full object-cover"
    />

    <!-- 말풍선 -->
    <div
      :class="[
        'px-4 py-2 max-w-xs text-sm rounded-xl',
        isMine
          ? 'bg-[#F2F5FD] text-right rounded-br-none'
          : 'border border-black text-left',
      ]"
    >
      {{ message }}
    </div>
  </div>
</template>

<script setup>

import imagePlaceholder from '@/assets/icons/image-placeholder.svg'

defineProps({
  isMine: Boolean,
  message: String,
  timestamp: String,
})
</script>

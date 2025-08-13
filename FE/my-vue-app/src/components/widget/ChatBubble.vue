<template>
  <div
    class="flex items-start gap-3 mb-4 font-paper"
    :class="isMine ? 'justify-end' : 'justify-start'"
  >
    <!-- 프로필 이미지: 내 메시지는 안 보임 -->
    <img
      v-if="!isMine"
      :src="profileImageSrc"
      alt="user"
      class="w-10 h-10 rounded-full object-cover"
    />

    <!-- 메시지 영역 -->
    <div :class="isMine ? 'flex flex-col items-end' : 'flex flex-col items-start'">
      <!-- 사용자 이름: 내 메시지는 안 보임 -->
      <div
        v-if="!isMine && userName"
        class="text-xs text-gray-500 mb-1 px-1"
      >
        {{ userName }}
      </div>
      
      <!-- 말풍선과 시간 -->
      <div :class="isMine ? 'flex flex-row-reverse items-end gap-2' : 'flex items-end gap-2'">
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
        
        <!-- 시간 -->
        <div class="text-xs text-gray-400 whitespace-nowrap pb-1">
          {{ formattedTime }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import imagePlaceholder from '@/assets/icons/image-placeholder.svg'
import { formatChatTime } from '@/utils/formatDate.js'

const props = defineProps({
  isMine: Boolean,
  message: String,
  timestamp: String,
  userName: String,
  userProfileImg: String,
})

// 시간 포맷팅
const formattedTime = computed(() => {
  return formatChatTime(props.timestamp)
})

// 프로필 이미지 처리
const profileImageSrc = computed(() => {
  // userProfileImg가 있으면 사용, 없으면 기본 이미지
  return props.userProfileImg || imagePlaceholder
})
</script>

<template>
  <!-- 시스템 메시지인 경우 -->
  <div v-if="isSystemMessage" class="flex justify-center mb-4">
    <div class="bg-gray-100 text-gray-600 text-xs px-4 py-2 rounded-full font-paper">
      {{ message }}
    </div>
  </div>
  
  <!-- 일반 사용자 메시지인 경우 -->
  <div
    v-else
    class="flex items-center gap-3 mb-6 font-paper px-3 py-2"
    :class="isMine ? 'justify-end' : 'justify-start'"
  >
    <!-- 프로필 이미지: 내 메시지는 안 보임 -->
    <img
      v-if="!isMine"
      :src="profileImageSrc"
      alt="user"
      class="w-12 h-12 rounded-full object-cover flex-shrink-0 border border-gray-200"
    />

    <!-- 메시지 영역 -->
    <div :class="isMine ? 'flex flex-col items-end min-h-[3rem] justify-center' : 'flex flex-col items-start min-h-[3rem] justify-center'">
      <!-- 사용자 이름: 내 메시지는 안 보임 -->
      <div
        v-if="!isMine && userName"
        class="text-sm text-gray-600 mb-2 px-1 font-medium"
      >
        {{ userName }}
      </div>
      
      <!-- 말풍선과 시간 -->
      <div :class="isMine ? 'flex flex-row-reverse items-end gap-3' : 'flex items-end gap-3'">
        <!-- 말풍선 -->
        <div
          :class="[
            'px-3 py-2 max-w-xs text-sm rounded-xl',
            isMine
              ? 'bg-blue-300/30 text-right rounded-br-none'
              : 'bg-white text-left rounded-tl-none',
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
  isAlternate: Boolean,
  messageType: String,
})

// 시스템 메시지 여부 확인
const isSystemMessage = computed(() => {
  return props.messageType === 'SYSTEM_JOIN' || props.messageType === 'SYSTEM_LEAVE'
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

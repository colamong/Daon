<template>
  <div
    class="flex items-center justify-between px-5 py-4 border rounded-full transition-colors duration-200"
    :class="borderClass"
  >
    <!-- 텍스트 (클릭 시 답변 선택) -->
    <span 
      class="text-base font-paper text-black flex-1 cursor-pointer"
      @click="handleClick"
    >
      {{ text }}
    </span>
    <!-- 음성 버튼 (클릭 시 음성 재생만) -->
    <button
      @click.stop="playAudio"
      class="ml-4 p-1 hover:bg-gray-100 rounded transition-colors"
    >
      <img
        src="@/assets/icons/sound.svg"
        alt="음성 재생"
        class="w-5 h-5"
      />
    </button>
  </div>
</template>



<script setup>
import { ref, computed } from 'vue'

const props = defineProps({
  text: String,
  isCorrect: Boolean
})

const emit = defineEmits(['correct', 'incorrect', 'playAudio'])

const isWrong = ref(false)

const handleClick = () => {
  if (props.isCorrect) {
    emit('correct')
  } else {
    isWrong.value = true
    emit('incorrect')
  }
}

const playAudio = () => {
  emit('playAudio')
}

const borderClass = computed(() => {
  return isWrong.value
    ? 'border-red-500'
    : 'border-gray-200 hover:border-hover-blue'
})
</script>

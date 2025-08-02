<template>
  <div
    class="flex items-center justify-between px-5 py-4 border rounded-full cursor-pointer transition-colors duration-200"
    :class="borderClass"
    @click="handleClick"
  >
    <!-- 텍스트 -->
    <span class="text-base font-paper text-black">
      {{ text }}
    </span>
    <img
      src="@/assets/icons/sound.svg"
      alt="아이콘"
      class="w-5 h-5 ml-4"
    />
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const props = defineProps({
  text: String,
  isCorrect: Boolean
})

const emit = defineEmits(['correct', 'incorrect'])

const isWrong = ref(false)

const handleClick = () => {
  if (props.isCorrect) {
    emit('correct')
  } else {
    isWrong.value = true
    emit('incorrect')
  }
}

const borderClass = computed(() => {
  return isWrong.value
    ? 'border-red-500'
    : 'border-gray-200 hover:border-hover-blue'
})
</script>

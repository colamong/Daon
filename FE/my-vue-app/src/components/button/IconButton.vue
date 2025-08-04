<template>
  <button
    @click="emit('click')"
    :aria-label="label"
    :class="[
      'flex items-center justify-center text-lg font-paper rounded-md px-3 py-2',
      'transition-colors duration-200',
      // variant에 따라 배경색 다르게
      variant === 'left-arrow'
        ? 'bg-greenLight text-transition-green'
        : variant === 'right-arrow'
        ? 'bg-background-footer text-transition-blue'
        : variant === 'close'
        ? 'bg-red-100 text-red-600'
        : 'bg-gray-200 text-black'
    ]"
  >
    <!-- 닫기 버튼은 아이콘 이미지로 -->
    <img
      v-if="variant === 'close'"
      src="@/assets/icons/x.svg"
      alt="닫기"
      class="w-5 h-5"
    />
    <!-- 슬롯이 제공되면 사용하고, 아니면 기본 아이콘 사용 -->
    <slot v-else>{{ iconSymbol }}</slot>
  </button>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  variant: {
    type: String,
    default: 'default',
  },
  label: {
    type: String,
    default: '',
  },
})

const emit = defineEmits(['click'])

const iconSymbol = computed(() => {
  switch (props.variant) {
    case 'left-arrow':
      return '⬅' // 이전
    case 'right-arrow':
      return '➡' // 이후
    case 'close':
      return '' // 닫기 (이미지 사용)
    default:
      return '?' // 기타
  }
})
</script>

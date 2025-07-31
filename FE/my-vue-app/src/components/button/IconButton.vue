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
    <img
      v-if="variant === 'close'"
      src="@/assets/icons/x.svg"
      alt="닫기"
      class="w-5 h-5"
    />
    <!-- 슬롯이 제공되면 사용하고, 아니면 기본 아이콘 사용 -->
    <slot>{{ iconSymbol }}</slot>
  </button>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  variant?: 'left-arrow' | 'right-arrow' | 'close' | 'default'
  label?: string
}>()

const emit = defineEmits(['click'])

const iconSymbol = computed(() => {
  switch (props.variant) {
    case 'left-arrow':
      return '⬅'
    case 'right-arrow':
      return '➡'
    case 'close':
      return ''
    default:
      return '?'
  }
})
</script>

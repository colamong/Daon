<template>
  <div class="w-full">
    <!-- 라벨 -->
    <label
      v-if="label"
      :for="id"
      class="block mb-1 text-sm font-medium text-gray-700"
    >
      {{ label }}
    </label>

    <!-- 셀렉트 박스 -->
    <select
      :id="id"
      :value="modelValue"
      @change="$emit('update:modelValue', $event.target.value)"
      :disabled="disabled"
      :class="[
        'w-full rounded-md border px-3 py-2 text-sm transition-colors',
        error ? 'border-red-500' : 'border-[#d9d9d9]',
        disabled ? 'bg-gray-100 text-gray-400 cursor-not-allowed' : 'bg-white'
      ]"
    >
      <option disabled value="">자신의 국가를 선택하세요</option>
      <option
        v-for="option in options"
        :key="option.value"
        :value="option.value"
      >
        {{ option.label }}
      </option>
    </select>

    <!-- 에러 메시지 -->
    <p v-if="error" class="mt-1 text-sm text-red-500">{{ error }}</p>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  modelValue: String,
  options: {
    type: Array,
    default: () => []
  },
  label: String,
  disabled: Boolean,
  error: String
})

const emit = defineEmits(['update:modelValue'])

const id = computed(() => `select-${Math.random().toString(36).slice(2, 8)}`)
</script>

<style scoped>
/* 필요 시 custom style */
</style>

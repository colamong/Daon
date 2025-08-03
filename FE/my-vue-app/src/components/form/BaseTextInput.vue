<template>
  <div class="w-full">
    <!-- 라벨: 고정 -->
    <label
      v-if="label"
      class="block mb-1 text-sm font-medium text-gray-700"
      :class="{ 'text-red-500': error }"
    >
      {{ label }}
    </label>

    <div class="relative w-full">
      <!-- 왼쪽 아이콘 슬롯 -->
      <div
        class="absolute top-1/2 left-3 -translate-y-1/2 text-gray-400"
        v-if="$slots.icon"
      >
        <slot name="icon" />
      </div>

      <!-- 입력 필드 -->
      <input
        :type="type"
        :placeholder="placeholder"
        :value="modelValue"
        :disabled="disabled"
        @input="$emit('update:modelValue', $event.target.value)"
        @focus="handleFocus"
        @blur="handleBlur"
        :class="[
          'w-full rounded-md border px-4 py-2 text-sm transition-colors',
          disabled ? 'bg-gray-100 text-gray-400 cursor-not-allowed' : 'bg-white',
          error ? 'border-red-500' : isFocused ? 'border-blue-600' : 'border-[#d9d9d9]',
          $slots.icon ? 'pl-10' : ''
        ]"
      />
    </div>

    <!-- 에러 메시지 -->
    <p v-if="error" class="mt-1 text-sm text-red-500">
      {{ error }}
    </p>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({
  modelValue: String,
  label: String,
  placeholder: String,
  error: String,
  type: {
    type: String,
    default: 'text'
  },
  disabled: Boolean
})

const emit = defineEmits(['update:modelValue'])

const isFocused = ref(false)

const handleFocus = () => {
  if (!props.disabled) isFocused.value = true
}

const handleBlur = () => {
  isFocused.value = false
}
</script>

<style scoped>
/* 필요 시 스타일 작성 */
</style>

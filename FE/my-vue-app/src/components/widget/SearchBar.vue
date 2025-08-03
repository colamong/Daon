<template>
  <div class="relative w-full">

    <!-- 입력창 -->
    <input
      v-model="inputValue"
      type="text"
      placeholder="지역명을 입력해주세요"
      @input="$emit('update:modelValue', inputValue)"
      class="w-full px-4 py-2 rounded-md border border-gray-300 text-gray-800 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-blue-600"
    />
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  modelValue: {
    type: String,
    required: true,
  },
})

const inputValue = ref(props.modelValue)

watch(() => props.modelValue, (newVal) => {
  inputValue.value = newVal
})
</script>

<style scoped>
/* 필요 시 스타일 작성 */
</style>

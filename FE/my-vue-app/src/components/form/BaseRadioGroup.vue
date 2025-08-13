<template>
  <div class="w-full">
    <!-- 상단 라벨 -->
    <label v-if="label" class="block mb-2 text-lg font-paperBold text-black">
      {{ label }}
    </label>

    <!-- 라디오 버튼들 -->
    <div
      class="flex gap-6 bg-white rounded-xl px-6 py-4 border-2 border-gray-300 shadow-[0_0_10px_rgba(0,0,0,0.05)]"
    >
      <label
        v-for="(option, index) in options"
        :key="index"
        class="flex items-center gap-2 cursor-pointer select-none text-lg font-paper text-black"
      >
        <span>{{ option.label }}</span>

        <!-- 숨겨진 radio input -->
        <input
          type="radio"
          :value="option.value"
          v-model="localValue"
          :name="name"
          class="hidden"
        />

        <!-- 커스텀 라디오 박스 -->
        <div
          class="w-6 h-6 flex items-center justify-center border-2 rounded-md"
          :class="{
            'bg-blue-600 border-blue-600 text-white':
              localValue === option.value,
            'border-[#1C1C1C] bg-white': localValue !== option.value,
          }"
        >
          <!-- 체크 아이콘 -->
          <svg
            v-if="localValue === option.value"
            xmlns="http://www.w3.org/2000/svg"
            class="w-4 h-4"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
            stroke-width="3"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              d="M5 13l4 4L19 7"
            />
          </svg>
        </div>
      </label>
    </div>

    <!-- 에러 메시지 -->
    <p v-if="error" class="mt-2 text-sm text-red-600">
      {{ error }}
    </p>
  </div>
</template>

<script setup>
import { computed } from "vue";

const props = defineProps({
  modelValue: [String, Number],
  label: String,
  name: String,
  options: {
    type: Array,
    required: true,
  },
  error: String,
});

const emit = defineEmits(["update:modelValue"]);

const localValue = computed({
  get: () => props.modelValue,
  set: (val) => emit("update:modelValue", val),
});
</script>

<style scoped>
/* 필요 시 스타일 작성 */
</style>

<template>
  <div class="w-full">
    <!-- 상단 라벨 -->
    <label v-if="label" class="block mb-2 text-lg font-paperBold text-black">
      {{ label }}
    </label>

    <!-- 체크박스 리스트 -->
    <div
      class="!border-2 !border-gray-300 grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6 bg-white rounded-xl px-8 py-8 min-h-[200px]"
    >
      <label
        v-for="(option, index) in options"
        :key="index"
        class="text-lg font-paper text-black group flex items-center justify-between cursor-pointer select-none text-lg text-black"
      >
        <span>{{ option.label }}</span>

        <!-- 숨겨진 체크박스 input -->
        <input
          type="checkbox"
          :value="option.value"
          v-model="localValue"
          class="hidden"
        />

        <!-- 커스텀 체크박스 -->
        <div
          class="border-gray-300 w-6 h-6 flex items-center justify-center border-2 rounded-md transition-colors md:group-hover:bg-blue-100 md:group-hover:border-blue-400"
          :class="{
            'bg-blue-600 border-blue-600 text-white': localValue.includes(
              option.value
            ),
            'border-[#1C1C1C] bg-white md:group-hover:bg-blue-100 md:group-hover:border-blue-400':
              !localValue.includes(option.value),
          }"
        >
          <!-- 체크 아이콘 -->
          <svg
            v-if="localValue.includes(option.value)"
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
  modelValue: Array,
  label: String,
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

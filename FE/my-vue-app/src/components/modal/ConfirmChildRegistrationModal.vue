<template>
  <div
    v-if="modelValue"
    class="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50"
    @click="handleCancel"
  >
    <!-- 모달 콘텐츠 -->
    <div
      class="relative bg-white rounded-2xl shadow-xl max-w-md w-full mx-4 p-6"
      @click.stop
    >
      <!-- 아이콘 -->
      <div class="text-center mb-4">
        <div
          class="mx-auto w-16 h-16 bg-yellow-100 rounded-full flex items-center justify-center"
        >
          <svg
            class="w-8 h-8 !text-yellow-600"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L4.082 16.5c-.77.833.192 2.5 1.732 2.5z"
            />
          </svg>
        </div>
      </div>

      <!-- 메시지 -->
      <div class="text-center mb-6 font-paper">
        <h3 class="text-lg font-bold text-gray-800 mb-2">
          아이를 등록해주세요
        </h3>
        <p class="text-gray-600">
          펭구와 놀기 위해서는 아이를 먼저 등록해야 합니다.
        </p>
      </div>

      <!-- 버튼들 -->
      <div class="flex gap-3">
        <button
          @click="handleCancel"
          class="flex-1 bg-gray-100 text-gray-700 py-3 rounded-lg font-semibold hover:bg-gray-200 transition-colors"
        >
          취소
        </button>
        <button
          @click="handleConfirm"
          class="flex-1 bg-purple-500 text-white py-3 rounded-lg font-semibold hover:bg-purple-600 transition-colors"
        >
          등록하기
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
// Props
const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false,
  },
});

// Emits
const emit = defineEmits(["update:modelValue", "confirm", "cancel"]);

// 확인
function handleConfirm() {
  emit("confirm");
  emit("update:modelValue", false);
}

// 취소
function handleCancel() {
  emit("cancel");
  emit("update:modelValue", false);
}
</script>

<style scoped>
/* 모달 트랜지션 */
.fade-enter-active,
.fade-leave-active {
  transition: all 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: scale(0.95);
}

.fade-enter-to,
.fade-leave-from {
  opacity: 1;
  transform: scale(1);
}
</style>

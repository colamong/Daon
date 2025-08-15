<template>
  <div
    v-if="modelValue"
    class="fixed inset-0 z-50 grid place-items-center bg-black bg-opacity-50 min-h-screen p-4"
    style="display: grid; place-items: center; align-content: center; justify-content: center;"
    @click="handleCancel"
  >
    <!-- 모달 콘텐츠 -->
    <div
      class="relative bg-white rounded-2xl shadow-xl max-w-xs sm:max-w-md w-full p-4 sm:p-6 my-auto"
      @click.stop
    >
      <!-- 아이콘 -->
      <div class="text-center mb-3 sm:mb-4">
        <div
          class="mx-auto w-12 h-12 sm:w-16 sm:h-16 bg-yellow-100 rounded-full flex items-center justify-center"
        >
          <svg
            class="w-6 h-6 sm:w-8 sm:h-8 !text-yellow-600"
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
      <div class="text-center mb-4 sm:mb-6 font-paper">
        <h3 class="text-base sm:text-lg font-paperBold text-gray-800 mb-2">
          아이를 등록해주세요
        </h3>
        <p class="text-sm sm:text-base text-gray-600 font-paper">
          펭구와 놀기 위해서는 아이를 먼저 등록해야 합니다.
        </p>
      </div>

      <!-- 버튼들 -->
      <div class="flex flex-col sm:flex-row gap-2 sm:gap-3">
        <button
          @click="handleCancel"
          class="flex-1 bg-gray-100 text-gray-700 py-2 sm:py-3 rounded-lg font-paperSemi hover:bg-gray-200 transition-colors text-sm sm:text-base"
        >
          취소
        </button>
        <button
          @click="handleConfirm"
          class="flex-1 bg-purple-500 text-white py-2 sm:py-3 rounded-lg font-paperSemi hover:bg-purple-600 transition-colors text-sm sm:text-base"
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

<template>
  <transition name="fade">
    <div
      v-if="modelValue"
      class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 font-paper"
    >
      <div
        class="bg-white rounded-xl overflow-hidden shadow-lg max-w-md w-full mx-4"
      >
        <!-- 본문 -->
        <div class="p-6 text-center">
          <div class="mb-4">
            <span class="text-4xl">⚠️</span>
          </div>
          <h3 class="text-lg font-paperSemi mb-2 text-gray-800">
            {{ title }}
          </h3>
          <p class="text-gray-600 mb-6">
            {{ message }}
          </p>
          
          <!-- 버튼 -->
          <div class="flex space-x-3 justify-center">
            <button
              @click="cancel"
              class="px-4 py-2 bg-gray-200 hover:bg-gray-300 text-gray-700 rounded-lg transition-colors font-paper"
            >
              {{ cancelText }}
            </button>
            <button
              @click="confirm"
              class="px-4 py-2 bg-red-500 hover:bg-red-600 text-white rounded-lg transition-colors font-paper"
            >
              {{ confirmText }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </transition>
</template>

<script setup>
const props = defineProps({
  modelValue: {
    type: Boolean,
    required: true,
  },
  title: {
    type: String,
    default: '확인',
  },
  message: {
    type: String,
    required: true,
  },
  confirmText: {
    type: String,
    default: '확인',
  },
  cancelText: {
    type: String,
    default: '취소',
  },
});

const emit = defineEmits(['update:modelValue', 'confirm', 'cancel']);

function confirm() {
  emit('confirm');
  emit('update:modelValue', false);
}

function cancel() {
  emit('cancel');
  emit('update:modelValue', false);
}
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
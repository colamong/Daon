<template>
  <transition name="fade">
    <div
      v-if="modelValue"
      class="fixed inset-0 bg-black bg-opacity-90 flex items-center justify-center z-50 font-paper"
    >
      <div
        class="bg-white rounded-xl overflow-hidden shadow-lg max-w-lg w-full min-w-[700px] min-h-[600px]"
      >
        <!-- 헤더 -->
        <div
          class="flex items-center justify-between px-4 py-2 bg-blue-100 border-b border-blue-200"
        >
          <h3 class="text-lg font-paperSemi text-gray-800">
            <slot name="header">Modal Title</slot>
          </h3>
          <IconButton
            variant="close"
            label="닫기"
            class="bg-white text-black hover:bg-gray-100"
            @click="close"
          />
        </div>

        <!-- 본문 -->
        <div class="p-4">
          <slot>
            <!-- 기본 콘텐츠 영역 -->
            Content goes here.
          </slot>
        </div>
      </div>
    </div>
  </transition>
</template>

<script setup>
import IconButton from "@/components/button/IconButton.vue";

const props = defineProps({
  modelValue: {
    type: Boolean,
    required: true,
  },
});

const emit = defineEmits(["update:modelValue"]);

function close() {
  emit("update:modelValue", false);
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

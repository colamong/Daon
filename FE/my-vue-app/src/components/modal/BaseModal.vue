<template>
  <transition name="fade">
    <div
      v-if="modelValue"
      class="fixed inset-0 bg-black bg-opacity-90 grid place-items-center z-50 font-paper p-2 md:p-4 min-h-screen"
      style="
        display: grid;
        place-items: center;
        align-content: center;
        justify-content: center;
      "
    >
      <div
        class="bg-white rounded-xl overflow-hidden shadow-lg w-full max-w-[95vw] lg:w-[500px] xl:w-[650px] overflow-y-auto my-auto"
      >
        <!-- 헤더 -->
        <div
          class="flex items-center justify-between px-3 md:px-4 py-2 md:py-3 bg-blue-100 border-b border-blue-200"
        >
          <h3 class="text-base md:text-lg font-paperSemi text-gray-800">
            <slot name="header">Modal Title</slot>
          </h3>
          <IconButton
            variant="close"
            label="닫기"
            class="bg-white text-black hover:bg-gray-100 text-sm md:text-base"
            @click="close"
          />
        </div>

        <!-- 본문 -->
        <div class="p-3 md:p-4 lg:p-6">
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
import { watch, onUnmounted } from "vue";
import IconButton from "@/components/button/IconButton.vue";

const props = defineProps({
  modelValue: {
    type: Boolean,
    required: true,
  },
});

const emit = defineEmits(["update:modelValue"]);

// 모달 열릴 때 스크롤 방지, 닫힐 때 스크롤 복원
watch(
  () => props.modelValue,
  (newValue) => {
    if (newValue) {
      document.body.style.overflow = "hidden";
    } else {
      document.body.style.overflow = "";
    }
  },
  { immediate: true }
);

// 컴포넌트 언마운트 시 스크롤 복원
onUnmounted(() => {
  document.body.style.overflow = "";
});

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

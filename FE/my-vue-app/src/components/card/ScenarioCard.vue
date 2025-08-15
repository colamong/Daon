<!-- 한글공부 상황선택 카드 -->
<template>
  <router-link
    :to="link"
    class="block w-full max-w-72 rounded-2xl overflow-hidden shadow-md hover:shadow-lg transition-shadow bg-white"
  >
    <!-- 이미지 영역 -->
    <div
      class="h-32 md:h-48 overflow-hidden bg-blue-100/40 flex items-center justify-center"
    >
      <img
        :src="resolvedImage"
        alt="카드 배경 이미지"
        class="w-full h-full object-contain"
      />
    </div>

    <!-- 제목 영역 -->
    <div class="px-2 md:px-4 py-2 md:py-4 text-center">
      <h3 class="text-sm md:text-xl font-paperBold text-black">
        {{ title }}
      </h3>
    </div>
  </router-link>
</template>

<script setup>
import { computed } from "vue";

const props = defineProps({
  image: String,
  title: {
    type: String,
    required: true,
  },
  description: {
    type: String,
    required: true,
  },
  link: {
    type: String,
    required: true,
  },
});

const resolvedImage = computed(() => {
  return props.image
    ? props.image
    : new URL("@/assets/icons/image-placeholder.svg", import.meta.url).href;
});
</script>

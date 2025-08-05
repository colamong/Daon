<!-- 채팅방 옆에 내 채팅방 목록 카드 -->
<template>
  <router-link
    :to="link"
    class="flex items-center w-full max-w-sm rounded-xl shadow border bg-white px-4 py-3"
  >
    <!-- 이미지 -->
    <img
      :src="resolvedImage"
      alt="지역 이미지"
      class="w-12 h-12 rounded-md object-cover mr-4"
    />

    <!-- 지역명 -->
    <div class="flex-1 text-lg font-paperBold text-black">
      {{ location }}
    </div>

    <!-- 화살표 -->
    <div class="text-gray-400 text-lg font-paperSemiBold">›</div>
  </router-link>
</template>

<script setup>
import { computed } from "vue";

const props = defineProps({
  location: String,
  image: String,
  link: {
    type: String,
    required: true,
  },
});

const resolvedImage = computed(() =>
  props.image
    ? props.image
    : new URL("@/assets/icons/image-placeholder.svg", import.meta.url).href
);
</script>

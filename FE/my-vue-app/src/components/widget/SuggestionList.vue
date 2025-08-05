<template>
  <ul
    class="bg-white border border-gray-200 rounded-md shadow-md w-full mt-2 space-y-1"
  >
    <li
      v-for="(item, index) in suggestions"
      :key="index"
      @mouseover="hoveredIndex = index"
      @mouseleave="hoveredIndex = null"
      @click="$emit('select', item)"
    >
      <CommunityRegionCard
        :title="item.title"
        :description="item.subtitle"
        :image="item.image"
        :link="item.link"
        :class="[
          'transition-colors',
          hoveredIndex === index ? 'bg-blue-100' : 'hover:bg-hover-blue/20',
        ]"
      />
    </li>
  </ul>
</template>

<script setup>
import { ref } from "vue";
import CommunityRegionCard from "@/components/card/CommunityRegionCard.vue";

const props = defineProps({
  suggestions: {
    type: Array,
    required: true,
  },
});

const hoveredIndex = ref(null);
</script>

<style scoped>
/* 더 이상 내부 스크롤이나 높이 제한 없음 */
</style>

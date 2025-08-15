<template>
  <div class="flex justify-center min-h-screen py-4 md:py-10">
    <div
      class="w-full max-w-4xl mx-4 md:mx-10 px-2 md:px-4 py-4 md:py-8 font-paper bg-white rounded-xl"
    >
    <!-- 헤더 -->
    <div class="text-center mb-6 md:mb-12">
      <h1 class="text-2xl md:text-4xl font-paperBold text-gray-800 mb-2 md:mb-4">상황별 학습</h1>
      <p class="text-base md:text-lg text-gray-600 mb-1 md:mb-2">
        실생활에 도움이 되는 한국어 상황별 학습
      </p>
      <p class="text-sm md:text-base text-gray-500">
        직접 상황에서의 대화를 연습하고 발음을 확인해보세요
      </p>
    </div>

    <!-- 테마 카드 그리드 -->
    <div class="flex justify-center">
      <div
        class="grid grid-cols-2 md:grid-cols-2 lg:grid-cols-3 gap-3 md:gap-6 justify-items-center"
      >
        <ScenarioCard
          v-for="theme in learningThemes"
          :key="theme.id"
          :title="theme.title"
          :description="theme.description"
          :image="theme.image"
          :link="`/dashboard/learning/theme/${theme.id}`"
          class="w-full max-w-sm"
        />
      </div>
    </div>
  </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import ScenarioCard from "@/components/card/ScenarioCard.vue";
import learningService from "@/services/learningService"; //

// 상태
const learningThemes = ref([]);
const loading = ref(true);
const error = ref("");

// 로딩
onMounted(async () => {
  try {
    const themes = await learningService.getThemes(); // [{id,title,description,image}]
    learningThemes.value = themes;
  } catch (e) {
    error.value = "failed";
  } finally {
    loading.value = false;
  }
});
</script>

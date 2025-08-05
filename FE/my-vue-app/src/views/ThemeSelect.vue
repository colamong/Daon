<template>
  <div
    class="m-10 mb-20 max-w-5xl mx-auto px-4 py-8 font-paper bg-white rounded-xl"
  >
    <!-- 헤더 -->
    <div class="text-center mb-12">
      <h1 class="text-4xl font-paperBold text-gray-800 mb-4">상황별 학습</h1>
      <p class="text-lg text-gray-600 mb-2">
        실생활에 도움이 되는 한국어 상황별 학습
      </p>
      <p class="text-gray-500">
        직접 상황에서의 대화를 연습하고 발음을 확인해보세요
      </p>
    </div>

    <!-- 테마 카드 그리드 -->
    <div
      class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 justify-items-center"
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
</template>

<script setup>
import ScenarioCard from "@/components/card/ScenarioCard.vue";
import { learningThemes } from "@/data/learningThemes.js";
</script>

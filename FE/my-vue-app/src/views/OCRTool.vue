<template>
  <section class="px-6 py-8 max-w-4xl mx-auto">
    <BaseFileUpload mode="file" @translate="onTranslateRequested" />
    <div class="mt-12 grid grid-cols-1 sm:grid-cols-3 gap-6">
      <BaseCard variant="ocr1" />
      <BaseCard variant="ocr2" />
      <BaseCard variant="ocr3" />
    </div>
  </section>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import BaseFileUpload from "@/components/form/BaseFileUpload.vue";
import BaseCard from "@/components/card/BaseCard.vue";

const router = useRouter();

function onTranslateRequested() {
  // 바로 결과 페이지로 이동 (JS)
  router.push({ name: "OCRResult" });
}
</script>

<style scoped>
/* 필요 시 스타일만 작성 */
</style>

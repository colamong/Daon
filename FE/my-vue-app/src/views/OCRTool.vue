<template>
  <section class="px-6 py-8 max-w-4xl mx-auto mb-10">
    <BaseFileUpload mode="file" @translate="onTranslateRequested" />
    <div class="mt-12 grid grid-cols-1 sm:grid-cols-3 gap-6">
      <BaseCard variant="ocr1" class="!bg-white" />
      <BaseCard variant="ocr2" class="!bg-white" />
      <BaseCard variant="ocr3" class="!bg-white" />
    </div>
  </section>
</template>

<script setup>
import { useRouter } from "vue-router";
import BaseFileUpload from "@/components/form/BaseFileUpload.vue";
import BaseCard from "@/components/card/BaseCard.vue";

const router = useRouter();

/**
 * @param {{ image: string|null, file: string|null }} payload
 */
function onTranslateRequested(payload) {
  router.push({
    name: "OCRResult",
    query: {
      image: payload.image || "",
      file: payload.file || "",
    },
  });
}
</script>

<style scoped>
/* 필요 시 스타일만 추가하세요 */
</style>

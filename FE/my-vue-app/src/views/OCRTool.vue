<template>
  <section class="px-6 py-8 max-w-4xl mx-auto mb-10">
    <BaseFileUpload
      mode="file"
      @translate="onTranslateRequested"
      @upload:file="onFileUploaded"
      :disabled="isLoading"
    />

    <!-- 로딩 알림 -->
    <div
      v-if="isLoading"
      class="mt-6 bg-blue-50 border-l-4 border-blue-400 p-4 rounded-md"
    >
      <div class="flex items-center">
        <div
          class="animate-spin rounded-full h-5 w-5 border-b-2 border-blue-600 mr-3"
        ></div>
        <p class="text-blue-700 font-paper">
          번역 중입니다... 잠시만 기다려주세요.
        </p>
      </div>
    </div>

    <div class="mt-12 grid grid-cols-1 sm:grid-cols-3 gap-6">
      <BaseCard variant="ocr1" class="ocr-card" />
      <BaseCard variant="ocr2" class="ocr-card" />
      <BaseCard variant="ocr3" class="ocr-card" />
    </div>
  </section>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import BaseFileUpload from "@/components/form/BaseFileUpload.vue";
import BaseCard from "@/components/card/BaseCard.vue";
import { ocrService } from "@/services/ocrService.js";

const router = useRouter();
const currentFile = ref(null);
const isLoading = ref(false);

function onFileUploaded(file) {
  currentFile.value = file;
}

async function onTranslateRequested(payload) {
  if (!currentFile.value) {
    alert("먼저 파일을 업로드해주세요.");
    return;
  }

  try {
    isLoading.value = true;

    const result = await ocrService.uploadAndOcr(currentFile.value);

    router.push({
      name: "OCRResult",
      query: {
        image: payload.image || "",
        file: payload.file || "",
        result: JSON.stringify(result),
      },
    });
  } catch (error) {
    console.error("OCR 처리 실패:", error);
    alert(error.message || "OCR 처리 중 오류가 발생했습니다.");
  } finally {
    isLoading.value = false;
  }
}
</script>

<style scoped>
/* OCR 카드 호버 효과 제거 */
:deep(.ocr-card) {
  transform: none !important;
  box-shadow: 0 1px 3px 0 rgb(0 0 0 / 0.1), 0 1px 2px -1px rgb(0 0 0 / 0.1) !important;
  background-color: white !important;
  cursor: default !important;
}

:deep(.ocr-card:hover) {
  transform: none !important;
  box-shadow: 0 1px 3px 0 rgb(0 0 0 / 0.1), 0 1px 2px -1px rgb(0 0 0 / 0.1) !important;
  background-color: white !important;
  cursor: default !important;
}
</style>

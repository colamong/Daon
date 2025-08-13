<template>
  <section class="px-6 py-4 max-w-6xl mx-auto min-h-[calc(100vh-200px)]">
    <div class="flex justify-between items-center font-paperBold mb-4">
      <h1 class="text-2xl">분석 결과</h1>
      <button
        @click="goBack"
        class="px-4 py-2 bg-white rounded hover:bg-blue-700 hover:text-white transition"
      >
        뒤로 가기
      </button>
    </div>
    <!-- 전체 높이를 뷰포트에 맞춤 -->
    <div
      class="grid grid-cols-1 md:grid-cols-3 gap-6 h-[calc(100vh-250px)] mb-10"
    >
      <!-- LEFT: 이미지/파일 영역 -->
      <div
        class="col-span-1 md:col-span-2 bg-white rounded-xl shadow p-4 flex items-center justify-center h-full"
      >
        <img
          v-if="image"
          :src="image"
          alt="OCR Preview"
          class="max-w-full max-h-[calc(100vh-300px)] object-contain"
        />
        <div
          v-else-if="file"
          class="flex flex-col items-center space-y-2 text-gray-700"
        >
          <img :src="folderIcon" alt="파일 아이콘" class="w-16 h-16" />
          <p class="break-all">{{ file }}</p>
        </div>
        <div v-else class="text-gray-500">미리볼 파일이 없습니다.</div>
      </div>

      <!-- RIGHT: 요약/번역 카드 -->
      <div class="col-span-1 flex flex-col gap-4 h-full max-h-full">
        <!-- 요약 결과 카드 -->
        <div
          class="bg-white rounded-xl shadow p-4 flex-1 overflow-y-auto max-h-[calc(50vh-150px)]"
        >
          <h2 class="font-paperSemi mb-2 text-sm">요약 결과</h2>
          <div
            class="text-gray-600 whitespace-pre-wrap font-paper text-sm leading-relaxed"
          >
            {{ summary || "요약 결과가 없습니다." }}
          </div>
        </div>

        <!-- 번역 결과 카드 -->
        <div
          class="bg-white rounded-xl shadow p-4 flex-1 overflow-y-auto max-h-[calc(50vh-150px)]"
        >
          <h2 class="font-paperSemi mb-2 text-sm">번역 결과</h2>
          <div
            class="text-gray-600 whitespace-pre-wrap font-paper text-sm leading-relaxed"
          >
            {{ translation || "번역 결과가 없습니다." }}
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import folderIcon from "@/assets/icons/Folder.svg";

const route = useRoute();
const router = useRouter();

const image = ref(route.query.image || "");
const file = ref(route.query.file || "");
const summary = ref("");
const translation = ref("");

onMounted(() => {
  // API 응답 결과가 있다면 파싱하여 표시
  if (route.query.result) {
    try {
      const apiResponse = JSON.parse(route.query.result);

      // 응답 구조 확인
      let ocrData = null;

      if (apiResponse.statusCode === 200 && apiResponse.data) {
        ocrData = apiResponse.data;
      } else if (apiResponse.data) {
        ocrData = apiResponse.data;
      } else if (
        apiResponse.summary ||
        apiResponse.translated ||
        apiResponse.ocrText
      ) {
        ocrData = apiResponse;
      }

      if (ocrData) {
        summary.value = ocrData.summary || "요약 결과가 없습니다.";
        translation.value = ocrData.translated || "번역 결과가 없습니다.";
      } else {
        summary.value = "데이터 구조를 인식할 수 없습니다.";
        translation.value = "데이터 구조를 인식할 수 없습니다.";
      }
    } catch (error) {
      summary.value = "결과를 불러오는 중 오류가 발생했습니다.";
      translation.value = "결과를 불러오는 중 오류가 발생했습니다.";
    }
  } else {
    summary.value = route.query.summary || "요약 결과가 없습니다.";
    translation.value = route.query.text || "번역 결과가 없습니다.";
  }
});

function goBack() {
  router.back();
}
</script>

<style scoped>
/* 
 h-[600px] 값은 예시입니다. 
 필요하다면 h-[70vh] 등으로 조정하세요. 
*/
</style>

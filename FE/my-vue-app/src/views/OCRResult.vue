<template>
  <section class="px-6 py-8 max-w-6xl mx-auto mb-10">
    <div class="flex justify-between items-center font-paperBold">
      <h1 class="text-2xl">분석 결과</h1>
      <button
        @click="goBack"
        class="px-4 py-2 bg-white rounded hover:bg-blue-700 hover:text-white transition"
      >
        뒤로 가기
      </button>
    </div>
    <!-- 전체 높이 고정 -->
    <div class="mt-6 grid grid-cols-1 md:grid-cols-3 gap-6 h-[600px]">
      <!-- LEFT: 이미지/파일 영역 (변경 없음) -->
      <div
        class="col-span-1 md:col-span-2 bg-white rounded-xl shadow p-4 flex items-center justify-center"
      >
        <img
          v-if="image"
          :src="image"
          alt="OCR Preview"
          class="max-w-full max-h-full object-contain"
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
      <!-- ⚠️ 여기서 min-h-0을 꼭 추가합니다. -->
      <div class="col-span-1 flex flex-col gap-6 h-full min-h-0">
        <!-- ⚠️ 각 카드에 flex-1, overflow-y-auto, min-h-0 -->
        <div
          class="bg-white rounded-xl shadow p-6 flex-1 overflow-y-auto min-h-0"
        >
          <h2 class="font-semibold mb-2">요약 결과</h2>
          <div class="text-gray-600 whitespace-pre-wrap">
            {{ summary || "요약 결과가 없습니다." }}
          </div>
        </div>

        <div
          class="bg-white rounded-xl shadow p-6 flex-1 overflow-y-auto min-h-0"
        >
          <h2 class="font-semibold mb-2">번역 결과</h2>
          <div class="text-gray-600 whitespace-pre-wrap">
            {{ translation || "번역 결과가 없습니다." }}
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { useRoute, useRouter } from "vue-router";
import folderIcon from "@/assets/icons/Folder.svg";

const route = useRoute();
const router = useRouter();

// 테스트용 긴 문자열 생성 함수
function makeLongText(label) {
  return Array(50).fill(`${label} 테스트용 긴 텍스트입니다.`).join("\n\n");
}

const image = route.query.image || "";
const file = route.query.file || "";
const summary = route.query.summary || "";
const translation = route.query.text || makeLongText("번역");

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

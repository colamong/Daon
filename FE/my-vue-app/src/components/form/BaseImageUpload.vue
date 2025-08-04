<!-- src/components/form/BaseImageUpload.vue -->
<template>
  <div
    class="w-full flex flex-col items-center justify-center bg-white border-2 border-dashed border-gray-300 rounded-xl py-12 px-4 text-center relative"
    @dragover.prevent
    @dragenter.prevent
    @drop.prevent="handleDrop"
  >
    <!-- 이미지 미리보기 -->
    <template v-if="preview">
      <div class="relative w-full">
        <img
          :src="preview"
          alt="이미지 미리보기"
          class="w-full h-auto object-contain rounded-md"
        />
        <IconButton
          class="absolute top-2 right-2"
          icon="close"
          @click="clearPreview"
        />
      </div>
    </template>

    <!-- 업로드 안내 -->
    <template v-else>
      <p class="text-black text-sm mb-4">
        아이의 사진을 드래그하거나 클릭하여 업로드<br />
        <span class="font-semibold">JPG, PNG, GIF</span> 파일을 지원합니다.
      </p>
      <label
        for="imageUpload"
        class="bg-purple-100 hover:bg-purple-200 text-black font-medium flex items-center gap-2 px-4 py-2 rounded-md cursor-pointer transition"
      >
        <img :src="cameraIcon" alt="카메라 아이콘" class="w-5 h-5" />
        사진 등록
        <input
          id="imageUpload"
          type="file"
          accept="image/*"
          class="hidden"
          @change="handleChange"
        />
      </label>
    </template>
  </div>
</template>

<script setup>
import { ref } from "vue";
import IconButton from "@/components/button/IconButton.vue";
import cameraIcon from "@/assets/icons/camera.png";

const emit = defineEmits(["upload:image"]);
const preview = ref(null);

function handleChange(e) {
  const file = e.target.files[0];
  processFile(file);
}
function handleDrop(e) {
  processFile(e.dataTransfer.files[0]);
}

function processFile(file) {
  if (!file || !file.type.startsWith("image/")) return;
  emit("upload:image", file);
  preview.value = URL.createObjectURL(file);
}

function clearPreview() {
  if (preview.value) URL.revokeObjectURL(preview.value);
  preview.value = null;
}
</script>

<style scoped>
/* 필요 시 추가 스타일 */
</style>

<!-- src/components/form/BaseImageUpload.vue -->
<template>
  <div
    class="w-full h-[300px] md:h-[400px] flex flex-col items-center justify-center bg-white border-2 border-dashed border-gray-300 rounded-xl text-center relative"
    @dragover.prevent
    @dragenter.prevent
    @drop.prevent="handleDrop"
  >
    <!-- 이미지 미리보기 -->
    <template v-if="preview">
      <div class="flex flex-col items-center space-y-4">
        <div class="relative">
          <img
            :src="preview"
            alt="아이 프로필"
            class="w-32 h-32 md:w-48 md:h-48 rounded-full object-cover border-4 border-purple-200 shadow-lg"
          />
        </div>
        <!-- 모바일: 두 개의 버튼 -->
        <div class="flex flex-col md:hidden gap-3">
          <label
            for="cameraCapture"
            class="bg-purple-100 hover:bg-purple-200 text-black font-medium flex items-center justify-center gap-2 px-4 py-2 rounded-lg cursor-pointer transition font-paper text-sm"
          >
            <img :src="cameraIcon" alt="카메라 아이콘" class="w-4 h-4" />
            카메라 촬영
            <input
              id="cameraCapture"
              type="file"
              accept="image/*"
              capture="environment"
              class="hidden"
              @change="handleChange"
            />
          </label>
          
          <label
            for="gallerySelect"
            class="bg-blue-100 hover:bg-blue-200 text-black font-medium flex items-center justify-center gap-2 px-4 py-2 rounded-lg cursor-pointer transition font-paper text-sm"
          >
            <img :src="galleryIcon" alt="갤러리 아이콘" class="w-4 h-4" />
            사진 불러오기
            <input
              id="gallerySelect"
              type="file"
              accept="image/*"
              class="hidden"
              @change="handleChange"
            />
          </label>
        </div>
        
        <!-- 데스크톱: 기존 버튼 -->
        <label
          for="imageUpload"
          class="hidden md:flex bg-purple-100 hover:bg-purple-200 text-black font-medium items-center gap-2 px-6 py-3 rounded-lg cursor-pointer transition font-paper text-base"
        >
          <img :src="cameraIcon" alt="카메라 아이콘" class="w-5 h-5" />
          사진 변경
          <input
            id="imageUpload"
            type="file"
            accept="image/*"
            class="hidden"
            @change="handleChange"
          />
        </label>
      </div>
    </template>

    <!-- 업로드 안내 -->
    <template v-else>
      <div class="flex flex-col items-center space-y-4">
        <p class="text-black text-base md:text-lg mb-3 md:mb-4 font-paper px-4">
          <!-- 모바일에서만 표시 -->
          <span class="block md:hidden text-center">
            아이의 사진을 촬영하거나 갤러리에서 선택<br />
            <span class="font-semibold">JPG, PNG, GIF</span> 파일을 지원합니다.
          </span>
          <!-- 데스크톱에서만 표시 -->
          <span class="hidden md:block text-center">
            아이의 사진을 드래그하거나 클릭하여 업로드<br />
            <span class="font-semibold">JPG, PNG, GIF</span> 파일을 지원합니다.
          </span>
        </p>
        <!-- 모바일: 두 개의 버튼 -->
        <div class="flex flex-col md:hidden gap-3">
          <label
            for="cameraCapture2"
            class="bg-purple-100 hover:bg-purple-200 text-black font-medium flex items-center justify-center gap-2 px-4 py-2 rounded-lg cursor-pointer transition font-paper text-sm"
          >
            <img :src="cameraIcon" alt="카메라 아이콘" class="w-4 h-4" />
            카메라 촬영
            <input
              id="cameraCapture2"
              type="file"
              accept="image/*"
              capture="environment"
              class="hidden"
              @change="handleChange"
            />
          </label>
          
          <label
            for="gallerySelect2"
            class="bg-blue-100 hover:bg-blue-200 text-black font-medium flex items-center justify-center gap-2 px-4 py-2 rounded-lg cursor-pointer transition font-paper text-sm"
          >
            <img :src="galleryIcon" alt="갤러리 아이콘" class="w-4 h-4" />
            사진 불러오기
            <input
              id="gallerySelect2"
              type="file"
              accept="image/*"
              class="hidden"
              @change="handleChange"
            />
          </label>
        </div>
        
        <!-- 데스크톱: 기존 버튼 -->
        <label
          for="imageUpload"
          class="hidden md:flex bg-purple-100 hover:bg-purple-200 text-black font-medium items-center gap-2 px-6 py-3 rounded-lg cursor-pointer transition font-paper text-base"
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
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref } from "vue";
import IconButton from "@/components/button/IconButton.vue";
import cameraIcon from "@/assets/icons/camera.png";
import galleryIcon from "@/assets/icons/image-placeholder.svg";

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

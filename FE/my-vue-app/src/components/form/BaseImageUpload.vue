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
import { useNotification } from "@/composables/useNotification.js";

const emit = defineEmits(["upload:image"]);
const preview = ref(null);
const { showError } = useNotification();

function handleChange(e) {
  const file = e.target.files[0];
  processFile(file);
}
function handleDrop(e) {
  processFile(e.dataTransfer.files[0]);
}

async function processFile(file) {
  if (!file || !file.type.startsWith("image/")) return;
  
  // 파일 크기 검증 (5MB 제한)
  const maxSize = 5 * 1024 * 1024; // 5MB
  if (file.size > maxSize) {
    showError("파일 크기가 너무 큽니다. 5MB 이하의 이미지를 선택해주세요.", "파일 크기 초과");
    return;
  }
  
  try {
    // 이미지 압축 (최대 800x800, 품질 0.8)
    const compressedFile = await compressImage(file, 800, 0.8);
    emit("upload:image", compressedFile);
    preview.value = URL.createObjectURL(compressedFile);
  } catch (error) {
    console.error("이미지 압축 실패:", error);
    // 압축 실패 시 원본 파일 사용
    emit("upload:image", file);
    preview.value = URL.createObjectURL(file);
  }
}

// 이미지 압축 함수
function compressImage(file, maxWidth = 800, quality = 0.8) {
  return new Promise((resolve) => {
    const canvas = document.createElement('canvas');
    const ctx = canvas.getContext('2d');
    const img = new Image();
    
    img.onload = () => {
      // 비율 유지하면서 크기 조정
      let { width, height } = img;
      if (width > height) {
        if (width > maxWidth) {
          height = (height * maxWidth) / width;
          width = maxWidth;
        }
      } else {
        if (height > maxWidth) {
          width = (width * maxWidth) / height;
          height = maxWidth;
        }
      }
      
      canvas.width = width;
      canvas.height = height;
      
      // 이미지 그리기
      ctx.drawImage(img, 0, 0, width, height);
      
      // Blob으로 변환
      canvas.toBlob((blob) => {
        // File 객체로 변환
        const compressedFile = new File([blob], file.name, {
          type: file.type,
          lastModified: Date.now()
        });
        resolve(compressedFile);
      }, file.type, quality);
    };
    
    img.src = URL.createObjectURL(file);
  });
}

function clearPreview() {
  if (preview.value) URL.revokeObjectURL(preview.value);
  preview.value = null;
}
</script>

<style scoped>
/* 필요 시 추가 스타일 */
</style>

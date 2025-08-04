<template>
  <div class="flex flex-col items-center gap-12 w-full">
    <!-- 사진 등록 영역 -->
    <div
      class="w-full flex flex-col items-center justify-center bg-white border-2 border-dashed border-gray-300 rounded-xl py-12 px-4 text-center"
      @dragover.prevent
      @dragenter.prevent
      @drop.prevent="handleImageDrop"
    >
      <p class="text-black text-sm mb-4">
        아이의 사진을 드래그하거나 클릭하여 업로드<br />
        <span class="font-semibold">JPG, PNG, GIF</span> 파일을 지원합니다.
      </p>
      <label
        for="imageUpload"
        class="bg-purple-100 hover:bg-purple-200 text-black font-medium flex items-center gap-2 px-4 py-2 rounded-md cursor-pointer transition"
      >
        <img
          src="@/assets/icons/camera.png"
          alt="카메라 아이콘"
          class="w-5 h-5"
        />
        사진 등록
        <input
          id="imageUpload"
          type="file"
          accept="image/*"
          class="hidden"
          @change="handleImageUpload"
        />
      </label>
    </div>

    <!-- 일반 파일 업로드 영역 -->
    <div
      class="w-full flex flex-col items-center justify-center bg-white border-2 border-dashed border-gray-300 rounded-xl py-12 px-4 text-center"
      @dragover.prevent
      @dragenter.prevent
      @drop.prevent="handleFileDrop"
    >
      <p class="text-black text-sm mb-4">
        파일을 드래그하거나 클릭하여 업로드<br />
        <span class="font-semibold">PDF, JPG, PNG, GIF</span> 파일을 지원합니다.
      </p>
      <label
        for="fileUpload"
        class="bg-green-100 hover:bg-green-200 text-black font-medium flex items-center gap-2 px-4 py-2 rounded-md cursor-pointer transition"
      >
        <img
          src="@/assets/icons/Folder.svg"
          alt="폴더 아이콘"
          class="w-5 h-5"
        />
        파일 불러오기
        <input
          id="fileUpload"
          type="file"
          accept=".pdf,.jpg,.jpeg,.png,.gif"
          class="hidden"
          @change="handleFileUpload"
        />
      </label>
    </div>
  </div>
</template>

<script setup>
const emit = defineEmits(['upload:image', 'upload:file'])

function handleImageUpload(event) {
  const file = event.target.files[0];
  if (file) emit('upload:image', file);
}

function handleFileUpload(event) {
  const file = event.target.files[0];
  if (file) emit('upload:file', file);
}

function handleImageDrop(event) {
  const file = event.dataTransfer.files[0];
  if (file) emit('upload:image', file);
}

function handleFileDrop(event) {
  const file = event.dataTransfer.files[0];
  if (file) emit('upload:file', file);
}
</script>

<style scoped>
/* 필요 시 스타일 작성 */
</style>

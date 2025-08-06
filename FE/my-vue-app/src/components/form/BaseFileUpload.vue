<!-- src/components/form/BaseFileUpload.vue -->
<template>
  <div
    class="relative w-full h-64 bg-white border-2 border-dashed border-gray-300 rounded-xl overflow-visible"
    @dragover.prevent="onDrop"
    @dragenter.prevent="onDrop"
    @drop.prevent="onDrop"
  >
    <!-- 업로드 전 안내 -->
    <div
      v-if="!hasPreview"
      class="w-full h-full flex flex-col items-center justify-center text-center px-4"
    >
      <p class="text-black text-sm mb-4">
        파일을 드래그하거나 클릭하여 업로드<br />
        <span class="font-semibold">JPG, PNG</span> 파일을 지원합니다.
      </p>
      <label
        for="fileInput"
        class="bg-green-100 hover:bg-green-200 text-black font-medium flex items-center gap-2 px-4 py-2 rounded-md cursor-pointer transition"
      >
        <img :src="folderIcon" alt="폴더 아이콘" class="w-5 h-5" />
        파일 불러오기
      </label>
      <input
        id="fileInput"
        type="file"
        accept=".jpg,.jpeg,.png"
        class="hidden"
        :disabled="props.disabled"
        @change="onChange"
      />
    </div>

    <!-- 업로드 후 프리뷰 -->
    <div v-else class="w-full h-full flex items-center justify-center">
      <img
        v-if="imagePreview"
        :src="imagePreview"
        alt="이미지 미리보기"
        class="max-w-full max-h-full object-contain"
      />
      <div v-else class="flex flex-col items-center space-y-2">
        <img :src="folderIcon" alt="파일 아이콘" class="w-12 h-12" />
        <p class="text-sm font-medium text-gray-700">{{ fileName }}</p>
      </div>
    </div>

    <!-- 닫기 버튼 (위 오른쪽) -->
    <IconButton
      v-if="hasPreview"
      variant="close"
      label="닫기"
      @click="clear"
      style="position: absolute; top: 8px; right: 8px; z-index: 10"
    />

    <!-- 번역하기 버튼 (아래 중앙) -->
    <BaseButton
      v-if="hasPreview"
      :class="[
        'absolute font-paperBold px-4 mt-16 py-2 h-10',
        props.disabled ? 'bg-gray-300 cursor-not-allowed' : 'bg-blue-200 hover:bg-blue-300'
      ]"
      style="bottom: 8px; left: 50%; transform: translateX(-50%); z-index: 10"
      :disabled="props.disabled"
      @click="onTranslate"
    >
      {{ props.disabled ? '처리 중...' : '번역하기' }}
    </BaseButton>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import IconButton from "@/components/button/IconButton.vue";
import BaseButton from "@/components/button/BaseButton.vue";
import folderIcon from "@/assets/icons/Folder.svg";

const props = defineProps({
  disabled: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(["upload:file", "translate"]);
const imagePreview = ref(null);
const fileName = ref(null);

const hasPreview = computed(() => !!imagePreview.value || !!fileName.value);

function onChange(e) {
  handleFile(e.target.files[0]);
}
function onDrop(e) {
  handleFile(e.dataTransfer.files[0]);
}

function handleFile(file) {
  if (!file) return;
  emit("upload:file", file);

  if (file.type.startsWith("image/")) {
    imagePreview.value = URL.createObjectURL(file);
    fileName.value = null;
  } else {
    fileName.value = file.name;
    if (imagePreview.value) URL.revokeObjectURL(imagePreview.value);
    imagePreview.value = null;
  }
}

function clear() {
  if (imagePreview.value) URL.revokeObjectURL(imagePreview.value);
  imagePreview.value = null;
  fileName.value = null;
}

function onTranslate() {
  if (props.disabled) return;
  emit("translate", { image: imagePreview.value, file: fileName.value });
}
</script>

<style scoped>
/* 모든 절대 위치는 인라인 스타일로 강제 적용됩니다. */
</style>

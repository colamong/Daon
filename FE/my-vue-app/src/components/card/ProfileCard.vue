<template>
  <div class="w-72 p-6 rounded-2xl border-2 border-purple-300 bg-purpleLight text-center shadow-md">
    <!-- 프로필 이미지 -->
    <img
      :src="image"
      alt="프로필 사진"
      class="w-28 h-28 rounded-full mx-auto object-cover mb-4"
    />

    <!-- 이름 -->
    <h2 class="text-xl font-paperBold text-purple-800 mb-1">
      {{ name }}
    </h2>

    <!-- 이메일 -->
    <p class="text-sm text-gray-500 mb-1 font-paper">
      {{ email }}
    </p>

    <!-- 국적 -->
    <p class="text-sm text-gray-600 font-paper">
      ≈ {{ nationality }}
    </p>

    <!-- 수정 버튼 -->
    <BaseButton variant="myprofile" class="mt-5" @click="goToEdit">
      프로필 수정
    </BaseButton>
  </div>
</template>



<script setup>
import { useRouter } from 'vue-router'
import BaseButton from '@/components/button/BaseButton.vue';

const router = useRouter()

const props = defineProps({
  name: String,
  email: String,
  nationality: String,
  image: {
    type: String,
    default: new URL('@/assets/icons/image-placeholder.svg', import.meta.url).href,
  },
})

function goToEdit() {
  router.push({ name: 'ProfileEdit' })
}
</script>

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
      {{ user?.nickname || '사용자' }}
    </h2>

    <!-- 이메일 -->
    <p class="text-sm text-gray-500 mb-1 font-paper">
      {{ user?.email || '' }}
    </p>

    <!-- 국적 -->
    <p class="text-sm text-gray-600 font-paper">
      ≈ {{ userNationName || '국가 정보 없음' }}
    </p>

    <!-- 수정 버튼 -->
    <BaseButton variant="myprofile" class="mt-5" @click="goToEdit">
      프로필 수정
    </BaseButton>
  </div>
</template>



<script setup>
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/store/auth'
import BaseButton from '@/components/button/BaseButton.vue';

const router = useRouter()
const auth = useAuthStore()

const user = computed(() => {
  log('ProfileCard - user:', auth.user)
  return auth.user
})

const userNationName = computed(() => {
  log('ProfileCard - userNationName:', auth.userNationName)
  log('ProfileCard - nations:', auth.nations?.length)
  log('ProfileCard - nationCode:', auth.user?.nationCode)
  return auth.userNationName
})

onMounted(async () => {
  log('ProfileCard mounted - 초기 상태:', {
    user: auth.user,
    token: auth.token,
    nationsCount: auth.nations?.length
  })
  
  // 국가 목록을 먼저 로드
  await auth.loadNations()
  
  // 사용자 정보가 없다면 가져오기
  if (!auth.user && auth.token) {
    try {
      await auth.getCurrentUser()
    } catch (error) {
      error('사용자 정보 로드 실패:', error)
    }
  }
  
  log('ProfileCard mounted 완료:', {
    user: auth.user,
    nationsCount: auth.nations?.length,
    userNationName: auth.userNationName
  })
})

function goToEdit() {
  router.push({ name: 'ProfileEdit' })
}
</script>

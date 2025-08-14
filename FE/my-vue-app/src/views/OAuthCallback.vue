
<template>
  <div class="p-6">로그인 처리 중...</div>
</template>

<script setup>
import { onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/store/auth';

const router = useRouter();
const auth = useAuthStore();

onMounted(async () => {
  try {
    const ok = await auth.checkAuthStatus(); // 쿠키 붙어서 /user/me 성공하면 true
    if (!ok) throw new Error();
    router.replace('/'); // 원하는 페이지로 이동
  } catch {
    router.replace('/login?error=oauth');
  }
});
</script>
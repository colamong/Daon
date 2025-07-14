<template>
  <div class="max-w-md mx-auto mt-10 p-6 bg-white rounded shadow space-y-4">
    <h2 class="text-2xl font-bold mb-4 text-center">로그인</h2>
    <form @submit.prevent="login">
      <input v-model="email" type="email" placeholder="이메일" class="input" required />
      <input v-model="password" type="password" placeholder="비밀번호" class="input" required />
      <button type="submit" class="btn bg-blue-500 hover:bg-blue-600 w-full">로그인</button>
    </form>
    <div class="mt-4 space-y-2">
      <button @click="social('google')" class="btn bg-red-500 hover:bg-red-600 w-full">구글 로그인</button>
      <button @click="social('kakao')" class="btn bg-yellow-400 hover:bg-yellow-500 w-full text-black">카카오 로그인</button>
      <button @click="social('naver')" class="btn bg-green-600 hover:bg-green-700 w-full">네이버 로그인</button>
    </div>
    <router-link to="/register" class="text-blue-500 mt-2 block text-center">회원가입</router-link>
  </div>
</template>

<script>
export default {
  data() {
    return { email: '', password: '' }
  },
  methods: {
    login() {
      // 일반 로그인 로직 (API 연동)
    },
    social(provider) {
      const urls = {
        google: 'https://your-auth-server.com/oauth2/authorization/google',
        kakao: 'https://your-auth-server.com/oauth2/authorization/kakao',
        naver: 'https://your-auth-server.com/oauth2/authorization/naver'
      }
      window.location.href = urls[provider]
    }
  }
}
</script>

<style scoped>
.input { @apply block w-full p-2 border rounded mb-2; }
.btn { @apply py-2 px-4 rounded text-white font-semibold; }
</style>

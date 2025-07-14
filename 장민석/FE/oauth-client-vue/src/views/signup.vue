<template>
  <div class="max-w-md mx-auto mt-10 p-6 bg-white rounded shadow space-y-4">
    <h2 class="text-2xl font-bold mb-4 text-center">회원가입</h2>
    <div class="space-y-2">
      <button @click="social('google')" class="btn bg-red-500 hover:bg-red-600 w-full">구글로 가입</button>
      <button @click="social('kakao')" class="btn bg-yellow-400 hover:bg-yellow-500 w-full text-black">카카오로 가입</button>
      <button @click="social('naver')" class="btn bg-green-600 hover:bg-green-700 w-full">네이버로 가입</button>
    </div>
    <div class="mt-6 text-center">
      <span class="text-gray-500">이미 계정이 있으신가요?</span>
      <button @click="goLogin" class="text-blue-500 underline ml-2">로그인</button>
    </div>
  </div>
</template>

<script>
export default {
  methods: {
    social(provider) {
      const urls = {
        google: 'https://your-auth-server.com/oauth2/authorization/google',
        kakao: 'https://your-auth-server.com/oauth2/authorization/kakao',
        naver: 'https://your-auth-server.com/oauth2/authorization/naver'
      }
      window.location.href = urls[provider]
    },
    goLogin() {
      this.$router.push('/login')
    }
  }
}
</script>

<style scoped>
.btn { @apply py-2 px-4 rounded text-white font-semibold; }
</style>

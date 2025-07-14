<template>
  <div class="max-w-md mx-auto mt-10 p-6 bg-white rounded shadow">
    <h2 class="text-2xl font-bold mb-4 text-center">마이페이지</h2>
    <div v-if="user">
      <p><span class="font-semibold">이메일:</span> {{ user.email }}</p>
      <!-- 추가 정보 필요시 여기에 표시 -->
      <button @click="logout" class="btn bg-gray-500 hover:bg-gray-600 w-full mt-4">로그아웃</button>
    </div>
    <div v-else>
      <p>로그인이 필요합니다.</p>
      <router-link to="/login" class="text-blue-500 mt-2 block text-center">로그인</router-link>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return { user: null }
  },
  mounted() {
    // 예시: localStorage에서 사용자 정보 불러오기
    const user = JSON.parse(localStorage.getItem('user'))
    if (user) this.user = user
  },
  methods: {
    logout() {
      localStorage.removeItem('user')
      localStorage.removeItem('access_token')
      this.$router.push('/login')
    }
  }
}
</script>

<style scoped>
.btn { @apply py-2 px-4 rounded text-white font-semibold; }
</style>

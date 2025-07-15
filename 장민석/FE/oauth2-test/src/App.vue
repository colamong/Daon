<template>
  <div>
    <MainPage
      v-if="page === 'main'"
      @goSignup="page = 'signup'"
      @loginSuccess="handleLoginSuccess"
    />
    <SignupPage
      v-if="page === 'signup'"
      @goMain="page = 'main'"
      @signupSuccess="handleLoginSuccess"
    />
    <MyPage
      v-if="page === 'mypage'"
      :user="user"
    />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import MainPage from './components/MainPage.vue'
import SignupPage from './components/SignUp.vue'
import MyPage from './components/MyPage.vue'

const page = ref('main')
const user = ref(null)

function handleLoginSuccess(userInfo) {
  user.value = userInfo
  page.value = 'mypage'
}
</script>


<style scoped>
.logo {
  height: 6em;
  padding: 1.5em;
  will-change: filter;
  transition: filter 300ms;
}
.logo:hover {
  filter: drop-shadow(0 0 2em #646cffaa);
}
.logo.vue:hover {
  filter: drop-shadow(0 0 2em #42b883aa);
}
</style>

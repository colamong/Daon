<template>
  <div class="container">
    <div class="left">
      <h1>회원가입</h1>
    </div>
    <div class="right">
      <button @click="signup('google')">구글로 회원가입</button>
      <button @click="signup('naver')">네이버로 회원가입</button>
      <button @click="signup('kakao')">카카오로 회원가입</button>
      <p class="switch">
        이미 계정이 있으신가요?
        <a href="#" @click.prevent="$emit('goMain')">로그인</a>
      </p>
    </div>
  </div>
</template>

<script>
export default {
  name: 'SignupPage',
  methods: {
    signup(provider) {
      alert(`${provider}로 회원가입`);
    }
  }
}
</script>

<style scoped>
.container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100vh;
  padding: 20px;
}
.left {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
}
.right {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 10px;
  justify-content: center;
  align-items: center;
}
.switch {
  margin-top: 20px;
}
.switch a {
  color: blue;
  cursor: pointer;
  text-decoration: underline;
}
</style>

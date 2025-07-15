<template>
  <div class="container">
    <div class="left">
      <h1>어서오세요! 회원님</h1>
    </div>
    <div class="right">
      <div v-if="user">
        <p><strong>Provider:</strong> {{ user.provider }}</p>
        <p><strong>Name:</strong> {{ user.name }}</p>
        <p><strong>Email:</strong> {{ user.email }}</p>
      </div>
      <div v-else>
        <p>사용자 정보가 없습니다.</p>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'MyPage',
  props: {
    user: Object
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
  align-items: flex-start;
  padding: 20px;
}
</style>

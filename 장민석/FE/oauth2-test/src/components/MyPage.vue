<template>
  <div class="mypage-container" v-if="user">
    <div class="mypage-title">내 정보</div>
    <img :src="user.picture" alt="Profile Picture" class="profile-pic" v-if="user.picture" />
    <div class="user-info">
      <p v-if="user.name"><strong>이름:</strong> {{ user.name }}</p>
      <p v-if="user.email"><strong>이메일:</strong> {{ user.email }}</p>
    </div>

    <div class="all-claims">
      <h3>전체 사용자 정보 (Claims)</h3>
      <pre>{{ formattedClaims }}</pre>
    </div>

    <button class="logout-btn" @click="logout">로그아웃</button>
  </div>

  <div class="mypage-container" v-else>
    <div class="mypage-title">로딩 중...</div>
  </div>
</template>

<script>
export default {
  name: "MyPage",
  data() {
    return {
      user: null,
    };
  },
  computed: {
    formattedClaims() {
      return JSON.stringify(this.user, null, 2);
    },
  },
  methods: {
    fetchUserInfo() {
      fetch("/mypage")
        .then((res) => {
          if (!res.ok) throw new Error("로그인 필요");
          return res.json();
        })
        .then((data) => {
          this.user = data;
        })
        .catch((err) => {
          console.error(err);
          this.$router.push("/login");
        });
    },
    logout() {
      window.location.href = "/logout";
    },
  },
  mounted() {
    this.fetchUserInfo();
  },
};
</script>

<style scoped>
.mypage-container {
  width: 340px;
  margin: 100px auto;
  padding: 32px 24px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.07);
  text-align: center;
}
.mypage-title {
  font-size: 1.3rem;
  font-weight: bold;
  margin-bottom: 1.5em;
}
.profile-pic {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  margin-bottom: 1em;
  object-fit: cover;
}
.user-info {
  font-size: 1rem;
  text-align: left;
  margin-bottom: 1.5em;
}
.user-info p {
  margin: 0.5em 0;
}
.all-claims {
  text-align: left;
  background: #f9f9f9;
  padding: 12px;
  border-radius: 5px;
  margin-bottom: 1.5em;
  font-size: 0.9rem;
  overflow-x: auto;
}
.logout-btn {
  width: 100%;
  height: 44px;
  background: #ddd;
  border: none;
  border-radius: 5px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.2s;
}
.logout-btn:hover {
  background: #ccc;
}
</style>

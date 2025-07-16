<template>
  <div class="login-container">
    <div class="login-title">로그인</div>
    <button class="login-btn btn-google" @click="onLogin('google')">
      <img src="https://developers.google.com/identity/images/g-logo.png" alt="Google" /> 구글로 계속하기
    </button>
    <button class="login-btn btn-naver" @click="onLogin('naver')">
      <img
        src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRCXfkDFiHLEEz_rTKknTtHibQriD3sy25ML-fxrz3Gxc8N5ZB5XVhVsL4wCwgYceg8O1g&usqp=CAU"
        alt="Naver"
      />
      네이버로 계속하기
    </button>
    <button class="login-btn btn-kakao" @click="onLogin('kakao')">
      <img
        src="https://developers.kakao.com/assets/img/about/logos/kakaotalksharing/kakaotalk_sharing_btn_small.png"
        alt="Kakao"
      />
      카카오로 계속하기
    </button>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "Login",
  data() {
    return {
      userInfo: null,
    };
  },
  methods: {
    onLogin(provider) {
      if (provider === "google") {
        // 구글 OAuth2 로그인 엔드포인트로 리디렉트
        window.location.href = "http://localhost:8080/oauth2/authorization/google";
      } else if (provider === "kakao") {
        window.location.href = "http://localhost:8080/oauth2/authorization/kakao";
      } else {
        alert(`${provider} 로그인은 아직 지원하지 않습니다.`);
      }
    },
    fetchUserInfo() {
      // 로그인 후 세션 쿠키를 포함해 사용자 정보 가져오기
      axios
        .get("http://localhost:8080/mypage", {
          withCredentials: true,
        })
        .then((res) => {
          this.userInfo = res.data;
          console.log("사용자 정보:", this.userInfo);
        })
        .catch((err) => {
          console.error("사용자 정보 가져오기 실패:", err);
        });
    },
  },
  mounted() {
    // 페이지가 로드될 때 자동으로 사용자 정보 요청 (선택)
    // this.fetchUserInfo();
  },
};
</script>

<style scoped>
.login-container {
  width: 340px;
  margin: 100px auto;
  padding: 32px 24px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.07);
  text-align: center;
}
.login-title {
  font-size: 1.3rem;
  font-weight: bold;
  margin-bottom: 1.5em;
}
.login-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 44px;
  margin-bottom: 12px;
  border: none;
  border-radius: 5px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.2s;
}
.btn-google {
  background: #fff;
  color: #222;
  border: 1px solid #ddd;
}
.btn-naver {
  background: #19ce60;
  color: #fff;
}
.btn-kakao {
  background: #fee500;
  color: #391b1b;
}
.login-btn img {
  height: 24px;
  margin-right: 10px;
}
.login-btn:last-child {
  margin-bottom: 0;
}
</style>

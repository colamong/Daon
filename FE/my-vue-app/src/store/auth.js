import { defineStore } from "pinia";
import apiClient from '@/utils/axios.js';

export const useAuthStore = defineStore("auth", {
  state: () => ({
    // 새로고침 시 localStorage에서 가져오기
    token: localStorage.getItem("auth_token") || "",
    user: JSON.parse(localStorage.getItem("auth_user") || "null"),
  }),
  getters: {
    isAuthenticated: (s) => !!s.token || !!s.user,
  },
  actions: {
    async signup({ nickname, email, password, nationCode }) {
      try {
        const response = await apiClient.post(`/api/user/signup`, {
          nickname,
          email,
          password,
          nationCode
        });
        
        // 회원가입 성공 시 별도 토큰 반환 없음
        // 회원가입 후 로그인을 유도하거나, 임시 처리
        console.log('회원가입 성공:', response.data);
        
        return response.data;
      } catch (error) {
        if (error.code === 'ECONNREFUSED' || error.code === 'ERR_NETWORK') {
          throw new Error('백엔드 서버에 연결할 수 없습니다. 서버가 실행 중인지 확인해주세요.');
        }
        
        if (error.response) {
          // 백엔드에서 오는 구체적인 오류 처리
          const status = error.response.status;
          const message = error.response.data?.message || error.response.data?.error;
          
          if (status === 409) {
            throw new Error(message || '이미 사용 중인 이메일입니다.');
          } else if (status === 400) {
            throw new Error(message || '입력 정보가 올바르지 않습니다.');
          } else if (status === 422) {
            throw new Error(message || '입력 형식이 올바르지 않습니다.');
          } else {
            throw new Error(message || '회원가입 중 오류가 발생했습니다.');
          }
        }
        
        throw new Error('네트워크 오류가 발생했습니다.');
      }
    },

    async login({ email, password }) {
      try {
        const response = await apiClient.post(`/api/user/signin`, {
          email,
          password
        });
        
        // JWT 토큰은 쿠키로 설정됨, 별도 토큰 처리 필요 없음
        // 사용자 정보는 /api/user/me에서 가져와야 함
        console.log('로그인 성공:', response.data);
        
        // 임시로 localStorage에서 토큰 정보 설정 (쿠키 사용 시에는 불필요할 수 있음)
        this.token = "cookie-based-auth";
        localStorage.setItem("auth_token", "cookie-based-auth");
        
        return response.data;
      } catch (error) {
        if (error.code === 'ECONNREFUSED' || error.code === 'ERR_NETWORK') {
          throw new Error('백엔드 서버에 연결할 수 없습니다. 서버가 실행 중인지 확인해주세요.');
        }
        
        if (error.response) {
          const message = error.response.data?.message || '로그인 중 오류가 발생했습니다.';
          throw new Error(message);
        }
        
        throw new Error('네트워크 오류가 발생했습니다.');
      }
    },

    async getCurrentUser() {
      try {
        const response = await apiClient.get(`/api/user/me`);
        
        this.user = response.data;
        localStorage.setItem("auth_user", JSON.stringify(response.data));
        
        return response.data;
      } catch (error) {
        if (error.response?.status === 401) {
          this.logout();
          throw new Error('인증이 만료되었습니다. 다시 로그인해주세요.');
        }
        throw new Error('사용자 정보를 가져오는 중 오류가 발생했습니다.');
      }
    },

    logout() {
      this.token = "";
      this.user = null;
      localStorage.removeItem("auth_token");
      localStorage.removeItem("auth_user");
    },

    async checkAuthStatus() {
      try {
        // 쿠키에 토큰이 있는지 확인하기 위해 /api/user/me 호출
        const response = await apiClient.get(`/api/user/me`);
        
        this.user = response.data;
        this.token = "cookie-based-auth"; // 쿠키 기반 인증 표시
        localStorage.setItem("auth_user", JSON.stringify(response.data));
        localStorage.setItem("auth_token", "cookie-based-auth");
        
        return true;
      } catch (error) {
        // 인증되지 않은 상태
        this.logout();
        return false;
      }
    },

    updateUserProfile(profileData) {
      if (this.user) {
        this.user = { ...this.user, ...profileData };
        localStorage.setItem("auth_user", JSON.stringify(this.user));
      }
    },
  },
});

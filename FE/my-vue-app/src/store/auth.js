import { defineStore } from "pinia";
import apiClient from '@/utils/axios.js';
import { userService } from '@/services/userService.js';
import { nationService } from '@/services/nationService.js';

export const useAuthStore = defineStore("auth", {
  state: () => {
    let user = null;
    try {
      const userData = localStorage.getItem("auth_user");
      if (userData && userData !== "null") {
        user = JSON.parse(userData);
      }
    } catch (e) {
      console.error('localStorage user 파싱 오류:', e);
    }
    
    return {
      // 새로고침 시 localStorage에서 가져오기
      token: localStorage.getItem("auth_token") || "",
      user: user,
      nations: [], // 국가 목록 저장
    };
  },
  getters: {
    isAuthenticated: (s) => !!s.token && !!s.user,
    userNationName: (s) => {
      if (!s.user?.nationCode) return '국가 정보 없음';
      const nation = s.nations.find(n => n.code === s.user.nationCode);
      return nation?.nameKo || s.user.nationCode;
    },
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
        const userData = await userService.getCurrentUser();
        
        this.user = userData;
        localStorage.setItem("auth_user", JSON.stringify(userData));
        
        return userData;
      } catch (error) {
        if (error.message.includes('인증이 만료')) {
          this.logout();
        }
        throw error;
      }
    },

    async updateProfile(profileData) {
      try {
        const result = await userService.updateProfile(profileData);
        
        // 프로필 수정 후 최신 사용자 정보 다시 가져오기
        await this.getCurrentUser();
        
        return result;
      } catch (error) {
        if (error.message.includes('인증이 만료')) {
          this.logout();
        }
        throw error;
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
        // 쿠키에 토큰이 있는지 확인하기 위해 사용자 정보 조회
        const userData = await userService.getCurrentUser();
        
        this.user = userData;
        this.token = "cookie-based-auth"; // 쿠키 기반 인증 표시
        localStorage.setItem("auth_user", JSON.stringify(userData));
        localStorage.setItem("auth_token", "cookie-based-auth");
        
        return true;
      } catch (error) {
        // 인증되지 않은 상태
        this.logout();
        return false;
      }
    },

    async loadNations() {
      if (this.nations.length > 0) return; // 이미 로드했으면 스킵
      
      try {
        this.nations = await nationService.getNations();
        console.log('국가 목록 로드 완료:', this.nations.length, '개국');
      } catch (error) {
        console.error('국가 목록 로드 실패:', error);
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

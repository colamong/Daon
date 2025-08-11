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
      token: localStorage.getItem("auth_token") || "",
      user,
      nations: [],

      // 프로필 이미지 업로드 후 미리보기용 objectURL 보관(메모리 누수 방지용)
      _profilePreviewUrl: null,
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
          nickname, email, password, nationCode
        });
        console.log('회원가입 성공:', response.data);
        return response.data;
      } catch (error) {
        if (error.code === 'ECONNREFUSED' || error.code === 'ERR_NETWORK') {
          throw new Error('백엔드 서버에 연결할 수 없습니다. 서버가 실행 중인지 확인해주세요.');
        }
        if (error.response) {
          const status = error.response.status;
          const message = error.response.data?.message || error.response.data?.error;
          if (status === 409) throw new Error(message || '이미 사용 중인 이메일입니다.');
          if (status === 400) throw new Error(message || '입력 정보가 올바르지 않습니다.');
          if (status === 422) throw new Error(message || '입력 형식이 올바르지 않습니다.');
          throw new Error(message || '회원가입 중 오류가 발생했습니다.');
        }
        throw new Error('네트워크 오류가 발생했습니다.');
      }
    },

    async login({ email, password }) {
      try {
        const response = await apiClient.post(`/api/user/signin`, { email, password });
        console.log('로그인 성공:', response.data);

        // 서버는 쿠키로 인증 유지 → 프론트엔드에선 표식용 토큰만 저장
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

        // 서버가 이미지 URL을 아직 안 준다면, 기존 미리보기 URL은 유지
        const prevImage = this.user?.profileImage || null;

        this.user = { ...userData };
        if (prevImage && !this.user.profileImage) {
          this.user.profileImage = prevImage;
        }

        localStorage.setItem("auth_user", JSON.stringify(this.user));
        return this.user;
      } catch (error) {
        if (error.message?.includes('인증이 만료')) this.logout();
        throw error;
      }
    },

    async updateProfile(profileData) {
      try {
        const result = await userService.updateProfile(profileData);
        await this.getCurrentUser();
        return result;
      } catch (error) {
        if (error.message?.includes('인증이 만료')) this.logout();
        throw error;
      }
    },

    // 프로필 이미지 업로드 & 즉시 미리보기 반영
    async uploadProfileImage(file) {
      await userService.uploadProfileImage(file);

      // 이전 objectURL 정리
      if (this._profilePreviewUrl) {
        try { URL.revokeObjectURL(this._profilePreviewUrl); } catch {}
      }
      this._profilePreviewUrl = URL.createObjectURL(file);

      // 화면 즉시 반영
      if (!this.user) this.user = {};
      this.user.profileImage = this._profilePreviewUrl;

      // 로컬 스토리지에도 반영 (새로고침 시엔 사라질 수 있음: presigned 연동 전이므로 참고용)
      localStorage.setItem("auth_user", JSON.stringify(this.user));
    },

    logout() {
      // objectURL 정리
      if (this._profilePreviewUrl) {
        try { URL.revokeObjectURL(this._profilePreviewUrl); } catch {}
        this._profilePreviewUrl = null;
      }
      this.token = "";
      this.user = null;
      localStorage.removeItem("auth_token");
      localStorage.removeItem("auth_user");
    },

    async checkAuthStatus() {
      try {
        const userData = await userService.getCurrentUser();
        this.user = userData;
        this.token = "cookie-based-auth";
        localStorage.setItem("auth_user", JSON.stringify(this.user));
        localStorage.setItem("auth_token", "cookie-based-auth");
        return true;
      } catch (_) {
        this.logout();
        return false;
      }
    },

    async loadNations() {
      if (this.nations.length > 0) return;
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

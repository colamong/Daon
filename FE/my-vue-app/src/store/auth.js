import { defineStore } from "pinia";

export const useAuthStore = defineStore("auth", {
  state: () => ({
    // 새로고침 시 localStorage에서 가져오기
    token: localStorage.getItem("auth_token") || "",
    user: JSON.parse(localStorage.getItem("auth_user") || "null"),
  }),
  getters: {
    isAuthenticated: (s) => !!s.token,
  },
  actions: {
    async signup({ nickname, email, password, country }) {
      // (여기에 mock or real API 호출)
      const fakeToken = "mock-token-" + Math.random().toString(36).slice(2);
      const fakeUser = { nickname, email, country };

      // 스토어에 저장
      this.token = fakeToken;
      this.user = fakeUser;

      // localStorage에도 저장
      localStorage.setItem("auth_token", fakeToken);
      localStorage.setItem("auth_user", JSON.stringify(fakeUser));

      return Promise.resolve({ token: fakeToken, user: fakeUser });
    },

    async login({ email, password }) {
      const fakeToken = "mock-token-" + Math.random().toString(36).slice(2);
      const fakeUser = { nickname: "MockUser", email, country: "대한민국" };

      this.token = fakeToken;
      this.user = fakeUser;

      localStorage.setItem("auth_token", fakeToken);
      localStorage.setItem("auth_user", JSON.stringify(fakeUser));

      return Promise.resolve({ token: fakeToken, user: fakeUser });
    },

    logout() {
      this.token = "";
      this.user = null;
      localStorage.removeItem("auth_token");
      localStorage.removeItem("auth_user");
    },
  },
});

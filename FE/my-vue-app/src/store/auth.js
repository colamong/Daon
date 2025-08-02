import { defineStore } from "pinia";
import axios from "axios";

export const useAuthStore = defineStore("auth", {
  state: () => ({
    token: "",
    user: null,
  }),
  getters: {
    isAuthenticated: (state) => !!state.token,
  },
  actions: {
    async login({ email, password }) {
      // 실제 API가 없을 땐 mock adapter로 대응
      const { data } = await axios.post("/api/login", { email, password });
      this.token = data.token;
      this.user = data.user;
    },
    async fetchMe() {
      const { data } = await axios.get("/api/me");
      this.user = data.user;
    },
    logout() {
      this.token = "";
      this.user = null;
    },
  },
});

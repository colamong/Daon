// src/store/child.js
import { defineStore } from "pinia";

export const useChildStore = defineStore("child", {
  state: () => ({
    // 자녀 목록 (테스트용으로 기본 빈 배열)
    children: [],
  }),
  actions: {
    // 자녀 한 명 추가
    addChild(child) {
      this.children.push(child);
    },
    // 전체 자녀 목록 설정
    setChildren(list) {
      this.children = list;
    },
  },
});

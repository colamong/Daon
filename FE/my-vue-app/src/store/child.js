// src/store/child.js
import { defineStore } from "pinia";
import { ensureAllChildrenHaveColors } from "@/utils/colorManager.js";

export const useChildStore = defineStore("child", {
  state: () => ({
    // 자녀 목록
    children: [],
    // 현재 선택된 아이의 ID
    selectedChildId: null,
  }),

  getters: {
    // 등록된 아이가 있는지 확인
    hasChildren: (state) => state.children.length > 0,
    
    // 현재 선택된 아이 정보
    selectedChild: (state) => {
      if (!state.selectedChildId) {
        return state.children.length > 0 ? state.children[0] : null;
      }
      return state.children.find(child => child.id === state.selectedChildId) || null;
    },

    // 선택된 아이의 인덱스
    selectedChildIndex: (state) => {
      if (!state.selectedChildId) return 0;
      return state.children.findIndex(child => child.id === state.selectedChildId);
    }
  },

  actions: {
    // 로컬스토리지에서 아이 목록 불러오기
    loadChildren() {
      const children = ensureAllChildrenHaveColors();
      this.children = children;
      
      // 선택된 아이가 없거나 해당 아이가 목록에 없으면 첫 번째 아이 선택
      if (!this.selectedChildId || !this.children.find(child => child.id === this.selectedChildId)) {
        this.selectedChildId = this.children.length > 0 ? this.children[0].id : null;
      }
    },

    // 아이 선택
    selectChild(childId) {
      const child = this.children.find(c => c.id === childId);
      if (child) {
        this.selectedChildId = childId;
        // 선택된 아이 정보를 localStorage에도 저장
        localStorage.setItem('selectedChildId', childId.toString());
      }
    },

    // 자녀 한 명 추가
    addChild(child) {
      this.children.push(child);
      // 첫 번째 아이를 추가하는 경우 자동으로 선택
      if (this.children.length === 1) {
        this.selectedChildId = child.id;
        localStorage.setItem('selectedChildId', child.id.toString());
      }
      this.saveChildrenToStorage();
    },

    // 전체 자녀 목록 설정
    setChildren(list) {
      this.children = list;
      if (this.children.length > 0 && !this.selectedChildId) {
        this.selectedChildId = this.children[0].id;
        localStorage.setItem('selectedChildId', this.children[0].id.toString());
      }
      this.saveChildrenToStorage();
    },

    // 아이 정보 수정
    updateChild(childId, updatedData) {
      const childIndex = this.children.findIndex(c => c.id === childId);
      if (childIndex !== -1) {
        this.children[childIndex] = { ...this.children[childIndex], ...updatedData };
        this.saveChildrenToStorage();
      }
    },

    // 아이 삭제
    removeChild(childId) {
      this.children = this.children.filter(c => c.id !== childId);
      
      // 삭제된 아이가 현재 선택된 아이였다면 다른 아이 선택
      if (this.selectedChildId === childId) {
        this.selectedChildId = this.children.length > 0 ? this.children[0].id : null;
        if (this.selectedChildId) {
          localStorage.setItem('selectedChildId', this.selectedChildId.toString());
        } else {
          localStorage.removeItem('selectedChildId');
        }
      }
      
      this.saveChildrenToStorage();
    },

    // localStorage에 아이 목록 저장
    saveChildrenToStorage() {
      localStorage.setItem('children', JSON.stringify(this.children));
    },

    // localStorage에서 선택된 아이 ID 불러오기
    loadSelectedChildId() {
      const savedId = localStorage.getItem('selectedChildId');
      if (savedId && this.children.find(child => child.id === parseInt(savedId))) {
        this.selectedChildId = parseInt(savedId);
      }
    },

    // 스토어 초기화
    initialize() {
      this.loadChildren();
      this.loadSelectedChildId();
    }
  },
});

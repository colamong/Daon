// src/store/child.js
import { defineStore } from "pinia";
import { childService } from "@/services/childService.js";
import { useAuthStore } from "@/store/auth";
import { assignColorToChild } from "@/utils/colorManager.js";

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
    // API에서 아이 목록 불러오기
    async loadChildren() {
      try {
        const auth = useAuthStore();
        const userId = auth.user?.id;
        
        if (!userId) {
          console.warn('사용자 ID가 없습니다.');
          this.children = [];
          return;
        }

        // API에서 실제 데이터 불러오기
        const response = await childService.getAllChildren(userId);
        // API 응답 데이터를 적절한 형식으로 변환
        const childrenData = response.data || [];
        this.children = childrenData.map((child, index) => {
          return {
            id: child.childId,
            name: child.name,
            birthDate: child.birthDate || child.birth_date,
            gender: child.gender,
            profileImage: child.profileImg || child.profile_img,
            interests: child.registeredInterests || child.interests || [],
            hasTodayDiary: false, // 당일 그림일기 생성 여부 (기본값 false)
            color: null // 먼저 null로 설정
          };
        });
        
        // 모든 아이에게 색상 할당 (서로 다른 색상이 되도록)
        this.children.forEach((child, index) => {
          child.color = assignColorToChild(child.id, this.children);
        });
        
        // 선택된 아이가 없거나 해당 아이가 목록에 없으면 첫 번째 아이 선택
        if (!this.selectedChildId || !this.children.find(child => child.id === this.selectedChildId)) {
          this.selectedChildId = this.children.length > 0 ? this.children[0].id : null;
        }
      } catch (error) {
        console.error('아이 목록 불러오기 실패:', error);
        this.children = [];
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

    // 특정 아이의 당일 그림일기 상태 및 conversationResultId 업데이트
    setChildTodayDiary(childId, hasTodayDiary, conversationResultId = null) {
      const childIndex = this.children.findIndex(c => c.id === childId);
      if (childIndex !== -1) {
        this.children[childIndex].hasTodayDiary = hasTodayDiary;
        console.log(`아이 ${childId}의 당일 그림일기 상태 업데이트:`, hasTodayDiary);
        
        // localStorage에도 저장하여 페이지 새로고침 후에도 유지
        const today = new Date().toDateString();
        const auth = useAuthStore();
        const userId = auth.user?.id || 'anonymous';
        
        const diaryStatusKey = `todayDiary_${userId}_${today}`;
        const conversationIdKey = `todayConversationId_${userId}_${today}`;
        
        const todayDiaryStatus = JSON.parse(localStorage.getItem(diaryStatusKey) || '{}');
        const todayConversationIds = JSON.parse(localStorage.getItem(conversationIdKey) || '{}');
        
        todayDiaryStatus[childId] = hasTodayDiary;
        if (conversationResultId) {
          todayConversationIds[childId] = conversationResultId;
        }
        
        localStorage.setItem(diaryStatusKey, JSON.stringify(todayDiaryStatus));
        localStorage.setItem(conversationIdKey, JSON.stringify(todayConversationIds));
      }
    },

    // 특정 아이의 당일 그림일기 상태 확인
    getChildTodayDiary(childId) {
      // 1순위: localStorage에서 확인 (페이지 새로고침 후에도 유지)
      const today = new Date().toDateString();
      const auth = useAuthStore();
      const userId = auth.user?.id || 'anonymous';
      const diaryStatusKey = `todayDiary_${userId}_${today}`;
      const todayDiaryStatus = JSON.parse(localStorage.getItem(diaryStatusKey) || '{}');
      
      if (todayDiaryStatus.hasOwnProperty(childId)) {
        return todayDiaryStatus[childId];
      }
      
      // 2순위: 메모리에서 확인
      const child = this.children.find(c => c.id === childId);
      return child ? child.hasTodayDiary : false;
    },

    // 특정 아이의 당일 conversationResultId 조회
    getChildTodayConversationId(childId) {
      const today = new Date().toDateString();
      const auth = useAuthStore();
      const userId = auth.user?.id || 'anonymous';
      const conversationIdKey = `todayConversationId_${userId}_${today}`;
      const todayConversationIds = JSON.parse(localStorage.getItem(conversationIdKey) || '{}');
      
      return todayConversationIds[childId] || null;
    },

    // 모든 아이의 당일 그림일기 상태 초기화 (자정에 호출)
    resetAllTodayDiaries() {
      this.children.forEach(child => {
        child.hasTodayDiary = false;
      });
      
      // localStorage의 당일 다이어리 상태 및 conversationId도 정리
      const today = new Date().toDateString();
      const auth = useAuthStore();
      const userId = auth.user?.id || 'anonymous';
      
      const diaryStatusKey = `todayDiary_${userId}_${today}`;
      const conversationIdKey = `todayConversationId_${userId}_${today}`;
      
      localStorage.removeItem(diaryStatusKey);
      localStorage.removeItem(conversationIdKey);
      console.log('당일 그림일기 상태 및 conversationId 초기화 완료');
    },

    // 날짜 변경 체크 및 초기화
    checkAndResetTodayDiaries() {
      const today = new Date().toDateString();
      const auth = useAuthStore();
      const userId = auth.user?.id || 'anonymous';
      const lastResetDateKey = `lastDiaryResetDate_${userId}`;
      const lastResetDate = localStorage.getItem(lastResetDateKey);
      
      if (lastResetDate !== today) {
        // 날짜가 바뀌었으면 초기화
        this.resetAllTodayDiaries();
        localStorage.setItem(lastResetDateKey, today);
      }
    },

    // 스토어 초기화
    async initialize() {
      await this.loadChildren();
      this.loadSelectedChildId();
      
      // 날짜 변경 체크 및 초기화
      this.checkAndResetTodayDiaries();
    }
  },
});

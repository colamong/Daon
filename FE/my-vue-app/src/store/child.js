import { defineStore } from "pinia";
import { childService } from "@/services/childService.js";
import { useAuthStore } from "@/store/auth";
import { assignColorToChild } from "@/utils/colorManager.js";

export const useChildStore = defineStore("child", {
  state: () => ({
    children: [],
    selectedChildId: null,
  }),

  getters: {
    hasChildren: (state) => state.children.length > 0,

    selectedChild: (state) => {
      if (!state.selectedChildId) {
        return state.children.length > 0 ? state.children[0] : null;
      }
      return state.children.find((child) => child.id === state.selectedChildId) || null;
    },

    selectedChildIndex: (state) => {
      if (!state.selectedChildId) return 0;
      return state.children.findIndex((child) => child.id === state.selectedChildId);
    },
  },

  actions: {
    // API에서 아이 목록 불러오기
    async loadChildren() {
      try {
        const auth = useAuthStore();
        const userId = auth.user?.id;

        if (!userId) {
          console.warn("사용자 ID가 없습니다.");
          this.children = [];
          this.selectedChildId = null;
          return;
        }

        // childService.getAllChildren은 payload(Array)를 바로 반환
        const list = await childService.getAllChildren(userId);

        // API 응답 -> 화면에서 쓰기 쉬운 형식으로 변환 (관심사는 별도 API로 가져오기)
        this.children = await Promise.all(
          (Array.isArray(list) ? list : []).map(async (child) => {
            let interests = [];
            try {
              // 부모용 관심사 API 호출
              interests = await childService.getChildInterestsForParent(userId, child.childId);
            } catch (error) {
              console.warn(`아이 ${child.name}의 관심사 로드 실패:`, error);
              interests = [];
            }

            return {
              id: child.childId,                                        // 통일 키
              name: child.name,
              birthDate: child.birthDate ?? child.birth_date ?? null,
              gender: child.gender ?? null,

              // 표시용 presigned URL
              imageUrl: child.imageUrl ?? null,

              // DB에 저장된 원본 S3 key (이미지 유지/수정 시 사용)
              profileImg: child.profileImg ?? null,

              // 기존 컴포넌트 호환
              profileImage: child.imageUrl ?? child.profileImg ?? null,

              interests: interests || [],
              hasTodayDiary: false,
              color: null,
            };
          })
        );

        // 색상 부여 (아이마다 고유색)
        this.children.forEach((c) => {
          c.color = assignColorToChild(c.id, this.children);
        });

        // 선택된 아이가 없거나 목록에 없으면 첫 번째 아이 선택
        if (!this.selectedChildId || !this.children.find((c) => c.id === this.selectedChildId)) {
          this.selectedChildId = this.children.length > 0 ? this.children[0].id : null;
        }
      } catch (error) {
        console.error("아이 목록 불러오기 실패:", error);
        this.children = [];
        this.selectedChildId = null;
      }
    },

    // 아이 선택
    selectChild(childId) {
      const child = this.children.find((c) => c.id === childId);
      if (child) {
        this.selectedChildId = childId;
        localStorage.setItem("selectedChildId", childId.toString());
      }
    },

    // 자녀 한 명 추가
    addChild(child) {
      this.children.push(child);
      if (this.children.length === 1) {
        this.selectedChildId = child.id;
        localStorage.setItem("selectedChildId", child.id.toString());
      }
      this.saveChildrenToStorage();
    },

    // 전체 자녀 목록 설정
    setChildren(list) {
      this.children = list;
      if (this.children.length > 0 && !this.selectedChildId) {
        this.selectedChildId = this.children[0].id;
        localStorage.setItem("selectedChildId", this.children[0].id.toString());
      }
      this.saveChildrenToStorage();
    },

    // 아이 정보 수정 (로컬 상태만)
    updateChild(childId, updatedData) {
      const idx = this.children.findIndex((c) => c.id === childId);
      if (idx !== -1) {
        this.children[idx] = { ...this.children[idx], ...updatedData };
        this.saveChildrenToStorage();
      }
    },

    // 아이 삭제
    removeChild(childId) {
      this.children = this.children.filter((c) => c.id !== childId);

      if (this.selectedChildId === childId) {
        this.selectedChildId = this.children.length > 0 ? this.children[0].id : null;
        if (this.selectedChildId) {
          localStorage.setItem("selectedChildId", this.selectedChildId.toString());
        } else {
          localStorage.removeItem("selectedChildId");
        }
      }

      this.saveChildrenToStorage();
    },

    // localStorage에 아이 목록 저장
    saveChildrenToStorage() {
      // presigned URL은 만료되니 참고 용도로만 저장
      localStorage.setItem("children", JSON.stringify(this.children));
    },

    // localStorage에서 선택된 아이 ID 불러오기
    loadSelectedChildId() {
      const savedId = localStorage.getItem("selectedChildId");
      if (savedId && this.children.find((child) => child.id === parseInt(savedId))) {
        this.selectedChildId = parseInt(savedId);
      }
    },

    // 특정 아이의 당일 그림일기 상태 및 conversationResultId 업데이트
    setChildTodayDiary(childId, hasTodayDiary, conversationResultId = null) {
      const idx = this.children.findIndex((c) => c.id === childId);
      if (idx !== -1) {
        this.children[idx].hasTodayDiary = hasTodayDiary;
        console.log(`아이 ${childId}의 당일 그림일기 상태 업데이트:`, hasTodayDiary);

        const today = new Date().toDateString();
        const auth = useAuthStore();
        const userId = auth.user?.id || "anonymous";

        const diaryStatusKey = `todayDiary_${userId}_${today}`;
        const conversationIdKey = `todayConversationId_${userId}_${today}`;

        const todayDiaryStatus = JSON.parse(localStorage.getItem(diaryStatusKey) || "{}");
        const todayConversationIds = JSON.parse(localStorage.getItem(conversationIdKey) || "{}");

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
      const today = new Date().toDateString();
      const auth = useAuthStore();
      const userId = auth.user?.id || "anonymous";
      const diaryStatusKey = `todayDiary_${userId}_${today}`;
      const todayDiaryStatus = JSON.parse(localStorage.getItem(diaryStatusKey) || "{}");

      if (Object.prototype.hasOwnProperty.call(todayDiaryStatus, childId)) {
        return todayDiaryStatus[childId];
      }
      const child = this.children.find((c) => c.id === childId);
      return child ? child.hasTodayDiary : false;
    },

    // 특정 아이의 당일 conversationResultId 조회
    getChildTodayConversationId(childId) {
      const today = new Date().toDateString();
      const auth = useAuthStore();
      const userId = auth.user?.id || "anonymous";
      const conversationIdKey = `todayConversationId_${userId}_${today}`;
      const todayConversationIds = JSON.parse(localStorage.getItem(conversationIdKey) || "{}");

      return todayConversationIds[childId] || null;
    },

    // 모든 아이의 당일 그림일기 상태 초기화 (자정에 호출)
    resetAllTodayDiaries() {
      this.children.forEach((child) => {
        child.hasTodayDiary = false;
      });

      const today = new Date().toDateString();
      const auth = useAuthStore();
      const userId = auth.user?.id || "anonymous";

      const diaryStatusKey = `todayDiary_${userId}_${today}`;
      const conversationIdKey = `todayConversationId_${userId}_${today}`;

      localStorage.removeItem(diaryStatusKey);
      localStorage.removeItem(conversationIdKey);
      console.log("당일 그림일기 상태 및 conversationId 초기화 완료");
    },

    // 날짜 변경 체크 및 초기화
    checkAndResetTodayDiaries() {
      const today = new Date().toDateString();
      const auth = useAuthStore();
      const userId = auth.user?.id || "anonymous";
      const lastResetDateKey = `lastDiaryResetDate_${userId}`;
      const lastResetDate = localStorage.getItem(lastResetDateKey);

      if (lastResetDate !== today) {
        this.resetAllTodayDiaries();
        localStorage.setItem(lastResetDateKey, today);
      }
    },

    // 스토어 초기화
    async initialize() {
      await this.loadChildren();
      this.loadSelectedChildId();
      this.checkAndResetTodayDiaries();
    },
  },
});

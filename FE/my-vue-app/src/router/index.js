import { createRouter, createWebHistory } from "vue-router";
import { useAuthStore } from "@/store/auth";
import audioManager from "@/utils/audioManager";

// 레이아웃
import ModalDemo from "@/views/ModalDemo.vue";
import BaseAuthLayout from "@/components/layout/BaseAuthLayout.vue";
import BaseAppLayout from "@/components/layout/BaseAppLayout.vue";

// 뷰 컴포넌트
import LandingPage from "@/views/LandingPage.vue";
import Login from "@/views/Login.vue";
import SignUp from "@/views/SignUp.vue";
import Dashboard from "@/views/Dashboard.vue";
import Home from "@/views/Home.vue";
// ocr
import OCRTool from "@/views/OCRTool.vue";
import OCRResult from "@/views/OCRResult.vue";
// 커뮤니티
import CommunityList from "@/views/CommunityList.vue";
import CommunityChat from "@/views/CommunityChat.vue";

// 상황별 학습
import ThemeSelect from "@/views/ThemeSelect.vue";
import ChapterSelect from "@/views/ChapterSelect.vue";
import LearningPage from "@/views/LearningPage.vue";
import RegisterChild from "@/views/RegisterChild.vue";
import EditChild from "@/views/EditChild.vue";
import ChildProfile from "@/views/ChildProfile.vue";
import Growth from "@/views/Growth.vue";

// 프로필
import ProfileEdit from "@/views/ProfileEdit.vue";

// 아이 활동 페이지
import ChildMain from "@/views/ChildMain.vue";
import ChildPet from "@/views/ChildPet.vue";
import ChildDrawing from "@/views/ChildDrawing.vue";

const routes = [
  // 1) 비로그인 메인
  {
    path: "/",
    name: "Landing",
    component: LandingPage,
    meta: { requiresAuth: false },
  },

  // 2) 인증 전용
  {
    path: "/auth",
    component: BaseAuthLayout,
    children: [
      { path: "login", name: "login", component: Login },
      { path: "signup", name: "signup", component: SignUp },
    ],
  },

  // 3) 헤더·푸터 포함
  {
    path: "/dashboard",
    component: BaseAppLayout,
    meta: { requiresAuth: true },
    children: [
      { path: "", name: "Dashboard", component: Dashboard },
      // 프로필 수정 (직접 접근용)
      {
        path: "/profile/edit",
        name: "ProfileEditDirect",
        component: ProfileEdit,
        meta: { requiresAuth: true },
      },
      { path: "home", name: "Home", component: Home },
      { path: "ocr", name: "OCRTool", component: OCRTool },
      { path: "ocr/result", name: "OCRResult", component: OCRResult },
      { path: "community", name: "Community", component: CommunityList },
      {
        // id를 동적 세그먼트로 바꿔주고 props: true
        path: "community/:id",
        name: "CommunityChat",
        component: CommunityChat,
        props: true,
      },
      { path: "learning", name: "LearningHelper", component: ThemeSelect },
      { path: "learning/theme/:id", name: "ChapterSelect", component: ChapterSelect, props: true },
      { path: "learning/theme/:themeId/chapter/:chapterId", name: "LearningPage", component: LearningPage, props: true },
      { path: "learning/theme/:themeId/chapter/:chapterId/question/:questionId", name: "LearningQuestion", component: LearningPage, props: true },
      {
        path: "child/register",
        name: "RegisterChild",
        component: RegisterChild,
      },
      { path: "child/profile", name: "ChildProfile", component: ChildProfile },
      { path: "child/edit", name: "EditChild", component: EditChild },
      { path: "growth", name: "Growth", component: Growth },
      { path: "profile/edit", name: "ProfileEdit", component: ProfileEdit },
    ],
  },

  // 4) 아이 전용 (헤더·푸터 없이)
  { path: "/child/:childId?", name: "ChildMain", component: ChildMain, props: true },
  { path: "/child/pet/:childId?", name: "ChildPet", component: ChildPet, props: true },
  { path: "/child/drawing/:childId?", name: "ChildDrawing", component: ChildDrawing, props: true },

  // 모달창 test
  {
    path: "/modal-demo",
    name: "ModalDemo",
    component: ModalDemo,
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    // 뒤로가기/앞으로가기 시 저장된 위치가 있으면 그 위치로
    if (savedPosition) {
      return savedPosition;
    }
    // 아니면 항상 상단으로
    return { top: 0 };
  },
});

router.beforeEach((to, from, next) => {
  const auth = useAuthStore();
  
  // 페이지 이동 시 모든 BGM 강제 정지 (오디오 겹침 방지)
  if (from.name && (from.name === 'ChildMain' || from.name === 'ChildDrawing')) {
    audioManager.stopAllBGM();
  }
  
  // 인증이 필요한 페이지인데 로그인하지 않은 경우
  if (to.meta.requiresAuth && !auth.isAuthenticated) {
    return next({ name: "Landing" });
  }
  
  // 이미 로그인한 사용자가 랜딩 페이지 접근 시 대시보드로 이동
  if (to.name === "Landing" && auth.isAuthenticated) {
    return next({ name: "Dashboard" });
  }
  
  next();
});

export default router;

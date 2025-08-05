import { createRouter, createWebHistory } from "vue-router";
import { useAuthStore } from "@/store/auth";

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
import RegisterChild from "@/views/RegisterChild.vue";
import EditChild from "@/views/EditChild.vue";
import ChildProfile from "@/views/ChildProfile.vue";

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
      {
        path: "child/register",
        name: "RegisterChild",
        component: RegisterChild,
      },
      { path: "child/profile", name: "ChildProfile", component: ChildProfile },
      { path: "child/edit", name: "EditChild", component: EditChild },
      { path: "profile/edit", name: "ProfileEdit", component: ProfileEdit },
    ],
  },

  // 4) 아이 전용 (헤더·푸터 없이)
  { path: "/child", name: "ChildMain", component: ChildMain },
  { path: "/child/pet", name: "ChildPet", component: ChildPet },
  { path: "/child/drawing", name: "ChildDrawing", component: ChildDrawing },

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
});

router.beforeEach((to, from, next) => {
  const auth = useAuthStore();
  if (to.meta.requiresAuth && !auth.isAuthenticated) {
    return next({ name: "Landing" });
  }
  if (to.name === "Landing" && auth.isAuthenticated) {
    return next({ name: "Dashboard" });
  }
  next();
});

export default router;

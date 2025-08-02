import { createRouter, createWebHistory } from "vue-router";
// 비로그인 시 메인
import LandingPage from "@/views/LandingPage.vue";
import Dashboard from "@/views/Dashboard.vue";
import { useAuthStore } from "@/store/auth";

//레이아웃
import BaseAppLayout from "@/components/layout/BaseAppLayout.vue";
import BaseAuthLayout from "@/components/layout/BaseAuthLayout.vue";

// 일반 페이지(헤더/푸터 있는 페이지)
import Home from "@/views/Home.vue";

// child 페이지 (헤더/푸터 없는 단독)
import ChildMain from "@/views/ChildMain.vue";
import ChildPet from "@/views/ChildPet.vue";
import ChildDrawing from "@/views/ChildDrawing.vue";

// auth 페이지 (헤더/푸터 없는 인증 전용)
const Login = () => import("@/views/Login.vue");
const SignUp = () => import("@/views/SignUp.vue");
const routes = [
  {
    path: "/",
    name: "Landing",
    component: LandingPage,
    meta: { requiresAuth: false },
  },
  {
    path: "/dashboard",
    name: "Dashboard",
    component: Dashboard,
    meta: { requiresAuth: true },
  },
  // BaseAppLayout 적용 (헤더/푸터 포함)
  {
    path: "/",
    component: BaseAppLayout,
    children: [
      {
        path: "",
        name: "Home",
        component: Home,
      },
      // 이 외에 Header/Footer 있는 페이지들 추가 가능
    ],
  },

  // BaseAuthLayout 적용 (로그인/회원가입 등 풀스크린 전용)
  {
    path: "/auth",
    component: BaseAuthLayout,
    children: [
      {
        path: "login",
        name: "login",
        component: Login,
      },
      {
        path: "signup",
        name: "signup",
        component: SignUp,
      },
    ],
  },

  // 아이 페이지 관련 router(header/footer 없는 페이지 - 단독 컴포넌트 렌더링)
  // 메인
  {
    path: "/child",
    name: "ChildMain",
    component: ChildMain,
  },
  // 펭귄과 대화
  {
    path: "/child/pet",
    name: "ChildPet",
    component: ChildPet,
  },
  // 캘린더
  {
    path: "/child/drawing",
    name: "ChildDrawing",
    component: ChildDrawing,
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

// mock 사용으로 인한 전역 가드
router.beforeEach((to, from, next) => {
  const auth = useAuthStore();
  const needsAuth = to.meta.requiresAuth;

  if (needsAuth && !auth.isAuthenticated) {
    return next({ name: "Landing" });
  }
  if (!needsAuth && auth.isAuthenticated && to.name === "Landing") {
    return next({ name: "Dashboard" });
  }
  next();
});

export default router;

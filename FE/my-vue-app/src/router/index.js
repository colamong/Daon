import { createRouter, createWebHistory } from "vue-router";
import ChildMain from "../views/ChildMain.vue";
import ChildPet from "../views/ChildPet.vue";
import ChildDrawing from "../views/ChildDrawing.vue";

const routes = [
  {
    path: "/",
    name: "Home",
    component: () => import("../views/Home.vue"),
  },

  // 아이 페이지 관련 router
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

export default router;

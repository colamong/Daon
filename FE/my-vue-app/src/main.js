import "./assets/styles/tailwind.css";
import "./assets/styles/child_page.css";

import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";
import { createPinia } from "pinia";

if (import.meta.env.DEV) {
  // 개발 시에만 mock 활성화
  import("./mock/axios-mock");
}

const app = createApp(App);
app.use(router);
app.use(createPinia());
app.mount("#app");

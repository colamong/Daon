import "./assets/styles/tailwind.css";
import "./assets/styles/child_page.css";

import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";
import { createPinia } from "pinia";

// Mock 비활성화 - 실제 백엔드 API 사용
// if (import.meta.env.DEV) {
//   import("./mock/axios-mock");
// }

const app = createApp(App);
app.use(router);
app.use(createPinia());
app.mount("#app");

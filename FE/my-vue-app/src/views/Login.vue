<template>
  <form
    @submit.prevent="handleLogin"
    class="mx-auto w-full max-w-lg space-y-6 bg-white rounded-2xl shadow-lg p-8"
  >
    <!-- 로고 + 제목 -->
    <div class="flex flex-col items-center space-y-2">
      <div class="w-20 h-20 bg-gray-300 rounded-lg"></div>
      <h2 class="text-2xl font-semibold text-black font-paper">로그인</h2>
      <p class="text-xs text-gray-500 font-paper">
        Get started with our app, just login to enjoy the experience.
      </p>
    </div>

    <!-- 이메일 -->
    <div>
      <label for="email" class="block text-xs text-black mb-1 font-paperBold">
        이메일
      </label>
      <div
        class="flex items-center border border-gray-200 rounded-lg overflow-hidden font-paper"
      >
        <div class="px-3">
          <img
            src="@/assets/icons/Email.svg"
            alt="이메일 아이콘"
            class="w-5 h-5"
          />
        </div>
        <input
          id="email"
          v-model="email"
          type="email"
          placeholder="daon@email.com"
          required
          class="flex-1 py-2 pr-3 text-sm focus:outline-none"
        />
      </div>
    </div>

    <!-- 비밀번호 -->
    <div>
      <label
        for="password"
        class="block text-xs text-black mb-1 font-paperBold"
      >
        비밀번호
      </label>
      <div
        class="flex items-center border border-gray-200 rounded-lg overflow-hidden font-paper"
      >
        <div class="px-3">
          <img
            src="@/assets/icons/password.svg"
            alt="비밀번호 아이콘"
            class="w-5 h-5"
          />
        </div>
        <input
          id="password"
          v-model="password"
          type="password"
          placeholder="Password"
          required
          class="flex-1 py-2 pr-3 text-sm focus:outline-none"
        />
      </div>
    </div>

    <!-- 에러 메시지 -->
    <p v-if="error" class="text-sm text-red-500 font-paper">
      {{ error }}
    </p>

    <!-- 로그인 버튼 -->
    <button
      type="submit"
      class="w-full h-11 bg-blue-500 hover:bg-blue-600 text-white text-sm font-paper font-medium rounded-lg transition"
    >
      로그인
    </button>

    <!-- OR 구분선 -->
    <div class="flex items-center text-xs text-gray-500">
      <hr class="flex-grow border-t border-gray-200" />
      <span class="px-2 font-paper">or</span>
      <hr class="flex-grow border-t border-gray-200" />
    </div>

    <!-- 구글 로그인 -->
    <button
      type="button"
      @click="handleGoogleLogin"
      class="w-full h-11 flex items-center justify-center gap-2 border border-gray-200 rounded-lg hover:bg-gray-100 transition text-sm font-paper"
    >
      <img src="@/assets/icons/google.svg" alt="구글 아이콘" class="w-5 h-5" />
      구글 로그인 하기
    </button>

    <!-- 회원가입 링크 -->
    <p class="text-xs text-gray-500 text-center font-paper">
      계정이 아직 없으신가요?
      <router-link
        :to="{ name: 'signup' }"
        class="text-blue-500 hover:underline"
      >
        회원가입 하러 가기
      </router-link>
    </p>
  </form>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/store/auth";
import SignUp from "./SignUp.vue";

const router = useRouter();
const auth = useAuthStore();

const email = ref("");
const password = ref("");
const error = ref("");

/**
 * handleLogin
 * - 이메일/비밀번호가 비어있으면 에러
 * - 0.5초 더미 딜레이 후 mock-token 발행
 * - 실제 auth.login 액션이 있다면 호출, 아니면 localStorage에 저장
 * - 로그인 성공 시 Dashboard로 이동
 */
async function handleLogin() {
  if (!email.value || !password.value) {
    error.value = "이메일과 비밀번호를 모두 입력해주세요.";
    return;
  }
  error.value = "";
  try {
    // 더미 API 콜 시뮬레이션
    await new Promise((resolve) => setTimeout(resolve, 500));

    // mock-token 생성
    const mockToken = "mock-token";
    if (auth.login) {
      // 향후 실제 백엔드 연결 시 auth.login() 사용
      await auth.login({ email: email.value, password: password.value });
    } else {
      // 임시 토큰 저장
      localStorage.setItem("token", mockToken);
      // Pinia store에 토큰 반영 (필요하다면 store 내부 구현에 맞게 수정)
      auth.token = mockToken;
    }

    router.push({ name: "Dashboard" });
  } catch {
    error.value = "로그인에 실패했습니다.";
  }
}

/** 구글 로그인은 아직 미지원 알림 */
function handleGoogleLogin() {
  alert("구글 로그인은 아직 지원되지 않습니다.");
}
</script>

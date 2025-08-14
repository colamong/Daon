<template>
  <form
    @submit.prevent="handleLogin"
    class="mx-auto w-full max-w-lg space-y-6 bg-white rounded-2xl shadow-lg p-8"
  >
    <!-- 로고 + 제목 -->
    <div class="flex flex-col items-center space-y-2">
      <img 
        :src="loginImage" 
        alt="로그인 이미지" 
        class="w-20 h-20 object-contain"
      />
      <h2 class="text-2xl font-semibold text-black font-paper">로그인</h2>
    </div>

    <!-- 이메일 -->
    <div class="space-y-2">
      <label for="emailLocal" class="block text-xs text-black mb-1 font-paperBold">
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

        <div class="flex-1 flex items-center">
          <!-- 아이디 -->
          <input
            id="emailLocal"
            v-model.trim="emailLocal"
            type="text"
            placeholder="daon"
            autocomplete="username"
            class="w-32 py-2 px-1 text-sm focus:outline-none"
          />

          <!-- @ 구분 -->
          <span class="w-8 flex justify-center text-gray-400 select-none">@</span>

          <!-- 도메인 선택 -->
          <select
            v-if="emailDomain !== '직접 입력'"
            v-model="emailDomain"
            class="w-40 py-2 pr-3 text-sm focus:outline-none bg-white"
            aria-label="이메일 도메인 선택"
          >
            <option v-for="d in domains" :key="d" :value="d">{{ d }}</option>
          </select>
          
          <!-- 직접 입력 모드 -->
          <input
            v-else
            v-model="customDomain"
            type="text"
            placeholder="직접 입력"
            required
            class="w-40 py-2 pr-3 text-sm focus:outline-none bg-white"
          />
        </div>
      </div>

      <!-- 조합 미리보기 -->
      <p class="text-xs text-gray-500 font-paper">입력 이메일: {{ fullEmail }}</p>
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
import { ref, computed } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/store/auth";
import { useNotification } from "@/composables/useNotification.js";
import loginImage from "@/assets/images/login.png";

const router = useRouter();
const auth = useAuthStore();
const { showSuccess, showError } = useNotification();

const emailLocal = ref("");
const emailDomain = ref("gmail.com");
const customDomain = ref("");
const password = ref("");
const error = ref("");

const domains = [
  "gmail.com",
  "naver.com",
  "daum.net",
  "yahoo.com",
  "kakao.com",
  "직접 입력"
];

const fullEmail = computed(() => {
  const local = emailLocal.value?.trim() || "";
  const domain = emailDomain.value === "직접 입력" ? customDomain.value : emailDomain.value;
  return local ? `${local}@${domain}` : `@${domain}`;
});

function isValidLocalPart(local) {
  if (!local) return false;
  const re = /^[A-Za-z0-9._%+\-]+$/;
  return re.test(local);
}


async function handleLogin() {
  error.value = "";

  if (!isValidLocalPart(emailLocal.value)) {
    showError("이메일 아이디를 올바르게 입력해주세요. (영문/숫자/._%-+ 가능)", "입력 오류");
    return;
  }
  
  if (emailDomain.value === "직접 입력" && !customDomain.value.trim()) {
    showError("도메인을 입력해주세요.", "입력 오류");
    return;
  }
  
  if (!password.value) {
    showError("비밀번호를 입력해주세요.", "입력 오류");
    return;
  }

  try {
    await auth.login({
      email: fullEmail.value,
      password: password.value
    });

    await auth.getCurrentUser();
    showSuccess("로그인되었습니다!", "환영합니다");
    router.push({ name: "Dashboard" });
  } catch (e) {
    error.value = e?.message || "로그인 중 오류가 발생했습니다.";
    showError(error.value, "로그인 실패");
  }
}
</script>

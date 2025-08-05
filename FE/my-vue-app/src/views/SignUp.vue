<template>
  <form
    @submit.prevent="handleSignUp"
    class="mx-auto w-full max-w-lg space-y-6 bg-white rounded-2xl shadow-lg p-8"
  >
    <!-- 로고 + 제목 -->
    <div class="flex flex-col items-center space-y-2">
      <div class="w-20 h-20 bg-gray-300 rounded-lg"></div>
      <h2 class="text-2xl font-semibold text-black font-paper">회원가입</h2>
    </div>

    <!-- 닉네임 -->
    <div class="font-paperBold">
      <label for="nickname" class="block text-xs text-black mb-1">
        닉네임
      </label>
      <div
        class="flex items-center border border-gray-200 rounded-lg overflow-hidden"
      >
        <div class="px-3">
          <img
            src="@/assets/icons/nick.svg"
            alt="닉네임 아이콘"
            class="w-5 h-5"
          />
        </div>
        <input
          id="nickname"
          v-model="nickname"
          type="text"
          maxlength="10"
          placeholder="1~10글자"
          required
          class="flex-1 py-2 pr-3 text-sm focus:outline-none font-paper"
        />
      </div>
    </div>

    <!-- 아이디(이메일) -->
    <div>
      <label class="block text-xs text-black mb-1 font-paperBold"
        >아이디(이메일)</label
      >
      <div
        class="flex items-center border border-gray-200 rounded-lg overflow-hidden"
      >
        <!-- 2번 아이콘 -->
        <div class="px-3 bg-whi">
          <img
            src="@/assets/icons/Email.svg"
            alt="이메일 아이콘"
            class="w-5 h-5"
          />
        </div>
        <input
          v-model="emailLocal"
          type="text"
          placeholder="Daon"
          required
          class="flex-1 text-sm focus:outline-none font-paper"
        />
        <span class="text-black font-paper">@</span>
        <!-- 도메인 선택 -->
        <select
          v-if="domainOption !== '직접 입력'"
          v-model="domainOption"
          class="flex-1 py-2 pl-2 pr-2 text-sm focus:outline-none font-paper"
        >
          <option disabled value="">도메인 선택</option>
          <option v-for="opt in domainOptions" :key="opt">{{ opt }}</option>
        </select>
        <input
          v-else
          v-model="customDomain"
          type="text"
          placeholder="직접 입력"
          required
          class="flex-1 py-2 pl-3 pr-3 text-sm focus:outline-none"
        />
      </div>
    </div>

    <!-- 비밀번호 -->
    <div>
      <label for="password" class="block text-xs text-black mb-1 font-paperBold"
        >비밀번호</label
      >
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
          class="flex-1 py-2 pr-3 text-sm focus:outline-none font-paper"
        />
      </div>
    </div>

    <!-- 비밀번호 확인 -->
    <div>
      <label for="confirm" class="block text-xs text-black font-paperBold mb-1"
        >비밀번호 확인</label
      >
      <div
        class="flex items-center border border-gray-200 rounded-lg overflow-hidden font-paper"
      >
        <div class="px-3">
          <img
            src="@/assets/icons/password.svg"
            alt="비밀번호 확인 아이콘"
            class="w-5 h-5"
          />
        </div>
        <input
          id="confirm"
          v-model="confirmPassword"
          type="password"
          placeholder="Password"
          required
          class="flex-1 py-2 pr-3 text-sm focus:outline-none"
        />
      </div>
    </div>

    <!-- 국가 선택 -->
    <div>
      <label for="country" class="block text-xs text-black font-paperBold mb-1"
        >국가</label
      >
      <div
        class="flex items-center border border-gray-200 rounded-lg overflow-hidden"
      >
        <!-- 1번 아이콘 -->
        <div class="px-3">
          <img
            src="@/assets/icons/country.svg"
            alt="국가 아이콘"
            class="w-5 h-5"
          />
        </div>
        <select
          id="country"
          v-model="country"
          required
          class="flex-1 py-2 pl-1 pr-3 text-sm focus:outline-none font-paper"
        >
          <option disabled value="">자신의 국가를 선택하세요</option>
          <option
            v-for="option in countryOptions"
            :key="option.value"
            :value="option.value"
          >
            {{ option.label }}
          </option>
        </select>
      </div>
    </div>

    <!-- 회원가입 버튼 -->
    <button
      type="submit"
      class="w-full h-11 bg-blue-500 hover:bg-blue-600 font-paper text-white font-medium rounded-lg transition"
    >
      회원가입
    </button>

    <!-- Or separator -->
    <div class="flex items-center text-xs text-gray-500">
      <hr class="flex-grow border-t border-gray-200" />
      <span class="px-2 font-paper text-sm">or</span>
      <hr class="flex-grow border-t border-gray-200" />
    </div>

    <!-- Google 회원가입 -->
    <button
      type="button"
      class="w-full h-11 flex items-center justify-center gap-2 border border-gray-200 rounded-lg hover:bg-gray-100 transition text-sm font-paper"
    >
      <img src="@/assets/icons/google.svg" alt="구글 아이콘" class="w-5 h-5" />
      구글로 회원가입하기
    </button>
  </form>
</template>

<script setup>
import { ref, computed } from "vue";
import { useAuthStore } from "@/store/auth";
import { useRouter } from "vue-router";
import { useNotification } from '@/composables/useNotification.js';

const auth = useAuthStore();
const router = useRouter();
const { showSuccess, showError, showWarning } = useNotification();

const nickname = ref("");
const emailLocal = ref("");
const domainOptions = ["gmail.com", "naver.com", "daum.net", "직접 입력"];
const domainOption = ref(domainOptions[0]);
const customDomain = ref("");
const password = ref("");
const confirmPassword = ref("");
const countryOptions = [
  { value: "대한민국", label: "🇰🇷 대한민국" },
  { value: "베트남", label: "🇻🇳 베트남" },
  { value: "필리핀", label: "🇵🇭 필리핀" },
  { value: "태국", label: "🇹🇭 태국" },
  { value: "캄보디아", label: "🇰🇭 캄보디아" },
  { value: "몽골", label: "🇲🇳 몽골" },
  { value: "우즈베키스탄", label: "🇺🇿 우즈베키스탄" },
  { value: "미국", label: "🇺🇸 미국" },
  { value: "일본", label: "🇯🇵 일본" },
  { value: "중국", label: "🇨🇳 중국" },
  { value: "영국", label: "🇬🇧 영국" },
  { value: "프랑스", label: "🇫🇷 프랑스" },
  { value: "독일", label: "🇩🇪 독일" },
  { value: "캐나다", label: "🇨🇦 캐나다" },
  { value: "호주", label: "🇦🇺 호주" },
  { value: "스페인", label: "🇪🇸 스페인" },
  { value: "이탈리아", label: "🇮🇹 이탈리아" },
  { value: "브라질", label: "🇧🇷 브라질" },
];
const country = ref("");

const email = computed(() =>
  domainOption.value === "직접 입력"
    ? `${emailLocal.value}@${customDomain.value}`
    : `${emailLocal.value}@${domainOption.value}`
);

async function handleSignUp() {
  // 입력 값 검증
  if (!nickname.value.trim()) {
    showError("닉네임을 입력해주세요.", "입력 오류");
    return;
  }
  
  if (!emailLocal.value.trim()) {
    showError("이메일을 입력해주세요.", "입력 오류");
    return;
  }
  
  if (domainOption.value === "직접 입력" && !customDomain.value.trim()) {
    showError("도메인을 입력해주세요.", "입력 오류");
    return;
  }
  
  if (password.value.length < 6) {
    showWarning("비밀번호는 6자 이상이어야 합니다.", "비밀번호 오류");
    return;
  }
  
  if (password.value !== confirmPassword.value) {
    showError("비밀번호가 일치하지 않습니다.", "비밀번호 확인");
    return;
  }
  
  if (!country.value) {
    showError("국가를 선택해주세요.", "입력 오류");
    return;
  }
  
  try {
    await auth.signup({
      nickname: nickname.value,
      email: email.value,
      password: password.value,
      country: country.value,
    });
    showSuccess("회원가입이 완료되었습니다!", "환영합니다");
    router.push({ name: "Dashboard" });
  } catch (error) {
    showError("회원가입 중 오류가 발생했습니다. 다시 시도해주세요.", "회원가입 실패");
  }
}
</script>

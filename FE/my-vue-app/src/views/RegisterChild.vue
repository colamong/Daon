<template>
  <div class="py-8 px-4">
    <div class="mx-auto max-w-6xl bg-white pt-10 pb-10 rounded-2xl mb-10">
      <!-- 상단 타이틀 -->
      <div class="text-center mb-12">
        <h1 class="text-4xl font-paperBold text-gray-800">아이 등록</h1>
      </div>

      <!-- 메인 콘텐츠: 좌우 레이아웃 -->
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-16 px-8">
        <!-- 좌측: 이미지 업로드 -->
        <div class="flex flex-col space-y-6">
          <BaseImageUpload @upload:image="handleImageUpload" />
        </div>

        <!-- 우측: 폼 필드들 -->
        <div class="space-y-8">
          <form @submit.prevent="handleRegisterChild" class="space-y-8">
            <!-- 이름 -->
            <div>
              <label
                for="childName"
                class="block text-lg font-paperBold text-black mb-3"
              >
                이름
              </label>
              <input
                id="childName"
                v-model="childData.name"
                type="text"
                required
                placeholder="아이의 이름을 입력하세요"
                class="w-full py-3 px-4 border-2 border-gray-300 rounded-lg focus:outline-none focus:border-purple-500 font-paper text-lg"
              />
            </div>

            <!-- 생년월일 -->
            <div>
              <label
                for="birthDate"
                class="block text-lg font-paperBold text-black mb-3"
              >
                생년월일
              </label>
              <div class="flex gap-2">
                <select
                  v-model="selectedYear"
                  class="flex-1 py-3 px-4 border-2 border-gray-300 rounded-lg focus:outline-none focus:border-purple-500 font-paper text-lg bg-white"
                >
                  <option value="">년도</option>
                  <option v-for="year in years" :key="year" :value="year">
                    {{ year }}년
                  </option>
                </select>
                <select
                  v-model="selectedMonth"
                  class="flex-1 py-3 px-4 border-2 border-gray-300 rounded-lg focus:outline-none focus:border-purple-500 font-paper text-lg bg-white"
                >
                  <option value="">월</option>
                  <option v-for="month in 12" :key="month" :value="month">
                    {{ month }}월
                  </option>
                </select>
                <select
                  v-model="selectedDay"
                  class="flex-1 py-3 px-4 border-2 border-gray-300 rounded-lg focus:outline-none focus:border-purple-500 font-paper text-lg bg-white"
                >
                  <option value="">일</option>
                  <option v-for="day in daysInMonth" :key="day" :value="day">
                    {{ day }}일
                  </option>
                </select>
              </div>
            </div>

            <!-- 성별 -->
            <div>
              <BaseRadioGroup
                v-model="childData.gender"
                label="성별"
                name="gender"
                :options="genderOptions"
              />
            </div>

            <!-- 관심사 -->
            <div>
              <BaseCheckboxGroup
                v-model="childData.interests"
                label="관심사"
                :options="interestOptions"
              />
            </div>

            <!-- 추가하고 싶은 관심사 -->
            <div>
              <label
                for="newInterest"
                class="block text-lg font-paperBold text-black mb-3"
              >
                추가하고 싶은 관심사
              </label>
              <div class="flex gap-2">
                <input
                  id="newInterest"
                  v-model="newInterest"
                  type="text"
                  placeholder="새로운 관심사를 입력하세요"
                  class="flex-1 py-3 px-4 border-2 border-gray-300 rounded-lg focus:outline-none focus:border-purple-500 font-paper text-lg"
                  @keypress.enter.prevent="addNewInterest"
                />
                <button
                  type="button"
                  @click="addNewInterest"
                  class="px-6 py-3 bg-purple-500 text-white font-paperBold text-lg rounded-lg hover:bg-purple-600 transition-colors"
                >
                  추가
                </button>
              </div>
            </div>

            <!-- 등록하기 버튼 -->
            <div class="pt-6">
              <button
                type="submit"
                :disabled="loading"
                class="w-full py-4 bg-purple-500 text-white font-paperBold text-lg rounded-lg hover:bg-purple-600 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
              >
                {{ loading ? "등록 중..." : "등록하기" }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/store/auth";
import BaseImageUpload from "@/components/form/BaseImageUpload.vue";
import BaseRadioGroup from "@/components/form/BaseRadioGroup.vue";
import BaseCheckboxGroup from "@/components/form/BaseCheckboxGroup.vue";

const router = useRouter();
const auth = useAuthStore();

const loading = ref(false);
const selectedYear = ref("");
const selectedMonth = ref("");
const selectedDay = ref("");
const newInterest = ref("");

// 아이 데이터
const childData = reactive({
  name: "",
  birthDate: "",
  gender: "",
  interests: [],
  profileImage: null,
});

// 년도 옵션 (현재년도부터 20년 전까지)
const currentYear = new Date().getFullYear();
const years = Array.from({ length: 20 }, (_, i) => currentYear - i);

// 해당 월의 일수 계산
const daysInMonth = computed(() => {
  if (!selectedYear.value || !selectedMonth.value) return 31;
  return new Date(selectedYear.value, selectedMonth.value, 0).getDate();
});

// 생년월일 업데이트 감시
watch([selectedYear, selectedMonth, selectedDay], () => {
  if (selectedYear.value && selectedMonth.value && selectedDay.value) {
    const month = selectedMonth.value.toString().padStart(2, "0");
    const day = selectedDay.value.toString().padStart(2, "0");
    childData.birthDate = `${selectedYear.value}-${month}-${day}`;
  }
});

// 성별 옵션
const genderOptions = [
  { label: "남자", value: "남자" },
  { label: "여자", value: "여자" },
];

// 관심사 옵션 (아이들이 흔히 좋아하는 것들)
const interestOptions = ref([
  { label: "스포츠", value: "스포츠" },
  { label: "음식", value: "음식" },
  { label: "여행", value: "여행" },
  { label: "동물", value: "동물" },
  { label: "음악", value: "음악" },
  { label: "춤", value: "춤" },
  { label: "게임", value: "게임" },
  { label: "책읽기", value: "책읽기" },
  { label: "요리", value: "요리" },
]);

// 국가 옵션 (다문화 가정 중심)
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

function handleImageUpload(file) {
  // 파일을 base64로 변환해서 저장
  if (file) {
    const reader = new FileReader();
    reader.onload = (e) => {
      childData.profileImage = e.target.result; // base64 문자열로 저장
    };
    reader.readAsDataURL(file);
  }
}

// 새로운 관심사 추가
function addNewInterest() {
  if (!newInterest.value.trim()) {
    alert("관심사를 입력해주세요.");
    return;
  }

  // 이미 존재하는 관심사인지 확인
  const exists = interestOptions.value.find(
    (option) =>
      option.value.toLowerCase() === newInterest.value.trim().toLowerCase()
  );

  if (exists) {
    alert("이미 존재하는 관심사입니다.");
    newInterest.value = "";
    return;
  }

  // 새로운 관심사 추가
  const newInterestOption = {
    label: newInterest.value.trim(),
    value: newInterest.value.trim(),
  };

  interestOptions.value.push(newInterestOption);

  // 추가된 관심사를 자동으로 선택
  if (!childData.interests.includes(newInterestOption.value)) {
    childData.interests.push(newInterestOption.value);
  }

  // 입력 필드 초기화
  newInterest.value = "";
}

async function handleRegisterChild() {
  // 필수 필드 검증
  if (!childData.name.trim()) {
    alert("아이의 이름을 입력해주세요.");
    return;
  }

  if (!childData.birthDate) {
    alert("생년월일을 선택해주세요.");
    return;
  }

  if (!childData.gender) {
    alert("성별을 선택해주세요.");
    return;
  }

  loading.value = true;

  try {
    // TODO: 실제 API 호출로 대체
    await new Promise((resolve) => setTimeout(resolve, 2000));

    // 아이 정보를 localStorage에 저장 (임시)
    const childInfo = {
      id: Date.now(),
      ...childData,
      registeredAt: new Date().toISOString(),
    };

    // 기존 아이 정보가 있다면 배열에 추가, 없다면 새로 생성
    const existingChildren = JSON.parse(
      localStorage.getItem("children") || "[]"
    );
    existingChildren.push(childInfo);
    localStorage.setItem("children", JSON.stringify(existingChildren));

    // auth 스토어에도 업데이트
    if (auth.user) {
      auth.user.children = existingChildren;
      localStorage.setItem("auth_user", JSON.stringify(auth.user));
    }

    alert(`${childData.name}의 정보가 성공적으로 등록되었습니다! 🎉`);

    // 대시보드로 이동
    router.push({ name: "Dashboard" });
  } catch (error) {
    console.error("아이 등록 실패:", error);
    alert("아이 등록에 실패했습니다. 다시 시도해주세요. 😥");
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
/* 필요시 추가 스타일 */
</style>

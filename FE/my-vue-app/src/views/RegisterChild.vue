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
                class=""
                v-model="childData.gender"
                label="성별"
                name="gender"
                :options="genderOptions"
              />
            </div>
          </form>
        </div>
      </div>

      <!-- 하단 관심사 영역 -->
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-16 px-8 mt-12">
        <!-- 좌측: 관심사 -->
        <div>
          <BaseCheckboxGroup
            v-model="childData.interests"
            label="관심사"
            :options="interestOptions"
          />
        </div>

        <!-- 우측: 추가하고 싶은 관심사 + 등록 버튼 -->
        <div class="space-y-8">
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
              type="button"
              @click="handleRegisterChild"
              :disabled="loading"
              class="w-full py-4 bg-purple-500 text-white font-paperBold text-lg rounded-lg hover:bg-purple-600 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
            >
              {{ loading ? "등록 중..." : "등록하기" }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch } from "vue";
import { useRouter } from "vue-router";
import { useNotification } from "@/composables/useNotification.js";
import { useAuthStore } from "@/store/auth";
import { useChildStore } from "@/store/child";
import { assignColorToChild } from "@/utils/colorManager.js"; // 사용 중이면 유지
import { childService } from "@/services/childService.js";
import BaseImageUpload from "@/components/form/BaseImageUpload.vue";
import BaseRadioGroup from "@/components/form/BaseRadioGroup.vue";
import BaseCheckboxGroup from "@/components/form/BaseCheckboxGroup.vue";
import boyImage from "@/assets/images/boy.png";
import girlImage from "@/assets/images/girl.png";

const router = useRouter();
const auth = useAuthStore();
const childStore = useChildStore();
const { showSuccess, showError, showWarning } = useNotification();

const loading = ref(false);
const selectedYear = ref("");
const selectedMonth = ref("");
const selectedDay = ref("");
const newInterest = ref("");

// 아이 데이터 (⚠ Base64 대신 File로 보관)
const childData = reactive({
  name: "",
  birthDate: "",
  gender: "",
  interests: [],
  profileFile: null, // File 객체
});

// 년도 옵션 (현재년도부터 15년 전까지)
const currentYear = new Date().getFullYear();
const years = Array.from({ length: 15 }, (_, i) => currentYear - i);

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
  { label: "남자", value: "MALE" },
  { label: "여자", value: "FEMALE" },
];

// 관심사 옵션
const interestOptions = ref([
  { label: "스포츠", value: "스포츠" },
  { label: "음식", value: "음식" },
  { label: "여행", value: "여행" },
  { label: "동물", value: "동물" },
  { label: "음악", value: "음악" },
  { label: "춤", value: "춤" },
  { label: "게임", value: "게임" },
  { label: "책읽기", value: "책읽기" },
]);

// 이미지 업로드 콜백: File만 저장 (Base64 변환 금지)
function handleImageUpload(file) {
  childData.profileFile = file || null;
}

// 새로운 관심사 추가
function addNewInterest() {
  if (!newInterest.value.trim()) {
    showWarning("관심사를 입력해주세요.", "입력 오류");
    return;
  }
  const exists = interestOptions.value.find(
    (option) =>
      option.value.toLowerCase() === newInterest.value.trim().toLowerCase()
  );
  if (exists) {
    showWarning("이미 존재하는 관심사입니다.", "중복된 관심사");
    newInterest.value = "";
    return;
  }
  const newInterestOption = {
    label: newInterest.value.trim(),
    value: newInterest.value.trim(),
  };
  interestOptions.value.push(newInterestOption);
  if (!childData.interests.includes(newInterestOption.value)) {
    childData.interests.push(newInterestOption.value);
  }
  newInterest.value = "";
}

// 성별에 따른 기본 이미지 업로드 함수
async function uploadDefaultChildImage(userId, childId, gender) {
  try {
    console.log("=== 아이 기본 이미지 업로드 시작 ===");
    console.log("성별:", gender);

    // 성별에 따른 이미지 선택
    const imageUrl = gender === "MALE" ? boyImage : girlImage;
    const fileName = gender === "MALE" ? "boy.png" : "girl.png";

    console.log("선택된 이미지:", imageUrl);

    // 이미지를 fetch로 가져와서 File 객체로 변환
    const response = await fetch(imageUrl);
    if (!response.ok) {
      throw new Error(`이미지 fetch 실패: ${response.status}`);
    }

    const blob = await response.blob();
    const file = new File([blob], fileName, { type: "image/png" });

    console.log("File 객체 생성 완료:", {
      name: file.name,
      size: file.size,
      type: file.type,
    });

    // childService를 사용해서 이미지 업로드
    await childService.uploadChildImage(userId, childId, file);
    console.log("아이 기본 프로필 이미지 업로드 완료");
  } catch (error) {
    console.warn("아이 기본 프로필 이미지 업로드 실패:", error);
    // 기본 이미지 업로드 실패는 아이 등록을 막지 않음
  }
}

async function handleRegisterChild() {
  if (!childData.name.trim()) {
    showError("아이의 이름을 입력해주세요.", "입력 오류");
    return;
  }
  if (!childData.birthDate) {
    showError("생년월일을 선택해주세요.", "입력 오류");
    return;
  }
  if (!childData.gender) {
    showError("성별을 선택해주세요.", "입력 오류");
    return;
  }

  loading.value = true;

  try {
    const userId = auth.user?.id || 1;

    // 1) JSON 등록 (Base64/URL 없이)
    const registerRes = await childService.registerChild(userId, {
      name: childData.name.trim(),
      birthDate: childData.birthDate,
      gender: childData.gender,
      interests: [...childData.interests],
    });
    const childId = registerRes?.childId;

    // 2) 이미지 업로드 처리
    if (childId) {
      if (childData.profileFile) {
        // 사용자가 업로드한 이미지가 있으면 그것을 사용
        await childService.uploadChildImage(
          userId,
          childId,
          childData.profileFile
        );
      } else {
        // 이미지가 없으면 성별에 따른 기본 이미지 업로드
        await uploadDefaultChildImage(userId, childId, childData.gender);
      }
    }

    // 3) 목록 새로고침 & 이동
    await childStore.loadChildren();
    showSuccess(
      `${childData.name}의 정보가 성공적으로 등록되었습니다!`,
      "등록 완료"
    );
    router.push({ name: "Dashboard" });
  } catch (error) {
    console.error("아이 등록 실패:", error);
    showError(
      error?.response?.data?.error ||
        error.message ||
        "아이 등록에 실패했습니다. 다시 시도해주세요.",
      "등록 실패"
    );
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
/* 드롭다운 최대 높이 제한 */
.select-dropdown {
  /* 브라우저별 드롭다운 높이 제한 */
  -webkit-appearance: menulist;
  -moz-appearance: menulist;
  appearance: menulist;
}

/* 드롭다운 옵션이 표시될 때의 최대 높이 설정 (일부 브라우저에서만 작동) */
.select-dropdown option {
  max-height: 80px;
}

/* 브라우저에서 지원하는 경우 드롭다운 높이 제한 */
.select-dropdown:focus {
  max-height: 80px;
  overflow-y: auto;
}
</style>

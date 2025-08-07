<template>
  <div class="py-8 px-4">
    <div class="mx-auto max-w-5xl bg-white pt-10 pb-10 rounded-2xl mb-10">
      <!-- 상단 타이틀 -->
      <div class="text-center mb-12">
        <h1 class="text-4xl font-paperBold text-gray-800">프로필 수정</h1>
      </div>

      <!-- 좌우 레이아웃 -->
      <div class="p-3">
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-16 items-center">
          <!-- 좌측: 프로필 이미지 -->
          <div class="flex flex-col items-center space-y-6">
            <!-- 프로필 이미지 -->
            <div>
              <img
                :src="
                  formData.profileImage ||
                  currentProfile.profileImage ||
                  'https://placehold.co/200x200'
                "
                alt="프로필 이미지"
                class="w-60 h-60 rounded-full object-cover border-4 border-gray-200"
              />
            </div>
            <input
              ref="fileInput"
              type="file"
              accept="image/*"
              @change="handleImageChange"
              class="hidden"
            />

            <button
              type="button"
              @click="triggerFileInput"
              class="px-6 py-2 bg-blue-100 text-black rounded-lg hover:bg-blue-500 hover:text-white transition-colors font-paper border border-gray-300"
            >
              사진 불러오기
            </button>
          </div>

          <!-- 우측: 수정 폼 -->
          <div class="space-y-8 px-4">
            <form @submit.prevent="handleUpdateProfile" class="space-y-8">
              <!-- 닉네임 -->
              <div>
                <label
                  for="nickname"
                  class="block text-lg font-paperBold text-black mb-3"
                >
                  닉네임
                </label>
                <input
                  id="nickname"
                  v-model="formData.nickname"
                  type="text"
                  maxlength="10"
                  placeholder="닉네임을 입력하세요"
                  class="w-4/5 py-4 px-4 border-2 border-gray-300 rounded-lg focus:outline-none focus:border-red-500 font-paper text-lg"
                />
              </div>

              <!-- 국가 -->
              <div>
                <label
                  for="country"
                  class="block text-lg font-paperBold text-black mb-3"
                >
                  국가
                </label>
                <select
                  id="country"
                  v-model="formData.nationCode"
                  class="w-4/5 py-4 px-4 border-2 border-gray-300 rounded-lg focus:outline-none focus:border-red-500 font-paper text-lg bg-white"
                >
                  <option value="">국가를 선택</option>
                  <option
                    v-for="option in countryOptions"
                    :key="option.code"
                    :value="option.code"
                  >
                    {{ option.nameKo }}
                  </option>
                </select>
              </div>

              <!-- 수정 완료 버튼 -->
              <div class="pt-6">
                <button
                  type="submit"
                  :disabled="loading || !hasChanges"
                  class="w-4/5 py-4 bg-purple-500 text-white rounded-lg hover:bg-purple-800 disabled:opacity-50 disabled:cursor-not-allowed transition-colors font-paperBold text-lg"
                >
                  {{ loading ? "저장 중..." : "수정 완료" }}
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/store/auth";
import { useNotification } from '@/composables/useNotification.js';
import { nationService } from '@/services/nationService.js';

const router = useRouter();
const auth = useAuthStore();
const { showSuccess, showError, showWarning } = useNotification();

const loading = ref(false);
const fileInput = ref(null);

// 현재 프로필 정보 (원본)
const currentProfile = reactive({
  nickname: "",
  email: "",
  nationCode: "",
  profileImage: "",
});

// 수정 폼 데이터
const formData = reactive({
  nickname: "",
  nationCode: "",
  profileImage: "",
});

// 국가 옵션 (백엔드에서 로드)
const countryOptions = ref([]);

// 변경사항이 있는지 확인
const hasChanges = computed(() => {
  return (
    formData.nickname !== currentProfile.nickname ||
    formData.nationCode !== currentProfile.nationCode ||
    formData.profileImage !== currentProfile.profileImage
  );
});

// 컴포넌트 마운트 시 현재 사용자 정보 및 국가 목록 로드
onMounted(async () => {
  await Promise.all([
    loadCurrentProfile(),
    loadCountries()
  ]);
});

async function loadCurrentProfile() {
  try {
    // 사용자 정보가 없다면 먼저 가져오기
    if (!auth.user && auth.token) {
      await auth.getCurrentUser();
    }
    
    if (auth.user) {
      // 현재 프로필 정보 설정
      currentProfile.nickname = auth.user.nickname || "";
      currentProfile.email = auth.user.email || "";
      currentProfile.nationCode = auth.user.nationCode || "";
      currentProfile.profileImage = auth.user.profileImage || "";

      // 폼 데이터도 현재 정보로 초기화
      formData.nickname = currentProfile.nickname;
      formData.nationCode = currentProfile.nationCode;
      formData.profileImage = currentProfile.profileImage;
    }
  } catch (error) {
    console.error('프로필 로드 실패:', error);
    showError('프로필 정보를 불러오는 중 오류가 발생했습니다.', '로드 실패');
  }
}

async function loadCountries() {
  try {
    const nations = await nationService.getNations();
    countryOptions.value = nations;
  } catch (error) {
    console.error('국가 목록 로드 실패:', error);
    showError('국가 목록을 불러오는 중 오류가 발생했습니다.', '로드 실패');
  }
}

function triggerFileInput() {
  fileInput.value?.click();
}

function handleImageChange(event) {
  const file = event.target.files[0];
  if (file) {
    // 파일 크기 체크 (5MB 제한)
    if (file.size > 5 * 1024 * 1024) {
      showWarning("파일 크기는 5MB 이하로 선택해주세요.", "파일 크기 초과");
      return;
    }

    // 이미지 파일인지 확인
    if (!file.type.startsWith("image/")) {
      showError("이미지 파일만 선택할 수 있습니다.", "파일 형식 오류");
      return;
    }

    const reader = new FileReader();
    reader.onload = (e) => {
      formData.profileImage = e.target.result;
    };
    reader.readAsDataURL(file);
  }
}

async function handleUpdateProfile() {
  // 입력 검증
  if (!formData.nickname.trim()) {
    showError("닉네임을 입력해주세요.", "입력 오류");
    return;
  }

  if (formData.nickname.length > 10) {
    showWarning("닉네임은 10글자 이하로 입력해주세요.", "글자 수 초과");
    return;
  }

  if (!formData.nationCode) {
    showError("국가를 선택해주세요.", "입력 오류");
    return;
  }

  loading.value = true;

  try {
    // 실제 API 호출
    await auth.updateProfile({
      nickname: formData.nickname,
      nationCode: formData.nationCode
      // profileImage는 나중에 구현 (파일 업로드가 필요)
    });

    // auth.updateProfile에서 이미 사용자 정보를 업데이트했으므로
    // 여기서는 currentProfile만 업데이트
    currentProfile.nickname = formData.nickname;
    currentProfile.nationCode = formData.nationCode;

    showSuccess("프로필이 성공적으로 업데이트되었습니다!", "수정 완료");

    // 대시보드로 이동
    router.push({ name: "Dashboard" });
  } catch (error) {
    console.error("프로필 업데이트 실패:", error);
    showError(error.message || "프로필 업데이트에 실패했습니다. 다시 시도해주세요.", "업데이트 실패");
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
/* 필요시 추가 스타일 */
</style>

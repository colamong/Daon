<template>
  <div class="py-8 px-4">
    <div class="mx-auto max-w-5xl bg-white pt-10 pb-10 rounded-2xl mb-10">
      <!-- 상단 타이틀 -->
      <div class="flex justify-between items-center mb-12 px-8">
        <div class="flex-1"></div>
        <h1 class="text-4xl font-paperBold text-gray-800">프로필 수정</h1>
        <div class="flex-1 flex justify-end">
          <button
            @click="goBack"
            class="px-6 py-2 bg-gray-300 text-gray-700 font-paperBold text-sm rounded-lg hover:bg-gray-400 transition-colors"
          >
            취소
          </button>
        </div>
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
                  formData.previewImage ||
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
              :disabled="uploadingImage"
              class="px-6 py-2 bg-blue-100 text-black rounded-lg hover:bg-blue-500 hover:text-white transition-colors font-paper border border-gray-300 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {{ uploadingImage ? '사진 업로드 중...' : '사진 불러오기' }}
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
const uploadingImage = ref(false); // 🔹 이미지 업로드 상태
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
  newImageFile: null, // 새로 선택한 이미지 파일
  previewImage: "", // 미리보기용 이미지
});

// 국가 옵션 (백엔드에서 로드)
const countryOptions = ref([]);

// 변경사항 확인 (이미지 변경도 포함)
const hasChanges = computed(() => {
  return (
    formData.nickname !== currentProfile.nickname ||
    formData.nationCode !== currentProfile.nationCode ||
    formData.newImageFile !== null
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
      await auth.getCurrentUser?.(); // 프로젝트 구현에 맞춰 사용
    }
    // auth에 me를 다시 불러오는 액션이 있다면 사용 (예: await auth.loadMe())

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
  const file = event.target.files?.[0];
  if (!file) return;

  // 파일 크기 체크 (5MB 제한)
  if (file.size > 5 * 1024 * 1024) {
    showWarning("파일 크기는 5MB 이하로 선택해주세요.", "파일 크기 초과");
    event.target.value = '';
    return;
  }

  // 이미지 파일인지 확인
  if (!file.type.startsWith("image/")) {
    showError("이미지 파일만 선택할 수 있습니다.", "파일 형식 오류");
    event.target.value = '';
    return;
  }

  // 파일 저장 및 미리보기 설정
  formData.newImageFile = file;
  formData.previewImage = URL.createObjectURL(file);
  
  // 같은 파일 다시 선택 가능하도록 초기화
  event.target.value = '';
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
    // 1. 이미지가 변경되었다면 먼저 업로드
    if (formData.newImageFile) {
      uploadingImage.value = true;
      await auth.uploadProfileImage(formData.newImageFile);
      uploadingImage.value = false;
    }

    // 2. 텍스트 정보 업데이트
    await auth.updateProfile?.({
      nickname: formData.nickname,
      nationCode: formData.nationCode
    });

    // 3. 현재 상태 동기화
    currentProfile.nickname = formData.nickname;
    currentProfile.nationCode = formData.nationCode;
    if (formData.newImageFile) {
      currentProfile.profileImage = auth.user?.profileImage || currentProfile.profileImage;
    }

    // 4. 임시 상태 초기화
    formData.newImageFile = null;
    if (formData.previewImage) {
      URL.revokeObjectURL(formData.previewImage);
      formData.previewImage = "";
    }

    showSuccess("프로필이 성공적으로 업데이트되었습니다!", "수정 완료");
    router.push({ name: "Dashboard" });
  } catch (error) {
    console.error("프로필 업데이트 실패:", error);
    showError(error?.message || "프로필 업데이트에 실패했습니다. 다시 시도해주세요.", "업데이트 실패");
  } finally {
    loading.value = false;
    uploadingImage.value = false;
  }
}

function goBack() {
  router.push({ name: "Dashboard" });
}
</script>

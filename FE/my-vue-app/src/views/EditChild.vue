<template>
  <div class="py-8 px-4">
    <div class="mx-auto max-w-6xl bg-white pt-10 pb-10 rounded-2xl mb-10">
      <!-- 상단 타이틀 -->
      <div class="text-center mb-12">
        <h1 class="text-4xl font-paperBold text-gray-800">아이 프로필 수정</h1>
      </div>

      <!-- 아이가 없는 경우 -->
      <div v-if="!hasChild" class="text-center py-20">
        <p class="text-xl text-gray-600 mb-8">등록된 아이가 없습니다.</p>
        <button
          @click="goToRegister"
          class="bg-purple-500 text-white px-8 py-4 rounded-lg text-lg font-paperBold hover:bg-purple-600 transition-colors"
        >
          아이 등록하기
        </button>
      </div>

      <!-- 아이가 있는 경우 -->
      <div v-else class="space-y-8">
        <!-- 아이 선택 탭 (여러 명인 경우) -->
        <div v-if="childrenList.length > 1" class="flex justify-center space-x-4 mb-8">
          <button
            v-for="(child, index) in childrenList"
            :key="child.id"
            @click="selectChild(index)"
            class="px-6 py-3 rounded-lg font-paperBold transition-colors"
            :class="{
              'bg-purple-500 text-white': selectedChildIndex === index,
              'bg-gray-200 text-gray-700 hover:bg-gray-300': selectedChildIndex !== index,
            }"
          >
            {{ child.name }}
          </button>
        </div>

        <!-- 수정 폼 -->
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-16 px-8">
          <!-- 좌측: 이미지 업로드 -->
          <div class="flex flex-col space-y-6">
            <BaseImageUpload
              @upload:image="handleImageUpload"        
              :initial-image="previewImage"            
            />
          </div>

          <!-- 우측: 폼 필드들 -->
          <div class="space-y-8">
            <form @submit.prevent="handleUpdateChild" class="space-y-8">
              <!-- 이름 -->
              <div>
                <label for="childName" class="block text-lg font-paperBold text-black mb-3">이름</label>
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
                <label for="birthDate" class="block text-lg font-paperBold text-black mb-3">생년월일</label>
                <div class="flex gap-2">
                  <select v-model="selectedYear" class="flex-1 py-3 px-4 border-2 border-gray-300 rounded-lg focus:outline-none focus:border-purple-500 font-paper text-lg bg-white">
                    <option value="">년도</option>
                    <option v-for="year in years" :key="year" :value="year">{{ year }}년</option>
                  </select>
                  <select v-model="selectedMonth" class="flex-1 py-3 px-4 border-2 border-gray-300 rounded-lg focus:outline-none focus:border-purple-500 font-paper text-lg bg-white">
                    <option value="">월</option>
                    <option v-for="month in 12" :key="month" :value="month">{{ month }}월</option>
                  </select>
                  <select v-model="selectedDay" class="flex-1 py-3 px-4 border-2 border-gray-300 rounded-lg focus:outline-none focus:border-purple-500 font-paper text-lg bg-white">
                    <option value="">일</option>
                    <option v-for="day in daysInMonth" :key="day" :value="day">{{ day }}일</option>
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
                <label for="newInterest" class="block text-lg font-paperBold text-black mb-3">추가하고 싶은 관심사</label>
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

              <!-- 수정/삭제 버튼 -->
              <div class="flex gap-4 pt-6">
                <button
                  type="submit"
                  :disabled="loading"
                  class="flex-1 py-4 bg-purple-500 text-white font-paperBold text-lg rounded-lg hover:bg-purple-600 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
                >
                  {{ loading ? "수정 중..." : "수정하기" }}
                </button>
                <button
                  type="button"
                  @click="confirmDelete"
                  class="px-8 py-4 text-white font-paperBold text-lg rounded-lg transition-colors"
                  :class="showDeleteConfirm ? 'bg-red-700 hover:bg-red-800' : 'bg-red-500 hover:bg-red-600'"
                >
                  {{ showDeleteConfirm ? '정말 삭제하기' : '삭제' }}
                </button>
              </div>
            </form>

            <!-- 취소 버튼 -->
            <div class="text-center">
              <button
                @click="goBack"
                class="px-8 py-3 bg-gray-300 text-gray-700 font-paperBold text-lg rounded-lg hover:bg-gray-400 transition-colors"
              >
                취소
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/store/auth";
import { useChildStore } from "@/store/child";
import { childService } from "@/services/childService.js";
import { useNotification } from '@/composables/useNotification.js';
import BaseImageUpload from "@/components/form/BaseImageUpload.vue";
import BaseRadioGroup from "@/components/form/BaseRadioGroup.vue";
import BaseCheckboxGroup from "@/components/form/BaseCheckboxGroup.vue";

const router = useRouter();
const auth = useAuthStore();
const childStore = useChildStore();
const { showSuccess, showError, showWarning, showInfo } = useNotification();

const loading = ref(false);
const selectedChildIndex = ref(0);
const selectedYear = ref("");
const selectedMonth = ref("");
const selectedDay = ref("");
const newInterest = ref("");
const showDeleteConfirm = ref(false);

// 아이 데이터
const childData = reactive({
  id: null,
  name: "",
  birthDate: "",
  gender: "",
  interests: [],
  imageUrl: null,            // 서버에서 내려준 presigned URL
  profileKey: null,          // 서버에 저장된 S3 key (URL이 오면 프런트에서 추출)
  profileFile: null,         // 새로 선택한 File (멀티파트 업로드용)
  profileImagePreview: null, // 미리보기용 Object URL
});

// 미리보기 이미지 선택 로직
const previewImage = computed(() =>
  childData.profileImagePreview || childData.imageUrl || 'https://placehold.co/300x300'
);

// 년도 옵션
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
  { label: "남자", value: "MALE" },
  { label: "여자", value: "FEMALE" },
];

// 관심사 옵션 (동적으로 생성)
const interestOptions = ref([]);

// store 연동
const hasChild = computed(() => childStore.hasChildren);
const childrenList = computed(() => childStore.children);
const selectedChild = computed(() => childrenList.value[selectedChildIndex.value] || childStore.selectedChild || {});

// 아이 정보 로드
async function loadChildren() {
  await childStore.initialize();
  if (childStore.hasChildren) {
    const child = childStore.selectedChild || childStore.children[0];
    selectedChildIndex.value = childStore.children.findIndex(c => c.id === child.id);
    loadChildData(child);
  }
}

// 특정 아이 데이터 로드
function loadChildData(child) {
  childData.id = child.id;
  childData.name = child.name || '';
  childData.birthDate = child.birthDate || '';
  childData.gender = child.gender || '';
  childData.interests = Array.isArray(child.interests) ? [...child.interests] : [];
  childData.imageUrl = child.imageUrl || child.profileImage || null;

  // 기존 S3 키 확보 (서버가 key 또는 URL을 줄 수 있음)
  childData.profileKey = child.profileImg || extractS3Key(childData.imageUrl) || null;

  // 생년월일 파싱
  if (childData.birthDate) {
    const [y, m, d] = childData.birthDate.split("-");
    selectedYear.value = parseInt(y);
    selectedMonth.value = parseInt(m);
    selectedDay.value = parseInt(d);
  }

  // 관심사 옵션
  interestOptions.value = (childData.interests || []).map(v => ({ label: v, value: v }));

  // 새 업로드 리셋
  childData.profileFile = null;
  childData.profileImagePreview = null;
}

// 아이 선택
function selectChild(index) {
  selectedChildIndex.value = index;
  loadChildData(selectedChild.value);
}

// 이미지 업로드 처리: File 그대로 보관 + 미리보기 생성
function handleImageUpload(file) {
  if (!file) return;
  childData.profileFile = file;                         // ✅ File 저장
  childData.profileImagePreview = URL.createObjectURL(file); // 미리보기
}

// 새로운 관심사 추가
async function addNewInterest() {
  if (!newInterest.value.trim()) {
    showWarning("관심사를 입력해주세요.", "입력 오류");
    return;
  }
  const newInterestValue = newInterest.value.trim();

  const exists = interestOptions.value.find(
    (option) => option.value.toLowerCase() === newInterestValue.toLowerCase()
  );
  if (exists) {
    showWarning("이미 존재하는 관심사입니다.", "중복된 관심사");
    newInterest.value = "";
    return;
  }

  try {
    const userId = auth.user?.id;
    if (!userId) {
      showError("로그인이 필요합니다.", "인증 오류");
      return;
    }

    await childService.addChildInterests(userId, childData.id, { interests: [newInterestValue] });

    const opt = { label: newInterestValue, value: newInterestValue };
    interestOptions.value.push(opt);
    if (!childData.interests.includes(newInterestValue)) {
      childData.interests.push(newInterestValue);
    }
    newInterest.value = "";
    showSuccess(`"${newInterestValue}" 관심사가 추가되었습니다.`, "관심사 추가");

    await childStore.loadChildren();
  } catch (error) {
    console.error("관심사 추가 실패:", error);
    showError("관심사 추가에 실패했습니다.", "추가 실패");
  }
}

// 아이 정보 수정
async function handleUpdateChild() {
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
    const userId = auth.user?.id;
    if (!userId) {
      showError("로그인이 필요합니다.", "인증 오류");
      return;
    }

    // 1) 텍스트 업데이트: profileImg에는 "기존 S3 key"만 유지해 보냄
    const requestData = {
      name: childData.name.trim(),
      birthDate: childData.birthDate,
      gender: childData.gender,             // 서버 DTO가 무시해도 무방
      profileImg: childData.profileKey || null, // ❗ Base64/Presigned 금지
    };
    await childService.updateChild(userId, childData.id, requestData);

    // 2) 새 파일이 있으면 멀티파트로 업로드 (서버가 새 key로 갱신)
    if (childData.profileFile && typeof childService.uploadChildImage === 'function') {
      await childService.uploadChildImage(userId, childData.id, childData.profileFile);
    }

    // 3) 관심사 변경(해제만 처리; 추가는 addNewInterest에서 즉시 반영)
    await updateInterests(userId, childData.id);

    // 4) 상태 갱신
    await childStore.loadChildren();
    showSuccess(`${childData.name}의 정보가 성공적으로 수정되었습니다!`, "수정 완료");
    router.push({ name: "ChildProfile" });
  } catch (error) {
    console.error("아이 정보 수정 실패:", error);
    showError(error?.message || "아이 정보 수정에 실패했습니다. 다시 시도해주세요.", "수정 실패");
  } finally {
    loading.value = false;
  }
}

// 관심사 변경 처리 - 체크 해제된 항목 삭제
async function updateInterests(userId, childId) {
  const originalChild = childStore.children.find(c => c.id === childId);
  const originalInterests = originalChild?.interests || [];
  const currentInterests = childData.interests || [];

  const interestsToDelete = originalInterests.filter(i => !currentInterests.includes(i));
  if (interestsToDelete.length > 0) {
    await childService.deleteChildInterests(userId, childId, { interests: interestsToDelete });
  }
}

// 페이지 이동 / 삭제
function confirmDelete() {
  if (!showDeleteConfirm.value) {
    showDeleteConfirm.value = true;
    showWarning(
      `정말로 ${childData.name}의 정보를 삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다. 삭제하려면 '정말 삭제하기' 버튼을 다시 눌러주세요.`,
      "삭제 확인"
    );
    setTimeout(() => (showDeleteConfirm.value = false), 5000);
  } else {
    deleteChild();
  }
}

async function deleteChild() {
  try {
    const userId = auth.user?.id;
    if (!userId) {
      showError("로그인이 필요합니다.", "인증 오류");
      return;
    }
    await childService.deleteChild(userId, childData.id);
    childStore.removeChild(childData.id);
    showInfo(`${childData.name}의 정보가 삭제되었습니다.`, "삭제 완료");
    if (childStore.hasChildren) {
      router.push({ name: "ChildProfile" });
    } else {
      router.push({ name: "Dashboard" });
    }
  } catch (error) {
    console.error("아이 삭제 실패:", error);
    showError("아이 삭제에 실패했습니다. 다시 시도해주세요.", "삭제 실패");
  }
}

function goToRegister() { router.push({ name: "RegisterChild" }); }
function goBack() { router.push({ name: "ChildProfile" }); }

// 프리사인/공개 URL 에서 S3 key 추출 (없으면 그대로 반환)
function extractS3Key(urlOrKey) {
  if (!urlOrKey) return null;
  if (!/^https?:\/\//i.test(urlOrKey)) return urlOrKey;

  try {
    const u = new URL(urlOrKey);
    // 경로에서 맨 앞 '/' 제거
    let key = u.pathname.startsWith('/') ? u.pathname.slice(1) : u.pathname;
    // 쿼리스트링 제거 (URL객체의 pathname은 이미 쿼리 미포함)
    // 혹시 도메인이 s3에서 리디렉션된 특수 케이스만 대비
    key = key.replace(/^\/+/, '');
    return key || null;
  } catch {
    // URL 파싱 실패 시 수동 처리
    const idx = urlOrKey.indexOf('.amazonaws.com/');
    if (idx > -1) {
      const rest = urlOrKey.substring(idx + '.amazonaws.com/'.length);
      return rest.split('?')[0];
    }
    const lastSlash = urlOrKey.lastIndexOf('/');
    return lastSlash > -1 ? urlOrKey.substring(lastSlash + 1) : urlOrKey;
  }
}

// 컴포넌트 마운트 시 실행
onMounted(async () => {
  await loadChildren();
});
</script>
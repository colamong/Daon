<template>
  <div class="py-4 md:py-8 px-4">
    <div class="mx-auto max-w-5xl bg-white pt-6 md:pt-10 pb-6 md:pb-10 rounded-xl md:rounded-2xl mb-6 md:mb-10">
      <!-- 상단 타이틀 -->
      <div class="text-center mb-6 md:mb-12">
        <h1 class="text-2xl md:text-4xl font-paperBold text-gray-800">아이 프로필</h1>
      </div>

      <!-- 아이가 없는 경우 -->
      <div v-if="!hasChild" class="text-center py-12 md:py-20">
        <p class="text-lg md:text-xl text-gray-600 mb-6 md:mb-8">등록된 아이가 없습니다.</p>
        <button
          @click="goToRegister"
          class="bg-purple-500 text-white px-6 md:px-8 py-3 md:py-4 rounded-lg text-base md:text-lg font-paperBold hover:bg-purple-600 transition-colors"
        >
          아이 등록하기
        </button>
      </div>

      <!-- 아이가 있는 경우 -->
      <div v-else class="space-y-6 md:space-y-8">
        <!-- 아이 선택 탭 -->
        <div class="flex justify-center items-center mb-6 md:mb-8 px-4">
          <div class="flex flex-wrap justify-center items-center gap-2 sm:gap-3">
            <!-- 아이 탭들 -->
            <template v-if="childrenList.length > 0">
              <button
                v-for="(child, index) in childrenList"
                :key="child.id"
                @click="selectedChildIndex = index"
                class="px-4 sm:px-6 py-2 sm:py-3 rounded-lg font-paperBold transition-colors text-sm sm:text-base"
                :class="{
                  'bg-purple-500 text-white': selectedChildIndex === index,
                  'bg-gray-200 text-gray-700 hover:bg-gray-300':
                    selectedChildIndex !== index,
                }"
              >
                {{ child.name }}
              </button>
            </template>
            
            <!-- 아이 추가 버튼 -->
            <button
              @click="goToRegister"
              class="bg-gray-200 hover:bg-green-500 text-gray-700 hover:text-white px-3 sm:px-4 py-2 sm:py-3 rounded-lg font-paperBold transition-colors flex items-center gap-1 sm:gap-2 text-sm sm:text-base"
              title="새 아이 추가"
            >
              <span class="text-base sm:text-lg">+</span>
              <span class="hidden sm:inline">아이 추가</span>
            </button>
          </div>
        </div>

        <!-- 선택된 아이의 프로필 -->
        <div class="grid grid-cols-1 lg:grid-cols-3 gap-6 md:gap-8 px-4 md:px-8">
          <!-- 좌측: 프로필 이미지 -->
          <div class="flex flex-col items-center space-y-4 md:space-y-6">
            <div class="relative">
              <img
                :src="
                  selectedChild.profileImage || 'https://placehold.co/300x300'
                "
                alt="아이 프로필"
                class="w-48 h-48 sm:w-64 sm:h-64 md:w-72 md:h-72 rounded-full object-cover border-4 md:border-6 border-purple-200 shadow-xl"
              />
            </div>
            <h2 class="text-xl sm:text-2xl md:text-3xl font-paperBold text-gray-800 text-center">
              {{ selectedChild.name }}
            </h2>
          </div>

          <!-- 우측: 상세 정보 -->
          <div class="lg:col-span-2 space-y-4 md:space-y-6">
            <!-- 기본 정보 -->
            <div class="bg-gray-50 rounded-xl p-4 md:p-6">
              <h3 class="text-lg md:text-2xl font-paperBold text-gray-800 mb-3 md:mb-4">
                기본 정보
              </h3>
              <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-1 xl:grid-cols-2 gap-3 md:gap-4">
                <div class="flex items-center space-x-2 md:space-x-3">
                  <span class="text-purple-500 font-paperBold text-lg md:text-xl">🎂</span>
                  <div>
                    <p class="text-xs md:text-sm text-gray-500">생년월일</p>
                    <p class="text-sm md:text-lg font-paper">
                      {{ selectedChild.birthDate }}
                    </p>
                  </div>
                </div>
                <div class="flex items-center space-x-2 md:space-x-3">
                  <span class="text-purple-500 font-paperBold text-lg md:text-xl">👶</span>
                  <div>
                    <p class="text-xs md:text-sm text-gray-500">나이</p>
                    <p class="text-sm md:text-lg font-paper">
                      {{ calculateAge(selectedChild.birthDate) }}세 (만
                      {{ calculateAge(selectedChild.birthDate) - 1 }}세)
                    </p>
                  </div>
                </div>
                <div class="flex items-center space-x-2 md:space-x-3 sm:col-span-2 lg:col-span-1 xl:col-span-2">
                  <span class="text-purple-500 font-paperBold text-lg md:text-xl">⚧</span>
                  <div>
                    <p class="text-xs md:text-sm text-gray-500">성별</p>
                    <p class="text-sm md:text-lg font-paper">{{ genderDisplay }}</p>
                  </div>
                </div>
              </div>
            </div>

            <!-- 관심사 -->
            <div class="bg-gray-50 rounded-xl p-4 md:p-6">
              <h3 class="text-lg md:text-2xl font-paperBold text-gray-800 mb-3 md:mb-4">관심사</h3>
              <div
                v-if="
                  selectedChild.interests && selectedChild.interests.length > 0
                "
                class="flex flex-wrap gap-2 md:gap-3"
              >
                <span
                  v-for="interest in selectedChild.interests"
                  :key="interest"
                  class="bg-purple-100 text-purple-800 px-3 md:px-4 py-1 md:py-2 rounded-full text-xs md:text-sm font-paper"
                >
                  {{ interest }}
                </span>
              </div>
              <p v-else class="text-gray-500 font-paper text-sm md:text-base">
                등록된 관심사가 없습니다.
              </p>
            </div>

            <!-- 버튼 영역 -->
            <div class="flex flex-row gap-3 md:gap-4 pt-4 md:pt-6">
              <button
                @click="goToEdit"
                class="flex-1 bg-purple-500 text-white py-3 md:py-4 rounded-lg text-sm md:text-lg font-paperBold hover:bg-purple-600 transition-colors"
              >
                프로필 수정
              </button>
              <button
                @click="goToActivity"
                class="flex-1 bg-blue-500 text-white py-3 md:py-4 rounded-lg text-sm md:text-lg font-paperBold hover:bg-blue-600 transition-colors"
              >
                활동하러 가기
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import { useChildStore } from "@/store/child";

const router = useRouter();
const childStore = useChildStore();

// childStore에서 데이터 가져오기
const childrenList = computed(() => childStore.children);
const selectedChildIndex = computed({
  get: () => childStore.selectedChildIndex,
  set: (index) => {
    if (childrenList.value[index]) {
      childStore.selectChild(childrenList.value[index].id);
    }
  },
});

// childStore의 computed 속성들 사용
const hasChild = computed(() => childStore.hasChildren);
const selectedChild = computed(() => childStore.selectedChild);

// 성별 한글 표시
const genderDisplay = computed(() => {
  if (!selectedChild.value?.gender) return "";
  return selectedChild.value.gender === "MALE" ? "남자" : "여자";
});

// 컴포넌트 마운트 시 아이 정보 로드
onMounted(() => {
  childStore.initialize();
});


// 나이 계산 함수
function calculateAge(birthDate) {
  if (!birthDate) return 0;
  const today = new Date();
  const birth = new Date(birthDate);
  let age = today.getFullYear() - birth.getFullYear();
  const monthDiff = today.getMonth() - birth.getMonth();

  if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birth.getDate())) {
    age--;
  }

  return age + 1; // 한국 나이로 표시 (만 나이 + 1)
}

// 날짜 포맷팅 함수
function formatDate(dateString) {
  if (!dateString) return "";
  const date = new Date(dateString);
  return date.toLocaleDateString("ko-KR", {
    year: "numeric",
    month: "long",
    day: "numeric",
  });
}

// 페이지 이동 함수들
function goToRegister() {
  router.push({ name: "RegisterChild" });
}

function goToEdit() {
  router.push({ name: "EditChild" });
}

function goToActivity() {
  if (selectedChild.value) {
    // 선택된 아이 정보와 함께 ChildMain으로 이동
    router.push({
      name: "ChildMain",
      params: { childId: selectedChild.value.id },
    });
  } else {
    router.push({ name: "RegisterChild" });
  }
}
</script>

<style scoped>
/* 필요시 추가 스타일 */
</style>

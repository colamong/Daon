<template>
  <div class="font-paper">
    <!-- 1. Hero 섹션 -->
    <section class="relative overflow-hidden py-8 md:py-12 lg:py-16">
      <div
        class="max-w-6xl container mx-auto px-4 md:px-6 flex flex-col-reverse md:flex-row items-center"
      >
        <!-- 텍스트 -->
        <div class="md:w-1/2 mt-6 md:mt-0 px-2 md:px-4 text-center md:text-left">
          <h2 class="text-2xl md:text-3xl lg:text-4xl font-bold text-gray-800 mb-3 md:mb-4">
            언어의 벽을 넘어,<br />
            <span class="text-pink-500">일상의 행복</span>을 만듭니다.
          </h2>
          <p class="text-gray-700 text-base md:text-lg lg:text-xl leading-relaxed">
            언어가 달라도 마음이 통할 수 있도록,<br class="hidden md:block" />
            일상의 작은 순간들이 행복으로 이어지는 다온입니다.
          </p>
        </div>
        <!-- 이미지 -->
        <div class="md:w-1/2 flex justify-center relative">
          <img
            src="@/assets/images/hero-bg.png"
            alt="다문화 가족"
            class="rounded-lg w-full max-w-xs md:max-w-md relative z-10"
          />
        </div>
      </div>
    </section>

    <!-- 4. 오늘의 활동 -->
    <section
      class="max-w-6xl container mx-auto px-4 md:px-6 py-4 md:py-5 bg-white rounded-xl shadow mb-8 md:mb-16 min-h-[350px] sm:min-h-[400px] md:h-[520px]"
    >
      <div class="flex justify-between items-center mb-4 md:mb-6 gap-2">
        <h3 class="text-lg md:text-2xl font-semibold flex-shrink-0">오늘의 활동</h3>
        <button
          @click="goToChildProfile"
          class="bg-blue-500 hover:bg-blue-600 text-white px-2 md:px-4 py-2 rounded-lg transition-colors font-paper text-xs md:text-base flex-shrink-0"
        >
          아이 프로필
        </button>
      </div>
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-4 md:gap-6 items-start">
        <div class="lg:col-span-2">
          <div
            class="bg-blue-100 h-[250px] md:h-[350px] lg:h-[400px] rounded-lg shadow p-3 md:p-6 flex flex-col items-center justify-center relative"
          >
            <!-- 자세히 보기 버튼 (우측 상단) -->
            <button
              @click="openTodayReport"
              class="text-xs md:text-sm text-gray-600 hover:underline hover:text-blue-600 bg-white/80 px-2 md:px-3 py-1 rounded-lg"
              style="position: absolute !important; top: 8px !important; right: 8px !important; z-index: 10 !important;"
            >
              <span class="hidden sm:inline">자세히 보기 →</span>
              <span class="sm:hidden">→</span>
            </button>
            <template v-if="isLoadingActivity">
              <div
                class="animate-spin rounded-full h-8 w-8 md:h-12 md:w-12 border-b-2 border-white"
              ></div>
              <p class="text-white mt-2 md:mt-4 text-sm md:text-base">활동 데이터를 불러오는 중...</p>
            </template>
            <template v-else-if="hasActivity && todayActivity">
              <img
                :src="todayActivity.imageUrl"
                alt="오늘의 그림일기"
                class="w-full h-full object-cover rounded-lg shadow"
              />
            </template>
            <template v-else>
              <p class="text-black mb-3 md:mb-4 text-sm md:text-base text-center px-2">
                {{
                  hasChild && selectedChild && selectedChild.name
                    ? getSubjectSentence(selectedChild.name)
                    : "오늘의 활동을 기다리고 있어요."
                }}
              </p>
              <button
                @click="goToActivity"
                class="bg-blue-600 text-white px-3 md:px-4 py-2 rounded hover:bg-white hover:text-blue-600 font-paper text-sm md:text-base"
              >
                {{ hasChild ? "활동하러 가기" : "아이 등록하러 가기" }}
              </button>
            </template>
          </div>
        </div>

        <div>
          <div v-if="hasChild" class="h-[280px] sm:h-[320px] md:h-[350px] lg:h-[400px] flex flex-col">
            <!-- 상단 탭 영역 -->
            <div class="flex items-start relative z-10 overflow-x-auto scrollbar-hide">
              <!-- 아이들 탭 -->
              <div
                v-for="(child, index) in childrenList"
                :key="child.id"
                @click="selectedChildIndex = index"
                class="relative flex-shrink-0"
              >
                <div
                  class="px-2 sm:px-3 md:px-4 py-1 sm:py-2 rounded-t-lg cursor-pointer transition-colors mr-1 hover:text-orange-500 hover:font-bold text-xs sm:text-sm md:text-base"
                  :class="{
                    'bg-yellow-200 text-black font-bold':
                      selectedChildIndex === index,
                    'bg-yellow-200 text-gray-700': selectedChildIndex !== index,
                  }"
                  style="
                    background-color: #fef08a !important;
                    margin-bottom: -1px;
                  "
                >
                  {{ child.name }}
                </div>
              </div>

              <!-- 아이 추가 버튼 탭 -->
              <div
                @click="goToChildRegister"
                class="px-2 sm:px-3 py-1 sm:py-2 bg-gray-600 text-white rounded-t-lg cursor-pointer hover:bg-black transition-colors flex items-center gap-1 flex-shrink-0"
              >
                <span class="text-xs sm:text-sm">아이추가 +</span>
              </div>
            </div>

            <!-- 선택된 아이의 프로필 카드 -->
            <div
              class="bg-yellow-200 rounded-lg flex-1 p-3 sm:p-4 md:p-6 flex flex-col items-center relative"
              style="
                background-color: #fef08a !important;
                border-top-left-radius: 0;
                border-top-right-radius: 0.5rem;
              "
            >
              <!-- 중앙 프로필 이미지 -->
              <div class="flex-1 flex items-center justify-center">
                <img
                  :src="
                    selectedChild?.profileImage ||
                    'https://placehold.co/200x200'
                  "
                  alt="아이 프로필"
                  class="w-32 h-32 sm:w-40 sm:h-40 md:w-48 md:h-48 rounded-full object-cover border-2 sm:border-4 border-white shadow-lg"
                  @error="
                    (e) => (e.target.src = 'https://placehold.co/200x200')
                  "
                />
              </div>

              <!-- 하단 정보 -->
              <div class="w-full text-center space-y-1 sm:space-y-2">
                <p class="text-sm sm:text-base md:text-lg font-bold text-black">
                  나이 :
                  {{
                    selectedChild ? calculateAge(selectedChild.birthDate) : 0
                  }}세(만
                  {{
                    selectedChild
                      ? calculateAge(selectedChild.birthDate) - 1
                      : 0
                  }}세)
                </p>
                <p class="text-sm sm:text-base md:text-lg font-bold text-black">
                  관심사 :
                  {{
                    selectedChild?.interests
                      ? selectedChild.interests.slice(0, 2).join(", ")
                      : "없음"
                  }}{{
                    selectedChild?.interests &&
                    selectedChild.interests.length > 2
                      ? " ..."
                      : ""
                  }}
                </p>
              </div>
            </div>
          </div>

          <div
            v-else
            class="bg-yellow-100 h-[280px] sm:h-[320px] md:h-[350px] lg:h-[400px] rounded-lg shadow p-4 sm:p-6 flex flex-col items-center justify-center"
          >
            <p class="text-sm sm:text-base text-black-700 mb-3 sm:mb-4 text-center">아직 등록된 아이가 없습니다.</p>
            <button
              @click="goToChildRegister"
              class="bg-purple-600 text-white px-3 sm:px-4 py-2 rounded hover:bg-purple-700 font-paper text-sm sm:text-base"
            >
              아이 등록하러 가기
            </button>
          </div>
        </div>
      </div>
    </section>

    <!-- 3. 기능 카드 -->
    <section class="max-w-8xl bg-white !mb-6 md:!mb-10">
      <div class="max-w-6xl container mx-auto px-4 md:px-6 py-6 md:py-12 bg-white mb-6 md:mb-10">
        <h3 class="text-xl md:text-2xl font-semibold text-center mb-2">
          다온과 함께하는 특별한 여정
        </h3>
        <p class="text-sm md:text-base text-center text-gray-600 mb-4 md:mb-8">
          다문화 가정의 행복한 내일을 위해 다온이 함께합니다.
        </p>
        <!-- 모바일에서는 2x2, 데스크톱에서는 1x4 레이아웃 -->
        <div class="grid grid-cols-2 lg:grid-cols-4 gap-3 sm:gap-4 md:gap-6">
          <BaseCard variant="schedule" @click="openScheduleModal" />
          <BaseCard variant="growth" :to="{ name: 'Growth' }" />
          <BaseCard variant="community" :to="{ name: 'Community' }" />
          <BaseCard variant="language" :to="{ name: 'OCRTool' }" />
        </div>
      </div>
    </section>

    <!-- 2. 캘린더 + 일정 목록 -->
    <section class="mb-6 md:mb-10">
      <!-- 오늘의 일정 제목 -->
      <div class="text-center font-paperBold mb-4 md:mb-6">
        <h2 class="text-2xl md:text-3xl text-gray-800">오늘의 일정</h2>
      </div>

      <div
        class="max-w-6xl container mx-auto px-4 md:px-6 pt-4 pb-8 md:pb-12 grid grid-cols-1 lg:grid-cols-3 gap-6 md:gap-8"
      >
        <!-- 달력 -->
        <div class="lg:col-span-2 bg-white rounded-xl shadow p-4 md:p-6 h-[400px] sm:h-[480px] md:h-[530px]">
          <CalendarWidget
            :events="
              events.map((ev) => ({ ...ev, date: ev.eventDate || ev.date }))
            "
            @update-month="onMonthChange"
            @date-selected="onDateSelected"
          />
        </div>

        <!-- 일정 목록 -->
        <div class="bg-white rounded-xl shadow p-4 md:p-6 flex flex-col h-[400px] sm:h-[480px] md:h-[530px]">
          <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center mb-4 gap-2 sm:gap-0">
            <h3 class="text-lg md:text-xl font-semibold">일정 목록</h3>
            <button
              @click="openModal"
              class="text-xs sm:text-sm text-blue-600 hover:underline bg-blue-50 px-2 py-1 rounded-md transition-colors"
            >
              + 일정 추가
            </button>
            <AddEventModal
              v-model="modalVisible"
              :initial-date="selectedDate"
              @add-event="handleAddEvent"
            />
          </div>
          <div class="flex-1 overflow-y-auto overflow-x-visible pr-1 md:pr-2 py-2">
            <template v-if="filteredEvents.length">
              <div class="space-y-6">
                <ScheduleCard
                  v-for="ev in filteredEvents"
                  :key="ev.id"
                  :id="ev.id"
                  :date="ev.date"
                  :title="ev.title"
                  :description="ev.description"
                  :all-events="filteredEvents"
                  @update="handleUpdate"
                  @delete="handleDelete"
                />
              </div>
            </template>
            <template v-else>
              <div
                class="h-full flex items-center justify-center text-gray-500 text-sm md:text-base text-center px-2"
              >
                아직 등록된 일정이 없습니다.
              </div>
            </template>
          </div>
        </div>
      </div>
    </section>

    <!-- 감정 리포트 모달 -->
    <EmotionReportModal
      v-if="hasActivity"
      v-model="showEmotionReportModal"
      :child-name="
        selectedChild && selectedChild.name ? selectedChild.name : ''
      "
      :report-date="dayjs().format('YYYY-MM-DD')"
      :report-data="todayActivity"
      :show-navigation="false"
    />

    <!-- 네비게이션 튜토리얼 모달 -->
    <NavigationTutorialModal
      v-model="showTutorialModal"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from "vue";
import { useRouter } from "vue-router";
import dayjs from "dayjs";

import AddEventModal from "@/components/modal/AddEventModal.vue";
import CalendarWidget from "@/components/widget/CalendarWidget.vue";
import ScheduleCard from "@/components/card/ScheduleCard.vue";
import BaseCard from "@/components/card/BaseCard.vue";
import EmotionReportModal from "@/components/modal/EmotionReportModal.vue";
import NavigationTutorialModal from "@/components/modal/NavigationTutorialModal.vue";
import { useAuthStore } from "@/store/auth";
import { useChildStore } from "@/store/child";
import { childService } from "@/services/childService.js";

import { useNotification } from "@/composables/useNotification.js";
import {
  fetchMonthlyEvents,
  createEvent,
  updateEvent,
  deleteEvent,
} from "@/store/calendar";

const router = useRouter();
const auth = useAuthStore();
const childStore = useChildStore();
const { showWarning, showInfo } = useNotification();

/* 일정 상태 */
const events = ref([]);
const selectedMonth = ref(dayjs().format("YYYY-MM"));
const selectedDate = ref(null);

async function loadEvents(year, month) {
  try {
    events.value = await fetchMonthlyEvents(year, month);
  } catch (e) {
    console.error("일정 불러오기 실패:", e);
  }
}

const filteredEvents = computed(() =>
  events.value
    .map((ev) => ({
      ...ev,
      id: ev.id || ev.calendarId,
      date: ev.eventDate || ev.date,
    }))
    .filter((ev) => dayjs(ev.date).format("YYYY-MM") === selectedMonth.value)
    .sort((a, b) => dayjs(a.date).unix() - dayjs(b.date).unix())
);

/* childStore 연동 */
const hasChild = computed(() => childStore.hasChildren);
const selectedChild = computed(() => childStore.selectedChild);
const childrenList = computed(() => childStore.children);
const selectedChildIndex = computed({
  get: () => childStore.selectedChildIndex,
  set: (index) => {
    if (childrenList.value[index]) {
      childStore.selectChild(childrenList.value[index].id);
    }
  },
});

/* 마운트 시 로딩 */
onMounted(async () => {
  const [year, month] = selectedMonth.value.split("-").map(Number);
  await loadEvents(year, month);

  if (typeof childStore.initialize === "function") {
    await childStore.initialize();
  } else if (typeof childStore.loadChildren === "function") {
    const userId = auth.user?.id;
    if (userId) await childStore.loadChildren(userId);
  }

  // 첫 회원가입 후 튜토리얼 자동 실행
  if (router.currentRoute.value.query.firstLogin === 'true') {
    // 쿼리 파라미터 제거하고 페이지 상단으로 스크롤
    router.replace({ name: 'Dashboard' });
    window.scrollTo({ top: 0, behavior: 'smooth' });
    
    // 스크롤 후 튜토리얼 모달 표시
    setTimeout(() => {
      showTutorialModal.value = true;
    }, 800);
  }

  // 스토어가 비어있을 때 직접 로드 보정 (child store에서 이미 새로운 API를 사용하므로 단순화)
  try {
    const userId = auth.user?.id;
    if (userId && (!childrenList.value || childrenList.value.length === 0)) {
      await childStore.loadChildren();
    }
  } catch (e) {
    console.warn("직접 자녀 목록 로드 실패:", e?.message || e);
  }

  if (
    childrenList.value.length > 0 &&
    selectedChildIndex.value >= childrenList.value.length
  ) {
    selectedChildIndex.value = 0;
  }

  await loadTodayActivity();
});

/* 목록 길이 변하면 선택 인덱스 보정 */
watch(
  () => childrenList.value.length,
  (len) => {
    if (len > 0 && selectedChildIndex.value >= len) {
      selectedChildIndex.value = 0;
    }
  }
);

/* 선택된 아이가 변경되면 오늘 활동 다시 로드 */
watch(
  () => selectedChild.value?.id,
  async (newChildId) => {
    if (newChildId) {
      await loadTodayActivity();
    }
  }
);

/* 달 변경 */
function onMonthChange(newYm) {
  selectedMonth.value = newYm;
  const [year, month] = newYm.split("-").map(Number);
  loadEvents(year, month);
}

/* 일정 모달/CRUD */
const modalVisible = ref(false);
function openModal() {
  selectedDate.value = null;
  modalVisible.value = true;
}
function openScheduleModal() {
  selectedDate.value = null;
  modalVisible.value = true;
}

/* 날짜 선택 */
function onDateSelected(date) {
  selectedDate.value = dayjs(date).format("YYYY-MM-DD");
  modalVisible.value = true;
}

async function handleAddEvent({ title, date, description }) {
  try {
    await createEvent({ title, eventDate: date, description });
    const [y, m] = selectedMonth.value.split("-").map(Number);
    await loadEvents(y, m);
    modalVisible.value = false;
  } catch (e) {
    showWarning("일정 추가 실패", e.message);
  }
}

async function handleUpdate({ id, newDate, newTitle, newDescription }) {
  try {
    await updateEvent(id, {
      eventDate: newDate,
      title: newTitle,
      description: newDescription,
    });
    const [y, m] = selectedMonth.value.split("-").map(Number);
    await loadEvents(y, m);
  } catch (e) {
    showWarning("일정 수정 실패", e.message);
  }
}

async function handleDelete(id) {
  try {
    await deleteEvent(id);
    const [y, m] = selectedMonth.value.split("-").map(Number);
    await loadEvents(y, m);
  } catch (e) {
    showWarning("일정 삭제 실패", e.message);
  }
}

/* 오늘의 활동 */
const todayActivity = ref(null);
const isLoadingActivity = ref(false);
const hasActivity = computed(() => !!todayActivity.value);

async function loadTodayActivity() {
  if (!selectedChild.value) {
    todayActivity.value = null;
    return;
  }
  try {
    isLoadingActivity.value = true;
    const today = dayjs();
    const year = today.year();
    const month = today.month() + 1;

    const response = await childService.getMonthlyDiaries(
      selectedChild.value.id,
      year,
      month
    );
    const arr = Array.isArray(response) ? response : response ? [response] : [];
    const todayStr = today.format("YYYY-MM-DD");

    const found = arr.find((d) => {
      const diaryDate = d.createdAt ? d.createdAt.split("T")[0] : d.date;
      return diaryDate === todayStr;
    });

    todayActivity.value = found || null;
  } catch (e) {
    console.error("월별 다이어리 조회 실패:", e);
    todayActivity.value = null;
  } finally {
    isLoadingActivity.value = false;
  }
}

/* 감정 리포트 모달 */
const showEmotionReportModal = ref(false);
const showTutorialModal = ref(false);

function openTodayReport() {
  if (hasActivity.value && todayActivity.value) {
    showEmotionReportModal.value = true;
  } else {
    showInfo(
      selectedChild.value && selectedChild.value.name
        ? `${selectedChild.value.name}의 멋진 하루가 시작됐어요! ✨<br>오늘 하루를 기록해주세요`
        : "멋진 하루가 시작됐어요! ✨<br>오늘 하루를 기록해주세요",
      "오늘의 활동"
    );
  }
}

/* 날짜/조사 유틸 */
function formatDate(dateString) {
  if (!dateString) return "";
  const date = new Date(dateString);
  return date.toLocaleDateString("ko-KR", {
    year: "numeric",
    month: "long",
    day: "numeric",
  });
}
function getParticle(name, particles) {
  if (!name) return particles[0];
  const lastChar = name[name.length - 1];
  const code = lastChar.charCodeAt(0);
  if (code < 0xac00 || code > 0xd7a3) return particles[0];
  const hasJongseong = (code - 0xac00) % 28 !== 0;
  return hasJongseong ? particles[0] : particles[1];
}
function getSubjectSentence(name) {
  return `${name}의 오늘의 활동을 기다리고 있어요`;
}
function getObjectSentence(name) {
  const p = getParticle(name, ["이", "가"]);
  return `${name}${p} 활동하게 해주세요.`;
}

/* 라우팅 */
function goToChildRegister() {
  router.push({ name: "RegisterChild" });
}
function goToChildEdit() {
  router.push({ name: "EditChild" });
}
function goToChildProfile() {
  router.push({
    name: hasChild.value ? "ChildProfile" : "RegisterChild",
  });
}
function goToActivity() {
  if (hasChild.value && selectedChild.value) {
    router.push({
      name: "ChildMain",
      params: { childId: selectedChild.value.id },
    });
  } else {
    router.push({ name: "RegisterChild" });
  }
}

/* 나이 계산 */
function calculateAge(birthDate) {
  if (!birthDate) return 0;
  const today = new Date();
  const birth = new Date(birthDate);
  let age = today.getFullYear() - birth.getFullYear();
  const md = today.getMonth() - birth.getMonth();
  if (md < 0 || (md === 0 && today.getDate() < birth.getDate())) age--;
  return age + 1; // 한국식(만 + 1)
}
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>

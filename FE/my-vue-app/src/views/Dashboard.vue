<template>
  <div class="font-paper">
    <!-- 1. Hero 섹션 -->
    <section class="relative overflow-hidden py-16">
      <div
        class="max-w-6xl container mx-auto px-6 flex flex-col-reverse md:flex-row items-center"
      >
        <!-- 텍스트 -->
        <div class="md:w-1/2 mt-8 md:mt-0 px-4">
          <h2 class="text-3xl md:text-4xl font-bold text-gray-800 mb-4">
            언어의 벽을 넘어,<br />
            <span class="text-pink-500">일상의 행복</span>을 만듭니다.
          </h2>
          <p class="text-gray-700 text-xl">
            언어가 달라도 마음이 통할 수 있도록,<br />
            일상의 작은 순간들이 행복으로 이어지는 다온입니다.
          </p>
        </div>
        <!-- 이미지 -->
        <div class="md:w-1/2 flex justify-center relative">
          <img
            src="@/assets/images/hero-group.png"
            alt="다문화 가족"
            class="rounded-lg w-full max-w-sm relative z-10"
          />
        </div>
      </div>
    </section>

    <!-- 2. 캘린더 + 일정 목록 -->
    <section
      class="max-w-6xl container mx-auto px-6 py-12 grid grid-cols-1 lg:grid-cols-3 gap-8"
    >
      <!-- 달력 -->
      <div class="lg:col-span-2 bg-white rounded-xl shadow p-6 h-[530px]">
        <CalendarWidget
          :events="
            events.map((ev) => ({
              ...ev,
              date: ev.eventDate || ev.date, // date 필드가 항상 있음
            }))
          "
          @update-month="onMonthChange"
        />
      </div>

      <!-- 일정 목록 -->
      <div class="bg-white rounded-xl shadow p-6 flex flex-col h-[530px]">
        <div class="flex justify-between items-center mb-4">
          <h3 class="text-xl font-semibold">일정 목록</h3>
          <button
            @click="openModal"
            class="text-sm text-blue-600 hover:underline"
          >
            + 일정 추가
          </button>
          <AddEventModal v-model="modalVisible" @add-event="handleAddEvent" />
        </div>
        <div class="flex-1 overflow-y-auto pr-2">
          <template v-if="filteredEvents.length">
            <div class="space-y-4">
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
            <div class="h-full flex items-center justify-center text-gray-500">
              아직 등록된 일정이 없습니다.
            </div>
          </template>
        </div>
      </div>
    </section>

    <!-- 3. 기능 카드 -->
    <section class="max-w-6xl container mx-auto px-6 py-12 bg-white mb-20">
      <h3 class="text-2xl font-semibold text-center mb-2">
        다온과 함께하는 특별한 여정
      </h3>
      <p class="text-center text-gray-600 mb-8">
        다문화 가정의 행복한 내일을 위해 다온이 함께합니다.
      </p>
      <div class="grid grid-cols-2 md:grid-cols-4 gap-6">
        <BaseCard variant="schedule" link="/schedule" />
        <BaseCard variant="growth" link="/growth" />
        <BaseCard variant="community" :to="{ name: 'Community' }" />
        <BaseCard variant="language" :to="{ name: 'OCRTool' }" />
      </div>
    </section>

    <!-- 4. 오늘의 활동 -->
    <section
      class="max-w-6xl container mx-auto px-6 py-5 bg-white rounded-xl shadow mb-20 h-[520px]"
    >
      <div class="flex justify-between items-center mb-6">
        <h3 class="text-2xl font-semibold">오늘의 활동</h3>
        <button
          @click="openTodayReport"
          class="text-sm text-gray-600 hover:underline"
        >
          자세히 보기 →
        </button>
      </div>
      <div class="grid grid-cols-1 md:grid-cols-3 gap-6 items-start">
        <div class="md:col-span-2">
          <div
            class="bg-gray-500 h-[400px] rounded-lg shadow p-6 flex flex-col items-center justify-center"
          >
            <template v-if="isLoadingActivity">
              <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-white"></div>
              <p class="text-white mt-4">활동 데이터를 불러오는 중...</p>
            </template>
            <template v-else-if="hasActivity && todayActivity">
              <img
                :src="todayActivity.imageUrl"
                alt="오늘의 그림일기"
                class="w-full h-full object-cover rounded-lg shadow"
              />
            </template>
            <template v-else>
              <p class="text-white mb-4">
                {{
                  hasChild && selectedChild && selectedChild.name
                    ? getSubjectSentence(selectedChild.name)
                    : "아직 활동하지 않았습니다."
                }}
              </p>
              <button
                @click="goToActivity"
                class="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 font-paper"
              >
                {{ hasChild ? "활동하러 가기" : "아이 등록하러 가기" }}
              </button>
            </template>
          </div>
        </div>
        <div>
          <div v-if="hasChild" class="h-[400px] flex flex-col">
            <!-- 상단 탭 영역 -->
            <div class="flex items-start relative z-10">
              <!-- 아이들 탭 -->
              <div
                v-for="(child, index) in childrenList"
                :key="child.id"
                @click="selectedChildIndex = index"
                class="relative"
              >
                <!-- 탭 내용 -->
                <div
                  class="px-4 py-2 rounded-t-lg cursor-pointer transition-colors mr-1"
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
                class="px-3 py-2 bg-gray-600 text-white rounded-t-lg cursor-pointer hover:bg-gray-700 transition-colors flex items-center gap-1"
              >
                <span class="text-sm">아이추가 +</span>
              </div>
            </div>

            <!-- 선택된 아이의 프로필 카드 -->
            <div
              class="bg-yellow-200 rounded-lg flex-1 p-6 flex flex-col items-center relative"
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
                  class="w-48 h-48 rounded-full object-cover border-4 border-white shadow-lg"
                />
              </div>

              <!-- 하단 정보 -->
              <div class="w-full text-center space-y-2">
                <p class="text-lg font-bold text-black">
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
                <p class="text-lg font-bold text-black">
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
            class="bg-yellow-100 h-[400px] rounded-lg shadow p-6 flex flex-col items-center justify-center"
          >
            <p class="text-black-700 mb-4">아직 등록된 아이가 없습니다.</p>
            <button
              @click="goToChildRegister"
              class="bg-purple-600 text-white px-4 py-2 rounded hover:bg-purple-700 font-paper"
            >
              아이 등록하러 가기
            </button>
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
const { showWarning } = useNotification();

// 일정 상태
const events = ref([]);

// 현재 선택된 월
const selectedMonth = ref(dayjs().format("YYYY-MM"));

async function loadEvents(year, month) {
  try {
    events.value = await fetchMonthlyEvents(year, month);
  } catch (e) {
    console.error("일정 불러오기 실패:", e);
  }
}

// 일정 데이터 서버에서 받아오는 함수
const filteredEvents = computed(() =>
  events.value
    .map((ev) => ({
      ...ev,
      id: ev.id || ev.calendarId, // ← calendarId를 id로 통일!
      date: ev.eventDate || ev.date, // ← 날짜도 통일
    }))
    .filter((ev) => dayjs(ev.date).format("YYYY-MM") === selectedMonth.value)
    .sort((a, b) => dayjs(a.date).unix() - dayjs(b.date).unix())
);

// 마운트 시 바로 일정 불러오기
onMounted(() => {
  const [year, month] = selectedMonth.value.split("-").map(Number);
  loadEvents(year, month);
  childStore.initialize();
});

// 달 변경 시 서버에서 다시 불러오기
function onMonthChange(newYm) {
  selectedMonth.value = newYm;
  const [year, month] = newYm.split("-").map(Number);
  loadEvents(year, month);
}

// 일정 추가 모달
const modalVisible = ref(false);
function openModal() {
  modalVisible.value = true;
}

// 일정 추가, 수정, 삭제는 모두 서버에 요청 후 새로고침!
async function handleAddEvent({ title, date, description }) {
  try {
    await createEvent({
      title,
      eventDate: date,
      description,
    });
    // 추가 후 다시 불러오기!
    const [year, month] = selectedMonth.value.split("-").map(Number);
    await loadEvents(year, month);
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
    const [year, month] = selectedMonth.value.split("-").map(Number);
    await loadEvents(year, month);
  } catch (e) {
    showWarning("일정 수정 실패", e.message);
  }
}

async function handleDelete(id) {
  try {
    await deleteEvent(id);
    const [year, month] = selectedMonth.value.split("-").map(Number);
    await loadEvents(year, month);
  } catch (e) {
    showWarning("일정 삭제 실패", e.message);
  }
}

// 오늘의 활동 데이터
const todayActivity = ref(null);
const isLoadingActivity = ref(false);

// childStore의 computed 속성 사용
const hasChild = computed(() => childStore.hasChildren);
const selectedChild = computed(() => childStore.selectedChild);
const childrenList = computed(() => childStore.children);
const selectedChildIndex = computed({
  get: () => childStore.selectedChildIndex,
  set: (index) => {
    if (childrenList.value[index]) {
      childStore.selectChild(childrenList.value[index].id);
      // watch가 자동으로 loadTodayActivity() 호출함
    }
  },
});

// 컴포넌트 마운트 시 아이 정보 로드
onMounted(async () => {
  await childStore.initialize();
  await loadTodayActivity();
});

// 선택된 아이가 변경될 때마다 활동 데이터 다시 로드
watch(selectedChild, async (newChild, oldChild) => {
  if (newChild && newChild.id !== oldChild?.id) {
    console.log('🔍 selectedChild 변경됨:', newChild.id);
    await loadTodayActivity();
  }
}, { deep: true });

// 오늘 활동이 있는지 여부
const hasActivity = computed(() => !!todayActivity.value);

// 선택된 아이의 오늘 다이어리 조회
async function loadTodayActivity() {
  if (!selectedChild.value) {
    todayActivity.value = null;
    return;
  }

  try {
    isLoadingActivity.value = true;
    // 한국 시간 기준으로 오늘 날짜 계산 (브라우저 로컬 시간 사용)
    const today = dayjs(); // 로컬 시간 그대로 사용
    const year = today.year();
    const month = today.month() + 1; // dayjs는 0부터 시작하므로 +1

    console.log('🔍 한국 시간 기준:', {
      today: today.format('YYYY-MM-DD HH:mm:ss'),
      childId: selectedChild.value.id,
      year,
      month
    });

    // 월별 다이어리 조회
    const response = await childService.getMonthlyDiaries(selectedChild.value.id, year, month);
    console.log('🔍 월별 다이어리 응답:', response);
    
    // ChildDrawing.vue와 동일한 방식으로 처리
    const responseArray = Array.isArray(response) ? response : (response ? [response] : []);
    console.log('🔍 responseArray:', responseArray);
    
    // 오늘 날짜와 일치하는 다이어리 찾기
    const todayDateStr = today.format('YYYY-MM-DD');
    
    const todayDiary = responseArray.find(diary => {
      console.log('🔍 다이어리 개별 항목:', diary);
      
      // ChildDrawing.vue와 동일한 방식: createdAt에서 날짜 부분만 추출
      const diaryDate = diary.createdAt ? diary.createdAt.split('T')[0] : diary.date;
      console.log(`🔍 날짜 비교: ${diaryDate} vs ${todayDateStr}`);
      
      return diaryDate === todayDateStr;
    });

    console.log('🔍 오늘 다이어리 결과:', todayDiary);
    todayActivity.value = todayDiary || null;
    
  } catch (error) {
    console.error('월별 다이어리 조회 실패:', error);
    todayActivity.value = null;
  } finally {
    isLoadingActivity.value = false;
  }
}

// 감정 리포트 모달 관련
const showEmotionReportModal = ref(false);

// 오늘의 리포트 모달 열기
function openTodayReport() {
  if (hasActivity.value && todayActivity.value) {
    showEmotionReportModal.value = true;
  } else {
    showWarning(
      selectedChild.value && selectedChild.value.name
        ? getObjectSentence(selectedChild.value.name)
        : "아직 활동하지 않았습니다!",
      "아직 활동하지 않았습니다!"
    );
  }
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

// 한국어 조사 선택 함수
function getParticle(name, particles) {
  if (!name) return particles[0];

  const lastChar = name[name.length - 1];
  const lastCharCode = lastChar.charCodeAt(0);

  // 한글인지 확인
  if (lastCharCode < 0xac00 || lastCharCode > 0xd7a3) {
    return particles[0]; // 한글이 아니면 첫 번째 조사 사용
  }

  // 받침 여부 확인 (종성이 있으면 받침 있음)
  const hasJongseong = (lastCharCode - 0xac00) % 28 !== 0;

  return hasJongseong ? particles[0] : particles[1]; // 받침있으면 첫번째, 없으면 두번째
}

// 조사가 포함된 문장 생성 함수들
function getSubjectSentence(name) {
  const particle = getParticle(name, ["은", "는"]);
  return `${name}${particle} 아직 활동하지 않았습니다.`;
}

function getObjectSentence(name) {
  const particle = getParticle(name, ["이", "가"]);
  return `${name}${particle} 활동하게 해주세요.`;
}

// 아이 등록/수정 페이지로 이동
function goToChildRegister() {
  router.push({ name: "RegisterChild" });
}

function goToChildEdit() {
  router.push({ name: "EditChild" });
}

// 활동하러 가기 버튼 클릭
function goToActivity() {
  if (hasChild.value && selectedChild.value) {
    // 선택된 아이 정보와 함께 ChildMain으로 이동
    router.push({
      name: "ChildMain",
      params: { childId: selectedChild.value.id },
    });
  } else {
    router.push({ name: "RegisterChild" });
  }
}

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
</script>

<style scoped>
/* 모달 전환 효과 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>

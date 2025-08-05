<template>
  <div class="py-8 px-4">
    <div class="mx-auto max-w-5xl bg-white pt-10 pb-10 rounded-2xl mb-10">
      <!-- 상단 타이틀 -->
      <div class="text-center mb-12">
        <h1 class="text-4xl font-paperBold text-gray-800">아이 프로필</h1>
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
        <div
          v-if="childrenList.length > 1"
          class="flex justify-center space-x-4 mb-8"
        >
          <button
            v-for="(child, index) in childrenList"
            :key="child.id"
            @click="selectedChildIndex = index"
            class="px-6 py-3 rounded-lg font-paperBold transition-colors"
            :class="{
              'bg-purple-500 text-white': selectedChildIndex === index,
              'bg-gray-200 text-gray-700 hover:bg-gray-300':
                selectedChildIndex !== index,
            }"
          >
            {{ child.name }}
          </button>
        </div>

        <!-- 선택된 아이의 프로필 -->
        <div class="grid grid-cols-1 lg:grid-cols-3 gap-8 px-8">
          <!-- 좌측: 프로필 이미지 -->
          <div class="flex flex-col items-center space-y-6">
            <div class="relative">
              <img
                :src="
                  selectedChild.profileImage || 'https://placehold.co/300x300'
                "
                alt="아이 프로필"
                class="w-72 h-72 rounded-full object-cover border-6 border-purple-200 shadow-xl"
              />
            </div>
            <h2 class="text-3xl font-paperBold text-gray-800">
              {{ selectedChild.name }}
            </h2>
          </div>

          <!-- 우측: 상세 정보 -->
          <div class="lg:col-span-2 space-y-6">
            <!-- 기본 정보 -->
            <div class="bg-gray-50 rounded-xl p-6">
              <h3 class="text-2xl font-paperBold text-gray-800 mb-4">
                기본 정보
              </h3>
              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div class="flex items-center space-x-3">
                  <span class="text-purple-500 font-paperBold">🎂</span>
                  <div>
                    <p class="text-sm text-gray-500">생년월일</p>
                    <p class="text-lg font-paper">
                      {{ selectedChild.birthDate }}
                    </p>
                  </div>
                </div>
                <div class="flex items-center space-x-3">
                  <span class="text-purple-500 font-paperBold">👶</span>
                  <div>
                    <p class="text-sm text-gray-500">나이</p>
                    <p class="text-lg font-paper">
                      {{ calculateAge(selectedChild.birthDate) }}세 (만
                      {{ calculateAge(selectedChild.birthDate) - 1 }}세)
                    </p>
                  </div>
                </div>
                <div class="flex items-center space-x-3">
                  <span class="text-purple-500 font-paperBold">⚧</span>
                  <div>
                    <p class="text-sm text-gray-500">성별</p>
                    <p class="text-lg font-paper">{{ selectedChild.gender }}</p>
                  </div>
                </div>
                <div class="flex items-center space-x-3">
                  <span class="text-purple-500 font-paperBold">📅</span>
                  <div>
                    <p class="text-sm text-gray-500">등록일</p>
                    <p class="text-lg font-paper">
                      {{ formatDate(selectedChild.registeredAt) }}
                    </p>
                  </div>
                </div>
              </div>
            </div>

            <!-- 관심사 -->
            <div class="bg-gray-50 rounded-xl p-6">
              <h3 class="text-2xl font-paperBold text-gray-800 mb-4">관심사</h3>
              <div
                v-if="
                  selectedChild.interests && selectedChild.interests.length > 0
                "
                class="flex flex-wrap gap-3"
              >
                <span
                  v-for="interest in selectedChild.interests"
                  :key="interest"
                  class="bg-purple-100 text-purple-800 px-4 py-2 rounded-full text-sm font-paper"
                >
                  {{ interest }}
                </span>
              </div>
              <p v-else class="text-gray-500 font-paper">
                등록된 관심사가 없습니다.
              </p>
            </div>

            <!-- 버튼 영역 -->
            <div class="flex gap-4 pt-6">
              <button
                @click="goToEdit"
                class="flex-1 bg-purple-500 text-white py-4 rounded-lg text-lg font-paperBold hover:bg-purple-600 transition-colors"
              >
                프로필 수정
              </button>
              <button
                @click="goToActivity"
                class="flex-1 bg-blue-500 text-white py-4 rounded-lg text-lg font-paperBold hover:bg-blue-600 transition-colors"
              >
                활동하러 가기
              </button>
            </div>
          </div>
        </div>

        <!-- 구분선 -->
        <div class="border-t-2 border-gray-300 mx-8 my-8"></div>

        <!-- 감정 리포트 달력 -->
        <div class="px-8">
          <div class="flex justify-between items-center mb-6">
            <h3 class="text-2xl font-paperBold text-gray-800">감정 리포트</h3>
            <!-- 아이 선택 체크박스 -->
            <div class="flex gap-3">
              <label
                v-for="child in childrenList"
                :key="child.id"
                class="flex items-center gap-2 px-4 py-2 rounded-lg border-2 cursor-pointer transition-colors"
                :style="{
                  borderColor: selectedChildrenForReport.includes(child.name)
                    ? getChildColor(child.name)
                    : '#d1d5db',
                  backgroundColor: selectedChildrenForReport.includes(
                    child.name
                  )
                    ? getChildColor(child.name) + '20'
                    : 'white',
                }"
              >
                <input
                  type="checkbox"
                  :value="child.name"
                  v-model="selectedChildrenForReport"
                  class="sr-only"
                />
                <span
                  class="text-sm font-paperBold transition-colors"
                  :style="{
                    color: selectedChildrenForReport.includes(child.name)
                      ? getChildColor(child.name)
                      : '#6b7280',
                  }"
                >
                  {{ child.name }}
                </span>
              </label>
            </div>
          </div>
          <div class="bg-white rounded-xl shadow-lg p-6">
            <FullCalendar :options="calendarOptions" ref="calendar" />
          </div>
        </div>
      </div>
    </div>

    <!-- 감정 리포트 모달 -->
    <EmotionReportModal
      v-model="showEmotionReportModal"
      :child-name="selectedReportData?.childName || ''"
      :report-date="selectedReportDate"
      :report-data="selectedReportData || {}"
      :show-navigation="true"
      :has-previous-report="hasPreviousReport"
      :has-next-report="hasNextReport"
      @previous="goToPreviousReport"
      @next="goToNextReport"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import FullCalendar from "@fullcalendar/vue3";
import dayGridPlugin from "@fullcalendar/daygrid";
import interactionPlugin from "@fullcalendar/interaction";
import EmotionReportModal from "@/components/modal/EmotionReportModal.vue";
import { emotionReportsByChild } from "@/data/emotionReports.js";
import {
  getChildColor,
  ensureAllChildrenHaveColors,
} from "@/utils/colorManager.js";

const router = useRouter();

const childrenList = ref([]);
const selectedChildIndex = ref(0);

// 감정 리포트 모달 관련
const showEmotionReportModal = ref(false);
const selectedReportDate = ref("");
const selectedReportData = ref(null);
const currentReportIndex = ref(0);

// 리포트 표시용 선택된 아이들
const selectedChildrenForReport = ref([]);

// 선택된 아이들의 모든 감정 리포트 데이터 (달력 표시용)
const allSelectedReports = computed(() => {
  const reports = [];
  selectedChildrenForReport.value.forEach((childName) => {
    const childData = emotionReportsByChild[childName];
    if (childData) {
      childData.reports.forEach((report) => {
        reports.push({
          ...report,
          childName,
          color: childData.color,
        });
      });
    }
  });
  return reports.sort((a, b) => new Date(a.date) - new Date(b.date));
});

// 아이별 우선순위 (등록 순서) - 김미래가 먼저 등록됨
const childPriority = { 김미래: 0, 김과거: 1 };

// 전체 리포트를 날짜와 아이 우선순위로 정렬 (네비게이션용)
const allReportsForNavigation = computed(() => {
  const reports = [];
  Object.keys(emotionReportsByChild).forEach((childName) => {
    const childData = emotionReportsByChild[childName];
    childData.reports.forEach((report) => {
      reports.push({
        ...report,
        childName,
        color: childData.color,
        priority: childPriority[childName] || 999,
      });
    });
  });
  // 날짜순 먼저, 같은 날짜면 아이 우선순위순으로 정렬
  return reports.sort((a, b) => {
    const dateCompare = new Date(a.date) - new Date(b.date);
    if (dateCompare !== 0) return dateCompare;
    return a.priority - b.priority;
  });
});

// 현재 리포트의 전체 인덱스 찾기
const currentReportGlobalIndex = computed(() => {
  if (!selectedReportData.value) return -1;
  return allReportsForNavigation.value.findIndex(
    (report) =>
      report.date === selectedReportData.value.date &&
      report.childName === selectedReportData.value.childName
  );
});

// 아이별 색상 가져오기 함수는 이제 colorManager에서 import

// 네비게이션 관련 computed
const hasPreviousReport = computed(() => currentReportGlobalIndex.value > 0);
const hasNextReport = computed(
  () =>
    currentReportGlobalIndex.value < allReportsForNavigation.value.length - 1
);

// 모달 외부 버튼 위치 계산 (모달 너비 700px 기준)
const leftPosition = computed(() => "left-[calc(50%-350px-60px)]");
const rightPosition = computed(() => "left-[calc(50%+350px+12px)]");

// FullCalendar 설정
const calendarOptions = computed(() => ({
  plugins: [dayGridPlugin, interactionPlugin],
  initialView: "dayGridMonth",
  locale: "ko",
  headerToolbar: {
    left: "prev,next",
    center: "title",
    right: "today",
  },
  events: allSelectedReports.value.map((report) => ({
    title: `${report.childName} 감정 리포트`,
    date: report.date,
    backgroundColor: report.color,
    borderColor: report.color,
    classNames: ["emotion-report-event"],
  })),
  eventClick: handleEventClick,
  dayCellContent: (info) => {
    const reportsForDate = allSelectedReports.value.filter(
      (report) => report.date === info.dateStr
    );
    const dayNumber = info.dayNumberText.replace("일", "");
    if (reportsForDate.length > 0) {
      // 여러 아이의 리포트가 있을 경우 첫 번째 아이의 색상 사용
      const primaryColor = reportsForDate[0].color;
      return {
        html: `
          <div class="relative">
            <span class="text-sm">${dayNumber}</span>
            <button class="absolute bottom-1 right-1 text-white text-xs px-2 py-1 rounded hover:opacity-80 transition-colors"
                    style="background-color: ${primaryColor}"
                    onclick="window.openEmotionReport('${info.dateStr}')">
              보고서
            </button>
          </div>
        `,
      };
    }
    return { html: `<span class="text-sm">${dayNumber}</span>` };
  },
  height: "auto",
  aspectRatio: 1.8,
}));

// 아이 정보 로드
function loadChildren() {
  const children = ensureAllChildrenHaveColors(); // 기존 아이들에게도 색상 할당
  childrenList.value = children;
}

// 감정 리포트 관련 함수
function handleEventClick(info) {
  openEmotionReport(info.event.startStr);
}

function openEmotionReport(dateStr) {
  const reportsForDate = allSelectedReports.value.filter(
    (report) => report.date === dateStr
  );
  if (reportsForDate.length > 0) {
    // 해당 날짜의 첫 번째 리포트 선택 (우선순위 순으로 정렬됨)
    const sortedReports = reportsForDate.sort(
      (a, b) =>
        (childPriority[a.childName] || 999) -
        (childPriority[b.childName] || 999)
    );
    const report = sortedReports[0];

    selectedReportDate.value = dateStr;
    selectedReportData.value = { ...report };
    showEmotionReportModal.value = true;
  }
}

// 이전/다음 리포트 네비게이션
function goToPreviousReport() {
  if (hasPreviousReport.value) {
    const prevIndex = currentReportGlobalIndex.value - 1;
    const report = allReportsForNavigation.value[prevIndex];
    selectedReportDate.value = report.date;
    selectedReportData.value = { ...report };
  }
}

function goToNextReport() {
  if (hasNextReport.value) {
    const nextIndex = currentReportGlobalIndex.value + 1;
    const report = allReportsForNavigation.value[nextIndex];
    selectedReportDate.value = report.date;
    selectedReportData.value = { ...report };
  }
}

// 컴포넌트 마운트 시 아이 정보 로드
onMounted(() => {
  loadChildren();
  // 전역 함수로 등록 (달력의 HTML 버튼에서 호출하기 위해)
  window.openEmotionReport = openEmotionReport;

  // 기본적으로 모든 아이 선택
  setTimeout(() => {
    selectedChildrenForReport.value = childrenList.value.map(
      (child) => child.name
    );
  }, 100);
});

const hasChild = computed(() => childrenList.value.length > 0);
const selectedChild = computed(
  () => childrenList.value[selectedChildIndex.value] || {}
);

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
  router.push({ name: "ChildMain" });
}
</script>

<style scoped>
/* FullCalendar 커스텀 스타일 */
:deep(.fc-toolbar-title) {
  font-family: "PaperlogySemiBold", sans-serif;
  font-size: 1.5rem;
  color: #374151;
}

:deep(.fc-button) {
  background-color: #8b5cf6;
  border-color: #8b5cf6;
  font-family: "Paperlog", sans-serif;
}

:deep(.fc-button:hover) {
  background-color: #7c3aed;
  border-color: #7c3aed;
}

:deep(.fc-daygrid-day) {
  position: relative;
  min-height: 80px;
}

:deep(.fc-daygrid-day-number) {
  font-family: "Paperlog", sans-serif;
  color: #374151;
}

:deep(.emotion-report-event) {
  border-radius: 4px;
  font-family: "Paperlog", sans-serif;
  font-size: 0.75rem;
}

:deep(.fc-event-title) {
  font-weight: 600;
}

/* 달력 셀 내 버튼 스타일 */
:deep(.fc-daygrid-day-frame) {
  position: relative;
}

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

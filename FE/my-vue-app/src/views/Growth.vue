<template>
  <div class="py-4 md:py-8 px-4">
    <div class="mx-auto max-w-6xl bg-white pt-6 md:pt-10 pb-6 md:pb-10 rounded-xl md:rounded-2xl mb-6 md:mb-10">
      <!-- 상단 타이틀 -->
      <div class="text-center mb-6 md:mb-12">
        <h1 class="text-2xl md:text-4xl font-paperBold text-gray-800">성장 기록</h1>
        <p class="text-sm md:text-lg text-gray-600 mt-2 md:mt-4 font-paper px-4">아이의 성장 과정을 상세히 기록하고 추적하세요</p>
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
      <div v-else class="space-y-8">

        <!-- 감정 리포트 달력 -->
        <div class="px-4 md:px-8">
          <div class="flex flex-col space-y-4 md:flex-row md:justify-between md:items-center md:space-y-0 mb-4 md:mb-6">
            <h3 class="text-xl md:text-2xl font-paperBold text-gray-800">감정 리포트</h3>
            <!-- 아이 선택 체크박스 -->
            <div class="flex flex-wrap gap-2 md:gap-3">
              <label
                v-for="child in childrenList"
                :key="child.id"
                class="flex items-center gap-1 md:gap-2 px-3 md:px-4 py-2 rounded-lg border-2 cursor-pointer transition-colors text-sm md:text-base"
                :style="{
                  borderColor: selectedChildrenForReport.includes(child.name)
                    ? (child.color || getChildColor(child.name))
                    : '#d1d5db',
                  backgroundColor: selectedChildrenForReport.includes(
                    child.name
                  )
                    ? (child.color || getChildColor(child.name)) + '20'
                    : 'white',
                }"
              >
                <input
                  type="checkbox"
                  :value="child.name"
                  v-model="selectedChildrenForReport"
                  @change="onSelectedChildrenChange"
                  class="sr-only"
                />
                <span
                  class="text-xs md:text-sm font-paperBold transition-colors"
                  :style="{
                    color: selectedChildrenForReport.includes(child.name)
                      ? (child.color || getChildColor(child.name))
                      : '#6b7280',
                  }"
                >
                  {{ child.name }}
                </span>
              </label>
            </div>
          </div>
          <div class="bg-white rounded-xl shadow-lg p-3 md:p-6">
            <FullCalendar :options="calendarOptions" ref="calendar" :key="calendarKey" />
          </div>
        </div>

        <!-- 뒤로 가기 버튼 -->
        <div class="text-center mt-6 md:mt-8">
          <button
            @click="goBack"
            class="px-6 md:px-8 py-2 md:py-3 bg-gray-300 text-gray-700 font-paperBold text-base md:text-lg rounded-lg hover:bg-gray-400 transition-colors"
          >
            뒤로가기
          </button>
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
import { ref, computed, watch, onMounted, nextTick } from "vue";
import { useRouter } from "vue-router";
import FullCalendar from "@fullcalendar/vue3";
import dayGridPlugin from "@fullcalendar/daygrid";
import interactionPlugin from "@fullcalendar/interaction";
import EmotionReportModal from "@/components/modal/EmotionReportModal.vue";
import {
  getChildColor,
  ensureAllChildrenHaveColors,
} from "@/utils/colorManager.js";
import { useChildStore } from "@/store/child";
import { childService } from "@/services/childService.js";

const router = useRouter();
const childStore = useChildStore();

const selectedChildIndex = ref(0);
const calendar = ref(null);
const calendarKey = ref(0);

// 감정 리포트 모달 관련
const showEmotionReportModal = ref(false);
const selectedReportDate = ref("");
const selectedReportData = ref(null);

// selectedReportData 보호를 위한 잠금 플래그
const isSettingReportData = ref(false);

// selectedReportData 안전 설정 함수
function setSelectedReportData(data) {
  if (isSettingReportData.value) {
    console.log('이미 설정 중이므로 무시됨');
    return;
  }
  
  isSettingReportData.value = true;
  console.log('=== selectedReportData 안전 설정 시작 ===');
  console.log('설정할 데이터:', data);
  
  selectedReportData.value = data;
  
  console.log('=== selectedReportData 안전 설정 완료 ===');
  console.log('최종 설정된 값:', selectedReportData.value);
  
  // 50ms 후에 잠금 해제 (다른 자동 실행 방지)
  setTimeout(() => {
    isSettingReportData.value = false;
  }, 50);
}

// selectedReportData 변경 추적 완전 제거
const currentReportIndex = ref(0);

// 리포트 표시용 선택된 아이들
const selectedChildrenForReport = ref([]);

// 다이어리 데이터 상태
const diaries = ref([]);
const currentDate = ref(new Date());

// 월별 다이어리 조회
async function fetchMonthlyDiaries() {
  console.log('fetchMonthlyDiaries 호출됨, 선택된 아이들:', selectedChildrenForReport.value);
  
  if (selectedChildrenForReport.value.length === 0) {
    console.log('선택된 아이가 없어서 모든 데이터 초기화');
    diaries.value = [];
    
    // 수동으로 캐시된 computed properties 초기화
    diariesByDate.value = {};
    allSelectedReports.value = [];
    reportsByDate.value = {};
    
    console.log('모든 데이터 초기화 완료');
    return;
  }
  
  try {
    const year = currentDate.value.getFullYear();
    const month = currentDate.value.getMonth() + 1;
    
    // 선택된 아이들의 다이어리를 모두 가져오기
    const allDiaries = [];
    
    for (const childName of selectedChildrenForReport.value) {
      const child = childrenList.value.find(c => c.name === childName);
      if (child && child.id) {
        try {
          const response = await childService.getMonthlyDiaries(child.id, year, month);
          const responseArray = Array.isArray(response) ? response : (response ? [response] : []);
          
          // 각 다이어리에 아이 이름과 색상 추가
          const childDiaries = responseArray.map(diary => ({
            ...diary,
            date: diary.createdAt ? diary.createdAt.split('T')[0] : diary.date,
            childName: childName,
            childId: child.id,
            color: child.color || getChildColor(childName)
          }));
          
          allDiaries.push(...childDiaries);
        } catch (error) {
          console.error(`${childName}의 다이어리 조회 실패:`, error);
        }
      }
    }
    
    diaries.value = allDiaries.sort((a, b) => new Date(a.date) - new Date(b.date));
    
    // 수동으로 캐시된 computed properties 업데이트
    updateDiariesByDate();
    updateAllSelectedReports();
    updateReportsByDate();
    
  } catch (error) {
    console.error('다이어리 조회 오류:', error);
    diaries.value = [];
    // 에러 시에도 캐시 초기화
    diariesByDate.value = {};
    allSelectedReports.value = [];
    reportsByDate.value = {};
  }
}

// 날짜별 다이어리 맵 (childId 기준으로 정렬) - 캐시된 버전으로 변경
const diariesByDate = ref({});

// diaries가 변경될 때만 수동으로 업데이트
function updateDiariesByDate() {
  const map = {};
  diaries.value.forEach(diary => {
    if (!map[diary.date]) {
      map[diary.date] = [];
    }
    map[diary.date].push(diary);
  });
  
  // 각 날짜별로 childId 기준 정렬
  Object.keys(map).forEach(date => {
    map[date].sort((a, b) => a.childId - b.childId);
  });
  
  diariesByDate.value = map;
}

// 선택된 아이들의 모든 감정 리포트 데이터 (달력 표시용) - 캐시된 버전
const allSelectedReports = ref([]);

// 리포트 데이터 수동 업데이트 함수
function updateAllSelectedReports() {
  console.log('=== allSelectedReports 수동 업데이트 시작 ===');
  console.log('원본 diaries.value:', diaries.value);
  console.log('사용 가능한 childrenList.value:', childrenList.value);
  
  const reports = diaries.value.map((diary, index) => {
    // childId로 올바른 아이 이름 찾기
    const child = childrenList.value.find(c => c.id === diary.childId);
    if (!child) {
      console.warn(`다이어리 ${index}: childId ${diary.childId}에 대한 아이를 찾을 수 없음`);
      console.log('사용 가능한 아이들:', childrenList.value.map(c => ({ id: c.id, name: c.name })));
    }
    const correctChildName = child ? child.name : `Unknown-${diary.childId}`;
    
    const report = {
      date: diary.date,
      childName: correctChildName,
      childId: diary.childId,
      color: diary.color,
      imageUrl: diary.imageUrl,
      diaryText: diary.diaryText || diary.text || diary.content || ''
    };
    
    console.log(`리포트 ${index} 생성:`, {
      diary: { childId: diary.childId, date: diary.date, childName: diary.childName },
      foundChild: child,
      finalReport: { childId: report.childId, childName: report.childName, date: report.date }
    });
    
    return report;
  });
  
  console.log('allSelectedReports 최종 계산 완료:', reports.map(r => ({ date: r.date, childId: r.childId, childName: r.childName })));
  allSelectedReports.value = reports;
}

// 아이별 우선순위 (등록 순서)
const childPriority = computed(() => {
  const priority = {};
  childrenList.value.forEach((child, index) => {
    priority[child.name] = index;
  });
  return priority;
});

// 날짜별로 그룹화된 리포트 데이터 (네비게이션용) - 캐시된 버전
const reportsByDate = ref({});

// 네비게이션용 리포트 그룹 수동 업데이트
function updateReportsByDate() {
  const groups = {};
  allSelectedReports.value.forEach(report => {
    if (!groups[report.date]) {
      groups[report.date] = [];
    }
    groups[report.date].push(report);
  });
  
  // 각 날짜별로 childId 순으로 정렬
  Object.keys(groups).forEach(date => {
    groups[date].sort((a, b) => a.childId - b.childId);
  });
  
  console.log('날짜별 리포트 그룹:', groups);
  reportsByDate.value = groups;
}

// 자동 호출 차단용 플래그
const allowNavigation = ref(false);

// 이전 리포트 찾기 로직
function findPreviousReport() {
  console.log('!!! findPreviousReport 호출됨 - 호출 스택:', new Error().stack);
  
  // 자동 호출 차단 - 사용자가 직접 버튼 누를 때만 허용
  if (!allowNavigation.value) {
    console.log('자동 호출 차단됨 - findPreviousReport');
    return null;
  }
  
  if (!selectedReportData.value) {
    console.log('이전 리포트 찾기: selectedReportData 없음');
    return null;
  }
  
  const currentDate = selectedReportData.value.date;
  const currentChildId = selectedReportData.value.childId;
  
  console.log('=== 이전 리포트 찾기 시작 ===');
  console.log('현재 리포트:', { currentDate, currentChildId });
  console.log('사용 가능한 reportsByDate:', reportsByDate.value);
  
  // 1. 같은 날짜에서 더 작은 childId 찾기
  const sameDate = reportsByDate.value[currentDate] || [];
  console.log('같은 날짜의 리포트들:', sameDate);
  
  const prevInSameDate = sameDate
    .filter(report => {
      const isSmaller = report.childId < currentChildId;
      console.log(`이전 찾기 - childId 비교: ${report.childId} < ${currentChildId} = ${isSmaller} (report: ${report.childName})`);
      return isSmaller;
    })
    .sort((a, b) => b.childId - a.childId)[0]; // 가장 큰 ID부터
  
  if (prevInSameDate) {
    console.log('같은 날짜에서 이전 리포트 발견:', prevInSameDate);
    return prevInSameDate;
  }
  
  // 2. 이전 날짜들에서 가장 최근 날짜의 가장 큰 childId 찾기
  const dates = Object.keys(reportsByDate.value).sort((a, b) => new Date(b) - new Date(a));
  console.log('정렬된 날짜들:', dates);
  
  for (const date of dates) {
    console.log(`날짜 비교: ${date} < ${currentDate} = ${date < currentDate}`);
    if (date < currentDate) {
      const reportsInDate = reportsByDate.value[date];
      const lastReport = reportsInDate[reportsInDate.length - 1]; // 가장 큰 childId
      console.log('이전 날짜에서 리포트 발견:', { date, lastReport });
      return lastReport;
    }
  }
  
  console.log('이전 리포트 없음');
  return null;
}

// 다음 리포트 찾기 로직
function findNextReport() {
  console.log('!!! findNextReport 호출됨 - 호출 스택:', new Error().stack);
  
  // 자동 호출 차단 - 사용자가 직접 버튼 누를 때만 허용
  if (!allowNavigation.value) {
    console.log('자동 호출 차단됨 - findNextReport');
    return null;
  }
  
  if (!selectedReportData.value) {
    console.log('다음 리포트 찾기: selectedReportData 없음');
    return null;
  }
  
  const currentDate = selectedReportData.value.date;
  const currentChildId = selectedReportData.value.childId;
  
  console.log('=== 다음 리포트 찾기 시작 ===');
  console.log('현재 리포트:', { currentDate, currentChildId });
  console.log('사용 가능한 reportsByDate:', reportsByDate.value);
  
  // 1. 같은 날짜에서 더 큰 childId 찾기
  const sameDate = reportsByDate.value[currentDate] || [];
  console.log('같은 날짜의 리포트들:', sameDate);
  
  const nextInSameDate = sameDate
    .filter(report => {
      const isBigger = report.childId > currentChildId;
      console.log(`다음 찾기 - childId 비교: ${report.childId} > ${currentChildId} = ${isBigger} (report: ${report.childName})`);
      return isBigger;
    })
    .sort((a, b) => a.childId - b.childId)[0]; // 가장 작은 ID부터
  
  if (nextInSameDate) {
    console.log('같은 날짜에서 다음 리포트 발견:', {
      childName: nextInSameDate.childName,
      childId: nextInSameDate.childId,
      date: nextInSameDate.date
    });
    return nextInSameDate;
  }
  
  // 2. 다음 날짜들에서 가장 가까운 날짜의 가장 작은 childId 찾기
  const dates = Object.keys(reportsByDate.value).sort((a, b) => new Date(a) - new Date(b));
  console.log('정렬된 날짜들:', dates);
  
  for (const date of dates) {
    console.log(`날짜 비교: ${date} > ${currentDate} = ${date > currentDate}`);
    if (date > currentDate) {
      const reportsInDate = reportsByDate.value[date];
      const firstReport = reportsInDate[0]; // 가장 작은 childId
      console.log('다음 날짜에서 리포트 발견:', { 
        date, 
        firstReport: {
          childName: firstReport.childName,
          childId: firstReport.childId,
          date: firstReport.date
        }
      });
      return firstReport;
    }
  }
  
  console.log('다음 리포트 없음');
  return null;
}

// 네비게이션 버튼 표시 여부 (사용자가 직접 버튼 누를 때만 계산)
const hasPreviousReport = ref(false);
const hasNextReport = ref(false);

// 모달이 열릴 때마다 버튼 상태 초기화
function resetNavigationButtons() {
  hasPreviousReport.value = false;
  hasNextReport.value = false;
  console.log('네비게이션 버튼 상태 초기화');
}

// 네비게이션 버튼 상태 계산 함수 (사용자가 버튼 클릭할 때만 호출)
function calculateNavigationState() {
  console.log('네비게이션 상태 계산 요청됨');
  
  if (!selectedReportData.value || !selectedReportData.value.date || !selectedReportData.value.childId) {
    console.log('네비게이션 상태 계산 불가: 데이터 부족');
    hasPreviousReport.value = false;
    hasNextReport.value = false;
    return;
  }
  
  console.log('네비게이션 상태 계산 시작:', {
    currentChild: selectedReportData.value.childName,
    currentChildId: selectedReportData.value.childId,
    currentDate: selectedReportData.value.date
  });
  
  // 임시로 navigation 허용
  allowNavigation.value = true;
  
  try {
    const prevReport = findPreviousReport();
    hasPreviousReport.value = prevReport !== null;
    
    const nextReport = findNextReport();
    hasNextReport.value = nextReport !== null;
    
    console.log('네비게이션 상태 계산 완료:', {
      hasPrev: hasPreviousReport.value,
      hasNext: hasNextReport.value,
      prevReport: prevReport ? { childName: prevReport.childName, date: prevReport.date } : null,
      nextReport: nextReport ? { childName: nextReport.childName, date: nextReport.date } : null
    });
  } catch (error) {
    console.error('네비게이션 상태 계산 오류:', error);
    hasPreviousReport.value = false;
    hasNextReport.value = false;
  } finally {
    // 계산 완료 후 다시 차단
    allowNavigation.value = false;
  }
}

// FullCalendar 설정 - computed 제거하고 직접 객체로 변경
const calendarOptions = {
  plugins: [dayGridPlugin, interactionPlugin],
  initialView: "dayGridMonth",
  locale: "ko",
  headerToolbar: {
    left: "prev,next",
    center: "title",
    right: "today",
  },
  height: window.innerWidth <= 768 ? 450 : 580,
  contentHeight: window.innerWidth <= 768 ? 400 : 530,
  
  // 월 변경 시 다이어리 재조회
  datesSet: async (info) => {
    const viewStart = new Date(info.start);
    const viewEnd = new Date(info.end);
    const middleDate = new Date((viewStart.getTime() + viewEnd.getTime()) / 2);
    
    currentDate.value = middleDate;
    await fetchMonthlyDiaries();
  },

  // 날짜 클릭 시 모달 열기 - 해당 날짜의 첫 번째 리포트 표시
  dateClick: (info) => {
    openEmotionReport(info.dateStr);
  },

  // 각 날짜 셀에 감정 리포트 버튼 표시
  dayCellDidMount: (info) => {
    info.el.style.position = "relative";
    
    // 시간대 문제 해결: 로컬 날짜로 변환
    const year = info.date.getFullYear();
    const month = String(info.date.getMonth() + 1).padStart(2, '0');
    const day = String(info.date.getDate()).padStart(2, '0');
    const key = `${year}-${month}-${day}`;
    
    // 해당 날짜의 다이어리가 있는지 확인
    let attempts = 0;
    const maxAttempts = 10;
    
    const checkAndRender = () => {
      attempts++;
      
      // 선택된 아이가 없으면 아무것도 렌더링하지 않음
      if (selectedChildrenForReport.value.length === 0) {
        console.log('선택된 아이가 없어서 렌더링 건너뜀');
        return;
      }
      
      const reportsForDate = diariesByDate.value[key] || [];
      
      if (reportsForDate.length > 0) {
        // 각 리포트를 버튼으로 표시하되, 칸 안에 세로로 정렬 (정렬 없이 원본 순서)
        const container = document.createElement("div");
        container.style.position = "absolute";
        container.style.top = window.innerWidth <= 768 ? "20px" : "25px"; // 날짜 글자 바로 아래부터 시작
        container.style.left = "2px";
        container.style.right = "2px";
        container.style.display = "flex";
        container.style.flexDirection = "column";
        container.style.gap = "2px";
        container.style.alignItems = "stretch";
        
        reportsForDate.forEach((report, index) => {
          // 최대 3개까지만 표시
          if (index < 3) {
            console.log(`버튼 ${index} 생성:`, {
              childName: report.childName,
              childId: report.childId,
              date: key,
              report: report
            });
            
            const button = document.createElement("button");
            button.textContent = `${report.childName}`;
            button.style.backgroundColor = report.color;
            button.style.color = "white";
            button.style.border = "none";
            button.style.borderRadius = "4px";
            button.style.padding = "2px 4px";
            button.style.fontSize = window.innerWidth <= 768 ? "8px" : "9px";
            button.style.fontWeight = "500";
            button.style.height = window.innerWidth <= 768 ? "14px" : "16px";
            button.style.cursor = "pointer";
            button.style.overflow = "hidden";
            button.style.whiteSpace = "nowrap";
            button.style.textOverflow = "ellipsis";
            button.style.transition = "opacity 0.2s";
            
            button.addEventListener("mouseenter", () => {
              button.style.opacity = "0.8";
            });
            
            button.addEventListener("mouseleave", () => {
              button.style.opacity = "1";
            });
            
            // 버튼에 데이터를 직접 저장
            button.setAttribute('data-child-name', report.childName);
            button.setAttribute('data-child-id', report.childId);
            console.log(`버튼 속성 설정:`, {
              'data-child-name': report.childName,
              'data-child-id': report.childId
            });
            button.setAttribute('data-date', report.date);
            
            button.addEventListener("click", async (e) => {
              e.stopPropagation();
              
              const clickedChildName = e.target.getAttribute('data-child-name');
              const clickedChildId = parseInt(e.target.getAttribute('data-child-id'));
              
              console.log('=== 버튼 클릭 시작 ===');
              console.log('클릭한 버튼 정보:', {
                clickedChildName,
                clickedChildId,
                clickedDate: key
              });
              
              // 핸심: 이미 가지고 있는 reportsForDate에서 해당 아이의 리포트 찾기
              console.log('타겟 찾기 전 reportsForDate:', reportsForDate);
              console.log('찾는 childId:', clickedChildId, 'type:', typeof clickedChildId);
              
              // 모든 리포트의 childId와 비교
              reportsForDate.forEach((r, idx) => {
                console.log(`리포트 ${idx}:`, {
                  childId: r.childId,
                  childName: r.childName,
                  type: typeof r.childId,
                  match: r.childId === clickedChildId
                });
              });
              
              const targetReport = reportsForDate.find(r => r.childId === clickedChildId);
              console.log('찾은 타겟 리포트:', targetReport);
              
              if (!targetReport) {
                console.error('타겟 리포트를 찾지 못함!');
                console.log('사용된 검색 조건:', { clickedChildId, type: typeof clickedChildId });
                console.log('사용가능한 리포트들:', reportsForDate.map(r => ({ childId: r.childId, childName: r.childName, type: typeof r.childId })));
                return; // 여기서 중단
              }
              
              if (targetReport) {
                console.log('=== 타겟 리포트 발견, API 호출 시작 ===');
                try {
                  // API로 원본 다이어리 데이터 가져오기
                  const clickedDate = new Date(key);
                  const year = clickedDate.getFullYear();
                  const month = clickedDate.getMonth() + 1;
                  
                  console.log('API 요청:', { childId: clickedChildId, year, month });
                  const response = await childService.getMonthlyDiaries(clickedChildId, year, month);
                  const responseArray = Array.isArray(response) ? response : (response ? [response] : []);
                  
                  // 해당 날짜의 다이어리 찾기
                  const todayDiary = responseArray.find(diary => {
                    const diaryDate = diary.createdAt ? diary.createdAt.split('T')[0] : diary.date;
                    return diaryDate === key;
                  });
                  
                  console.log('찾은 오늘 다이어리:', todayDiary);
                  
                  if (todayDiary) {
                    selectedReportDate.value = key;
                    
                    // 최종 데이터 설정 (반드시 타겟 리포트의 정보 사용)
                    const finalData = {
                      ...todayDiary,
                      date: key,
                      childName: targetReport.childName, // 반드시 타겟의 이름
                      childId: targetReport.childId // 반드시 타겟의 childId
                    };
                    
                    console.log('=== selectedReportData 설정 예정 ===');
                    console.log('설정할 데이터:', finalData);
                    setSelectedReportData(finalData);
                    console.log('=== selectedReportData 설정 완료 ===');
                    console.log('타겟 리포트로 최종 설정:', {
                      targetChildName: targetReport.childName,
                      targetChildId: targetReport.childId,
                      finalData
                    });
                    
                    // 모달 열고 네비게이션 상태 계산
                    showEmotionReportModal.value = true;
                    console.log('모달 열음');
                    
                    // 버튼 상태 계산 (사용자가 클릭한 후에만)
                    calculateNavigationState();
                    
                    console.log('=== 버튼 클릭 성공 완료 ===');
                  } else {
                    console.warn('해당 날짜의 다이어리를 찾을 수 없음');
                  }
                } catch (error) {
                  console.error('해당 아이의 다이어리 조회 실패:', error);
                }
              } else {
                console.error('!!! 타겟 리포트를 찾지 못함 - 이것이 문제의 원인일 수 있음 !!!');
                // targetReport를 찾지 못했을 때 어떤 일이 일어나는지 확인
                // 아마도 여기서 fallback 로직이 실행되어 첫 번째 리포트가 나올 수 있음
                return; // 여기서 완전히 중단
              }
            });
            
            container.appendChild(button);
          }
        });
        
        // 3개 이상이면 더보기 버튼
        if (reportsForDate.length > 3) {
          const moreButton = document.createElement("button");
          moreButton.textContent = `+${reportsForDate.length - 3}개 더`;
          moreButton.style.backgroundColor = "#6b7280";
          moreButton.style.color = "white";
          moreButton.style.border = "none";
          moreButton.style.borderRadius = "4px";
          moreButton.style.padding = "2px 4px";
          moreButton.style.fontSize = "8px";
          moreButton.style.height = "14px";
          moreButton.style.cursor = "pointer";
          moreButton.style.transition = "opacity 0.2s";
          
          moreButton.addEventListener("mouseenter", () => {
            moreButton.style.opacity = "0.8";
          });
          
          moreButton.addEventListener("mouseleave", () => {
            moreButton.style.opacity = "1";
          });
          
          // 첫 번째 추가 리포트로 모달 열기 (원본 순서에서)
          moreButton.addEventListener("click", async (e) => {
            e.stopPropagation();
            const additionalReport = reportsForDate[3];
            
            try {
              // 해당 아이의 원본 데이터를 다시 API에서 가져오기
              const clickedDate = new Date(key);
              const year = clickedDate.getFullYear();
              const month = clickedDate.getMonth() + 1;
              
              const response = await childService.getMonthlyDiaries(additionalReport.childId, year, month);
              const responseArray = Array.isArray(response) ? response : (response ? [response] : []);
              
              const todayDiary = responseArray.find(diary => {
                const diaryDate = diary.createdAt ? diary.createdAt.split('T')[0] : diary.date;
                return diaryDate === key;
              });
              
              if (todayDiary) {
                selectedReportDate.value = key;
                // childId로 올바른 아이 이름 찾기
                const child = childrenList.value.find(c => c.id === additionalReport.childId);
                const correctChildName = child ? child.name : `아이${additionalReport.childId}`;
                
                console.log('더보기 버튼 - 아이 정보:', {
                  additionalReport, child, correctChildName,
                  availableChildren: childrenList.value.map(c => ({ id: c.id, name: c.name }))
                });
                
                setSelectedReportData({
                  ...todayDiary,
                  date: key, // 날짜 필드 명시적 설정
                  childName: correctChildName,
                  childId: additionalReport.childId
                });
                showEmotionReportModal.value = true;
                // 네비게이션 상태 계산
                calculateNavigationState();
              }
            } catch (error) {
              console.error('추가 리포트 조회 실패:', error);
            }
          });
          
          container.appendChild(moreButton);
        }
        
        info.el.appendChild(container);
        
      } else if (attempts < maxAttempts) {
        // 데이터가 없으면 100ms 후 다시 시도
        setTimeout(checkAndRender, 100);
      }
    };
    
    // 첫 시도는 700ms 후
    setTimeout(checkAndRender, 700);
  },
};

// childStore 직접 접근으로 변경
const hasChild = ref(false);
const childrenList = ref([]);
const selectedChild = ref({});

// 아이 데이터 수동 업데이트 함수
function updateChildData() {
  hasChild.value = childStore.hasChildren;
  childrenList.value = [...childStore.children];
  selectedChild.value = childrenList.value[selectedChildIndex.value] || childStore.selectedChild || {};
}

// 아이 정보 로드
async function loadChildren() {
  await childStore.initialize();
  updateChildData(); // 수동으로 아이 데이터 업데이트
  
  if (childStore.hasChildren) {
    const child = childStore.selectedChild || childStore.children[0];
    selectedChildIndex.value = childStore.children.findIndex(c => c.id === child.id);
    updateChildData(); // 인덱스 변경 후 다시 업데이트
  }
}

// 아이 선택
async function selectChild(index) {
  selectedChildIndex.value = index;
  updateChildData(); // 선택 변경 후 수동 업데이트
}

// 감정 리포트 관련 함수
function handleEventClick(info) {
  openEmotionReport(info.event.startStr);
}

async function openEmotionReport(dateStr) {
  const reportsForDate = diariesByDate.value[dateStr] || [];
  if (reportsForDate.length > 0) {
    // 정렬 없이 첫 번째 리포트 사용
    const report = reportsForDate[0];

    try {
      // 해당 아이의 원본 데이터를 API에서 다시 가져오기
      const clickedDate = new Date(dateStr);
      const year = clickedDate.getFullYear();
      const month = clickedDate.getMonth() + 1;
      
      const response = await childService.getMonthlyDiaries(report.childId, year, month);
      const responseArray = Array.isArray(response) ? response : (response ? [response] : []);
      
      const todayDiary = responseArray.find(diary => {
        const diaryDate = diary.createdAt ? diary.createdAt.split('T')[0] : diary.date;
        return diaryDate === dateStr;
      });
      
      if (todayDiary) {
        selectedReportDate.value = dateStr;
        // childId로 올바른 아이 이름 찾기
        const child = childrenList.value.find(c => c.id === report.childId);
        const correctChildName = child ? child.name : `아이${report.childId}`;
        
        console.log('날짜 클릭 - 아이 정보:', {
          report, child, correctChildName,
          availableChildren: childrenList.value.map(c => ({ id: c.id, name: c.name }))
        });
        
        setSelectedReportData({
          ...todayDiary,
          date: dateStr, // 날짜 필드 명시적 설정
          childName: correctChildName,
          childId: report.childId
        });
        showEmotionReportModal.value = true;
        // 네비게이션 상태 계산
        calculateNavigationState();
      }
    } catch (error) {
      console.error('감정 리포트 조회 실패:', error);
    }
  }
}

// 이전/다음 리포트 네비게이션 (사용자 버튼 클릭시에만)
async function goToPreviousReport() {
  console.log('사용자가 이전 리포트 버튼 클릭');
  
  // 네비게이션 허용
  allowNavigation.value = true;
  
  try {
    const prevReport = findPreviousReport();
    if (prevReport) {
      console.log('이동할 이전 리포트:', prevReport);
      await loadReportData(prevReport);
      // 이동 후 새로운 위치에서 버튼 상태 다시 계산
      calculateNavigationState();
    }
  } finally {
    allowNavigation.value = false;
  }
}

async function goToNextReport() {
  console.log('사용자가 다음 리포트 버튼 클릭');
  
  // 네비게이션 허용
  allowNavigation.value = true;
  
  try {
    const nextReport = findNextReport();
    if (nextReport) {
      console.log('이동할 다음 리포트:', nextReport);
      await loadReportData(nextReport);
      // 이동 후 새로운 위치에서 버튼 상태 다시 계산
      calculateNavigationState();
    }
  } finally {
    allowNavigation.value = false;
  }
}

// 리포트 데이터 로드 헬퍼 함수
async function loadReportData(report) {
  console.log('!!! loadReportData 호출됨 - 호출 스택:', new Error().stack);
  
  try {
    console.log('리포트 데이터 로드 시도:', report);
    console.log('타겟 childId:', report.childId, '타겟 날짜:', report.date);
    
    const clickedDate = new Date(report.date);
    const year = clickedDate.getFullYear();
    const month = clickedDate.getMonth() + 1;
    
    console.log('API 요청:', { childId: report.childId, year, month, date: report.date });
    
    // 핵심: 해당 아이의 데이터만 요청
    const response = await childService.getMonthlyDiaries(report.childId, year, month);
    const responseArray = Array.isArray(response) ? response : (response ? [response] : []);
    
    console.log('API 응답 데이터:', responseArray);
    
    // 해당 날짜의 다이어리 찾기 (이미 해당 childId의 데이터만 있으므로 날짜만 매칭)
    const todayDiary = responseArray.find(diary => {
      const diaryDate = diary.createdAt ? diary.createdAt.split('T')[0] : diary.date;
      const match = diaryDate === report.date;
      console.log('다이어리 매칭 검사:', {
        diaryDate,
        targetDate: report.date,
        match,
        diaryId: diary.id
      });
      return match;
    });
    
    console.log('찾은 최종 다이어리:', todayDiary);
    
    if (todayDiary) {
      selectedReportDate.value = report.date;
      
      // 핵심: report에서 전달된 childName을 그대로 사용
      const finalData = {
        ...todayDiary,
        date: report.date,
        childName: report.childName, // report에서 오는 childName 사용
        childId: report.childId
      };
      
      setSelectedReportData(finalData);
      console.log('최종 설정된 selectedReportData:', finalData);
      console.log('사용된 childName:', report.childName, '사용된 childId:', report.childId);
    } else {
      console.warn('해당 날짜의 다이어리를 찾을 수 없음:', report.date);
    }
  } catch (error) {
    console.error('리포트 데이터 로드 실패:', error);
  }
}

// 페이지 이동 함수들
function goToRegister() {
  router.push({ name: "RegisterChild" });
}

function goBack() {
  router.push({ name: "Dashboard" });
}

// selectedChildrenForReport 변경 시 수동으로 호출할 함수
async function onSelectedChildrenChange() {
  console.log('선택된 아이들 변경됨:', selectedChildrenForReport.value);
  await fetchMonthlyDiaries();
  
  // 달력 완전 재마운트를 위해 키를 변경
  calendarKey.value += 1;
}

// 컴포넌트 마운트 시 실행
onMounted(async () => {
  await childStore.initialize();
  updateChildData(); // 아이 데이터 수동 업데이트
  
  // 전역 함수로 등록 (달력의 HTML 버튼에서 호출하기 위해)
  window.openEmotionReport = openEmotionReport;

  // 기본적으로 모든 아이를 선택된 상태로 설정
  selectedChildrenForReport.value = childrenList.value.map(child => child.name);
  
  // 모든 아이가 선택된 상태로 다이어리 로드
  await onSelectedChildrenChange();
});
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

:deep(.fc .fc-daygrid-day) {
  height: 80px !important;
  min-width: 80px !important;
}

@media (min-width: 768px) {
  :deep(.fc .fc-daygrid-day) {
    height: 100px !important;
    min-width: 120px !important;
  }
}

:deep(.fc .fc-daygrid-day-frame) {
  position: relative !important;
  overflow: hidden !important;
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
<template>
  <div class="py-8 px-4">
    <div class="mx-auto max-w-6xl bg-white pt-10 pb-10 rounded-2xl mb-10">
      <!-- 상단 타이틀 -->
      <div class="text-center mb-12">
        <h1 class="text-4xl font-paperBold text-gray-800">성장 기록</h1>
        <p class="text-lg text-gray-600 mt-4 font-paper">아이의 성장 과정을 상세히 기록하고 추적하세요</p>
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
                  class="sr-only"
                />
                <span
                  class="text-sm font-paperBold transition-colors"
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
          <div class="bg-white rounded-xl shadow-lg p-6">
            <FullCalendar :options="calendarOptions" ref="calendar" />
          </div>
        </div>

        <!-- 뒤로 가기 버튼 -->
        <div class="text-center mt-8">
          <button
            @click="goBack"
            class="px-8 py-3 bg-gray-300 text-gray-700 font-paperBold text-lg rounded-lg hover:bg-gray-400 transition-colors"
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
import { ref, computed, watch, onMounted } from "vue";
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

// 감정 리포트 모달 관련
const showEmotionReportModal = ref(false);
const selectedReportDate = ref("");
const selectedReportData = ref(null);
const currentReportIndex = ref(0);

// 리포트 표시용 선택된 아이들
const selectedChildrenForReport = ref([]);

// 다이어리 데이터 상태
const diaries = ref([]);
const currentDate = ref(new Date());

// 월별 다이어리 조회
async function fetchMonthlyDiaries() {
  if (selectedChildrenForReport.value.length === 0) {
    diaries.value = [];
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
    
  } catch (error) {
    console.error('다이어리 조회 오류:', error);
    diaries.value = [];
  }
}

// 날짜별 다이어리 맵
const diariesByDate = computed(() => {
  const map = {};
  diaries.value.forEach(diary => {
    if (!map[diary.date]) {
      map[diary.date] = [];
    }
    map[diary.date].push(diary);
  });
  return map;
});

// 선택된 아이들의 모든 감정 리포트 데이터 (달력 표시용)
const allSelectedReports = computed(() => {
  return diaries.value.map(diary => ({
    date: diary.date,
    childName: diary.childName,
    childId: diary.childId,
    color: diary.color,
    imageUrl: diary.imageUrl,
    diaryText: diary.diaryText || diary.text || diary.content || ''
  }));
});

// 아이별 우선순위 (등록 순서)
const childPriority = computed(() => {
  const priority = {};
  childrenList.value.forEach((child, index) => {
    priority[child.name] = index;
  });
  return priority;
});

// 전체 리포트를 날짜 순으로만 정렬 (네비게이션용, 원본 순서 유지)
const allReportsForNavigation = computed(() => {
  return allSelectedReports.value.sort((a, b) => {
    // 날짜 우선 정렬
    const dateCompare = new Date(a.date) - new Date(b.date);
    if (dateCompare !== 0) return dateCompare;
    
    // 같은 날짜면 원본 순서 유지 (정렬 안 함)
    return 0;
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

// 네비게이션 관련 computed
const hasPreviousReport = computed(() => currentReportGlobalIndex.value > 0);
const hasNextReport = computed(
  () =>
    currentReportGlobalIndex.value < allReportsForNavigation.value.length - 1
);

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
  height: 580,
  contentHeight: 530,
  
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
      const reportsForDate = diariesByDate.value[key] || [];
      
      if (reportsForDate.length > 0) {
        // 각 리포트를 버튼으로 표시하되, 칸 안에 세로로 정렬 (정렬 없이 원본 순서)
        const container = document.createElement("div");
        container.style.position = "absolute";
        container.style.top = "25px"; // 날짜 글자 바로 아래부터 시작
        container.style.left = "2px";
        container.style.right = "2px";
        container.style.display = "flex";
        container.style.flexDirection = "column";
        container.style.gap = "2px";
        container.style.alignItems = "stretch";
        
        reportsForDate.forEach((report, index) => {
          // 최대 3개까지만 표시
          if (index < 3) {
            const button = document.createElement("button");
            button.textContent = `${report.childName}`;
            button.style.backgroundColor = report.color;
            button.style.color = "white";
            button.style.border = "none";
            button.style.borderRadius = "4px";
            button.style.padding = "2px 4px";
            button.style.fontSize = "9px";
            button.style.fontWeight = "500";
            button.style.height = "16px";
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
            button.setAttribute('data-date', report.date);
            
            button.addEventListener("click", async (e) => {
              e.stopPropagation();
              
              const clickedChildName = e.target.getAttribute('data-child-name');
              const clickedChildId = e.target.getAttribute('data-child-id');
              
              try {
                // 해당 아이의 해당 날짜 데이터를 직접 API로 조회
                const clickedDate = new Date(key);
                const year = clickedDate.getFullYear();
                const month = clickedDate.getMonth() + 1;
                
                const response = await childService.getMonthlyDiaries(parseInt(clickedChildId), year, month);
                const responseArray = Array.isArray(response) ? response : (response ? [response] : []);
                
                // 해당 날짜의 다이어리 찾기
                const todayDiary = responseArray.find(diary => {
                  const diaryDate = diary.createdAt ? diary.createdAt.split('T')[0] : diary.date;
                  return diaryDate === key;
                });
                
                if (todayDiary) {
                  // 해당 아이의 색상 찾기
                  const childInfo = childrenList.value.find(child => child.name === clickedChildName);
                  const childColor = childInfo?.color || getChildColor(clickedChildName);
                  
                  selectedReportDate.value = key;
                  selectedReportData.value = {
                    date: key,
                    childName: clickedChildName,
                    childId: parseInt(clickedChildId),
                    color: childColor,
                    imageUrl: todayDiary.imageUrl,
                    diaryText: todayDiary.diaryText || todayDiary.text || todayDiary.content || '',
                    diaryImage: todayDiary.imageUrl
                  };
                  showEmotionReportModal.value = true;
                }
              } catch (error) {
                console.error('해당 아이의 다이어리 조회 실패:', error);
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
          moreButton.addEventListener("click", (e) => {
            e.stopPropagation();
            const additionalReport = reportsForDate[3];
            selectedReportDate.value = key;
            selectedReportData.value = {
              date: additionalReport.date,
              childName: additionalReport.childName,
              childId: additionalReport.childId,
              color: additionalReport.color,
              imageUrl: additionalReport.imageUrl,
              diaryText: additionalReport.diaryText,
              diaryImage: additionalReport.imageUrl
            };
            showEmotionReportModal.value = true;
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
}));

// childStore의 computed 속성들 사용
const hasChild = computed(() => childStore.hasChildren);
const childrenList = computed(() => childStore.children);
const selectedChild = computed(() => {
  return childrenList.value[selectedChildIndex.value] || childStore.selectedChild || {};
});

// 아이 정보 로드
async function loadChildren() {
  await childStore.initialize();
  
  if (childStore.hasChildren) {
    const child = childStore.selectedChild || childStore.children[0];
    selectedChildIndex.value = childStore.children.findIndex(c => c.id === child.id);
  }
}

// 아이 선택
async function selectChild(index) {
  selectedChildIndex.value = index;
}

// 감정 리포트 관련 함수
function handleEventClick(info) {
  openEmotionReport(info.event.startStr);
}

function openEmotionReport(dateStr) {
  const reportsForDate = diariesByDate.value[dateStr] || [];
  if (reportsForDate.length > 0) {
    // 정렬 없이 첫 번째 리포트 사용
    const report = reportsForDate[0];

    selectedReportDate.value = dateStr;
    selectedReportData.value = {
      date: report.date,
      childName: report.childName,
      childId: report.childId,
      color: report.color,
      imageUrl: report.imageUrl,
      diaryText: report.diaryText,
      // EmotionReportModal에서 필요한 필드들 추가
      diaryImage: report.imageUrl
    };
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

// 페이지 이동 함수들
function goToRegister() {
  router.push({ name: "RegisterChild" });
}

function goBack() {
  router.push({ name: "Dashboard" });
}

// selectedChildrenForReport 변경 시 다이어리 재조회
watch(selectedChildrenForReport, () => {
  fetchMonthlyDiaries();
});

// 컴포넌트 마운트 시 실행
onMounted(async () => {
  await childStore.initialize();
  
  // childStore에서 이미 색상이 할당되어 있으므로 추가 설정 불필요
  
  // 전역 함수로 등록 (달력의 HTML 버튼에서 호출하기 위해)
  window.openEmotionReport = openEmotionReport;

  // 기본적으로 모든 아이 선택
  setTimeout(() => {
    selectedChildrenForReport.value = childrenList.value.map(
      (child) => child.name
    );
    // 초기 다이어리 조회
    fetchMonthlyDiaries();
  }, 100);
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
  height: 100px !important;
  min-width: 120px !important;
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
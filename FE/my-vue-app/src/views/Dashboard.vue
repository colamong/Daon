<template>
  <div class="font-paper">
    <!-- 1. Hero 섹션 -->
    <section class="relative overflow-hidden py-16">
      <div class="max-w-6xl container mx-auto px-6 flex flex-col-reverse md:flex-row items-center">
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
    <section class="max-w-6xl container mx-auto px-6 py-12 grid grid-cols-1 lg:grid-cols-3 gap-8">
      <!-- 달력 -->
      <div class="lg:col-span-2 bg-white rounded-xl shadow p-6 h-[530px]">
        <CalendarWidget
          :events="events.map(ev => ({ ...ev, date: ev.eventDate || ev.date }))"
          @update-month="onMonthChange"
        />
      </div>

      <!-- 일정 목록 -->
      <div class="bg-white rounded-xl shadow p-6 flex flex-col h-[530px]">
        <div class="flex justify-between items-center mb-4">
          <h3 class="text-xl font-semibold">일정 목록</h3>
          <button @click="openModal" class="text-sm text-blue-600 hover:underline">+ 일정 추가</button>
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
      <h3 class="text-2xl font-semibold text-center mb-2">다온과 함께하는 특별한 여정</h3>
      <p class="text-center text-gray-600 mb-8">다문화 가정의 행복한 내일을 위해 다온이 함께합니다.</p>
      <div class="grid grid-cols-2 md:grid-cols-4 gap-6">
        <BaseCard variant="schedule" link="/schedule" />
        <BaseCard variant="growth" link="/growth" />
        <BaseCard variant="community" :to="{ name: 'Community' }" />
        <BaseCard variant="language" :to="{ name: 'OCRTool' }" />
      </div>
    </section>

    <!-- 4. 오늘의 활동 -->
    <section class="max-w-6xl container mx-auto px-6 py-5 bg-white rounded-xl shadow mb-20 h-[520px]">
      <div class="flex justify-between items-center mb-6">
        <h3 class="text-2xl font-semibold">오늘의 활동</h3>
        <button @click="openTodayReport" class="text-sm text-gray-600 hover:underline">
          자세히 보기 →
        </button>
      </div>
      <div class="grid grid-cols-1 md:grid-cols-3 gap-6 items-start">
        <div class="md:col-span-2">
          <div class="bg-gray-500 h-[400px] rounded-lg shadow p-6 flex flex-col items-center justify-center">
            <template v-if="hasActivity">
              <img
                :src="todayActivity.diaryImage"
                alt="오늘의 그림일기"
                class="w-full h-full object-cover rounded-lg shadow"
              />
            </template>
            <template v-else>
              <p class="text-white mb-4">
                {{ hasChild && currentChild && currentChild.name ? getSubjectSentence(currentChild.name) : '아직 활동하지 않았습니다.' }}
              </p>
              <button
                @click="goToActivity"
                class="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 font-paper"
              >
                {{ hasChild ? '활동하러 가기' : '아이 등록하러 가기' }}
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
                <div
                  class="px-4 py-2 rounded-t-lg cursor-pointer transition-colors mr-1"
                  :class="{
                    'bg-yellow-200 text-black font-bold': selectedChildIndex === index,
                    'bg-yellow-200 text-gray-700': selectedChildIndex !== index
                  }"
                  style="background-color: #fef08a !important; margin-bottom: -1px;"
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
              style="background-color: #fef08a !important; border-top-left-radius: 0; border-top-right-radius: 0.5rem;"
            >
              <!-- 중앙 프로필 이미지 -->
              <div class="flex-1 flex items-center justify-center">
                <img
                  :src="currentChild?.imageUrl || 'https://placehold.co/200x200'"
                  alt="아이 프로필"
                  class="w-48 h-48 rounded-full object-cover border-4 border-white shadow-lg"
                  @error="(e) => (e.target.src = 'https://placehold.co/200x200')"
                />
              </div>

              <!-- 하단 정보 -->
              <div class="w-full text-center space-y-2">
                <p class="text-lg font-bold text-black">
                  나이 : {{ currentChild ? calculateAge(currentChild.birthDate) : 0 }}세(만 {{ currentChild ? calculateAge(currentChild.birthDate) - 1 : 0 }}세)
                </p>
                <p class="text-lg font-bold text-black">
                  관심사 : {{ currentChild?.interests?.length ? currentChild.interests.slice(0, 2).join(', ') : '없음' }}
                  {{ currentChild?.interests?.length > 2 ? ' ...' : '' }}
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
      :child-name="currentChild && currentChild.name ? currentChild.name : ''"
      :report-date="dayjs().format('YYYY-MM-DD')"
      :report-data="todayActivity"
      :show-navigation="false"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from "vue";
import { useRouter } from 'vue-router';
import dayjs from "dayjs";

import AddEventModal from "@/components/modal/AddEventModal.vue";
import CalendarWidget from "@/components/widget/CalendarWidget.vue";
import ScheduleCard from "@/components/card/ScheduleCard.vue";
import BaseCard from "@/components/card/BaseCard.vue";
import EmotionReportModal from '@/components/modal/EmotionReportModal.vue';
import { useAuthStore } from "@/store/auth";
import { useChildStore } from "@/store/child";
import { useNotification } from '@/composables/useNotification.js';
import { fetchMonthlyEvents, createEvent, updateEvent, deleteEvent } from "@/store/calendar";
import { childService } from "@/services/childService"; // 로컬 보정용 직접 호출

const router = useRouter();
const auth = useAuthStore();
const childStore = useChildStore();
const { showWarning } = useNotification();

/* 일정 상태 */
const events = ref([]);
const selectedMonth = ref(dayjs().format("YYYY-MM"));

async function loadEvents(year, month) {
  try {
    events.value = await fetchMonthlyEvents(year, month);
  } catch (e) {
    console.error("일정 불러오기 실패:", e);
  }
}

const filteredEvents = computed(() =>
  events.value
    .map(ev => ({ ...ev, id: ev.id || ev.calendarId, date: ev.eventDate || ev.date }))
    .filter(ev => dayjs(ev.date).format("YYYY-MM") === selectedMonth.value)
    .sort((a, b) => dayjs(a.date).unix() - dayjs(b.date).unix())
);

/* 자녀 목록: 로컬 우선 → 스토어 후순위 */
const localChildren = ref([]);
const sourceChildren = computed(() =>
  (localChildren.value?.length ? localChildren.value : (childStore.children ?? childStore.items ?? []))
);

/* 필드 정규화 */
const childrenList = computed(() =>
  (sourceChildren.value || []).map(c => ({
    id: c.id ?? c.childId,
    childId: c.childId ?? c.id,
    name: c.name,
    birthDate: c.birthDate,
    imageUrl: c.imageUrl ?? c.profileImage ?? c.profileImg ?? null,
    interests: c.registeredInterests ?? c.interests ?? [],
    raw: c,
  }))
);

const selectedChildIndex = ref(0);
const hasChild = computed(() => childrenList.value.length > 0);
const currentChild = computed(() => childrenList.value[selectedChildIndex.value] || null);

/* 마운트 시 로딩 */
onMounted(async () => {
  const [year, month] = selectedMonth.value.split("-").map(Number);
  await loadEvents(year, month);

  // 스토어 초기화/로딩
  if (typeof childStore.initialize === 'function') {
    await childStore.initialize();
  } else if (typeof childStore.loadChildren === 'function') {
    const userId = auth.user?.id;
    if (userId) await childStore.loadChildren(userId);
  }

  // 로컬 보정: 스토어가 비어있어도 직접 호출해서 채움
  try {
    const userId = auth.user?.id;
    if (userId) {
      const list = await childService.getAllChildren(userId); // payload 배열
      if (Array.isArray(list) && list.length) {
        localChildren.value = list;
      }
    }
  } catch (e) {
    console.warn("직접 자녀 목록 로드 실패:", e?.message || e);
  }

  // 첫 아이 자동 선택
  if (childrenList.value.length > 0) selectedChildIndex.value = 0;
});

/* 목록이 늦게 도착할 때 탭 자동 보정 */
watch(
  () => childrenList.value.length,
  (len) => {
    if (len > 0 && selectedChildIndex.value >= len) {
      selectedChildIndex.value = 0;
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
function openModal() { modalVisible.value = true; }

async function handleAddEvent({ title, date, description }) {
  try {
    await createEvent({ title, eventDate: date, description });
    const [y, m] = selectedMonth.value.split('-').map(Number);
    await loadEvents(y, m);
    modalVisible.value = false;
  } catch (e) {
    showWarning("일정 추가 실패", e.message);
  }
}

async function handleUpdate({ id, newDate, newTitle, newDescription }) {
  try {
    await updateEvent(id, { eventDate: newDate, title: newTitle, description: newDescription });
    const [y, m] = selectedMonth.value.split('-').map(Number);
    await loadEvents(y, m);
  } catch (e) {
    showWarning("일정 수정 실패", e.message);
  }
}

async function handleDelete(id) {
  try {
    await deleteEvent(id);
    const [y, m] = selectedMonth.value.split('-').map(Number);
    await loadEvents(y, m);
  } catch (e) {
    showWarning("일정 삭제 실패", e.message);
  }
}

/* 오늘의 활동 (향후 API 연동) */
const todayActivity = computed(() => null);
const hasActivity = computed(() => !!todayActivity.value);

/* 감정 리포트 모달 */
const showEmotionReportModal = ref(false);
function openTodayReport() {
  if (hasActivity.value) {
    showEmotionReportModal.value = true;
  } else {
    showWarning(
      currentChild.value && currentChild.value.name ? getObjectSentence(currentChild.value.name) : '아직 활동하지 않았습니다!',
      '아직 활동하지 않았습니다!'
    );
  }
}

/* 날짜/조사 유틸 */
function formatDate(dateString) {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleDateString('ko-KR', { year: 'numeric', month: 'long', day: 'numeric' });
}
function getParticle(name, particles) {
  if (!name) return particles[0];
  const lastChar = name[name.length - 1];
  const code = lastChar.charCodeAt(0);
  if (code < 0xAC00 || code > 0xD7A3) return particles[0];
  const hasJongseong = (code - 0xAC00) % 28 !== 0;
  return hasJongseong ? particles[0] : particles[1];
}
function getSubjectSentence(name) {
  const p = getParticle(name, ['은', '는']);
  return `${name}${p} 아직 활동하지 않았습니다.`;
}
function getObjectSentence(name) {
  const p = getParticle(name, ['이', '가']);
  return `${name}${p} 활동하게 해주세요.`;
}

/* 라우팅 */
function goToChildRegister() { router.push({ name: 'RegisterChild' }); }
function goToChildEdit() { router.push({ name: 'EditChild' }); }
function goToActivity() {
  if (hasChild.value && currentChild.value) {
    router.push({ name: 'ChildMain', params: { childId: currentChild.value.childId } });
  } else {
    router.push({ name: 'RegisterChild' });
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
.fade-leave-active { transition: opacity 0.2s ease; }
.fade-enter-from,
.fade-leave-to { opacity: 0; }
</style>

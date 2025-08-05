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
        <CalendarWidget :events="events" @update-month="onMonthChange" />
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
        <router-link to="/child" class="text-sm text-gray-600 hover:underline"
          >자세히 보기 →</router-link
        >
      </div>
      <div class="grid grid-cols-1 md:grid-cols-3 gap-6 items-start">
        <div class="md:col-span-2">
          <div
            class="bg-gray-500 h-[400px] rounded-lg shadow p-6 flex flex-col items-center justify-center"
          >
            <template v-if="hasActivity">
              <img
                :src="todayDrawing.imgUrl"
                alt="오늘의 그림일기"
                class="w-full rounded-lg shadow"
              />
            </template>
            <template v-else>
              <p class="text-white mb-4">아직 활동하지 않았습니다.</p>
              <router-link
                to="/child"
                class="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
              >
                활동하러 가기
              </router-link>
            </template>
          </div>
        </div>
        <div>
          <div
            v-if="hasChild"
            class="h-[400px] bg-yellow-100 rounded-lg p-6 flex flex-col"
          >
            <div class="flex items-center mb-4 space-x-4">
              <img
                :src="child.children[0].profile || 'https://placehold.co/64x64'"
                alt="아이 프로필"
                class="w-16 h-16 rounded-full shadow"
              />
              <div>
                <h4 class="text-lg font-semibold">
                  {{ child.children[0].name }}
                </h4>
                <button
                  @click="$router.push('/child/edit')"
                  class="text-xs text-gray-700 border border-gray-300 px-2 py-1 rounded"
                >
                  프로필 수정
                </button>
              </div>
            </div>
            <p class="text-sm text-gray-700 mb-1">
              <span class="font-medium">나이:</span> {{ child.children[0].age }}
            </p>
            <p class="text-sm text-gray-700">
              <span class="font-medium">관심사:</span>
              {{ child.children[0].interests }}
            </p>
          </div>
          <div
            v-else
            class="bg-yellow-100 h-[400px] rounded-lg shadow p-6 flex flex-col items-center justify-center"
          >
            <p class="text-black-700 mb-4">아직 등록된 아이가 없습니다.</p>
            <router-link
              to="/child/register"
              class="bg-purple-600 text-white px-4 py-2 rounded hover:bg-purple-700"
            >
              아이 등록하러 가기
            </router-link>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import dayjs from "dayjs";

import AddEventModal from "@/components/modal/AddEventModal.vue";
import CalendarWidget from "@/components/widget/CalendarWidget.vue";
import ScheduleCard from "@/components/card/ScheduleCard.vue";
import BaseCard from "@/components/card/BaseCard.vue";
import { useChildStore } from "@/store/child";
import { dummyEvents } from "@/data/dummyData.js";

const child = useChildStore();
const events = ref(dummyEvents);
const selectedMonth = ref(dayjs().format("YYYY-MM"));

function onMonthChange(newYm) {
  selectedMonth.value = newYm;
}
const modalVisible = ref(false);

const filteredEvents = computed(() =>
  events.value
    .filter((ev) => dayjs(ev.date).format("YYYY-MM") === selectedMonth.value)
    .sort((a, b) => dayjs(a.date).unix() - dayjs(b.date).unix())
);

// 일정 추가 버튼 클릭 시 모달 열기
function openModal() {
  modalVisible.value = true;
}

// 일정 실제 등록 처리
function handleAddEvent({ title, date, description }) {
  events.value.push({
    id: Date.now(),
    title,
    date,
    description,
  });
}

function handleUpdate({ oldDate, newDate, newTitle, newDescription }) {
  const idx = events.value.findIndex(
    (ev) => ev.date === oldDate && ev.title === newTitle
  );
  if (idx !== -1) {
    events.value[idx].date = newDate;
    events.value[idx].title = newTitle;
    events.value[idx].description = newDescription;
  }
}

function handleDelete(id) {
  events.value = events.value.filter((ev) => ev.id !== id);
}

// 오늘의 활동 & 아이 정보 (더미)
const todayDrawing = ref({
  imgUrl: "",
  profile: "",
  name: "",
  age: "",
  interests: "",
});
const hasChild = computed(() => child.children.length > 0);
const hasActivity = computed(() => !!todayDrawing.value.imgUrl);
</script>

<style scoped>
/* 필요 시 추가 스타일을 여기에 작성하세요 */
</style>

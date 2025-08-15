<template>
  <div class="mx-4">
    <!-- 1) 카드: 클릭하면 모달 열림 -->
    <div
      class="group flex items-center w-full max-w-sm rounded-xl shadow border bg-white px-4 py-3 cursor-pointer transition-all duration-300 hover:scale-105 hover:z-10 relative"
      :style="cardHoverStyle"
      @click="openView"
    >
      <!-- 컬러 스퀘어 -->
      <div
        class="w-12 h-12 rounded-md mr-4 flex-shrink-0 transition-all duration-300 group-hover:shadow-lg group-hover:scale-110"
        :style="{ backgroundColor: cardColor }"
      ></div>

      <!-- 날짜 / 제목 -->
      <div class="flex-1">
        <div class="text-lg font-paperBold text-gray-800 transition-colors duration-300 group-hover:text-blue-700">
          {{ formattedDate }}
        </div>
        <div class="text-base font-paper text-gray-700 transition-colors duration-300 group-hover:text-blue-600">
          {{ title }}
        </div>
      </div>

      <!-- 오른쪽 화살표 -->
      <div class="text-gray-400 text-lg font-paperSemiBold transition-all duration-300 group-hover:text-blue-500 group-hover:transform group-hover:translate-x-1">›</div>
    </div>

    <!-- 2) 모달만 teleport -->
    <teleport to="body">
      <BaseModal v-model="showModal" class="z-[50]">
        <!-- header 슬롯 -->
        <template #header>
          <div
            class="flex items-center justify-between bg-blue-100 px-4 py-2 rounded-t-xl"
          >
            <h3 class="text-lg font-semibold text-gray-800">
              {{ mode === "view" ? `일정: ${title}` : "일정 수정" }}
            </h3>
            <button
              @click="showModal = false"
              class="p-1 hover:bg-gray-200 rounded-full"
            ></button>
          </div>
        </template>

        <!-- default 슬롯: view/edit 모두 여기로 감쌈 -->
        <template #default>
          <div class="px-6 py-4">
            <!-- view 모드 -->
            <div v-if="mode === 'view'">
              <!-- 기간 -->
              <div class="flex items-center text-gray-700 mb-4">
                <svg
                  class="w-5 h-5 mr-2"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M8 7V3m8 4V3m-9 8h10m-12 5h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v9a2 2 0 002 2z"
                  />
                </svg>
                <span class="font-paper text-lg">기간: {{ fullDate }}</span>
              </div>

              <!-- 상세 내용 박스 -->
              <div
                class="border border-black rounded-lg p-4 h-64 overflow-y-auto mb-6"
              >
                <p class="font-paper text-gray-800 whitespace-pre-wrap">
                  상세 내용:<br />
                  {{ description || "세부 내용이 없습니다." }}
                </p>
              </div>

              <!-- 액션 버튼 -->
              <div class="flex justify-end space-x-2">
                <button
                  @click="switchToEdit"
                  class="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
                >
                  일정 수정
                </button>
                <button
                  @click="onDelete"
                  class="px-4 py-2 border border-gray-300 rounded hover:bg-gray-100"
                >
                  일정 삭제
                </button>
              </div>
            </div>

            <!-- edit 모드 -->
            <div v-else class="space-y-4">
              <label class="block">
                <span class="text-gray-700">제목</span>
                <input
                  v-model="editTitle"
                  type="text"
                  class="mt-1 w-full border rounded px-2 py-1"
                />
              </label>
              <label class="block">
                <span class="text-gray-700">날짜</span>
                <input
                  v-model="editDate"
                  type="date"
                  class="mt-1 w-full border rounded px-2 py-1"
                />
              </label>
              <label class="block">
                <span class="text-gray-700">상세 내용</span>
                <textarea
                  v-model="editDescription"
                  rows="6"
                  class="mt-1 w-full border rounded px-2 py-1"
                ></textarea>
              </label>
              <div class="flex justify-end space-x-2">
                <button
                  @click="saveEdit"
                  class="px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700"
                >
                  저장
                </button>
                <button
                  @click="mode = 'view'"
                  class="px-4 py-2 border border-gray-300 rounded hover:bg-gray-100"
                >
                  취소
                </button>
              </div>
            </div>
          </div>
        </template>
      </BaseModal>
    </teleport>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import dayjs from "@/utils/dayjs";
import BaseModal from "@/components/modal/BaseModal.vue";

import { cardColors } from "@/data/cardColors.js";

const props = defineProps({
  id: { type: [String, Number], required: true },
  date: { type: String, required: true },
  title: { type: String, required: true },
  description: { type: String, default: "" },
});
const emit = defineEmits(["update", "delete"]);

const showModal = ref(false);
const mode = ref("view");

const editTitle = ref("");
const editDate = ref("");
const editDescription = ref("");

// 날짜 포맷
const formattedDate = computed(() => dayjs(props.date).tz('Asia/Seoul').format("MM.DD"));
const fullDate = computed(() =>
  dayjs(props.date).tz('Asia/Seoul').format("YYYY년 MM월 DD일 (ddd)")
);

// 카드 색상
const cardColor = computed(() => {
  const idx = (dayjs(props.date).tz('Asia/Seoul').date() - 1) % cardColors.length;
  return cardColors[idx];
});

// 카드 색상에 따른 호버 스타일
const cardHoverStyle = computed(() => {
  const color = cardColor.value;
  
  // RGB 값 추출 (hex to rgb)
  const hex = color.replace('#', '');
  const r = parseInt(hex.substr(0, 2), 16);
  const g = parseInt(hex.substr(2, 2), 16);
  const b = parseInt(hex.substr(4, 2), 16);
  
  return {
    '--hover-shadow': `0 0 12px rgba(${r}, ${g}, ${b}, 0.5)`,
    '--hover-border-color': color,
    '--hover-bg-color': `rgba(${r}, ${g}, ${b}, 0.05)`
  };
});

// 메서드들
function openView() {
  showModal.value = true;
  mode.value = "view";
}
function switchToEdit() {
  editTitle.value = props.title;
  editDate.value = props.date;
  editDescription.value = props.description;
  mode.value = "edit";
}
function saveEdit() {
  emit("update", {
    id: props.id, 
    oldDate: props.date,
    newDate: editDate.value,
    newTitle: editTitle.value,
    newDescription: editDescription.value,
  });
  showModal.value = false;
  mode.value = "view";
}
function onDelete() {
  emit("delete", props.id);
  showModal.value = false;
}
</script>

<style scoped>
.group:hover {
  box-shadow: var(--hover-shadow);
  border-color: var(--hover-border-color);
  background-color: var(--hover-bg-color);
}
</style>

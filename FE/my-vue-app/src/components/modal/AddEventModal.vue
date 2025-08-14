<template>
  <BaseModal v-model="visible">
    <template #header>일정 등록하기</template>

    <div class="px-4 py-2">
      <label class="block mb-2 font-paperSemi">일정 제목:</label>
      <input
        v-model="title"
        type="text"
        placeholder="제목을 입력해주세요"
        class="w-full border rounded px-3 py-2 mb-4"
      />

      <label class="block mb-2 font-paperSemi">기간:</label>
      <input
        v-model="date"
        type="date"
        class="w-full border rounded px-3 py-2 mb-4"
      />

      <label class="block mb-2 font-paperSemi">상세 내용:</label>
      <textarea
        v-model="description"
        placeholder="내용을 입력해주세요"
        class="w-full border rounded px-3 py-2 h-40 resize-none font-paper"
      ></textarea>

      <button
        @click="submitEvent"
        class="mt-4 px-4 py-2 bg-blue-600 text-white rounded font-paper hover:bg-blue-700"
      >
        일정 등록
      </button>
    </div>
  </BaseModal>
</template>

<script setup>
import { ref, watch } from "vue";
import BaseModal from "@/components/modal/BaseModal.vue";
import { useNotification } from "@/composables/useNotification.js";

const props = defineProps({
  modelValue: Boolean,
  initialDate: String,
});

const emit = defineEmits(["update:modelValue", "add-event"]);
const { showError } = useNotification();

const visible = ref(false);
const title = ref("");
const date = ref(new Date().toISOString().substr(0, 10)); // 기본값 당일 날짜
const description = ref("");

watch(
  () => props.modelValue,
  (newVal) => {
    visible.value = newVal;
    if (newVal && props.initialDate) {
      date.value = props.initialDate;
    } else if (newVal) {
      date.value = new Date().toISOString().substr(0, 10);
    }
  }
);

watch(visible, (newVal) => emit("update:modelValue", newVal));

function submitEvent() {
  if (!title.value || !date.value) {
    showError("제목과 날짜는 필수 입력사항입니다.", "입력 오류");
    return;
  }

  emit("add-event", {
    title: title.value,
    date: date.value,
    description: description.value,
  });

  // 초기화 후 닫기
  title.value = "";
  description.value = "";
  visible.value = false;
}
</script>

<template>
  <BaseModal v-model="internalValue">
    <template #header> 아이 선택 </template>

    <div class="text-center mb-6">
      <p class="text-gray-600 text-lg">해당 아이의 프로필로 연결됩니다.</p>
    </div>

    <!-- 아이 목록 -->
    <div class="space-y-4 max-h-80 overflow-y-auto">
      <div
        v-for="child in children"
        :key="child.id"
        @click="selectChild(child)"
        class="flex items-center p-4 rounded-xl border-2 cursor-pointer transition-all hover:shadow-md hover:border-blue-300 hover:bg-blue-50"
        :style="{
          borderColor: '#e5e7eb',
          backgroundColor: 'white',
        }"
      >
        <!-- 프로필 이미지 -->
        <img
          :src="child.profileImage || 'https://placehold.co/80x80'"
          :alt="`${child.name} 프로필`"
          class="w-16 h-16 rounded-full object-cover border-4 border-gray-200"
        />

        <!-- 아이 정보 -->
        <div class="ml-4 flex-1">
          <p class="text-xl font-paperBold text-gray-800 mb-1">
            이름 : {{ child.name }}
          </p>
          <p class="text-base text-sm text-gray-600">
            {{ calculateAge(child.birthDate) }}세
          </p>
          <p
            v-if="child.interests && child.interests.length > 0"
            class="text-m text-gray-500 mt-1"
          >
            관심사: {{ child.interests.slice(0, 5).join(", ")
            }}{{ child.interests.length > 5 ? " 외" : "" }}
          </p>
        </div>

        <!-- 화살표 아이콘 -->
        <div class="text-gray-400 text-2xl">
          <svg
            class="w-6 h-6"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M9 5l7 7-7 7"
            />
          </svg>
        </div>
      </div>
    </div>

    <!-- 아이 추가 버튼 -->
    <div class="mt-6 pt-4 border-t border-gray-200">
      <button
        @click="goToRegister"
        class="w-full bg-purple-500 text-white py-4 rounded-xl font-paperBold text-lg hover:bg-purple-600 transition-colors flex items-center justify-center"
      >
        <svg
          class="w-6 h-6 mr-2"
          fill="none"
          stroke="currentColor"
          viewBox="0 0 24 24"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            d="M12 6v6m0 0v6m0-6h6m-6 0H6"
          />
        </svg>
        새 아이 등록하기
      </button>
    </div>
  </BaseModal>
</template>

<script setup>
import { computed } from "vue";
import BaseModal from "@/components/modal/BaseModal.vue";

// Props
const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false,
  },
  children: {
    type: Array,
    default: () => [],
  },
  actionText: {
    type: String,
    default: "활동",
  },
});

// Emits
const emit = defineEmits(["update:modelValue", "select", "register"]);

// BaseModal과 연동하기 위한 computed
const internalValue = computed({
  get: () => props.modelValue,
  set: (value) => emit("update:modelValue", value),
});

// 아이 선택
function selectChild(child) {
  emit("select", child);
  internalValue.value = false;
}

// 아이 등록하기
function goToRegister() {
  emit("register");
  internalValue.value = false;
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

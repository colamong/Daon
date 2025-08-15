<template>
  <BaseModal v-model="internalValue">
    <template #header> 아이 선택 </template>

    <div class="text-center mb-4 md:mb-6">
      <p class="text-gray-600 text-sm md:text-lg">해당 아이의 프로필로 연결됩니다.</p>
    </div>

    <!-- 아이 목록 -->
    <div class="space-y-3 md:space-y-4 max-h-60 md:max-h-80 overflow-y-auto">
      <div
        v-for="child in children"
        :key="child.id"
        @click="selectChild(child)"
        class="flex items-center p-3 md:p-4 rounded-xl border-2 cursor-pointer transition-all hover:shadow-md hover:border-blue-300 hover:bg-blue-50"
        :style="{
          borderColor: '#e5e7eb',
          backgroundColor: 'white',
        }"
      >
        <!-- 프로필 이미지 -->
        <img
          :src="child.profileImage || 'https://placehold.co/60x60'"
          :alt="`${child.name} 프로필`"
          class="w-12 h-12 md:w-16 md:h-16 rounded-full object-cover border-2 md:border-4 border-gray-200 flex-shrink-0"
        />

        <!-- 아이 정보 -->
        <div class="ml-3 md:ml-4 flex-1 min-w-0">
          <p class="text-base md:text-xl font-paperBold text-gray-800 mb-1 truncate">
            이름 : {{ child.name }}
          </p>
          <p class="text-xs md:text-sm text-gray-600">
            {{ calculateAge(child.birthDate) }}세
          </p>
          <p
            v-if="child.interests && child.interests.length > 0"
            class="text-xs md:text-sm text-gray-500 mt-1 truncate"
          >
            관심사: {{ child.interests.slice(0, 3).join(", ")
            }}{{ child.interests.length > 3 ? " 외" : "" }}
          </p>
        </div>

        <!-- 화살표 아이콘 -->
        <div class="text-gray-400 flex-shrink-0">
          <svg
            class="w-5 h-5 md:w-6 md:h-6"
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
    <div class="mt-4 md:mt-6 pt-3 md:pt-4 border-t border-gray-200">
      <button
        @click="goToRegister"
        class="w-full bg-purple-500 text-white py-3 md:py-4 rounded-xl font-paperBold text-sm md:text-lg hover:bg-purple-600 transition-colors flex items-center justify-center"
      >
        <svg
          class="w-5 h-5 md:w-6 md:h-6 mr-2"
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

<template>
  <transition name="fade">
    <div
      v-if="modelValue"
      class="fixed inset-0 bg-black bg-opacity-90 flex items-center justify-center z-50 font-paper"
    >
      <!-- 이전 리포트 버튼 -->
      <IconButton
        v-if="showNavigation && hasPreviousReport"
        variant="left-arrow"
        label="이전 리포트"
        @click="$emit('previous')"
        class="fixed top-1/2 -translate-y-1/2 z-60"
        :class="leftPosition"
      />

      <!-- 모달 내용 -->
      <div class="bg-white rounded-xl overflow-hidden shadow-lg max-w-lg w-full min-w-[700px] min-h-[600px]">
        <!-- 헤더 -->
        <div class="flex items-center justify-between px-4 py-2 bg-blue-100 border-b border-blue-200">
          <h3 class="text-lg font-semibold text-gray-800">
            {{ childName }} - {{ formatDate(reportDate) }} 감정 리포트
          </h3>
          <IconButton
            variant="close"
            label="닫기"
            class="bg-white text-black hover:bg-gray-100"
            @click="close"
          />
        </div>

        <!-- 본문 -->
        <div class="p-4">
          <div v-if="reportData" class="space-y-6">
            <!-- 그림일기 -->
            <div>
              <h4 class="text-lg font-paperBold text-gray-800 mb-3">그림일기</h4>
              <div class="bg-gray-50 rounded-lg p-4">
                <img 
                  :src="reportData.diaryImage" 
                  :alt="`${formatDate(reportDate)} 그림일기`"
                  class="w-full h-48 object-cover rounded-lg mb-3"
                />
                <p class="text-gray-700 font-paper leading-relaxed">
                  {{ reportData.diaryText }}
                </p>
              </div>
            </div>

            <!-- 감정 상태 -->
            <div>
              <h4 class="text-lg font-paperBold text-gray-800 mb-3">오늘의 감정</h4>
              <div class="flex flex-wrap gap-2">
                <span
                  v-for="emotion in reportData.emotions"
                  :key="emotion"
                  class="bg-purple-100 text-purple-800 px-3 py-2 rounded-full text-sm font-paper"
                >
                  {{ emotion }}
                </span>
              </div>
            </div>

            <!-- 활동 내용 -->
            <div>
              <h4 class="text-lg font-paperBold text-gray-800 mb-3">주요 활동</h4>
              <div class="flex flex-wrap gap-2">
                <span
                  v-for="activity in reportData.activities"
                  :key="activity"
                  class="bg-blue-100 text-blue-800 px-3 py-2 rounded-full text-sm font-paper"
                >
                  {{ activity }}
                </span>
              </div>
            </div>

            <!-- 요약 -->
            <div>
              <h4 class="text-lg font-paperBold text-gray-800 mb-3">하루 요약</h4>
              <div class="bg-gray-50 rounded-lg p-4">
                <p class="text-gray-700 font-paper leading-relaxed">
                  {{ reportData.summary }}
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 다음 리포트 버튼 -->
      <IconButton
        v-if="showNavigation && hasNextReport"
        variant="right-arrow"
        label="다음 리포트"
        @click="$emit('next')"
        class="fixed top-1/2 -translate-y-1/2 z-60"
        :class="rightPosition"
      />
    </div>
  </transition>
</template>

<script setup>
import { computed } from 'vue'
import IconButton from '@/components/button/IconButton.vue'

const props = defineProps({
  modelValue: {
    type: Boolean,
    required: true
  },
  childName: {
    type: String,
    required: true
  },
  reportDate: {
    type: String,
    required: true
  },
  reportData: {
    type: Object,
    required: true
  },
  showNavigation: {
    type: Boolean,
    default: false
  },
  hasPreviousReport: {
    type: Boolean,
    default: false
  },
  hasNextReport: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue', 'previous', 'next'])

// 모달 외부 버튼 위치 계산 (모달 너비 700px 기준)
const leftPosition = computed(() => "left-[calc(50%-350px-60px)]")
const rightPosition = computed(() => "left-[calc(50%+350px+12px)]")

function close() {
  emit('update:modelValue', false)
}

// 날짜 포맷팅 함수
function formatDate(dateString) {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
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
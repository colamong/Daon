<template>
  <transition name="fade">
    <div
      v-if="modelValue"
      class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 font-paper"
    >
      <div class="bg-white rounded-xl overflow-hidden shadow-lg max-w-md w-full mx-4">
        <!-- 본문 -->
        <div class="p-8 text-center">
          <!-- 제목 -->
          <h3 class="text-xl font-paperBold text-gray-800 mb-2">
            발음 평가하기
          </h3>
          <p class="text-sm text-gray-500 mb-8">
            아래의 시준에서 발음 평가 점수를 높여보세요!
          </p>

          <!-- 발음할 문장 -->
          <div class="mb-8">
            <p class="text-2xl font-paperBold text-gray-800 mb-2">
              {{ answerText }}
            </p>
            <p class="text-sm text-gray-500">
              {{ pronunciation }}
            </p>
          </div>

          <!-- 녹음 완료 후 점수 표시 -->
          <div v-if="recordingComplete" class="mb-8">
            <p class="text-sm text-gray-500 mb-2">발음 평가</p>
            <div class="border border-gray-200 rounded-lg p-6 mb-4">
              <div class="text-6xl font-paperBold text-gray-800">
                {{ score }}점
              </div>
            </div>
            <button
              @click="retryRecording"
              class="text-gray-500 text-sm px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors"
            >
              다시 연습하기
            </button>
          </div>

          <!-- 버튼 -->
          <div class="space-y-3">
            <button
              v-if="!recordingComplete"
              @click="toggleRecording"
              :disabled="isRecording"
              class="w-full px-6 py-3 bg-blue-500 hover:bg-blue-600 disabled:bg-blue-300 text-white rounded-lg transition-colors font-paperBold"
            >
              {{ isRecording ? '녹음 중...' : '녹음하기' }}
            </button>
            
            <div v-if="recordingComplete" class="flex space-x-3">
              <button
                @click="completeAssessment"
                class="flex-1 px-6 py-3 bg-blue-500 hover:bg-blue-600 text-white rounded-lg transition-colors font-paperBold"
              >
                종료
              </button>
            </div>

            <!-- 항상 표시되는 종료 버튼 -->
            <button
              @click="close"
              class="w-full px-4 py-2 bg-gray-200 hover:bg-gray-300 text-gray-700 rounded-lg transition-colors font-paper"
            >
              종료
            </button>
          </div>
        </div>
      </div>
    </div>
  </transition>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({
  modelValue: {
    type: Boolean,
    required: true,
  },
  answerText: {
    type: String,
    required: true,
  },
  pronunciation: {
    type: String,
    required: true,
  }
})

const emit = defineEmits(['update:modelValue', 'complete'])

const isRecording = ref(false)
const recordingComplete = ref(false)
const score = ref(0)

const toggleRecording = async () => {
  if (isRecording.value) {
    return // 이미 녹음 중이면 무시
  }
  
  // 녹음 시작
  isRecording.value = true
  
  // 실제로는 여기서 음성 녹음 API 호출
  // 임시로 2초 후 녹음 완료 처리
  setTimeout(() => {
    isRecording.value = false
    recordingComplete.value = true
    // 임시 점수 (실제로는 음성 인식 API 결과)
    score.value = Math.floor(Math.random() * 20) + 80 // 80-100점 랜덤
  }, 2000)
}

const retryRecording = () => {
  isRecording.value = false
  recordingComplete.value = false
  score.value = 0
}

const completeAssessment = () => {
  emit('complete', score.value)
  close()
}

const close = () => {
  retryRecording() // 상태 초기화
  emit('update:modelValue', false)
}
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
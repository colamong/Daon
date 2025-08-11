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
import RecordRTC, { StereoAudioRecorder } from 'recordrtc'
import { evaluatePronunciation } from '@/services/pronunciationService'

const props = defineProps({
  modelValue: { type: Boolean, required: true },
  answerText: { type: String, required: true },
  pronunciation: { type: String, required: true },
  questionId: { type: [Number, String], required: true }
})
const emit = defineEmits(['update:modelValue', 'complete'])

const isRecording = ref(false)
const recordingComplete = ref(false)
const score = ref(0)

let stream, recorder

async function toggleRecording() {
  if (isRecording.value) return
  stream = await navigator.mediaDevices.getUserMedia({ audio: true })

  recorder = new RecordRTC(stream, {
    type: 'audio',
    mimeType: 'audio/wav',             // WAV로 인코딩
    recorderType: StereoAudioRecorder,
    numberOfAudioChannels: 1,          // mono
    desiredSampRate: 16000             // 16kHz (기기 따라 다를 수 있음)
  })

  isRecording.value = true
  recorder.startRecording()

  // 2초 녹음 후 자동 종료/업로드 (원하면 수동 종료 버튼으로 변경)
  setTimeout(stopAndUpload, 2000)
}

async function stopAndUpload() {
  await new Promise(r => recorder.stopRecording(r))
  const blob = recorder.getBlob() // audio/wav
  stream.getTracks().forEach(t => t.stop())
  recorder.destroy()
  recorder = null

  // 업로드
  const file = new File([blob], 'speech.wav', { type: 'audio/wav' })
  const res = await evaluatePronunciation(props.questionId, file)
  console.log('[Pronunciation] evaluate response:', res) 

  // 백엔드 점수가 0~1이면 100점 환산
  const raw = res?.score
  const scaled = raw <= 1 ? Math.round(raw * 100) : Math.round(raw)
  score.value = Number.isFinite(scaled) ? scaled : 0
  recordingComplete.value = true
  isRecording.value = false
}

function retryRecording() {
  isRecording.value = false
  recordingComplete.value = false
  score.value = 0
}

function completeAssessment() {
  emit('complete', score.value)
  close()
}

function close() {
  try { stream?.getTracks().forEach(t => t.stop()) } catch {}
  if (recorder) { try { recorder.destroy() } catch {} }
  retryRecording()
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
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
            
            <!-- 평가 완료 후 종료 버튼 -->
            <button
              v-if="recordingComplete"
              @click="completeAssessment"
              class="w-full px-6 py-3 bg-blue-500 hover:bg-blue-600 text-white rounded-lg transition-colors font-paperBold"
            >
              종료
            </button>

            <!-- 녹음 이전에만 표시되는 종료 버튼 -->
            <button
              v-if="!recordingComplete"
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
// 업로드 직전에 모노·16k WAV로 강제 정규화하는 API (ensureMono16kWav 내장)
import { evaluatePronunciation } from '@/services/pronunciationService.js'

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

let stream = null
let recorder = null

async function toggleRecording() {
  if (isRecording.value) return

  // 1) 모노/노이즈 억제 힌트로 입력 품질 고정 시도
  stream = await navigator.mediaDevices.getUserMedia({
    audio: {
      channelCount: 1,          // 모노 요청
      sampleRate: 16000,        // 샘플레이트 힌트(브라우저가 무시할 수 있음)
      sampleSize: 16,
      echoCancellation: true,
      noiseSuppression: true,
      autoGainControl: true
    }
  })

  // 2) localhost에서 점수가 잘 나왔던 녹음기 타입으로 고정
  recorder = new RecordRTC(stream, {
    type: 'audio',
    mimeType: 'audio/wav',
    recorderType: StereoAudioRecorder,
    numberOfAudioChannels: 1,   // 모노
    desiredSampRate: 16000      // 16kHz 힌트
  })

  isRecording.value = true
  recorder.startRecording()

  // 필요 시 수동 종료 버튼으로 바꿔도 됨
  setTimeout(stopAndUpload, 2000)
}

async function stopAndUpload() {
  // 3) 녹음 정지 + 짧은 플러시 대기
  await new Promise(res => recorder.stopRecording(res))
  await new Promise(r => setTimeout(r, 100))

  const rawBlob = recorder.getBlob() // webm/wav 상관 없음(아래 API에서 정규화)
  stream.getTracks().forEach(t => t.stop())
  recorder.destroy()
  recorder = null
  stream = null

  // 4) 업로드 직전 pronunciationService.js 내부에서 ensureMono16kWav 실행됨
  const res = await evaluatePronunciation(props.questionId, rawBlob)
  console.log('[Pronunciation] evaluate response:', res)

  // 5) 백엔드가 0~100 정수 점수 반환 
  score.value = Number.isFinite(res?.score) ? res.score : 0
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
  try {
    stream?.getTracks()?.forEach(t => t.stop())
  } catch (e) {
  }

  if (recorder) {
    try { recorder.destroy() } catch (e) { /* ignore */ }
  }

  recorder = null
  stream = null

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
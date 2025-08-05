<template>
  <div class="m-10 mb-20 max-w-5xl mx-auto px-4 py-8 font-paper bg-white rounded-xl">
    <!-- 헤더 -->
    <div v-if="currentContent && currentTheme">
      <div class="flex justify-between items-center mb-6">
        <div>
          <h1 class="text-4xl font-paperBold text-gray-800 mb-2">
            {{ currentTheme.title }}에서
          </h1>
          <p class="text-lg text-gray-600">
            Ch.{{ chapterId }} {{ currentChapter?.title || '' }}
          </p>
        </div>
        
        <!-- 버튼들 -->
        <div class="flex space-x-2">
          <IconButton
            v-if="questionId > 1"
            variant="left-arrow"
            label="이전 질문"
            @click="confirmNavigation('prev')"
            class="hover:bg-green-500"
          />
          <IconButton
            v-if="questionId < totalQuestions"
            variant="right-arrow"
            label="다음 질문"
            @click="confirmNavigation('next')"
            class="bg-blue-400/80 hover:bg-blue-500/80 text-white"
          />
          <button
            @click="confirmExit"
            class="bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded-lg transition-colors"
          >
            나가기
          </button>
        </div>
      </div>
      
      <!-- 구분선 -->
      <hr class="border-gray-300 mb-8">
    </div>

    <!-- 학습 콘텐츠 -->
    <div v-if="currentContent" class="space-y-8">
      <!-- 상황 설명 -->
      <div class="bg-blue-100 rounded-lg p-6">
        <h2 class="text-xl font-paperBold text-gray-800 mb-2">상황</h2>
        <p class="text-gray-700">{{ currentContent.situation }}</p>
      </div>

      <!-- 질문 카드 -->
      <div class="mb-8">
        <div class="bg-transition-blue/20 px-6 py-4 rounded-2xl w-full">
          <div class="flex justify-between items-center">
            <div class="flex-1">
              <p class="text-base font-paperBold text-black mb-2">
                {{ currentContent.question }}
              </p>
              <p class="text-gray-600 text-sm">
                {{ currentContent.questionPronunciation }}
              </p>
            </div>
            <button
              @click="playQuestionAudio"
              class="ml-4 bg-white hover:bg-gray-100 p-2 rounded-lg transition-colors border flex items-center justify-center w-8 h-8"
            >
              🔊
            </button>
          </div>
        </div>
      </div>

      <!-- 답변 선택지 -->
      <div class="space-y-4">
        <h3 class="text-xl font-paperBold text-gray-800">답변을 선택하세요:</h3>
        <div 
          v-for="answer in currentContent.answers"
          :key="answer.id"
          class="space-y-2"
        >
          <AnswerCard 
            :text="answer.text"
            :isCorrect="answer.isCorrect"
            :class="selectedAnswer === answer.id ? 'ring-2 ring-blue-500' : ''"
            @correct="handleCorrectAnswer(answer)"
            @incorrect="handleIncorrectAnswer(answer)"
            @playAudio="playAnswerAudio(answer)"
          />
        </div>
      </div>

      <!-- 다음 버튼 -->
      <div class="text-center mt-8">
        <button
          v-if="selectedAnswer"
          @click="submitAnswer"
          class="bg-blue-500 hover:bg-blue-600 text-white px-8 py-3 rounded-lg transition-colors text-lg font-paperBold"
        >
          답변 확인
        </button>
      </div>
    </div>

    <!-- 콘텐츠를 찾을 수 없는 경우 -->
    <div v-else class="text-center py-12">
      <h2 class="text-2xl font-paperBold text-gray-600 mb-4">
        학습 콘텐츠를 찾을 수 없습니다
      </h2>
      <button 
        @click="$router.push('/dashboard/learning')"
        class="bg-blue-500 hover:bg-blue-600 text-white px-6 py-3 rounded-lg transition-colors"
      >
        테마 선택으로 돌아가기
      </button>
    </div>

    <!-- 발음 학습 확인 모달 -->
    <div
      v-if="showPronunciationConfirm"
      class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 font-paper"
    >
      <div class="bg-white rounded-xl overflow-hidden shadow-lg max-w-md w-full mx-4">
        <div class="p-6 text-center">
          <div class="mb-4">
            <span class="text-4xl">🎉</span>
          </div>
          <h3 class="text-lg font-paperSemi mb-2 text-gray-800">
            발음을 학습하시겠습니까?
          </h3>
          <p class="text-gray-600 mb-6">
            정답입니다! 발음 연습을 통해 더 완벽하게 학습해보세요.
          </p>
          
          <div class="flex space-x-3 justify-center">
            <button
              @click="skipPronunciationPractice"
              class="px-4 py-2 bg-gray-200 hover:bg-gray-300 text-gray-700 rounded-lg transition-colors font-paper"
            >
              아니요
            </button>
            <button
              @click="startPronunciationPractice"
              class="px-4 py-2 bg-blue-500 hover:bg-blue-600 text-white rounded-lg transition-colors font-paper"
            >
              학습하기
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 발음 평가 모달 -->
    <PronunciationModal
      v-model="showPronunciationModal"
      :answer-text="selectedCorrectAnswer?.text || ''"
      :pronunciation="selectedCorrectAnswer?.pronunciation || ''"
      @complete="completePronunciationPractice"
    />

    <!-- 질문 이동 확인 모달 -->
    <div
      v-if="showNavigationConfirm"
      class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 font-paper"
    >
      <div class="bg-white rounded-xl overflow-hidden shadow-lg max-w-md w-full mx-4">
        <div class="p-6 text-center">
          <div class="mb-4">
            <span class="text-4xl">⚠️</span>
          </div>
          <h3 class="text-lg font-paperSemi mb-2 text-gray-800">
            학습 중단
          </h3>
          <p class="text-gray-600 mb-6">
            학습이 완료되지 않았습니다. 정말 이동하시겠습니까?
          </p>
          
          <div class="flex space-x-3 justify-center">
            <button
              @click="cancelNavigation"
              class="px-4 py-2 bg-gray-200 hover:bg-gray-300 text-gray-700 rounded-lg transition-colors font-paper"
            >
              아니요
            </button>
            <button
              @click="executeNavigation"
              class="px-4 py-2 bg-red-500 hover:bg-red-600 text-white rounded-lg transition-colors font-paper"
            >
              네
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 나가기 확인 모달 -->
    <div
      v-if="showExitConfirm"
      class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 font-paper"
    >
      <div class="bg-white rounded-xl overflow-hidden shadow-lg max-w-md w-full mx-4">
        <div class="p-6 text-center">
          <div class="mb-4">
            <span class="text-4xl">⚠️</span>
          </div>
          <h3 class="text-lg font-paperSemi mb-2 text-gray-800">
            학습 중단
          </h3>
          <p class="text-gray-600 mb-6">
            학습을 중단하고 나가시겠습니까?
          </p>
          
          <div class="flex space-x-3 justify-center">
            <button
              @click="cancelExit"
              class="px-4 py-2 bg-gray-200 hover:bg-gray-300 text-gray-700 rounded-lg transition-colors font-paper"
            >
              아니요
            </button>
            <button
              @click="executeExit"
              class="px-4 py-2 bg-red-500 hover:bg-red-600 text-white rounded-lg transition-colors font-paper"
            >
              네
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useNotification } from '@/composables/useNotification'
import { learningThemes } from '@/data/learningThemes.js'
import { learningContent } from '@/data/learningContent.js'
import IconButton from '@/components/button/IconButton.vue'
import QuestionCard from '@/components/card/QuestionCard.vue'
import AnswerCard from '@/components/card/AnswerCard.vue'
import ConfirmModal from '@/components/modal/ConfirmModal.vue'
import PronunciationModal from '@/components/modal/PronunciationModal.vue'

const route = useRoute()
const router = useRouter()
const { showInfo, showWarning } = useNotification()

// 라우트 파라미터
const themeId = computed(() => route.params.themeId)
const chapterId = computed(() => Number(route.params.chapterId))
const questionId = computed(() => Number(route.params.questionId) || 1)

// 현재 테마와 챕터 정보
const currentTheme = computed(() => 
  learningThemes.find(theme => theme.id === themeId.value)
)

const currentChapter = computed(() => 
  currentTheme.value?.chapters.find(chapter => chapter.id === chapterId.value)
)

// 현재 학습 콘텐츠
const currentContent = computed(() => {
  const themeContent = learningContent[themeId.value]
  if (!themeContent) return null
  
  const chapterContent = themeContent[chapterId.value]
  if (!chapterContent) return null
  
  return chapterContent[questionId.value - 1]
})

// 총 질문 수
const totalQuestions = computed(() => {
  const themeContent = learningContent[themeId.value]
  if (!themeContent) return 0
  
  const chapterContent = themeContent[chapterId.value]
  if (!chapterContent) return 0
  
  return chapterContent.length
})

// 상태
const selectedAnswer = ref(null)
const showPronunciationConfirm = ref(false)
const showPronunciationModal = ref(false)
const selectedCorrectAnswer = ref(null)
const showNavigationConfirm = ref(false)
const showExitConfirm = ref(false)
const pendingNavigation = ref(null)

// 답변 선택
const selectAnswer = (answerId) => {
  selectedAnswer.value = answerId
}

// 정답 처리
const handleCorrectAnswer = (answer) => {
  selectedAnswer.value = answer.id
  selectedCorrectAnswer.value = answer
  showPronunciationConfirm.value = true
}

// 오답 처리
const handleIncorrectAnswer = (answer) => {
  showWarning('틀렸습니다. 다시 시도해보세요.', '오답', { duration: 2000 })
}

// 발음 연습 시작
const startPronunciationPractice = () => {
  showPronunciationConfirm.value = false
  showPronunciationModal.value = true
}

// 발음 연습 생략
const skipPronunciationPractice = () => {
  showPronunciationConfirm.value = false
  showInfo('정답입니다!', '성공', { duration: 2000 })
  setTimeout(() => {
    nextQuestion()
  }, 1500)
}

// 발음 연습 완료
const completePronunciationPractice = (score) => {
  showPronunciationModal.value = false
  showInfo(`발음 점수: ${score}점! 잘했습니다!`, '발음 평가 완료', { duration: 3000 })
  setTimeout(() => {
    nextQuestion()
  }, 2000)
}

// 답변 제출
const submitAnswer = () => {
  const answer = currentContent.value.answers.find(a => a.id === selectedAnswer.value)
  if (answer.isCorrect) {
    showInfo('정답입니다!', '성공', { duration: 2000 })
    // 다음 문제로 이동하거나 챕터 완료 처리
    nextQuestion()
  } else {
    showWarning('틀렸습니다. 다시 시도해보세요.', '오답', { duration: 2000 })
    selectedAnswer.value = null
  }
}

// 다음 문제로 이동
const nextQuestion = () => {
  const currentQuestionNum = questionId.value
  const totalQuestions = learningContent[themeId.value][chapterId.value].length
  
  // 상태 초기화
  selectedAnswer.value = null
  selectedCorrectAnswer.value = null
  
  if (currentQuestionNum < totalQuestions) {
    // 같은 챕터의 다음 문제로
    router.push(`/dashboard/learning/theme/${themeId.value}/chapter/${chapterId.value}/question/${currentQuestionNum + 1}`)
  } else {
    // 챕터 완료
    showInfo('챕터를 완료했습니다!', '완료', { duration: 3000 })
    setTimeout(() => {
      router.push(`/dashboard/learning/theme/${themeId.value}`)
    }, 2000)
  }
}

// 네비게이션 확인
const confirmNavigation = (direction) => {
  pendingNavigation.value = direction
  showNavigationConfirm.value = true
}

// 네비게이션 실행
const executeNavigation = () => {
  const direction = pendingNavigation.value
  showNavigationConfirm.value = false
  
  if (direction === 'prev') {
    router.push(`/dashboard/learning/theme/${themeId.value}/chapter/${chapterId.value}/question/${questionId.value - 1}`)
  } else if (direction === 'next') {
    router.push(`/dashboard/learning/theme/${themeId.value}/chapter/${chapterId.value}/question/${questionId.value + 1}`)
  }
  
  // 상태 초기화
  selectedAnswer.value = null
  selectedCorrectAnswer.value = null
}

// 네비게이션 취소
const cancelNavigation = () => {
  showNavigationConfirm.value = false
  pendingNavigation.value = null
}

// 나가기 확인
const confirmExit = () => {
  showExitConfirm.value = true
}

// 나가기 실행
const executeExit = () => {
  showExitConfirm.value = false
  router.push('/dashboard/learning')
}

// 나가기 취소
const cancelExit = () => {
  showExitConfirm.value = false
}

// 오디오 재생 (실제로는 TTS API 연동)
const playQuestionAudio = () => {
  showInfo('질문 음성을 재생합니다', '음성 재생', { duration: 1000 })
}

const playAnswerAudio = (answer) => {
  showInfo(`"${answer.text}" 음성을 재생합니다`, '음성 재생', { duration: 1000 })
  // 실제로는 여기서 TTS API를 호출하여 answer.text 음성을 재생
}
</script>
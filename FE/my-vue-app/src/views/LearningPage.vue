<template>
  <div
    class="m-10 mb-20 max-w-4xl mx-auto px-4 py-8 font-paper bg-white rounded-xl"
  >
    <!-- 헤더 -->
    <div v-if="currentContent && currentTheme">
      <!-- 모바일 레이아웃 -->
      <div class="md:hidden mb-4 px-2">
        <!-- 제목과 나가기 버튼이 같은 라인 -->
        <div class="flex items-center justify-between mb-1">
          <div class="flex-1"></div>
          <h1 class="text-xl font-paperBold text-gray-800 flex-1 text-center">
            {{ currentTheme.title }}에서
          </h1>
          <div class="flex-1 flex justify-end">
            <button
              @click="confirmExit"
              class="bg-red-500 hover:bg-red-600 text-white px-2 py-1 rounded text-xs"
            >
              나가기
            </button>
          </div>
        </div>

        <!-- 설명과 이동 버튼이 같은 라인 -->
        <div class="flex items-center justify-between mb-2">
          <div class="flex-1"></div>
          <p class="text-xs text-gray-600 flex-1 text-center whitespace-nowrap px-1">
            Ch.{{ currentChapterIndex }} {{ currentChapter?.title || "" }}
          </p>
          <div class="flex-1 flex justify-end">
            <div class="flex space-x-1">
              <IconButton
                v-if="questionId > 1"
                variant="left-arrow"
                label="이전 질문"
                @click="confirmNavigation('prev')"
                class="hover:bg-green-500 text-xs"
              />
              <IconButton
                v-if="questionId < totalQuestions"
                variant="right-arrow"
                label="다음 질문"
                @click="confirmNavigation('next')"
                class="bg-blue-400/80 hover:bg-blue-500/80 text-white text-xs"
              />
            </div>
          </div>
        </div>
      </div>

      <!-- 데스크톱 레이아웃 -->
      <div
        class="hidden md:flex md:justify-between md:items-center mb-6 pl-10 pr-5"
      >
        <div>
          <h1 class="text-4xl font-paperBold text-gray-800 mb-2">
            {{ currentTheme.title }}에서
          </h1>
          <p class="text-lg text-gray-600">
            Ch.{{ currentChapterIndex }} {{ currentChapter?.title || "" }}
          </p>
        </div>

        <!-- 데스크톱용 버튼들 -->
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
      <hr class="border-gray-300 mb-4 md:mb-8" />
    </div>

    <!-- 학습 콘텐츠 -->
    <div
      v-if="currentContent"
      class="space-y-4 md:space-y-8 px-2 md:pl-20 md:pr-20"
    >
      <!-- 질문 (타이핑 효과) -->
      <TypingEffect
        :key="questionId"
        :text="currentContent.question"
        :pronunciation="currentContent.questionPronunciation"
        :typing-speed="50"
        @typing-complete="onTypingComplete"
        @play-audio="playQuestionAudio"
      />

      <!-- 답변 선택지 -->
      <div v-if="showAnswers" class="space-y-3 md:space-y-4">
        <h3 class="text-lg md:text-xl font-paperBold text-gray-800 text-center">
          답변을 선택하세요
        </h3>
        <div
          v-for="answer in currentContent.answers"
          :key="answer.id"
          class="space-y-2 px-2 md:pl-20 md:pr-20"
        >
          <AnswerCard
            class="text-center"
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
      <div class="text-center mt-6 md:mt-8">
        <button
          v-if="selectedAnswer"
          @click="submitAnswer"
          class="bg-blue-500 hover:bg-blue-600 text-white px-6 md:px-8 py-2 md:py-3 rounded-lg transition-colors text-base md:text-lg font-paperBold"
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
      <div
        class="bg-white rounded-xl overflow-hidden shadow-lg max-w-md w-full mx-4"
      >
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
      :question-id="currentContent?.id"
      @complete="completePronunciationPractice"
    />

    <!-- 질문 이동 확인 모달 -->
    <div
      v-if="showNavigationConfirm"
      class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 font-paper"
    >
      <div
        class="bg-white rounded-xl overflow-hidden shadow-lg max-w-md w-full mx-4"
      >
        <div class="p-6 text-center">
          <div class="mb-4">
            <span class="text-4xl">⚠️</span>
          </div>
          <h3 class="text-lg font-paperSemi mb-2 text-gray-800">학습 중단</h3>
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
      <div
        class="bg-white rounded-xl overflow-hidden shadow-lg max-w-md w-full mx-4"
      >
        <div class="p-6 text-center">
          <div class="mb-4">
            <span class="text-4xl">⚠️</span>
          </div>
          <h3 class="text-lg font-paperSemi mb-2 text-gray-800">학습 중단</h3>
          <p class="text-gray-600 mb-6">학습을 중단하고 나가시겠습니까?</p>

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

    <!-- Confetti 축하 효과 -->
    <ConfettiEffect :show="showConfetti" />

    <!-- 오답 모달 -->
    <IncorrectAnswerModal
      :show="showIncorrectModal"
      @close="closeIncorrectModal"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useNotification } from "@/composables/useNotification";
import IconButton from "@/components/button/IconButton.vue";
import AnswerCard from "@/components/card/AnswerCard.vue";
import PronunciationModal from "@/components/modal/PronunciationModal.vue";
import ConfettiEffect from "@/components/effect/ConfettiEffect.vue";
import IncorrectAnswerModal from "@/components/modal/IncorrectAnswerModal.vue";
import TypingEffect from "@/components/effect/TypingEffect.vue";
import learningService from "@/services/learningService";
import ttsService from "@/services/ttsService";

const route = useRoute();
const router = useRouter();
const { showInfo, showWarning } = useNotification();

// 라우트 파라미터
const themeId = computed(() => Number(route.params.themeId));
const chapterId = computed(() => Number(route.params.chapterId));
const questionId = computed(() =>
  Math.max(1, Number(route.params.questionId) || 1)
); // 1-base index

// 상태
const themes = ref([]); // 전체 테마
const chapters = ref([]); // 현재 테마의 챕터 목록
const questions = ref([]); // 현재 챕터의 질문 목록

// 현재 테마/챕터/문항
const currentTheme = computed(() =>
  themes.value.find((t) => t.id === themeId.value)
);

const currentChapter = computed(() =>
  chapters.value.find((c) => c.id === chapterId.value)
);

const currentChapterIndex = computed(() => {
  const chapter = currentChapter.value;
  if (!chapter) return 0;
  return chapters.value.findIndex((c) => c.id === chapter.id) + 1;
});

const currentContent = computed(() => questions.value[questionId.value - 1]);

const totalQuestions = computed(() => questions.value.length);

// UI 상태
const selectedAnswer = ref(null);
const showPronunciationConfirm = ref(false);
const showPronunciationModal = ref(false);
const selectedCorrectAnswer = ref(null);
const showNavigationConfirm = ref(false);
const showExitConfirm = ref(false);
const pendingNavigation = ref(null);
const showConfetti = ref(false);
const showIncorrectModal = ref(false);
const showAnswers = ref(false);
const showQuestionCard = ref(false);

// 사운드 미리 로딩
const successAudio = ref(null);

// ---------- API 로딩 ----------
const loadThemes = async () => {
  const list = await learningService.getThemes();
  themes.value = list;
};

const loadChapters = async () => {
  if (!themeId.value) return;
  const list = await learningService.getChaptersByTheme(themeId.value);
  chapters.value = list;
};

const loadQuestions = async () => {
  if (!chapterId.value) return;
  const list = await learningService.getQuestionsByChapter(chapterId.value);
  questions.value = list;
  // 범위 보정
  if (questionId.value > totalQuestions.value && totalQuestions.value > 0) {
    router.replace(
      `/dashboard/learning/theme/${themeId.value}/chapter/${chapterId.value}/question/1`
    );
  }
};

// 사운드 미리 로딩
const loadSuccessSound = async () => {
  try {
    const answerSoundModule = await import("@/assets/effects/answer.mp3");
    successAudio.value = new Audio(answerSoundModule.default);
    successAudio.value.volume = 0.7;
    successAudio.value.preload = "auto";
  } catch (error) {
  }
};

// 최초 로딩
onMounted(async () => {
  await Promise.all([loadThemes(), loadChapters(), loadSuccessSound()]);
  await loadQuestions();
});

// 라우트 변경 대응
watch(
  () => chapterId.value,
  async () => {
    await loadQuestions();
  }
);
watch(
  () => questionId.value,
  () => {
    selectedAnswer.value = null;
    selectedCorrectAnswer.value = null;
    showAnswers.value = false;
    showQuestionCard.value = false;
  }
);

// ---------- 정답/발음 ----------
const handleCorrectAnswer = async (answer) => {
  selectedAnswer.value = answer.id;
  selectedCorrectAnswer.value = answer;

  // Confetti 효과 시작
  showConfetti.value = true;
  setTimeout(() => {
    showConfetti.value = false;
  }, 3000);

  // 성공 효과음 재생
  await playSuccessSound();

  showPronunciationConfirm.value = true;
};

// 성공 효과음 재생
const playSuccessSound = async () => {
  if (!successAudio.value) {
    return;
  }

  try {
    // 오디오 재설정 (이전 재생 중지)
    successAudio.value.currentTime = 0;

    const playPromise = successAudio.value.play();
    if (playPromise !== undefined) {
      playPromise
        .then(() => {
        })
        .catch((error) => {
        });
    }
  } catch (error) {
  }
};

const handleIncorrectAnswer = () => {
  // 오답 모달 표시
  showIncorrectModal.value = true;
};

// 오답 모달 닫기
const closeIncorrectModal = () => {
  showIncorrectModal.value = false;
  selectedAnswer.value = null;
};

const startPronunciationPractice = () => {
  showPronunciationConfirm.value = false;
  showPronunciationModal.value = true;
};

const skipPronunciationPractice = () => {
  showPronunciationConfirm.value = false;
  setTimeout(() => nextQuestion(), 500);
};

const completePronunciationPractice = (score) => {
  showPronunciationModal.value = false;
  showInfo(`발음 점수: ${score}점! 잘했습니다!`, "발음 평가 완료", {
    duration: 3000,
  });
  setTimeout(() => nextQuestion(), 2000);
};

const submitAnswer = () => {
  const answer = currentContent.value?.answers.find(
    (a) => a.id === selectedAnswer.value
  );
  if (!answer) return;
  // 서버 DTO에 isCorrect가 이미 있음 (DB is_correct)
  if (answer.isCorrect) {
    nextQuestion();
  } else {
    showWarning("틀렸습니다. 다시 시도해보세요.", "오답", { duration: 2000 });
    selectedAnswer.value = null;
  }

  // (옵션) 서버 텍스트 비교로 검증하려면:
  // const correctText = await learningService.getCorrectChoiceText(currentContent.value.id)
  // const isCorrect = answer.text?.trim() === String(correctText).trim()
};

const nextQuestion = () => {
  const curr = questionId.value;
  const total = totalQuestions.value;
  selectedAnswer.value = null;
  selectedCorrectAnswer.value = null;

  if (curr < total) {
    router.push(
      `/dashboard/learning/theme/${themeId.value}/chapter/${
        chapterId.value
      }/question/${curr + 1}`
    );
  } else {
    showInfo("챕터를 완료했습니다!", "완료", { duration: 3000 });
    setTimeout(() => {
      router.push(`/dashboard/learning/theme/${themeId.value}`);
    }, 1200);
  }
};

// ---------- 네비게이션 ----------
const confirmNavigation = (direction) => {
  pendingNavigation.value = direction;
  showNavigationConfirm.value = true;
};

const executeNavigation = () => {
  const direction = pendingNavigation.value;
  showNavigationConfirm.value = false;
  const target =
    direction === "prev" ? questionId.value - 1 : questionId.value + 1;
  if (target >= 1 && target <= totalQuestions.value) {
    router.push(
      `/dashboard/learning/theme/${themeId.value}/chapter/${chapterId.value}/question/${target}`
    );
  }
  selectedAnswer.value = null;
  selectedCorrectAnswer.value = null;
};

const cancelNavigation = () => {
  showNavigationConfirm.value = false;
  pendingNavigation.value = null;
};

// ---------- 나가기 ----------
const confirmExit = () => {
  showExitConfirm.value = true;
};
const executeExit = () => {
  showExitConfirm.value = false;
  router.push("/dashboard/learning");
};
const cancelExit = () => {
  showExitConfirm.value = false;
};

// ---------- 오디오 ----------
const playQuestionAudio = async () => {
  const text = (currentContent.value?.question || "").trim();
  const url = currentContent.value?.audioUrl;

  try {
    if (ttsService.supported && text) {
      await ttsService.speakText(text, { lang: "ko-KR", rate: 1, pitch: 1.05 });
    } else if (url) {
      await new Audio(url).play();
    } else {
      showInfo("질문 음성을 재생합니다", "음성 재생", { duration: 1000 });
    }
  } catch {}
};

const playAnswerAudio = async (answer) => {
  const text = (answer?.text || "").trim();
  const url = answer?.audioUrl;

  try {
    if (ttsService.supported && text) {
      await ttsService.speakText(text, { lang: "ko-KR", rate: 1, pitch: 1.05 });
    } else if (url) {
      await new Audio(url).play();
    } else {
      showInfo(`"${answer.text}" 음성을 재생합니다`, "음성 재생", {
        duration: 1000,
      });
    }
  } catch {
    showWarning("음성 재생에 실패했습니다.", "오류", { duration: 1500 });
  }
};

// 타이핑 완료 시 답변 선택지 표시
const onTypingComplete = () => {
  // 타이핑 완료 후 약간의 딜레이 후 답변 선택지 표시
  setTimeout(() => {
    showAnswers.value = true;
  }, 500);
};
</script>

<template>
  <div class="relative w-screen h-screen overflow-hidden">
    <!-- background -->
    <img
      :src="bgImage"
      alt="background"
      class="absolute inset-0 w-full h-full object-cover -z-10"
    />

    <!-- header -->
    <header
      class="fixed top-4 left-4 right-4 z-20 flex items-center justify-between"
    >
      <button
        @click="goBack"
        :disabled="isLoading"
        :class="[
          'w-20 h-20 bg-white rounded-lg shadow flex items-center justify-center',
          isLoading ? 'opacity-50 cursor-not-allowed' : 'hover:bg-gray-50',
        ]"
      >
        <div
          v-if="isLoading"
          class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"
        ></div>
        <img
          v-else
          :src="HomeIcon"
          alt="뒤로가기"
          class="w-full h-full object-contain"
        />
      </button>

      <!-- 선택된 아이 이름 표시 -->
      <div
        v-if="selectedChild && selectedChild.name"
        class="bg-white/70 backdrop-blur-sm rounded-2xl px-6 py-3 shadow-lg"
      >
        <div class="flex items-center gap-3">
          <img
            :src="selectedChild.profileImage || 'https://placehold.co/40x40'"
            :alt="`${selectedChild.name} 프로필`"
            class="w-12 h-12 rounded-full object-cover border-2 border-white"
          />
          <p class="text-4xl text-gray-800 font-shark">
            {{ selectedChild.name }}
          </p>
        </div>
      </div>
    </header>

    <!-- main (펭귄 + 아래에 붙는 게이지) -->
    <main
      class="absolute left-1/2 top-[63%] -translate-x-1/2 -translate-y-1/2 transform z-10 flex flex-col items-center relative"
    >
      <span class="mb-4 text-black text-4xl text-outline-white font-shark">
        {{ "펭구" }}
      </span>
      <img
        :src="penguinSrc"
        alt="펭귄 단계 이미지"
        class="object-contain w-[140px] sm:w-[160px] lg:w-[200px] xl:w-[250px]"
      />

      <!-- 펭귄 바로 아래에 붙이는 게이지 바 -->
      <div
        class="mt-2 w-[clamp(200px,80vw,600px)] h-8 bg-white border-4 border-rose-600 rounded-full overflow-hidden relative"
      >
        <div
          class="h-full bg-emerald-300 rounded-full"
          :style="{ width: expRatio + '%' }"
        ></div>
      </div>

      <!-- 대화 상태 표시 -->
      <div
        v-if="conversationState.isActive"
        class="mt-6 bg-white/90 backdrop-blur-sm rounded-2xl p-6 max-w-md w-full shadow-lg"
      >
        <div class="text-center">
          <!-- 진행 상태 -->
          <div class="mb-4">
            <span class="text-sm text-gray-600">
              {{ conversationState.currentStep }} /
              {{ conversationState.totalSteps }}
            </span>
            <div class="w-full bg-gray-200 rounded-full h-2 mt-2">
              <div
                class="bg-blue-500 h-2 rounded-full transition-all duration-300"
                :style="{
                  width:
                    (conversationState.currentStep /
                      conversationState.totalSteps) *
                      100 +
                    '%',
                }"
              ></div>
            </div>
          </div>

          <!-- 현재 질문 -->
          <div v-if="conversationState.currentQuestion" class="mb-4">
            <p class="text-lg text-gray-800 font-medium">
              {{ conversationState.currentQuestion }}
            </p>
          </div>

          <!-- 상태 메시지 -->
          <div class="text-sm text-gray-600">
            <div
              v-if="conversationState.isSpeaking"
              class="flex items-center justify-center gap-2"
            >
              <div class="animate-pulse w-2 h-2 bg-blue-500 rounded-full"></div>
              <span>펭구가 말하고 있어요...</span>
            </div>
            <div
              v-else-if="conversationState.isListening"
              class="flex items-center justify-center gap-2"
            >
              <div class="animate-ping w-2 h-2 bg-red-500 rounded-full"></div>
              <span>듣고 있어요... 말해주세요!</span>
            </div>
            <div v-else class="flex items-center justify-center gap-2">
              <div class="w-2 h-2 bg-gray-400 rounded-full"></div>
              <span>스페이스바를 눌러 대답하세요</span>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from "vue";
import { useRouter, useRoute } from "vue-router";
import { useChildStore } from "@/store/child";
import {
  getChildPenguinData,
  incrementConversation,
} from "@/data/penguinData.js";
import { childService } from "@/services/childService.js";
import { speechService } from "@/services/speechService.js";

// 이미지
import HomeIcon from "../assets/images/Home.png";
import bgImage from "../assets/images/pet_bg.png";
import lvl1 from "../assets/images/lv_1.png";
import lvl2 from "../assets/images/lv_2.png";
import lvl3 from "../assets/images/lv_3.png";
import lvl4 from "../assets/images/lv_4.png";
import lvl5 from "../assets/images/lv_5.png";
import lvl6 from "../assets/images/lv_6.png";
import lvl7 from "../assets/images/lv_7.png";

// props 정의
const props = defineProps({
  childId: {
    type: [String, Number],
    default: null,
  },
});

const router = useRouter();
const route = useRoute();
const childStore = useChildStore();

// route params 또는 props에서 childId 받아오기 (fallback으로 selectedChild 사용)
const childId = computed(() => {
  // 1순위: props로 전달된 childId
  if (props.childId) {
    return parseInt(props.childId);
  }
  
  // 2순위: route params의 childId
  const routeChildId = route.params.childId;
  if (routeChildId) {
    return parseInt(routeChildId);
  }
  
  // 3순위: 현재 선택된 아이의 ID
  return selectedChild.value?.id || null;
});

// 선택된 아이 정보
const selectedChild = computed(() => childStore.selectedChild);

// 펭귄 데이터 상태
const currentStage = ref(1);
const conversationCnt = ref(0);
const dinosaur = { max_stage: 7 }; // 최대 단계
const isLoading = ref(false); // 로딩 상태

// 대화 상태 관리
const conversationState = ref({
  isActive: false,
  currentStep: 0,
  totalSteps: 5,
  topicId: null,
  conversationResultId: null,
  currentQuestion: "",
  isListening: false,
  isSpeaking: false,
  answers: [],
});

// 선택된 아이의 펭귄 데이터 로드
function loadPenguinData() {
  if (selectedChild.value && selectedChild.value.name) {
    const penguinData = getChildPenguinData(selectedChild.value.name);
    currentStage.value = penguinData.currentStage;
    conversationCnt.value = penguinData.conversationCount;
  }
}

// 선택된 아이가 변경될 때마다 펭귄 데이터 다시 로드
watch(
  selectedChild,
  (newChild) => {
    if (newChild && newChild.name) {
      loadPenguinData();
    }
  },
  { immediate: true }
);

async function goBack() {
  if (isLoading.value) return; // 이미 로딩 중이면 중복 실행 방지

  const currentChildId = childId.value;
  
  // 가장 먼저 당일 그림일기 상태를 확인
  const hasTodayDiary = childStore.getChildTodayDiary(currentChildId);
  
  if (hasTodayDiary) {
    router.back();
    return;
  }

  try {
    isLoading.value = true;

    const conversationResultId = conversationState.value.conversationResultId;

    // childId와 conversationResultId가 있을 때만 API 호출
    if (currentChildId && conversationResultId) {
      // 1. 아이 표정 기록 API 호출
      await childService.recordExpression(
        currentChildId,
        conversationResultId
      );

      // 2. 다이어리 생성 API 호출
      await childService.createDiary(conversationResultId);

      // 3. 다이어리 생성 성공 시 해당 아이의 당일 그림일기 상태를 true로 설정
      childStore.setChildTodayDiary(currentChildId, true);
    }

    // 4. 모든 API 호출이 완료되면 이전 페이지로 이동
    router.back();
  } catch (error) {
    console.error("홈으로 가기 중 오류:", error);
    // 에러가 발생해도 페이지는 이동
    alert(error.message || "처리 중 오류가 발생했지만 홈으로 이동합니다.");
    router.back();
  } finally {
    isLoading.value = false;
  }
}

// 대화 시작 함수
async function startConversation() {
  try {
    const currentChildId = childId.value;

    if (!currentChildId) {
      throw new Error("아이 ID를 찾을 수 없습니다.");
    }

    // 1. 대화 시작 API 호출하여 주제 받기
    await childService.startConversation(currentChildId);

    // 대화 상태 초기화
    conversationState.value = {
      isActive: true,
      currentStep: 1,
      totalSteps: 5,
      topicId: 2,
      // topicId: conversationStart.id || conversationStart.topicId || 2, // id 필드 우선 확인
      currentQuestion: "",
      isListening: false,
      isSpeaking: false,
      answers: [],
    };


    // 첫 번째 질문 받기
    await getFirstQuestion();
  } catch (error) {
    console.error("대화 시작 오류:", error);
    alert("대화를 시작할 수 없습니다: " + error.message);
  }
}

// 첫 번째 질문 받기
async function getFirstQuestion() {
  try {
    const currentChildId = childId.value;
    const { topicId } = conversationState.value;

    if (!currentChildId) {
      throw new Error("아이 ID를 찾을 수 없습니다.");
    }

    // 첫 번째 질문은 answer를 빈 문자열로 보내기
    const response = await childService.sendConversationAnswer(
      currentChildId,
      topicId,
      1,
      ""
    );

    // 응답에서 질문 추출
    conversationState.value.currentQuestion =
      response.question ||
      response.text ||
      response.prompt ||
      "질문을 받지 못했습니다.";

    // TTS로 질문 읽기
    await speakQuestion(conversationState.value.currentQuestion);
  } catch (error) {
    console.error("첫 번째 질문 받기 오류:", error);
    alert("첫 번째 질문을 받을 수 없습니다: " + error.message);
  }
}

// 다음 질문 받기 (답변 제출 후)
async function getNextQuestion(previousAnswer) {
  try {
    const currentChildId = childId.value;
    const { topicId, currentStep } = conversationState.value;

    if (!currentChildId) {
      throw new Error("아이 ID를 찾을 수 없습니다.");
    }

    const response = await childService.sendConversationAnswer(
      currentChildId,
      topicId,
      currentStep, // 현재 단계의 답변 제출
      previousAnswer,
      conversationState.value.currentQuestion
    );

    // 응답에서 다음 질문 추출
    conversationState.value.currentQuestion =
      response.question ||
      response.text ||
      response.prompt ||
      "질문을 받지 못했습니다.";

    // TTS로 질문 읽기
    await speakQuestion(conversationState.value.currentQuestion);
  } catch (error) {
    console.error("다음 질문 받기 오류:", error);

    // API 오류 시에도 대화를 계속 진행할 수 있도록 기본 질문으로 처리
    conversationState.value.currentQuestion =
      "서버 연결에 문제가 있어요. 다음 질문으로 넘어갈게요.";

    try {
      await speakQuestion(conversationState.value.currentQuestion);
    } catch (ttsError) {
      console.error("TTS 오류:", ttsError);
    }
  }
}

// 질문을 음성으로 출력
async function speakQuestion(question) {
  try {
    conversationState.value.isSpeaking = true;

    // 아이를 위한 친근한 음성 설정
    const voiceOptions = {
      rate: 0.7, // 천천히
      pitch: 1.2, // 조금 높게 (아이 친화적)
      volume: 0.9, // 적당한 볼륨
      // voiceName: 'Google 한국의'  // 특정 음성 지정 (선택사항)
    };

    await speechService.speak(question, voiceOptions);
    conversationState.value.isSpeaking = false;

  } catch (error) {
    console.error("TTS 오류:", error);
    conversationState.value.isSpeaking = false;
  }
}

// 사용자 답변 듣기
async function listenForAnswer() {
  try {
    conversationState.value.isListening = true;
    const answer = await speechService.listen({
      continuous: false,
      interimResults: false,
    });

    conversationState.value.isListening = false;

    // 답변 저장
    conversationState.value.answers[conversationState.value.currentStep - 1] =
      answer;

    // 다음 단계로 진행
    await processAnswer();
  } catch (error) {
    console.error("음성 인식 오류:", error);
    conversationState.value.isListening = false;
    alert("음성 인식에 실패했습니다. 다시 시도해주세요.");
  }
}

// 답변 처리 및 다음 단계 진행
async function processAnswer() {
  const { currentStep, totalSteps, answers } = conversationState.value;
  const currentAnswer = answers[currentStep - 1]; // 현재 단계의 답변


  if (currentStep < totalSteps) {
    // 현재 답변을 제출하고 다음 질문을 받은 후 단계 증가
    await getNextQuestion(currentAnswer);
    conversationState.value.currentStep++;
  } else {
    // 마지막 답변 제출 및 마무리
    await finishConversation(currentAnswer);
  }
}

// 대화 마무리
async function finishConversation(finalAnswer) {
  try {
    const currentChildId = childId.value;
    const { topicId } = conversationState.value;

    if (!currentChildId) {
      throw new Error("아이 ID를 찾을 수 없습니다.");
    }

    let closingMessage = "대화가 완료되었습니다. 수고했어요!";
    let response = null;

    try {
      // 마지막 답변 제출
      response = await childService.sendConversationAnswer(
        currentChildId,
        topicId,
        5,
        finalAnswer,
        conversationState.value.currentQuestion
      );

      // 서버에서 받은 마무리 멘트 사용
      closingMessage =
        response.closingMessage ||
        response.text ||
        response.prompt ||
        closingMessage;
    } catch (apiError) {
      console.error("마지막 답변 제출 오류:", apiError);
      closingMessage =
        "서버 연결에 문제가 있었지만 대화가 완료되었어요. 수고했어요!";
    }

    // 마무리 멘트 TTS로 출력
    try {
      await speechService.speak(closingMessage);
    } catch (ttsError) {
      console.error("TTS 오류:", ttsError);
    }

    // 마지막 답변 제출 시 응답에서 conversationResultIds 추출
    if (response && response.conversationResultIds) {
      conversationState.value.conversationResultId = response.conversationResultIds;
      console.log("conversationResultId 저장됨:", response.conversationResultIds);
    } else {
      console.warn("응답에서 conversationResultIds를 찾을 수 없음");
    }

    // 대화 상태 초기화
    conversationState.value.isActive = false;
  } catch (error) {
    console.error("대화 마무리 오류:", error);

    // 어떤 오류가 발생해도 대화는 종료시킴
    conversationState.value.isActive = false;

    // 기본 마무리 멘트 출력
    try {
      await speechService.speak("대화가 완료되었습니다. 수고했어요!");
    } catch (ttsError) {
      console.error("TTS 오류:", ttsError);
    }
  }
}

// 키보드 이벤트 핸들러
function handleKeyPress(event) {
  // 스페이스바 (코드 32)
  if (
    event.code === "Space" &&
    conversationState.value.isActive &&
    !conversationState.value.isListening &&
    !conversationState.value.isSpeaking
  ) {
    event.preventDefault();
    listenForAnswer();
  }
}

// 컴포넌트 마운트 시 초기화
onMounted(async () => {
  await childStore.initialize();
  
  // URL에서 childId가 전달된 경우 해당 아이를 선택
  const currentChildId = childId.value;
  if (currentChildId && childStore.children.find(child => child.id === currentChildId)) {
    childStore.selectChild(currentChildId);
  }
  
  loadPenguinData();

  // 키보드 이벤트 리스너 등록
  window.addEventListener("keydown", handleKeyPress);

  // 페이지 진입 시 대화 시작
  await startConversation();
});

// 컴포넌트 언마운트 시 정리
onUnmounted(() => {
  // 키보드 이벤트 리스너 제거
  window.removeEventListener("keydown", handleKeyPress);

  // 스피치 서비스 정리
  speechService.cleanup();
});

// 2) 각 레벨별 다음 단계까지 필요한 대화 횟수 매핑
const levelRequirements = {
  1: 2, // 1→2레벨: 2번 대화
  2: 4, // 2→3레벨: 4번 대화
  3: 6, // 3→4레벨: 6번 대화
  4: 8, // 4→5레벨: 8번 대화
  5: 10, // 5→6레벨: 10번 대화
  6: 12, // 6→7레벨: 12번 대화
  7: 0, // 7레벨은 최대 레벨
};

// 3) 현재 레벨에서 다음 단계까지 필요한 대화 횟수
const thresholdConvs = computed(() => {
  return levelRequirements[currentStage.value] || 0;
});

// 4) 단계별 펭귄 이미지
const penguinImgs = {
  1: lvl1,
  2: lvl2,
  3: lvl3,
  4: lvl4,
  5: lvl5,
  6: lvl6,
  7: lvl7,
};
const penguinSrc = computed(() => {
  const stage = Math.min(currentStage.value, dinosaur.max_stage);
  return penguinImgs[stage] || lvl1;
});

// 5) 경험치(대화) 비율 0~100%
const expRatio = computed(() => {
  // 7레벨(최대 레벨) 달성 시 항상 100%
  if (currentStage.value >= dinosaur.max_stage) {
    return 100;
  }

  // 현재 레벨에서 다음 레벨까지 필요한 대화 횟수
  const requiredConversations = thresholdConvs.value;

  // 필요 대화 횟수가 0이면 100% (에러 방지)
  if (requiredConversations === 0) {
    return 100;
  }

  // 경험치 비율 계산: (현재 대화 횟수 / 필요 대화 횟수) * 100
  const ratio = (conversationCnt.value / requiredConversations) * 100;

  // 100%를 넘지 않도록 제한
  return Math.min(100, Math.max(0, ratio));
});
</script>

<style scoped>
/* TailwindCSS 유틸만 사용 */
</style>

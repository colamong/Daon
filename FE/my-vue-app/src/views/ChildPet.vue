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
      <!-- 첫 상호작용 유도 오버레이 -->
      <div
        v-if="!audioUnlocked"
        class="fixed inset-0 z-50 bg-black/40 backdrop-blur-sm flex flex-col items-center justify-center gap-6"
      >
        <div class="bg-white rounded-2xl px-8 py-6 shadow-xl text-center">
          <p class="text-xl font-shark mb-2">
            펭구가 말을 시작할 준비가 됐어요!
          </p>
          <p class="text-gray-600 font-shark mb-4">
            버튼을 눌러 오디오를 활성화해 주세요.
          </p>
          <button
            @click="handleFirstTap"
            class="px-6 py-3 bg-rose-500 text-white rounded-xl font-semibold hover:bg-rose-600 transition"
          >
            대화 시작
          </button>
        </div>
      </div>

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
      class="absolute left-1/2 !top-[57%] -translate-x-1/2 -translate-y-1/2 transform z-10 flex flex-col items-center relative"
    >
      <span class="mb-4 text-black text-4xl text-outline-white font-shark">
        {{ penguinData.name }}
      </span>

      <!-- 펭귄과 대화 UI를 감싸는 컨테이너 -->
      <div class="relative flex items-center justify-center">
        <!-- 펭귄 이미지 -->
        <img
          :src="getPenguinImage(penguinData.currentStage)"
          alt="펭귄 단계 이미지"
          :class="[
            'object-contain w-[140px] sm:w-[160px] lg:w-[200px] xl:w-[250px] transition-transform duration-100',
            { 'animate-wiggle': conversationState.isSpeaking },
          ]"
        />
      </div>

      <!-- 대화 말풍선 - 펭귄과 우측 사이에 배치 -->
      <div
        v-if="conversationState.isActive"
        class="fixed right-[8%] top-1/2 -translate-y-1/2 !w-[450px] z-20 font-shark"
      >
        <!-- 대화 UI 패널 - 손그림 말풍선 스타일 -->
        <div class="bg-white p-4 shadow-lg hand-drawn-bubble">
          <!-- 말풍선 내용 -->
          <div class="text-center">
            <!-- 진행 상태 -->
            <div class="mb-3">
              <span class="text-xs text-gray-600">
                {{ conversationState.currentStep }} /
                {{ conversationState.totalSteps }}
              </span>
              <div class="w-full bg-gray-200 rounded-full h-1.5 mt-1">
                <div
                  class="bg-rose-400 h-1.5 rounded-full transition-all duration-300"
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
            <div v-if="conversationState.currentQuestion" class="mb-3">
              <p class="text-xl text-gray-800 leading-relaxed">
                {{ conversationState.currentQuestion }}
              </p>
            </div>

            <!-- 상태 메시지 -->
            <div class="text-s text-gray-600">
              <div
                v-if="conversationState.isSpeaking"
                class="flex items-center justify-center gap-2"
              >
                <div
                  class="animate-pulse w-1.5 h-1.5 bg-blue-500 rounded-full"
                ></div>
                <span>펭구가 말하고 있어요...</span>
              </div>
              <div
                v-else-if="conversationState.isListening"
                class="flex items-center justify-center gap-2"
              >
                <div
                  class="animate-ping w-1.5 h-1.5 bg-red-500 rounded-full"
                ></div>
                <span>듣고 있어요... 말해주세요!</span>
              </div>
              <div v-else class="flex items-center justify-center gap-2">
                <div class="w-1.5 h-1.5 bg-gray-400 rounded-full"></div>
                <span>스페이스바를 눌러 대답하세요</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 펭귄 바로 아래에 고정된 경험치 바 -->
      <div
        class="mt-4 w-[clamp(200px,80vw,600px)] h-8 bg-white border-4 border-rose-600 rounded-full overflow-hidden relative"
      >
        <div
          class="h-full bg-emerald-300 rounded-full transition-all duration-1000 ease-out"
          :style="{ width: animatedProgress + '%' }"
        ></div>
      </div>
    </main>

    <!-- 재생용(hidden) 오디오: GMS TTS가 여기로 흘러들어감 -->
    <audio ref="ttsPlayer" class="hidden"></audio>

    <!-- 배경 눈 내리는 효과 -->
    <SnowEffect :show="true" :flake-count="120" intensity="medium" />

    <!-- 펭귄 진화 시 구름 전환 효과 -->
    <CloudTransition
      :show="showEvolutionTransition"
      @complete="onEvolutionComplete"
      @coverComplete="onEvolutionCoverComplete"
    />

    <!-- 그림일기 생성 중 로딩 모달 -->
    <div
      v-if="isLoading"
      class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
    >
      <div
        class="bg-white rounded-2xl p-8 max-w-md mx-4 text-center shadow-2xl"
      >
        <!-- 햄스터 휠 애니메이션 -->
        <div class="mb-6 flex justify-center">
          <HamsterLoading />
        </div>

        <!-- 메시지 -->
        <h3 class="text-xl font-shark text-gray-800 mb-2">
          그림일기를 만들고 있는 중입니다
        </h3>
        <p class="text-gray-600 font-shark">
          펭구와의 특별한 대화를 바탕으로<br />
          아름다운 그림일기를 생성하고 있어요!<br />
          <span class="text-rose-500 font-semibold"
            >조금만 기다려 주세요 ✨</span
          >
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import { useChildStore } from "@/store/child";
import { childService } from "@/services/childService.js";
import HamsterLoading from "@/components/common/HamsterLoading.vue";
import CloudTransition from "@/components/effect/CloudTransition.vue";
import SnowEffect from "@/components/effect/SnowEffect.vue";

/** ✅ GMS TTS 서비스(default export) */
import ttsService from "@/services/ttsService_gms.js";

const recognitionRef = ref(null);
const isRecSupported =
  "webkitSpeechRecognition" in window || "SpeechRecognition" in window;

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

// 사운드 이펙트
import lvlUpSound from "../assets/effects/lvl_up.mp3";

// props
const props = defineProps({
  childId: { type: [String, Number], default: null },
});

const router = useRouter();
const route = useRoute();
const childStore = useChildStore();

// 오디오 엘리먼트
const ttsPlayer = ref(null);

// childId 계산
const selectedChild = computed(() => childStore.selectedChild);
const childId = computed(() => {
  if (props.childId) return parseInt(props.childId);
  const routeChildId = route.params.childId;
  if (routeChildId) return parseInt(routeChildId);
  return selectedChild.value?.id || null;
});

// 펭귄 상태
const penguinData = ref({
  currentStage: 1,
  conversationCount: 0,
  totalConversations: 0,
  expRatio: 0,
});
const isLoading = ref(false);

// 애니메이션용 진행률 상태
const animatedProgress = ref(0);

// 펭귄 진화 관련 상태
const showEvolutionTransition = ref(false);
const previousStage = ref(1);
const newStage = ref(1);

// 게이지 애니메이션 함수
function animateProgress(targetPercent, duration = 1000) {
  const startPercent = animatedProgress.value;
  const difference = targetPercent - startPercent;
  const startTime = performance.now();

  function updateProgress(currentTime) {
    const elapsed = currentTime - startTime;
    const progress = Math.min(elapsed / duration, 1);

    // easeOutQuart 이징 함수로 부드러운 애니메이션
    const easeOutQuart = 1 - Math.pow(1 - progress, 4);

    animatedProgress.value = startPercent + difference * easeOutQuart;

    if (progress < 1) {
      requestAnimationFrame(updateProgress);
    } else {
      animatedProgress.value = targetPercent;
    }
  }

  requestAnimationFrame(updateProgress);
}

// 레벨업 사운드 재생 함수
function playLevelUpSound() {
  try {
    const audio = new Audio(lvlUpSound);
    audio.volume = 0.7; // 볼륨 조절
    audio.play().catch((e) => {
      console.warn("레벨업 사운드 재생 실패:", e);
    });
  } catch (error) {
    console.warn("레벨업 사운드 로드 실패:", error);
  }
}

// 펭귄 진화 관련 함수들
function checkEvolution(oldStage, newStageValue) {
  if (newStageValue > oldStage) {
    previousStage.value = oldStage;
    newStage.value = newStageValue;
    // 진화 발생 시 레벨업 사운드 재생
    playLevelUpSound();
    return true; // 진화 발생
  }
  return false; // 진화 없음
}

function onEvolutionCoverComplete() {
  // 구름이 펭귄을 다 가렸을 때 이미지 변경
  penguinData.value.currentStage = newStage.value;
}

function onEvolutionComplete() {
  // 구름 전환 효과 완료
  showEvolutionTransition.value = false;
}

// 사용자 첫 클릭으로 오디오 재생 허용 여부
const audioUnlocked = ref(false);

// 대화 상태
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

// 펭귄 데이터 로드
async function loadPenguinData(animate = false) {
  const currentChildId = childId.value;
  if (!currentChildId) return;

  try {
    const response = await childService.getPetStatus(currentChildId);
    const newProgressPercent = response.progressPercent || 0;
    const newStageValue = response.currentStage || 1;

    // 진화 체크 (보상을 받았을 때만)
    const shouldEvolutionEffect =
      animate && checkEvolution(penguinData.value.currentStage, newStageValue);

    if (shouldEvolutionEffect) {
      // 진화 발생 - 구름 전환 효과 시작
      showEvolutionTransition.value = true;
      // 게이지 애니메이션은 구름 효과와 함께
      animateProgress(newProgressPercent, 2000);
    } else {
      // 일반 업데이트
      penguinData.value = {
        name: response.name || "펭구",
        currentStage: newStageValue,
        totalExperience: response.totalExperience || 0,
        progressPercent: newProgressPercent,
        imageUrl: response.imageUrl || "/images/lv_1.png",
      };

      // 애니메이션 적용 여부에 따라 처리
      if (animate) {
        animateProgress(newProgressPercent, 2000); // 2초 동안 애니메이션
      } else {
        animatedProgress.value = newProgressPercent; // 즉시 적용
      }
    }
  } catch (e) {
    console.error("펭귄 데이터 로드 실패:", e);
  }
}

// 뒤로가기
async function goBack() {
  if (isLoading.value) return;
  const currentChildId = childId.value;

  const hasTodayDiary = childStore.getChildTodayDiary(currentChildId);
  if (hasTodayDiary) {
    router.push({ name: "ChildMain", params: { childId: currentChildId } });
    return;
  }

  try {
    isLoading.value = true;
    const conversationResultId = conversationState.value.conversationResultId;
    if (currentChildId && conversationResultId) {
      await childService.recordExpression(currentChildId, conversationResultId);
      await childService.createDiary(conversationResultId);
      childStore.setChildTodayDiary(currentChildId, true, conversationResultId);
    }
    router.push({ name: "ChildMain", params: { childId: currentChildId } });
  } catch (e) {
    console.error("펭귄 메뉴로 가기 중 오류:", e);
    router.push({ name: "ChildMain", params: { childId: currentChildId } });
  } finally {
    isLoading.value = false;
  }
}

async function unlockAudio() {
  try {
    const Ctx = window.AudioContext || window.webkitAudioContext;
    if (Ctx) {
      const ctx = new Ctx();
      if (ctx.state === "suspended") {
        await ctx.resume();
      }
      const osc = ctx.createOscillator();
      const gain = ctx.createGain();
      gain.gain.value = 0;
      osc.connect(gain).connect(ctx.destination);
      osc.start();
      osc.stop(ctx.currentTime + 0.02);
    }

    const silent = new Audio(
      "data:audio/wav;base64,UklGRiQAAABXQVZFZm10IBAAAAABAAEAESsAACJWAAACABAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA="
    );
    silent.muted = true;
    await silent.play().catch(() => {});
    silent.pause();
  } catch (e) {
    console.warn("unlockAudio warn:", e);
  }
}

async function handleFirstTap() {
  await unlockAudio();
  audioUnlocked.value = true;
  await startConversation();
}

// 대화 시작
async function startConversation() {
  try {
    const currentChildId = childId.value;
    if (!currentChildId) throw new Error("아이 ID를 찾을 수 없습니다.");

    const conversationStart = await childService.startConversation(
      currentChildId
    );

    conversationState.value = {
      isActive: true,
      currentStep: 1,
      totalSteps: 5,
      topicId: conversationStart.id || conversationStart.data?.id || 1,
      currentQuestion: "",
      isListening: false,
      isSpeaking: false,
      answers: [],
    };

    await getFirstQuestion();
  } catch (e) {
    console.error("대화 시작 오류:", e);
    alert("대화를 시작할 수 없습니다: " + e.message);
  }
}

// 첫 질문
async function getFirstQuestion() {
  try {
    const currentChildId = childId.value;
    const { topicId } = conversationState.value;
    if (!currentChildId) throw new Error("아이 ID를 찾을 수 없습니다.");

    const response = await childService.sendConversationAnswer(
      currentChildId,
      topicId,
      1,
      ""
    );

    conversationState.value.currentQuestion =
      response.question ||
      response.text ||
      response.prompt ||
      "질문을 받지 못했습니다.";

    await speakQuestion(
      conversationState.value.currentQuestion,
      response.audioUrl
    );
  } catch (e) {
    console.error("첫 번째 질문 받기 오류:", e);
    alert("첫 번째 질문을 받을 수 없습니다: " + e.message);
  }
}

// 다음 질문
async function getNextQuestion(previousAnswer) {
  try {
    const currentChildId = childId.value;
    const { topicId, currentStep } = conversationState.value;
    if (!currentChildId) throw new Error("아이 ID를 찾을 수 없습니다.");

    const response = await childService.sendConversationAnswer(
      currentChildId,
      topicId,
      currentStep,
      previousAnswer,
      conversationState.value.currentQuestion
    );

    conversationState.value.currentQuestion =
      response.question ||
      response.text ||
      response.prompt ||
      "질문을 받지 못했습니다.";

    await speakQuestion(
      conversationState.value.currentQuestion,
      response.audioUrl
    );
  } catch (e) {
    console.error("다음 질문 받기 오류:", e);
    const fallback = "서버 연결에 문제가 있어요. 다음 질문으로 넘어갈게요.";
    conversationState.value.currentQuestion = fallback;
    try {
      await speakQuestion(fallback);
    } catch (err) {
      console.error("TTS 오류:", err);
    }
  }
}

/** ✅ 질문을 음성으로 출력 (audioUrl 있으면 바로 재생, 없으면 텍스트로 스트리밍 호출) */
async function speakQuestion(question, audioUrl = null) {
  conversationState.value.isSpeaking = true;
  try {
    if (audioUrl) {
      await ttsService.playByUrl(audioUrl, ttsPlayer.value);
    } else {
      await ttsService.playText(
        question,
        { voice: "nova", speed: 1.0 },
        ttsPlayer.value
      );
    }
  } catch (e) {
    console.error("TTS 오류:", e);
  } finally {
    conversationState.value.isSpeaking = false;
  }
}

// 음성 인식(필요시 확장)
// 음성 인식: 스페이스바로 시작 -> 한 문장 후 자동 종료
async function listenForAnswer() {
  if (!isRecSupported || !recognitionRef.value) {
    alert(
      "이 브라우저에서는 음성 인식을 지원하지 않아요. 크롬/엣지(데스크톱)에서 시도해 주세요."
    );
    return "";
  }

  // 권한 팝업은 브라우저가 알아서 띄움(최초 1회)
  conversationState.value.isListening = true;

  const rec = recognitionRef.value;

  return new Promise((resolve, reject) => {
    let resolved = false;

    rec.onresult = (e) => {
      const result = Array.from(e.results)
        .map((r) => r[0]?.transcript || "")
        .join(" ")
        .trim();
      if (!resolved) {
        resolved = true;
        resolve(result);
      }
    };

    rec.onerror = (e) => {
      console.error("STT error:", e.error);
      if (!resolved) reject(new Error(e.error || "stt_error"));
    };

    rec.onend = () => {
      conversationState.value.isListening = false;
      // onresult 없이 onend만 온 경우(아무 말 안 함)
      if (!resolved) resolve("");
    };

    try {
      rec.start(); // ⏺️ 녹음 시작
    } catch (err) {
      // 연속 호출 방지
      console.warn("rec.start() blocked:", err);
      conversationState.value.isListening = false;
      reject(err);
    }
  });
}

// 단계 처리
async function processAnswer() {
  const { currentStep, totalSteps, answers } = conversationState.value;
  const currentAnswer = answers[currentStep - 1];
  if (currentStep < totalSteps) {
    await getNextQuestion(currentAnswer);
    conversationState.value.currentStep++;
  } else {
    await finishConversation(currentAnswer);
  }
}

// 마무리
async function finishConversation(finalAnswer) {
  try {
    const currentChildId = childId.value;
    const { topicId } = conversationState.value;
    if (!currentChildId) throw new Error("아이 ID를 찾을 수 없습니다.");

    let closingMessage = "대화가 완료되었습니다. 수고했어요!";
    let response = null;

    try {
      response = await childService.sendConversationAnswer(
        currentChildId,
        topicId,
        5,
        finalAnswer,
        conversationState.value.currentQuestion
      );
      closingMessage =
        response.closingMessage ||
        response.text ||
        response.prompt ||
        closingMessage;
    } catch (e) {
      console.error("마지막 답변 제출 오류:", e);
      closingMessage =
        "서버 연결에 문제가 있었지만 대화가 완료되었어요. 수고했어요!";
    }

    // 마무리 멘트 재생
    await speakQuestion(closingMessage, response?.audioUrl);

    if (response?.conversationResultIds) {
      conversationState.value.conversationResultId =
        response.conversationResultIds;
    }

    await childService.givePetReward(currentChildId);
    await loadPenguinData(true); // 애니메이션과 함께 로드
    conversationState.value.isActive = false;
  } catch (e) {
    console.error("대화 마무리 오류:", e);
    conversationState.value.isActive = false;
    try {
      await speakQuestion("대화가 완료되었습니다. 수고했어요!");
    } catch {}
  }
}

// 키 입력
async function handleKeyPress(event) {
  if (
    event.code === "Space" &&
    conversationState.value.isActive &&
    !conversationState.value.isListening &&
    !conversationState.value.isSpeaking
  ) {
    event.preventDefault();

    try {
      const transcript = await listenForAnswer(); // 🎤 말하기
      // 공백이면(말 안 했으면) 그냥 무시
      if (!transcript || !transcript.trim()) return;

      // 답변 저장
      conversationState.value.answers[conversationState.value.currentStep - 1] =
        transcript.trim();

      // 다음 단계로 진행
      await processAnswer();
    } catch (e) {
      console.error("음성 인식 실패:", e);
      alert("음성 인식에 실패했어요. 다시 시도해 주세요.");
    }
  }
}

// 라이프사이클
onMounted(async () => {
  await childStore.initialize();
  const currentChildId = childId.value;
  if (
    currentChildId &&
    childStore.children.find((c) => c.id === currentChildId)
  ) {
    childStore.selectChild(currentChildId);
  }

  // STT 준비
  if (isRecSupported) {
    const SR = window.SpeechRecognition || window.webkitSpeechRecognition;
    const rec = new SR();
    rec.lang = "ko-KR"; // 한국어 인식
    rec.interimResults = false; // 중간 결과 꺼두기(원하면 true)
    rec.maxAlternatives = 1;
    rec.continuous = false; // 한 문장 말하면 자동 종료
    recognitionRef.value = rec;
  } else {
    console.warn(
      "이 브라우저는 Web Speech API(SpeechRecognition)를 지원하지 않습니다."
    );
  }

  await loadPenguinData();
  window.addEventListener("keydown", handleKeyPress);
});

onUnmounted(() => {
  window.removeEventListener("keydown", handleKeyPress);
  ttsService.stop(ttsPlayer.value);
  try {
    recognitionRef.value?.stop?.();
  } catch {}
});

// 유틸
function getPenguinImage(stage) {
  const penguinImgs = {
    1: lvl1,
    2: lvl2,
    3: lvl3,
    4: lvl4,
    5: lvl5,
    6: lvl6,
    7: lvl7,
  };
  return penguinImgs[stage] || lvl1;
}
</script>

<style scoped>
/* 손그림 말풍선 스타일 */
.hand-drawn-bubble {
  position: relative;
  border-radius: 25px 20px 28px 18px; /* 불규칙한 모서리 */
  border: 3px solid #e11d48;
  /* 손그림 느낌의 그림자와 효과 */
  box-shadow: 2px 2px 0 #e11d48, 4px 4px 0 rgba(225, 29, 72, 0.3);
  animation: wobble 0.3s ease-in-out;
}

/* 말풍선 꼬리 - 삼각형 스타일 */
.hand-drawn-bubble::before {
  content: "";
  position: absolute;
  left: -20px;
  top: 50%;
  transform: translateY(-50%);
  width: 0;
  height: 0;
  border: 15px solid transparent;
  border-right: 20px solid #e11d48;
  border-left: 0;
}

.hand-drawn-bubble::after {
  content: "";
  position: absolute;
  left: -16px;
  top: 50%;
  transform: translateY(-50%);
  width: 0;
  height: 0;
  border: 12px solid transparent;
  border-right: 16px solid white;
  border-left: 0;
  z-index: 1;
}

/* 나타날 때 애니메이션 */
@keyframes wobble {
  0% {
    transform: scale(0.8) rotate(-2deg);
    opacity: 0;
  }
  50% {
    transform: scale(1.05) rotate(1deg);
    opacity: 0.8;
  }
  100% {
    transform: scale(1) rotate(0deg);
    opacity: 1;
  }
}

/* 펭귄이 말할 때 흔들리는 애니메이션 */
@keyframes wiggle {
  0%,
  100% {
    transform: rotate(0deg) translateX(0px);
  }
  25% {
    transform: rotate(-1deg) translateX(-2px);
  }
  50% {
    transform: rotate(1deg) translateX(2px);
  }
  75% {
    transform: rotate(-0.5deg) translateX(-1px);
  }
}

.animate-wiggle {
  animation: wiggle 0.3s ease-in-out infinite;
}
</style>

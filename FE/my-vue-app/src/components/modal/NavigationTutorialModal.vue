<template>
  <div v-if="modelValue">
    <!-- 오버레이 -->
    <div class="fixed inset-0 bg-black bg-opacity-50 z-40"></div>

    <!-- BaseModal 스타일을 따온 커스텀 모달 (오버레이 없음) -->
    <div
      class="fixed inset-0 grid place-items-center z-50 font-paper p-4 min-h-screen"
      style="pointer-events: none; display: grid; place-items: center; align-content: center; justify-content: center;"
    >
      <div
        class="bg-white rounded-xl overflow-hidden shadow-lg w-full max-w-sm sm:max-w-md md:max-w-lg lg:max-w-2xl xl:max-w-3xl max-h-[85vh] overflow-y-auto my-auto"
        style="pointer-events: auto"
        @click.stop
      >
        <!-- 헤더 -->
        <div
          class="flex items-center justify-between px-3 md:px-4 py-2 md:py-3 bg-blue-100 border-b border-blue-200"
        >
          <h3 class="text-sm md:text-lg font-paperSemi text-gray-800 truncate pr-2">
            {{ currentStepData.title }} - 이용 가이드
          </h3>
          <!-- 컨트롤 버튼들 -->
          <div class="flex gap-1 md:gap-3 flex-shrink-0">
            <button
              class="tutorial-btn px-2 md:px-4 py-1 md:py-2 border-none rounded-lg cursor-pointer text-xs md:text-sm bg-gray-100 text-gray-600 hover:bg-gray-200 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
              :disabled="currentStep === 0"
              @click="previousStep"
            >
              <span class="hidden sm:inline">이전</span>
              <span class="sm:hidden">←</span>
            </button>
            <button
              class="tutorial-btn px-2 md:px-4 py-1 md:py-2 border-none rounded-lg cursor-pointer text-xs md:text-sm bg-gradient-to-r from-purple-500 to-purple-700 text-white hover:from-purple-600 hover:to-purple-800 hover:transform hover:-translate-y-0.5 hover:shadow-lg transition-all"
              @click="nextStep"
            >
              <span class="hidden sm:inline">{{ currentStep === tutorialSteps.length - 1 ? "완료" : "다음" }}</span>
              <span class="sm:hidden">{{ currentStep === tutorialSteps.length - 1 ? "✓" : "→" }}</span>
            </button>
            <button
              class="tutorial-btn px-2 md:px-4 py-1 md:py-2 border-none rounded-lg cursor-pointer text-xs md:text-sm bg-gray-100 text-gray-600 hover:bg-gray-200 transition-colors"
              @click="skipTutorial"
            >
              <span class="hidden sm:inline">건너뛰기</span>
              <span class="sm:hidden">×</span>
            </button>
          </div>
        </div>

        <!-- 본문 -->
        <div class="p-3 md:p-4 lg:p-6">
          <!-- 진행 표시기 -->
          <div class="tutorial-progress flex justify-between items-center mb-4 md:mb-6">
            <div class="progress-dots flex gap-1 md:gap-2">
              <div
                v-for="(step, index) in tutorialSteps"
                :key="index"
                class="progress-dot w-2 h-2 md:w-3 md:h-3 rounded-full transition-colors"
                :class="index <= currentStep ? 'bg-purple-500' : 'bg-gray-300'"
              ></div>
            </div>
            <div class="progress-text text-xs md:text-sm text-gray-500">
              {{ currentStep + 1 }}/{{ tutorialSteps.length }}
            </div>
          </div>

          <!-- 이미지/GIF 영역 -->
          <div class="tutorial-image mb-3 md:mb-4 flex justify-center">
            <div
              class="w-full bg-gray-100 rounded-lg p-2 md:p-4 flex items-center justify-center"
            >
              <img
                v-if="currentStepData.image"
                :src="currentStepData.image"
                :alt="currentStepData.title"
                class="w-full h-40 md:h-60 lg:h-80 object-contain rounded-md"
              />
              <div
                v-else
                class="w-full h-40 md:h-60 lg:h-80 bg-gradient-to-br from-purple-400 to-purple-600 rounded-md flex items-center justify-center text-white text-3xl md:text-4xl lg:text-6xl"
              >
                {{ currentStepData.icon }}
              </div>
            </div>
          </div>

          <!-- 예시 -->
          <div
            class="tutorial-example bg-purple-50 p-3 md:p-4 rounded-lg text-sm md:text-base text-gray-700 border-l-4 border-purple-500 mb-4 md:mb-6 transition-all duration-500 ease-in-out flex justify-between items-start gap-2 md:gap-3"
          >
            <div class="flex-1">
              {{ currentExample }}
            </div>
            <!-- 예시 텍스트 인디케이터 -->
            <div
              v-if="
                Array.isArray(currentStepData.example) &&
                currentStepData.example.length > 1
              "
              class="flex-shrink-0 bg-white text-xs text-gray-500 px-2 py-1 rounded-full border border-gray-200 shadow-sm"
            >
              {{ currentExampleIndex + 1 }}/{{ currentStepData.example.length }}
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import {
  ref,
  computed,
  watch,
  onMounted,
  onBeforeUnmount,
  nextTick,
} from "vue";
import IconButton from "@/components/button/IconButton.vue";

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits(["update:modelValue"]);

// 튜토리얼 스텝 데이터
const tutorialSteps = [
  {
    target: "",
    title: "튜토리얼 다시 재생",
    icon: "🔄",
    description: "튜토리얼을 다시 볼 수 있습니다.",
    example: [
      "💡 언제든지 튜토리얼을 다시 볼 수 있어요.",
      "🔄 튜토리얼의 도움이 필요하면 다시 확인해보세요.",
      "✨ 언제나 더 나은 사용 경험을 위해 준비되어 있습니다!",
    ],
    image: new URL("@/assets/images/retry.gif", import.meta.url).href,
  },
  {
    target: '[data-tutorial="penguin"]',
    title: "펭구랑 놀자",
    icon: "🎮",
    description:
      "재미있는 게임과 놀이를 통해 아이들이 즐겁게 학습할 수 있는 공간입니다.",
    example: [
      "⚠️ 펭구랑 놀자는 아이가 등록되어야 할 수 있습니다.",
      "🐧 펭귄과 대화를 통해 아이들이 즐겁게 하루를 기록합니다.",
      "🖼️ 아이의 하루를 그림으로 확인해보세요.",
    ],
    image: new URL("@/assets/images/pet.gif", import.meta.url).href,
  },
  // {
  //   target: '[data-tutorial="profile"]',
  //   title: "아이 프로필",
  //   icon: "👤",
  //   description:
  //     "자녀의 학습 현황, 관심사, 성장 기록을 관리할 수 있는 개인화된 공간입니다.",
  //   example: [
  //     "💡 아이를 등록하고 아이의 활동을 기록하고 관찰하세요.",
  //     "📈 아이의 활동을 확인하고 소통해보세요",
  //     "⚙️ 아이의 관심사에 맞는 맞춤 설정을 할 수 있습니다.",
  //   ],
  //   image: new URL("@/assets/images/child_register.gif", import.meta.url).href,
  // },
  {
    target: '[data-tutorial="document"]',
    title: "문서 도우미",
    icon: "📝",
    description:
      "숙제나 과제를 도와주는 AI 도구로, 글쓰기부터 문제 풀이까지 지원합니다.",
    example: [
      "💡 어려운 문서를 번역해보세요.",
      "📝 가정통신문부터 다양한 공문서까지.",
      "🤖 번역과 요약을 한번에!.",
    ],
    image: new URL("@/assets/images/ocr.gif", import.meta.url).href,
  },
  {
    target: '[data-tutorial="community"]',
    title: "온동네",
    icon: "💬",
    description:
      "다른 부모님들과 정보를 공유하고 소통할 수 있는 커뮤니티 공간입니다.",
    example: [
      "💡 다른 사람들과 소통할 수 있는 공간이에요.",
      "💬 육아 고민과 유용한 정보를 공유해보세요.",
      "🎆 경험을 나누고 서로 도움을 받을 수 있어요.",
    ],
    image: new URL("@/assets/images/chat.gif", import.meta.url).href,
  },
  {
    target: '[data-tutorial="growth"]',
    title: "상황별 학습",
    icon: "🎯",
    description: "아이의 상황과 필요에 맞는 맞춤형 학습 콘텐츠를 제공합니다.",
    example: [
      "💡 다양한 상황에 맞는 학습 콘텐츠예요.",
      "🎯 특정 테마를 집중적으로 학습할 수 있어요.",
      "📈 발음 평가도 제공합니다.",
    ],
    image: new URL("@/assets/images/learning.gif", import.meta.url).href,
  },
];

const currentStep = ref(0);
const spotlightStyle = ref({});
const currentExampleIndex = ref(0);
let exampleInterval = null;

const currentStepData = computed(() => tutorialSteps[currentStep.value]);

// 현재 example 텍스트를 반환
const currentExample = computed(() => {
  const examples = currentStepData.value.example;
  if (Array.isArray(examples)) {
    return examples[currentExampleIndex.value];
  }
  return examples; // 배열이 아닌 경우 그대로 반환
});

// 하이라이트 효과 적용/제거
const updateHighlight = async () => {
  await nextTick();

  // 기존 하이라이트 제거 및 클릭 이벤트 복원
  const allButtons = document.querySelectorAll("[data-tutorial]");
  allButtons.forEach((btn) => {
    btn.classList.remove("tutorial-highlight");
    btn.style.pointerEvents = "";
  });

  const step = tutorialSteps[currentStep.value];
  
  // target이 없거나 빈 문자열인 경우 하이라이트 적용하지 않음
  if (!step.target || step.target.trim() === "") {
    console.log("No target specified for this step, skipping highlight");
    return;
  }

  const targetElement = document.querySelector(step.target);

  console.log("Looking for:", step.target);
  console.log("Found element:", targetElement);

  if (!targetElement) {
    console.log("Element not found!");
    return;
  }

  // 현재 단계 버튼에 하이라이트 클래스 추가 및 클릭 방지
  targetElement.classList.add("tutorial-highlight");
  targetElement.style.pointerEvents = "none";
};

// example 순환 시작
const startExampleRotation = () => {
  if (exampleInterval) {
    clearInterval(exampleInterval);
  }

  currentExampleIndex.value = 0;
  const examples = currentStepData.value.example;

  if (Array.isArray(examples) && examples.length > 1) {
    exampleInterval = setInterval(() => {
      currentExampleIndex.value =
        (currentExampleIndex.value + 1) % examples.length;
    }, 2000);
  }
};

// example 순환 중지
const stopExampleRotation = () => {
  if (exampleInterval) {
    clearInterval(exampleInterval);
    exampleInterval = null;
  }
};

// 다음 스텝
const nextStep = () => {
  stopExampleRotation();
  if (currentStep.value < tutorialSteps.length - 1) {
    currentStep.value++;
    updateHighlight();
    startExampleRotation();
  } else {
    closeTutorial();
  }
};

// 이전 스텝
const previousStep = () => {
  stopExampleRotation();
  if (currentStep.value > 0) {
    currentStep.value--;
    updateHighlight();
    startExampleRotation();
  }
};

// 튜토리얼 건너뛰기
const skipTutorial = () => {
  closeTutorial();
};

// 튜토리얼 닫기
const closeTutorial = () => {
  stopExampleRotation();
  // 모든 하이라이트 제거 및 클릭 이벤트 복원
  const allButtons = document.querySelectorAll("[data-tutorial]");
  allButtons.forEach((btn) => {
    btn.classList.remove("tutorial-highlight");
    btn.style.pointerEvents = "";
  });

  emit("update:modelValue", false);
};

// 모달이 열릴 때 하이라이트 적용 및 스크롤 방지
watch(
  () => props.modelValue,
  (newValue) => {
    if (newValue) {
      // 스크롤 방지
      document.body.style.overflow = "hidden";
      currentStep.value = 0;
      // 좀 더 여유를 두고 하이라이트 적용
      setTimeout(() => {
        updateHighlight();
        startExampleRotation();
      }, 300);
    } else {
      // 스크롤 복원
      document.body.style.overflow = "";
      stopExampleRotation();
    }
  }
);

// ESC 키로 튜토리얼 닫기
onMounted(() => {
  const handleKeydown = (event) => {
    if (event.key === "Escape" && props.modelValue) {
      closeTutorial();
    }
  };

  document.addEventListener("keydown", handleKeydown);

  // 컴포넌트 언마운트 시 이벤트 리스너 제거
  const cleanup = () => {
    document.removeEventListener("keydown", handleKeydown);
  };

  return cleanup;
});

// 컴포넌트 언마운트 시 스크롤 복원
onBeforeUnmount(() => {
  document.body.style.overflow = "";
});
</script>

<style>
/* 글로벌 스타일로 설정 */
.tutorial-highlight {
  position: relative;
  animation: tutorialPulse 2s infinite;
  box-shadow: 0 0 0 3px rgba(147, 51, 234, 0.8),
    0 0 20px rgba(147, 51, 234, 0.6) !important;
  border-radius: 16px !important; /* 더 둥글게 */
  z-index: 45 !important; /* 오버레이(z-40) 위, 모달(z-50) 아래 */
  background: rgb(
    147 197 253
  ) !important; /* bg-blue-300으로 설정하여 더 잘 보이게 */
  padding: 6px 16px !important; /* 더 길고 넓은 패딩 */
  margin: -2px -8px !important; /* 음수 마진으로 더 자연스럽게 확장 */
}

@keyframes tutorialPulse {
  0%,
  100% {
    box-shadow: 0 0 0 3px rgba(147, 51, 234, 0.8),
      0 0 20px rgba(147, 51, 234, 0.6);
  }
  50% {
    box-shadow: 0 0 0 4px rgba(147, 51, 234, 0.6),
      0 0 25px rgba(147, 51, 234, 0.8);
  }
}

/* 스코프된 스타일 */
</style>
<style scoped>
/* 애니메이션 */
@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.animate-slideUp {
  animation: slideUp 0.4s ease;
}
</style>

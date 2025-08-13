<template>
  <div
    :style="{ backgroundImage: `url(${bgImage})` }"
    class="h-screen relative bg-cover bg-center"
  >
    <header class="px-4 py-4 flex items-center justify-between">
      <button
        @click="goDashboard"
        aria-label="뒤로가기"
        class="w-20 h-20 flex items-center justify-center bg-white rounded-lg shadow"
      >
        <img :src="outIcon" alt="뒤로가기" class="w-20 h-20 object-contain" />
      </button>

      <!-- 선택된 아이 이름 표시 -->
      <div
        v-if="currentChild && currentChild.name"
        class="bg-white/70 backdrop-blur-sm rounded-2xl px-6 py-3 shadow-lg"
      >
        <div class="flex items-center gap-3">
          <img
            :src="currentChild.profileImage || 'https://placehold.co/40x40'"
            :alt="`${currentChild.name} 프로필`"
            class="w-12 h-12 rounded-full object-cover border-2 border-white"
          />
          <p class="text-4xl text-gray-800 font-shark">
            {{ currentChild.name }}
          </p>
        </div>
      </div>
    </header>

    <main
      class="absolute inset-0 flex items-center justify-center pointer-events-none"
    >
      <div
        class="pointer-events-auto grid grid-cols-1 sm:grid-cols-2 gap-y-6 gap-x-16 w-full max-w-[900px] px-4"
      >
        <div
          @click="goToPenguin"
          @mouseenter="playHoverSound"
          class="block bg-purple-300 rounded-2xl overflow-hidden shadow hover:shadow-xl hover:scale-105 transition-all duration-300 cursor-pointer hover-glow"
        >
          <div class="p-4">
            <h2
              class="text-4xl mb-2 text-black text-center font-shark text-outline-white"
            >
              <span class="text-blue-600 font-shark">펭귄</span>과 대화하기
            </h2>
            <img
              src="../assets/images/penguin.png"
              alt="펭귄과 대화하기"
              class="w-full h-full object-contain"
            />
          </div>
        </div>

        <div
          @click="goToDrawing"
          @mouseenter="playHoverSound"
          class="block bg-blue-400 rounded-2xl overflow-hidden shadow hover:shadow-xl hover:scale-105 transition-all duration-300 cursor-pointer hover-glow"
        >
          <div class="p-4">
            <h2 class="text-4xl font-shark mb-2 text-center text-outline-white">
              나의 <span class="font-shark text-red-500">그림</span> 일기
            </h2>
            <img
              src="../assets/images/drawing.png"
              alt="나의 그림 일기"
              class="w-full h-full object-contain py-5"
            />
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, onUnmounted } from "vue";
import { useRouter } from "vue-router";
import { useChildStore } from "@/store/child";
import outIcon from "../assets/images/out.png";
import bgImage from "../assets/images/child_main.png";
import bgmSound from "../assets/effects/bgm.mp3";

// props 정의
const props = defineProps({
  childId: {
    type: [String, Number],
    default: null,
  },
});

const router = useRouter();
const childStore = useChildStore();

// 현재 선택된 아이 정보
const currentChild = computed(() => childStore.selectedChild);

// BGM 관리
const bgmAudio = ref(null);
const isBGMPlaying = ref(false);

// 컴포넌트 마운트 시 실행
onMounted(() => {
  childStore.initialize();

  // URL에서 childId가 전달된 경우 해당 아이를 선택
  if (props.childId) {
    const childId = parseInt(props.childId);
    if (childStore.children.find((child) => child.id === childId)) {
      childStore.selectChild(childId);
    }
  }

  // BGM 자동 재생 시도
  playBGM();
});

// 컴포넌트 언마운트 시 BGM 정지
onUnmounted(() => {
  stopBGM();
});

const goDashboard = () => router.push({ name: "Dashboard" });

// 펭귀과 대화하기 클릭 시 바로 이동 (이미 선택된 아이 정보가 있음)
function goToPenguin() {
  // 현재 선택된 아이가 있으면 바로 펭귄 페이지로
  if (currentChild.value) {
    router.push({
      name: "ChildPet",
      params: { childId: currentChild.value.id },
    });
  }
}

// 그림일기 클릭 시 바로 이동 (이미 선택된 아이 정보가 있음)
function goToDrawing() {
  // 현재 선택된 아이가 있으면 바로 그림일기 페이지로
  if (currentChild.value) {
    router.push({
      name: "ChildDrawing",
      params: { childId: currentChild.value.id },
    });
  }
}

// BGM 재생
function playBGM() {
  if (!isBGMPlaying.value) {
    try {
      console.log('BGM 재생 시도 중...');
      bgmAudio.value = new Audio('/src/assets/effects/bgm.mp3');
      bgmAudio.value.loop = true;
      bgmAudio.value.volume = 0.3;
      bgmAudio.value.autoplay = true;
      
      // 강제 재생 시도
      const playPromise = bgmAudio.value.play();
      
      if (playPromise !== undefined) {
        playPromise.then(() => {
          isBGMPlaying.value = true;
          console.log('BGM 재생 성공');
        }).catch(error => {
          console.log('자동 재생 차단됨, 사용자 상호작용 대기 중:', error);
          // 차단된 경우 첫 번째 클릭/터치 이벤트에서 재생
          document.addEventListener('click', playBGM, { once: true });
          document.addEventListener('touchstart', playBGM, { once: true });
        });
      }
    } catch (error) {
      console.error('BGM 생성 실패:', error);
    }
  }
}

// BGM 정지
function stopBGM() {
  if (bgmAudio.value) {
    bgmAudio.value.pause();
    bgmAudio.value.currentTime = 0;
    bgmAudio.value = null;
    isBGMPlaying.value = false;
  }
}

// 호버 시 효과음 재생
function playHoverSound() {
  try {
    const audio = new Audio('/src/assets/effects/decide.mp3');
    audio.volume = 0.4;
    audio.play().catch(error => {
      console.warn('호버 효과음 재생 실패:', error);
    });
  } catch (error) {
    console.warn('호버 효과음 로드 실패:', error);
  }
}
</script>

<style scoped>
.hover-glow:hover {
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.3), 
              0 0 20px rgba(255, 255, 255, 0.5),
              inset 0 0 20px rgba(255, 255, 255, 0.2);
  transform: translateY(-5px) scale(1.05);
}

/* 펭귀 버튼 전용 호버 효과 */
.bg-purple-300.hover-glow:hover {
  box-shadow: 0 20px 40px rgba(147, 51, 234, 0.4), 
              0 0 25px rgba(196, 181, 253, 0.6),
              inset 0 0 20px rgba(255, 255, 255, 0.3);
}

/* 그림일기 버튼 전용 호버 효과 */
.bg-blue-400.hover-glow:hover {
  box-shadow: 0 20px 40px rgba(59, 130, 246, 0.4), 
              0 0 25px rgba(147, 197, 253, 0.6),
              inset 0 0 20px rgba(255, 255, 255, 0.3);
}
</style>

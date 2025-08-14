<template>
  <!-- 오답 모달 -->
  <div
    v-if="show"
    class="fixed inset-0 bg-red-900 bg-opacity-70 flex items-center justify-center z-50 font-paper"
  >
    <div 
      class="bg-white rounded-xl overflow-hidden shadow-lg max-w-md w-full mx-4 shake-animation"
      :class="{ 'animate-shake': show }"
    >
      <div class="p-6 text-center bg-red-50">
        <div class="mb-4">
          <span class="text-6xl animate-bounce">❌</span>
        </div>
        <h3 class="text-xl font-paperSemi mb-2 text-red-800">
          틀렸습니다!
        </h3>
        <p class="text-red-600 mb-6 text-lg">
          다시 한번 생각해보세요.
        </p>
        
        <div class="flex justify-center">
          <button
            @click="closeModal"
            class="px-6 py-3 bg-red-500 hover:bg-red-600 text-white rounded-lg transition-colors font-paper text-lg"
          >
            다시 시도하기
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, watch, ref } from 'vue'

const props = defineProps({
  show: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['close'])

const closeModal = () => {
  emit('close')
}

// 사운드 미리 로딩
const failAudio = ref(null)

// 사운드 미리 로딩
const loadFailSound = async () => {
  try {
    const failSoundModule = await import("@/assets/effects/fail.mp3")
    failAudio.value = new Audio(failSoundModule.default)
    failAudio.value.volume = 0.7
    failAudio.value.preload = "auto"
  } catch (error) {
    console.warn("오답 효과음 로드 실패:", error)
  }
}

// 오답 효과음 재생
const playIncorrectSound = async () => {
  if (!failAudio.value) {
    console.warn("오답 효과음이 아직 로드되지 않았습니다.")
    return
  }
  
  try {
    // 오디오 재설정 (이전 재생 중지)
    failAudio.value.currentTime = 0
    
    const playPromise = failAudio.value.play()
    if (playPromise !== undefined) {
      playPromise
        .then(() => {
          console.log("오답 효과음 재생 성공")
        })
        .catch((error) => {
          console.warn("오답 효과음 재생 실패 - 사용자 상호작용 필요:", error)
        })
    }
  } catch (error) {
    console.warn("오답 효과음 재생 중 오류:", error)
  }
}

// 컴포넌트 마운트 시 사운드 로드
onMounted(async () => {
  await loadFailSound()
})

// 모달이 표시될 때 효과음 재생
watch(() => props.show, (newValue) => {
  if (newValue) {
    playIncorrectSound()
    // 3초 후 자동으로 닫기 (선택사항)
    // setTimeout(() => {
    //   closeModal()
    // }, 3000)
  }
})
</script>

<style scoped>
/* 흔들림 애니메이션 */
@keyframes shake {
  0%, 100% { transform: translateX(0); }
  10%, 30%, 50%, 70%, 90% { transform: translateX(-5px); }
  20%, 40%, 60%, 80% { transform: translateX(5px); }
}

.animate-shake {
  animation: shake 0.6s ease-in-out;
}

.shake-animation {
  animation: shake 0.6s ease-in-out;
}

/* 모달 등장 애니메이션 */
.fixed {
  animation: fadeIn 0.3s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

/* 모달 내용 등장 애니메이션 */
.bg-white {
  animation: slideUp 0.3s ease-out;
}

@keyframes slideUp {
  from {
    transform: translateY(20px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}
</style>
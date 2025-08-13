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
import { onMounted, watch } from 'vue'

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

// 오답 효과음 재생
const playIncorrectSound = () => {
  try {
    const audio = new Audio('/src/assets/effects/fail.mp3')
    audio.volume = 0.7
    audio.play().catch(error => {
    })
  } catch (error) {
  }
}

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
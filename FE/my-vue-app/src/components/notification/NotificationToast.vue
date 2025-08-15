<template>
  <transition name="slide-down">
    <div
      v-if="visible"
      class="fixed left-1/2 transform -translate-x-1/2 z-50 font-paper max-w-[85vw] sm:max-w-sm w-full mx-2 sm:mx-4"
      :style="topStyle"
    >
      <div
        class="rounded-xl shadow-lg px-3 sm:px-6 py-3 sm:py-4 flex items-center gap-2 sm:gap-4"
        :class="typeClasses"
      >
        <!-- 아이콘 -->
        <div class="flex-shrink-0">
          <span class="text-lg sm:text-2xl">{{ typeIcon }}</span>
        </div>
        
        <!-- 내용 -->
        <div class="flex-1 min-w-0">
          <p v-if="title" class="font-paperBold text-sm sm:text-lg mb-1">{{ title }}</p>
          <p class="text-xs sm:text-sm" :class="title ? '' : 'font-paperBold'" v-html="message"></p>
        </div>
        
        <!-- 닫기 버튼 (선택적) -->
        <button
          v-if="closable"
          @click="close"
          class="flex-shrink-0 ml-1 sm:ml-2 text-lg sm:text-xl opacity-70 hover:opacity-100 transition-opacity"
        >
          ✕
        </button>
      </div>
    </div>
  </transition>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'

const props = defineProps({
  type: {
    type: String,
    default: 'info',
    validator: (value) => ['success', 'error', 'warning', 'info'].includes(value)
  },
  title: {
    type: String,
    default: ''
  },
  message: {
    type: String,
    required: true
  },
  duration: {
    type: Number,
    default: 2000
  },
  closable: {
    type: Boolean,
    default: true
  },
  persistent: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['close'])

const visible = ref(false)
let timer = null

// 타입별 스타일과 아이콘
const typeClasses = computed(() => {
  const classes = {
    success: 'bg-green-500 text-white',
    error: 'bg-red-500 text-white',
    warning: 'bg-orange-500 text-white',
    info: 'bg-blue-500 text-white'
  }
  return classes[props.type]
})

const typeIcon = computed(() => {
  const icons = {
    success: '✅',
    error: '❌',
    warning: '⚠️',
    info: 'ℹ️'
  }
  return icons[props.type]
})

const topStyle = computed(() => {
  return {
    top: typeof window !== 'undefined' && window.innerWidth >= 768 ? '48px !important' : '64px !important'
  }
})

function show() {
  visible.value = true
  
  if (!props.persistent && props.duration > 0) {
    timer = setTimeout(() => {
      close()
    }, props.duration)
  }
}

function close() {
  visible.value = false
  if (timer) {
    clearTimeout(timer)
    timer = null
  }
  emit('close')
}

onMounted(() => {
  show()
})

onUnmounted(() => {
  if (timer) {
    clearTimeout(timer)
  }
})

// 외부에서 호출할 수 있도록 expose
defineExpose({
  show,
  close
})
</script>

<style scoped>
/* 알림 토스트 애니메이션 */
.slide-down-enter-active,
.slide-down-leave-active {
  transition: all 0.3s ease;
}
.slide-down-enter-from {
  transform: translate(-50%, -100%);
  opacity: 0;
}
.slide-down-leave-to {
  transform: translate(-50%, -100%);
  opacity: 0;
}
</style>
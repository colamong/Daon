<template>
  <div v-show="isActive" class="cloud-transition-overlay">
    <!-- 구름 파티클들이 여기에 동적으로 생성됩니다 -->
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick, watch } from 'vue'

const props = defineProps({
  show: {
    type: Boolean,
    default: false
  },
  duration: {
    type: Number,
    default: 6000 // 6초로 증가
  }
})

const emit = defineEmits(['complete', 'coverComplete'])

const isActive = ref(false)

// 구름 시스템 변수들
let width = 0
let height = 0
let xC = 0
let yC = 0
let stepCount = 0
let population = 200 // 구름 개수 증가
let particles = []
let animationFrame = null
let isAnimating = false
let evolutionPhase = 'growing' // 'growing', 'covering', 'shrinking'

// 구름 파티클 생성
const birth = () => {
  const speed = 0.08 // 속도 느리게
  const x = xC + 220 * (-1 + 2 * Math.random()) // 범위 증가
  const y = yC + 120 * (-1 + 2 * Math.random()) // 범위 증가
  const distToCenter = Math.sqrt(Math.pow(x - xC, 2) + Math.pow(y - yC, 2))
  const size = evolutionPhase === 'growing' 
    ? 8 + Math.random() * 25 // 시작 크기 증가
    : 180 - 0.4 * distToCenter // 최대 크기 증가

  const particle = {
    x: x,
    y: y,
    xStart: x,
    yStart: y,
    speed: speed * (-0.5 + Math.random()),
    dist: 8 + Math.random() * 12, // 움직임 범위 줄임
    r: size,
    targetR: 180 - 0.4 * distToCenter, // 목표 크기 증가
    opacity: 0.25 // 투명도 진하게
  }

  particles.push(particle)

  // DOM에 구름 요소 생성
  const div = document.createElement('div')
  div.className = 'drop'
  div.style.position = 'absolute'
  div.style.borderRadius = '50%'
  div.style.background = 'radial-gradient(circle, rgba(255, 255, 255, 0.4) 0%, rgba(255, 255, 255, 0.2) 60%, rgba(255, 255, 255, 0.1) 100%)'
  div.style.pointerEvents = 'none'
  div.style.filter = 'blur(2px)' // 살짝 블러 효과
  
  const overlay = document.querySelector('.cloud-transition-overlay')
  if (overlay) {
    overlay.appendChild(div)
  }
}

// 구름 움직임 업데이트
const move = () => {
  for (let i = 0; i < particles.length; i++) {
    const p = particles[i]
    p.x = p.xStart + p.dist * Math.cos(p.speed * stepCount)
    p.y = p.yStart + p.dist * Math.sin(p.speed * stepCount)
    
    // 크기 애니메이션
    if (evolutionPhase === 'growing' && p.r < p.targetR) {
      p.r += (p.targetR - p.r) * 0.03 // 더 천천히 크기 증가
    } else if (evolutionPhase === 'shrinking') {
      p.r *= 0.92 // 더 천천히 크기 감소
      p.opacity *= 0.93 // 투명도도 천천히 감소
    }
  }
}

// 구름 그리기
const draw = () => {
  const divs = document.querySelectorAll('.cloud-transition-overlay .drop')
  for (let i = 0; i < particles.length && i < divs.length; i++) {
    const particle = particles[i]
    const div = divs[i]
    
    const r = particle.r
    const x = particle.x - r / 2
    const y = particle.y - r / 2
    
    div.style.left = x + 'px'
    div.style.top = y + 'px'
    div.style.height = r + 'px'
    div.style.width = r + 'px'
    div.style.background = `radial-gradient(circle, rgba(255, 255, 255, ${particle.opacity * 1.5}) 0%, rgba(255, 255, 255, ${particle.opacity}) 60%, rgba(255, 255, 255, ${particle.opacity * 0.5}) 100%)`
  }
}

// 구름 진화 함수
const evolve = () => {
  if (!isAnimating) return
  
  stepCount++
  move()
  draw()
  
  animationFrame = requestAnimationFrame(evolve)
}

// 구름 초기화
const init = () => {
  width = window.innerWidth
  height = window.innerHeight
  xC = width / 2
  yC = height / 2
  stepCount = 0
  particles = []
  
  // 구름 파티클들 생성
  for (let i = 0; i < population; i++) {
    birth()
  }
}

// DOM 정리
const cleanup = () => {
  const overlay = document.querySelector('.cloud-transition-overlay')
  if (overlay) {
    const drops = overlay.querySelectorAll('.drop')
    drops.forEach(drop => drop.remove())
  }
  particles = []
}

// 전환 효과 시작
const startTransition = async () => {
  isActive.value = true
  isAnimating = true
  evolutionPhase = 'growing'
  
  await nextTick()
  
  init()
  evolve()
  
  // 2.5초 후 구름이 충분히 커지면 펭귄을 가렸다고 알림
  setTimeout(() => {
    evolutionPhase = 'covering'
    emit('coverComplete') // 펭귄 이미지 바꾸라고 알림
  }, 2500)
  
  // 4초 후 구름 걷어내기 시작
  setTimeout(() => {
    evolutionPhase = 'shrinking'
  }, 4000)
  
  // 6초 후 완료
  setTimeout(() => {
    stopTransition()
    emit('complete')
  }, props.duration)
}

// 전환 효과 중지
const stopTransition = () => {
  isActive.value = false
  isAnimating = false
  
  if (animationFrame) {
    cancelAnimationFrame(animationFrame)
    animationFrame = null
  }
  
  cleanup()
}

// Props 변화 감지
watch(() => props.show, (newValue) => {
  if (newValue) {
    startTransition()
  } else {
    stopTransition()
  }
})

onMounted(() => {
  // 초기화는 show가 true가 될 때만
})

onUnmounted(() => {
  stopTransition()
})
</script>

<style scoped>
.cloud-transition-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1000;
  pointer-events: none;
  background: transparent;
}

:deep(.drop) {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.15);
  pointer-events: none;
  transition: all 0.1s ease-out;
}
</style>
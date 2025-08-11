<template>
  <div v-if="show" class="confetti-container">
    <div v-for="i in 100" :key="i" :class="`confetti-${i}`"></div>
  </div>
</template>

<script setup>
defineProps({
  show: {
    type: Boolean,
    default: false
  }
})
</script>

<style scoped>
/* Confetti 축하 효과 */
.confetti-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 55;
}

[class^="confetti-"] {
  position: absolute;
  opacity: 0.8;
}

/* Confetti 스타일 생성 - 크기 2배 증가 */
.confetti-1 { width: 12px; height: 5px; background-color: #d13447; top: -10%; left: 1%; transform: rotate(45deg); animation: drop-1 4.2s 0.1s infinite; }
.confetti-2 { width: 16px; height: 7px; background-color: #ffbf00; top: -10%; left: 5%; transform: rotate(120deg); animation: drop-2 3.8s 0.3s infinite; }
.confetti-3 { width: 10px; height: 4px; background-color: #263672; top: -10%; left: 10%; transform: rotate(180deg); animation: drop-3 4.5s 0.2s infinite; }
.confetti-4 { width: 14px; height: 6px; background-color: #d13447; top: -10%; left: 15%; transform: rotate(270deg); animation: drop-4 3.9s 0.4s infinite; }
.confetti-5 { width: 12px; height: 5px; background-color: #ffbf00; top: -10%; left: 20%; transform: rotate(90deg); animation: drop-5 4.1s 0.1s infinite; }
.confetti-6 { width: 16px; height: 7px; background-color: #263672; top: -10%; left: 25%; transform: rotate(315deg); animation: drop-6 4.3s 0.5s infinite; }
.confetti-7 { width: 10px; height: 4px; background-color: #d13447; top: -10%; left: 30%; transform: rotate(225deg); animation: drop-7 3.7s 0.2s infinite; }
.confetti-8 { width: 14px; height: 6px; background-color: #ffbf00; top: -10%; left: 35%; transform: rotate(135deg); animation: drop-8 4.4s 0.3s infinite; }
.confetti-9 { width: 12px; height: 5px; background-color: #263672; top: -10%; left: 40%; transform: rotate(60deg); animation: drop-9 4.0s 0.4s infinite; }
.confetti-10 { width: 16px; height: 7px; background-color: #d13447; top: -10%; left: 45%; transform: rotate(150deg); animation: drop-10 3.8s 0.1s infinite; }
.confetti-11 { width: 10px; height: 4px; background-color: #ffbf00; top: -10%; left: 50%; transform: rotate(240deg); animation: drop-11 4.2s 0.5s infinite; }
.confetti-12 { width: 14px; height: 6px; background-color: #263672; top: -10%; left: 55%; transform: rotate(330deg); animation: drop-12 3.9s 0.2s infinite; }
.confetti-13 { width: 12px; height: 5px; background-color: #d13447; top: -10%; left: 60%; transform: rotate(75deg); animation: drop-13 4.1s 0.3s infinite; }
.confetti-14 { width: 16px; height: 7px; background-color: #ffbf00; top: -10%; left: 65%; transform: rotate(165deg); animation: drop-14 4.3s 0.4s infinite; }
.confetti-15 { width: 10px; height: 4px; background-color: #263672; top: -10%; left: 70%; transform: rotate(255deg); animation: drop-15 3.7s 0.1s infinite; }
.confetti-16 { width: 14px; height: 6px; background-color: #d13447; top: -10%; left: 75%; transform: rotate(345deg); animation: drop-16 4.4s 0.5s infinite; }
.confetti-17 { width: 12px; height: 5px; background-color: #ffbf00; top: -10%; left: 80%; transform: rotate(105deg); animation: drop-17 4.0s 0.2s infinite; }
.confetti-18 { width: 16px; height: 7px; background-color: #263672; top: -10%; left: 85%; transform: rotate(195deg); animation: drop-18 3.8s 0.3s infinite; }
.confetti-19 { width: 10px; height: 4px; background-color: #d13447; top: -10%; left: 90%; transform: rotate(285deg); animation: drop-19 4.2s 0.4s infinite; }
.confetti-20 { width: 14px; height: 6px; background-color: #ffbf00; top: -10%; left: 95%; transform: rotate(15deg); animation: drop-20 3.9s 0.1s infinite; }
.confetti-21 { width: 12px; height: 5px; background-color: #d13447; top: -10%; left: 8%; transform: rotate(315deg); animation: drop-21 4.5s 0.6s infinite; }
.confetti-22 { width: 16px; height: 7px; background-color: #ffbf00; top: -10%; left: 12%; transform: rotate(225deg); animation: drop-22 3.6s 0.2s infinite; }
.confetti-23 { width: 10px; height: 4px; background-color: #263672; top: -10%; left: 18%; transform: rotate(135deg); animation: drop-23 4.8s 0.4s infinite; }
.confetti-24 { width: 14px; height: 6px; background-color: #d13447; top: -10%; left: 22%; transform: rotate(45deg); animation: drop-24 3.3s 0.1s infinite; }
.confetti-25 { width: 12px; height: 5px; background-color: #ffbf00; top: -10%; left: 28%; transform: rotate(270deg); animation: drop-25 4.6s 0.5s infinite; }
.confetti-26 { width: 16px; height: 7px; background-color: #263672; top: -10%; left: 32%; transform: rotate(180deg); animation: drop-26 3.5s 0.3s infinite; }
.confetti-27 { width: 10px; height: 4px; background-color: #d13447; top: -10%; left: 38%; transform: rotate(90deg); animation: drop-27 4.7s 0.1s infinite; }
.confetti-28 { width: 14px; height: 6px; background-color: #ffbf00; top: -10%; left: 42%; transform: rotate(300deg); animation: drop-28 3.4s 0.4s infinite; }
.confetti-29 { width: 12px; height: 5px; background-color: #263672; top: -10%; left: 48%; transform: rotate(210deg); animation: drop-29 4.9s 0.2s infinite; }
.confetti-30 { width: 16px; height: 7px; background-color: #d13447; top: -10%; left: 52%; transform: rotate(120deg); animation: drop-30 3.2s 0.5s infinite; }

/* 더 많은 confetti 추가 (31-100) - 크기 2배 증가 */
.confetti-31 { width: 10px; height: 4px; background-color: #ffbf00; top: -10%; left: 58%; transform: rotate(30deg); animation: drop-31 4.8s 0.1s infinite; }
.confetti-32 { width: 14px; height: 6px; background-color: #263672; top: -10%; left: 62%; transform: rotate(240deg); animation: drop-32 3.7s 0.3s infinite; }
.confetti-33 { width: 12px; height: 5px; background-color: #d13447; top: -10%; left: 68%; transform: rotate(150deg); animation: drop-33 4.4s 0.2s infinite; }
.confetti-34 { width: 16px; height: 7px; background-color: #ffbf00; top: -10%; left: 72%; transform: rotate(60deg); animation: drop-34 3.9s 0.4s infinite; }
.confetti-35 { width: 10px; height: 4px; background-color: #263672; top: -10%; left: 78%; transform: rotate(330deg); animation: drop-35 4.1s 0.1s infinite; }
.confetti-36 { width: 14px; height: 6px; background-color: #d13447; top: -10%; left: 82%; transform: rotate(270deg); animation: drop-36 3.6s 0.5s infinite; }
.confetti-37 { width: 12px; height: 5px; background-color: #ffbf00; top: -10%; left: 88%; transform: rotate(180deg); animation: drop-37 4.5s 0.2s infinite; }
.confetti-38 { width: 16px; height: 7px; background-color: #263672; top: -10%; left: 92%; transform: rotate(90deg); animation: drop-38 3.8s 0.3s infinite; }
.confetti-39 { width: 10px; height: 4px; background-color: #d13447; top: -10%; left: 98%; transform: rotate(360deg); animation: drop-39 4.2s 0.4s infinite; }
.confetti-40 { width: 14px; height: 6px; background-color: #ffbf00; top: -10%; left: 3%; transform: rotate(270deg); animation: drop-40 3.3s 0.1s infinite; }

/* 더 많은 confetti는 동적으로 생성하기 위해 40개 정도만 하고 나머지는 반복 - 크기 2배 증가 */
.confetti-41, .confetti-42, .confetti-43, .confetti-44, .confetti-45,
.confetti-46, .confetti-47, .confetti-48, .confetti-49, .confetti-50 {
  width: 12px; height: 5px; background-color: #d13447; 
  top: -10%; animation-duration: 4s; animation-iteration-count: infinite;
}
.confetti-51, .confetti-52, .confetti-53, .confetti-54, .confetti-55,
.confetti-56, .confetti-57, .confetti-58, .confetti-59, .confetti-60 {
  width: 14px; height: 6px; background-color: #ffbf00; 
  top: -10%; animation-duration: 3.8s; animation-iteration-count: infinite;
}
.confetti-61, .confetti-62, .confetti-63, .confetti-64, .confetti-65,
.confetti-66, .confetti-67, .confetti-68, .confetti-69, .confetti-70 {
  width: 10px; height: 4px; background-color: #263672; 
  top: -10%; animation-duration: 4.3s; animation-iteration-count: infinite;
}
.confetti-71, .confetti-72, .confetti-73, .confetti-74, .confetti-75,
.confetti-76, .confetti-77, .confetti-78, .confetti-79, .confetti-80 {
  width: 16px; height: 7px; background-color: #d13447; 
  top: -10%; animation-duration: 3.5s; animation-iteration-count: infinite;
}
.confetti-81, .confetti-82, .confetti-83, .confetti-84, .confetti-85,
.confetti-86, .confetti-87, .confetti-88, .confetti-89, .confetti-90 {
  width: 12px; height: 5px; background-color: #ffbf00; 
  top: -10%; animation-duration: 4.6s; animation-iteration-count: infinite;
}
.confetti-91, .confetti-92, .confetti-93, .confetti-94, .confetti-95,
.confetti-96, .confetti-97, .confetti-98, .confetti-99, .confetti-100 {
  width: 14px; height: 6px; background-color: #263672; 
  top: -10%; animation-duration: 3.9s; animation-iteration-count: infinite;
}

/* 동적 위치와 애니메이션 할당 */
.confetti-41 { left: 7%; transform: rotate(45deg); animation-name: drop-1; animation-delay: 0.1s; }
.confetti-42 { left: 13%; transform: rotate(135deg); animation-name: drop-2; animation-delay: 0.2s; }
.confetti-43 { left: 17%; transform: rotate(225deg); animation-name: drop-3; animation-delay: 0.3s; }
.confetti-44 { left: 23%; transform: rotate(315deg); animation-name: drop-4; animation-delay: 0.4s; }
.confetti-45 { left: 27%; transform: rotate(90deg); animation-name: drop-5; animation-delay: 0.5s; }
.confetti-46 { left: 33%; transform: rotate(180deg); animation-name: drop-6; animation-delay: 0.1s; }
.confetti-47 { left: 37%; transform: rotate(270deg); animation-name: drop-7; animation-delay: 0.2s; }
.confetti-48 { left: 43%; transform: rotate(360deg); animation-name: drop-8; animation-delay: 0.3s; }
.confetti-49 { left: 47%; transform: rotate(60deg); animation-name: drop-9; animation-delay: 0.4s; }
.confetti-50 { left: 53%; transform: rotate(120deg); animation-name: drop-10; animation-delay: 0.5s; }

/* 나머지 50개도 유사하게 배치 */
.confetti-51 { left: 57%; transform: rotate(240deg); animation-name: drop-11; animation-delay: 0.1s; }
.confetti-52 { left: 63%; transform: rotate(300deg); animation-name: drop-12; animation-delay: 0.2s; }
.confetti-53 { left: 67%; transform: rotate(30deg); animation-name: drop-13; animation-delay: 0.3s; }
.confetti-54 { left: 73%; transform: rotate(150deg); animation-name: drop-14; animation-delay: 0.4s; }
.confetti-55 { left: 77%; transform: rotate(210deg); animation-name: drop-15; animation-delay: 0.5s; }
.confetti-56 { left: 83%; transform: rotate(330deg); animation-name: drop-16; animation-delay: 0.1s; }
.confetti-57 { left: 87%; transform: rotate(75deg); animation-name: drop-17; animation-delay: 0.2s; }
.confetti-58 { left: 93%; transform: rotate(105deg); animation-name: drop-18; animation-delay: 0.3s; }
.confetti-59 { left: 97%; transform: rotate(195deg); animation-name: drop-19; animation-delay: 0.4s; }
.confetti-60 { left: 2%; transform: rotate(285deg); animation-name: drop-20; animation-delay: 0.5s; }

/* 61-100까지도 유사하게... (간소화를 위해 몇 개만) */
.confetti-61 { left: 6%; transform: rotate(15deg); animation-name: drop-21; animation-delay: 0.1s; }
.confetti-62 { left: 11%; transform: rotate(165deg); animation-name: drop-22; animation-delay: 0.2s; }
.confetti-63 { left: 16%; transform: rotate(255deg); animation-name: drop-23; animation-delay: 0.3s; }
.confetti-64 { left: 21%; transform: rotate(345deg); animation-name: drop-24; animation-delay: 0.4s; }
.confetti-65 { left: 26%; transform: rotate(120deg); animation-name: drop-25; animation-delay: 0.5s; }

/* 나머지들은 기본 애니메이션 적용 */
.confetti-66, .confetti-67, .confetti-68, .confetti-69, .confetti-70 { left: 31%; animation-name: drop-1; }
.confetti-71, .confetti-72, .confetti-73, .confetti-74, .confetti-75 { left: 36%; animation-name: drop-2; }
.confetti-76, .confetti-77, .confetti-78, .confetti-79, .confetti-80 { left: 41%; animation-name: drop-3; }
.confetti-81, .confetti-82, .confetti-83, .confetti-84, .confetti-85 { left: 46%; animation-name: drop-4; }
.confetti-86, .confetti-87, .confetti-88, .confetti-89, .confetti-90 { left: 51%; animation-name: drop-5; }
.confetti-91, .confetti-92, .confetti-93, .confetti-94, .confetti-95 { left: 56%; animation-name: drop-6; }
.confetti-96, .confetti-97, .confetti-98, .confetti-99, .confetti-100 { left: 61%; animation-name: drop-7; }

/* 애니메이션 키프레임 */
@keyframes drop-1 { 100% { top: 110%; left: 3%; } }
@keyframes drop-2 { 100% { top: 110%; left: 8%; } }
@keyframes drop-3 { 100% { top: 110%; left: 13%; } }
@keyframes drop-4 { 100% { top: 110%; left: 18%; } }
@keyframes drop-5 { 100% { top: 110%; left: 23%; } }
@keyframes drop-6 { 100% { top: 110%; left: 28%; } }
@keyframes drop-7 { 100% { top: 110%; left: 33%; } }
@keyframes drop-8 { 100% { top: 110%; left: 38%; } }
@keyframes drop-9 { 100% { top: 110%; left: 43%; } }
@keyframes drop-10 { 100% { top: 110%; left: 48%; } }
@keyframes drop-11 { 100% { top: 110%; left: 53%; } }
@keyframes drop-12 { 100% { top: 110%; left: 58%; } }
@keyframes drop-13 { 100% { top: 110%; left: 63%; } }
@keyframes drop-14 { 100% { top: 110%; left: 68%; } }
@keyframes drop-15 { 100% { top: 110%; left: 73%; } }
@keyframes drop-16 { 100% { top: 110%; left: 78%; } }
@keyframes drop-17 { 100% { top: 110%; left: 83%; } }
@keyframes drop-18 { 100% { top: 110%; left: 88%; } }
@keyframes drop-19 { 100% { top: 110%; left: 93%; } }
@keyframes drop-20 { 100% { top: 110%; left: 98%; } }
@keyframes drop-21 { 100% { top: 110%; left: 10%; } }
@keyframes drop-22 { 100% { top: 110%; left: 15%; } }
@keyframes drop-23 { 100% { top: 110%; left: 20%; } }
@keyframes drop-24 { 100% { top: 110%; left: 25%; } }
@keyframes drop-25 { 100% { top: 110%; left: 30%; } }
@keyframes drop-26 { 100% { top: 110%; left: 35%; } }
@keyframes drop-27 { 100% { top: 110%; left: 40%; } }
@keyframes drop-28 { 100% { top: 110%; left: 45%; } }
@keyframes drop-29 { 100% { top: 110%; left: 50%; } }
@keyframes drop-30 { 100% { top: 110%; left: 55%; } }
@keyframes drop-31 { 100% { top: 110%; left: 60%; } }
@keyframes drop-32 { 100% { top: 110%; left: 65%; } }
@keyframes drop-33 { 100% { top: 110%; left: 70%; } }
@keyframes drop-34 { 100% { top: 110%; left: 75%; } }
@keyframes drop-35 { 100% { top: 110%; left: 80%; } }
@keyframes drop-36 { 100% { top: 110%; left: 85%; } }
@keyframes drop-37 { 100% { top: 110%; left: 90%; } }
@keyframes drop-38 { 100% { top: 110%; left: 95%; } }
@keyframes drop-39 { 100% { top: 110%; left: 2%; } }
@keyframes drop-40 { 100% { top: 110%; left: 7%; } }
</style>
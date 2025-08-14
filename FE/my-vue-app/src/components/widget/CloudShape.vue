<template>
  <div class="cloud-container">
    <slot></slot>
  </div>
</template>

<script setup>
// props로 색상을 받을 수 있도록 설정
const props = defineProps({
  bgColor: {
    type: String,
    default: 'rgba(255, 255, 255, 0.7)'
  }
})
</script>

<style scoped>
.cloud-container {
  position: relative;
  background: v-bind(bgColor);
  backdrop-filter: blur(4px);
  border-radius: 50px;
  padding: 12px 24px;
}

.cloud-container::before {
  content: '';
  position: absolute;
  top: -15px;
  left: 15px;
  width: 25px;
  height: 25px;
  background: v-bind(bgColor);
  backdrop-filter: blur(4px);
  border-radius: 50%;
  box-shadow: 30px 0 0 12px v-bind(bgColor), 50px 0 0 8px v-bind(bgColor);
}

.cloud-container::after {
  content: '';
  position: absolute;
  top: -10px;
  right: 20px;
  width: 18px;
  height: 18px;
  background: v-bind(bgColor);
  backdrop-filter: blur(4px);
  border-radius: 50%;
}
</style>
<template>
  <div class="cloud-wrapper">
    <svg class="cloud-svg" viewBox="0 0 400 160" xmlns="http://www.w3.org/2000/svg">
      <path d="M60,110 
               C30,110 30,80 50,80 
               C40,40 80,40 100,65 
               C130,25 180,25 210,65 
               C240,40 280,40 290,80 
               C310,80 310,110 290,110 
               L60,110 Z" 
            :fill="bgColor" 
            />
    </svg>
    <div class="cloud-content">
      <slot></slot>
    </div>
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
.cloud-wrapper {
  position: relative;
  display: inline-block;
  min-width: 400px;
  height: 160px;
}

.cloud-svg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

.cloud-content {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 80%;
  text-align: center;
}
</style>
<template>
  <div v-show="isActive" class="snow-container">
    <svg 
      class="snowflakes-svg"
      xmlns="http://www.w3.org/2000/svg" 
      version="1.1" 
      viewBox="0 0 1243 500"
      preserveAspectRatio="xMidYMid slice"
    >
      
      <!-- 눈송이 그룹 1 - 큰 사이즈 -->
      <g class="snowflakes-group-1">
        <circle class="snowflake" cx="592" cy="48" r="7"/>
        <circle class="snowflake" cx="833" cy="148" r="7"/>
        <circle class="snowflake" cx="371" cy="105" r="7"/>
        <circle class="snowflake" cx="115" cy="28" r="7"/>
        <circle class="snowflake" cx="1106" cy="208" r="7"/>
        <circle class="snowflake" cx="1032" cy="35" r="7"/>
        <circle class="snowflake" cx="200" cy="250" r="7"/>
        <circle class="snowflake" cx="450" cy="300" r="7"/>
        <circle class="snowflake" cx="750" cy="280" r="7"/>
        <circle class="snowflake" cx="950" cy="320" r="7"/>
      </g>
      
      <!-- 눈송이 그룹 2 - 큰 사이즈 -->
      <g class="snowflakes-group-2">
        <circle class="snowflake" cx="566" cy="162" r="7"/>
        <circle class="snowflake" cx="764" cy="21" r="7"/>
        <circle class="snowflake" cx="958" cy="184" r="7"/>
        <circle class="snowflake" cx="411" cy="201" r="7"/>
        <circle class="snowflake" cx="297" cy="7" r="7"/>
        <circle class="snowflake" cx="136" cy="141" r="7"/>
        <circle class="snowflake" cx="680" cy="350" r="7"/>
        <circle class="snowflake" cx="320" cy="380" r="7"/>
        <circle class="snowflake" cx="1050" cy="400" r="7"/>
      </g>
      
      <!-- 눈송이 그룹 3 - 작은 사이즈 -->
      <g class="snowflakes-group-3">
        <circle class="snowflake" cx="726.5" cy="225.5" r="4.5"/>
        <circle class="snowflake" cx="688.5" cy="123.5" r="4.5"/>
        <circle class="snowflake" cx="252.5" cy="168.5" r="4.5"/>
        <circle class="snowflake" cx="480.5" cy="83.5" r="4.5"/>
        <circle class="snowflake" cx="1180.5" cy="83.5" r="4.5"/>
        <circle class="snowflake" cx="907.5" cy="40.5" r="4.5"/>
        <circle class="snowflake" cx="1025.5" cy="253.5" r="4.5"/>
        <circle class="snowflake" cx="150.5" cy="350.5" r="4.5"/>
        <circle class="snowflake" cx="850.5" cy="420.5" r="4.5"/>
        <circle class="snowflake" cx="550.5" cy="450.5" r="4.5"/>
      </g>
      
      <!-- 눈송이 그룹 4 - 작은 사이즈 -->
      <g class="snowflakes-group-4">
        <circle class="snowflake" cx="4.5" cy="214.5" r="4.5"/>
        <circle class="snowflake" cx="52.5" cy="87.5" r="4.5"/>
        <circle class="snowflake" cx="256.5" cy="78.5" r="4.5"/>
        <circle class="snowflake" cx="598.5" cy="237.5" r="4.5"/>
        <circle class="snowflake" cx="1021.5" cy="123.5" r="4.5"/>
        <circle class="snowflake" cx="1238.5" cy="176.5" r="4.5"/>
        <circle class="snowflake" cx="380.5" cy="430.5" r="4.5"/>
        <circle class="snowflake" cx="780.5" cy="380.5" r="4.5"/>
        <circle class="snowflake" cx="120.5" cy="480.5" r="4.5"/>
      </g>
    </svg>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  show: {
    type: Boolean,
    default: true
  },
  flakeCount: {
    type: Number,
    default: 120 // SVG는 고정된 개수
  },
  intensity: {
    type: String,
    default: 'medium',
    validator: (value) => ['light', 'medium', 'heavy'].includes(value)
  }
})

const isActive = computed(() => props.show)
</script>

<style scoped>
.snow-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  pointer-events: none;
  z-index: 10;
  opacity: 0.9;
}

.snowflakes-svg {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

.snowflakes-group-1 {
  opacity: 0;
  animation: snowfall1 6.0s ease-out 0.5s infinite;
}

.snowflakes-group-2 {
  opacity: 0;
  animation: snowfall2 5.5s ease-out 1.2s infinite;
}

.snowflakes-group-3 {
  opacity: 0;
  animation: snowfall3 5.0s ease-out 0.8s infinite;
}

.snowflakes-group-4 {
  opacity: 0;
  animation: snowfall4 6.5s ease-out 1.5s infinite;
}

.snowflake {
  fill: rgba(255, 255, 255, 0.8);
}

@keyframes snowfall1 {
  0% { 
    opacity: 0;
    transform: translateY(-100px);
  }
  20% { 
    opacity: 1;
  }
  80% { 
    opacity: 0.8;
  }
  100% { 
    opacity: 0;
    transform: translateY(600px) translateX(50px);
  }
}

@keyframes snowfall2 {
  0% { 
    opacity: 0;
    transform: translateY(-100px);
  }
  25% { 
    opacity: 1;
  }
  75% { 
    opacity: 0.7;
  }
  100% { 
    opacity: 0;
    transform: translateY(600px) translateX(-30px);
  }
}

@keyframes snowfall3 {
  0% { 
    opacity: 0;
    transform: translateY(-100px);
  }
  30% { 
    opacity: 0.9;
  }
  70% { 
    opacity: 0.6;
  }
  100% { 
    opacity: 0;
    transform: translateY(600px) translateX(20px);
  }
}

@keyframes snowfall4 {
  0% { 
    opacity: 0;
    transform: translateY(-100px);
  }
  15% { 
    opacity: 0.8;
  }
  85% { 
    opacity: 0.5;
  }
  100% { 
    opacity: 0;
    transform: translateY(600px) translateX(-20px);
  }
}
</style>
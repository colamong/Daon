<!-- 메인화면 달력 옆에 일정 표시해줄카드 -->
<template>
  <router-link
    :to="`/schedule/${formattedDate}`"
    class="flex items-center w-full max-w-sm rounded-xl shadow border bg-white px-4 py-3 hover:shadow-md transition"
  >
    <!-- 컬러 사각형 -->
    <div
      class="w-12 h-12 rounded-md mr-4"
      :style="{ backgroundColor: cardColor }"
    ></div>

    <!-- 텍스트 영역 -->
    <div class="flex-1">
      <div class="text-lg font-paperBold text-gray-800">
        {{ formattedDate }}
      </div>
      <div class="text-sm font-paper text-gray-600">
        {{ title }}
      </div>
    </div>

    <!-- 화살표 -->
    <div class="text-gray-400 text-lg font-paperSemiBold">›</div>
  </router-link>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  date: String, // 'YYYY-MM-DD'
  title: String
})

// MM.DD 포맷으로 변환
const formattedDate = computed(() => {
  const d = new Date(props.date)
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${month}.${day}`
})

// 날짜를 기반으로 색 결정
const colorList = [
  '#FF8A80', '#FFB74D', '#FFD54F', '#81C784', '#4FC3F7', '#BA68C8', '#90A4AE', '#F06292',
  '#A1887F', '#7986CB', '#AED581', '#4DB6AC', '#FFF176', '#E57373', '#64B5F6', '#DCE775'
]
const cardColor = computed(() => {
  const day = new Date(props.date).getDate()
  return colorList[(day - 1) % colorList.length]
})
</script>

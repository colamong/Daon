<!-- 메인화면 사이트 기능소개 카드, OCR 장점 소개 카드 -->
<template>
  <component
    :is="(to || link) ? 'router-link' : 'div'"
    :to="to || link"
    @click="handleClick"
    class="w-64 rounded-2xl shadow-md p-6 block hover:shadow-lg transition-shadow cursor-pointer"
    :style="{ backgroundColor: cardStyle.bg }"
  >
    <div class="flex justify-center mb-4">
      <img
        :src="cardStyle.icon"
        :alt="cardStyle.title + ' 아이콘'"
        class="w-15 h-15"
      />
    </div>
    <h3 class="text-xl font-bold text-center mb-2 font-paper">
      {{ cardStyle.title }}
    </h3>
    <p class="text-sm text-center text-gray-700 font-paper">
      {{ cardStyle.description }}
    </p>
  </component>
</template>

<script setup>
import { computed } from "vue";

const props = defineProps({
  variant: {
    type: String,
    required: true,
  },
  link: {
    type: String,
    default: "",
  },
  to: {
    type: [String, Object],
    default: null,
  },
});

const emit = defineEmits(['click']);

function handleClick(event) {
  // router-link가 아닌 경우에만 클릭 이벤트 emit
  if (!props.to && !props.link) {
    emit('click', event);
  }
}

const cardStyle = computed(() => {
  const styles = {
    schedule: {
      title: "일정 관리",
      description: "아이의 중요한 일정을 체계적으로 관리하세요.",
      icon: new URL("@/assets/icons/calendar.svg", import.meta.url).href,
      bg: "#DDEBFF",
    },
    growth: {
      title: "성장 기록",
      description: "아이의 성장 과정을 상세히 기록하고 추적하세요.",
      icon: new URL("@/assets/icons/diary.svg", import.meta.url).href,
      bg: "#E4F5EA",
    },
    community: {
      title: "커뮤니티",
      description: "다른 부모들과 소통하고 정보를 나누세요.",
      icon: new URL("@/assets/icons/community.svg", import.meta.url).href,
      bg: "#FFECDC",
    },
    language: {
      title: "AI 도움",
      description: "OCR과 한국어 도움으로 편리함을 더하세요",
      icon: new URL("@/assets/icons/language.svg", import.meta.url).href,
      bg: "#FFE49C",
    },
    ocr1: {
      title: "빠른 처리",
      description: "AI 기반 OCR로 몇 초만에 텍스트 추출",
      icon: new URL("@/assets/icons/ocr1.svg", import.meta.url).href,
      bg: "#F2F5FD",
    },
    ocr2: {
      title: "다국어 지원",
      description: "다국어 텍스트도 빠르게 인식하세요.",
      icon: new URL("@/assets/icons/ocr2.svg", import.meta.url).href,
      bg: "#F2F5FD",
    },
    ocr3: {
      title: "실생활 밀착",
      description: "가정통신문, 공공기관 문서 등 실질적 활용",
      icon: new URL("@/assets/icons/ocr3.svg", import.meta.url).href,
      bg: "#F2F5FD",
    },
  };

  return styles[props.variant] || {};
});
</script>

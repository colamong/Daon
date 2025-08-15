<!-- 메인화면 사이트 기능소개 카드, OCR 장점 소개 카드 -->
<template>
  <component
    :is="to || link ? 'router-link' : 'div'"
    :to="to || link"
    @click="handleClick"
    class="w-full max-w-xs sm:max-w-sm md:max-w-md lg:w-64 rounded-xl sm:rounded-2xl shadow-md p-3 sm:p-4 md:p-6 block hover:shadow-xl hover:shadow-gray-600/60 transition-all duration-300 cursor-pointer transform hover:-translate-y-1"
    :style="{ backgroundColor: cardStyle.bg }"
  >
    <div class="flex justify-center mb-2 sm:mb-3 md:mb-4">
      <img
        :src="cardStyle.icon"
        :alt="cardStyle.title + ' 아이콘'"
        class="w-10 h-10 sm:w-12 sm:h-12 md:w-15 md:h-15"
      />
    </div>
    <h3 class="text-base sm:text-lg md:text-xl font-bold text-center mb-1 sm:mb-2 font-paper">
      {{ cardStyle.title }}
    </h3>
    <p class="text-xs sm:text-sm text-center text-gray-700 font-paper leading-relaxed">
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

const emit = defineEmits(["click"]);

function handleClick(event) {
  // router-link가 아닌 경우에만 클릭 이벤트 emit
  if (!props.to && !props.link) {
    emit("click", event);
  }
}

const cardStyle = computed(() => {
  const styles = {
    schedule: {
      title: "일정 관리",
      description: "중요한 일정을 체계적으로 관리",
      icon: new URL("@/assets/icons/calendar.svg", import.meta.url).href,
      bg: "#DDEBFF",
    },
    growth: {
      title: "성장 기록",
      description: "아이의 하루를 그림으로 기록",
      icon: new URL("@/assets/icons/diary.svg", import.meta.url).href,
      bg: "#E4F5EA",
    },
    community: {
      title: "커뮤니티",
      description: "다른 사람들과 소통하고 교류",
      icon: new URL("@/assets/icons/community.svg", import.meta.url).href,
      bg: "#FFECDC",
    },
    language: {
      title: "AI 도움",
      description: "문서 요약과 번역으로 편리함",
      icon: new URL("@/assets/icons/language.svg", import.meta.url).href,
      bg: "#FFE49C",
    },
    ocr1: {
      title: "빠른 처리",
      description: "AI 기반 빠른 텍스트 추출",
      icon: new URL("@/assets/icons/ocr1.svg", import.meta.url).href,
      bg: "#F2F5FD",
    },
    ocr2: {
      title: "다국어 지원",
      description: "다양한 언어에서 사용 가능",
      icon: new URL("@/assets/icons/ocr2.svg", import.meta.url).href,
      bg: "#F2F5FD",
    },
    ocr3: {
      title: "실생활 밀착",
      description: "가정통신문, 공문서 등 실생활에 도움",
      icon: new URL("@/assets/icons/ocr3.svg", import.meta.url).href,
      bg: "#F2F5FD",
    },
  };

  return styles[props.variant] || {};
});
</script>

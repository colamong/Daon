<!-- src/views/Dashboard.vue -->
<template>
  <div></div>
</template>

<script setup>
import { ref } from "vue";
import CalendarWidget from "../components/widget/CalendarWidget.vue";
import ScheduleList from "../components/widget/ScheduleList.vue";
// import FeatureCard from "../components/card/FeatureCard.vue";

const events = ref([
  { date: "2025-08-06", title: "유치원 운동회" },
  { date: "2025-08-06", title: "저거 뭐냐 무시짱이" },
  { date: "2025-08-18", title: "너무 졸려요" },
  { date: "2025-08-22", title: "잠에 들고 싶어요" },
]);

const todayDrawing = ref({
  imgUrl: "/drawings/seaside.png",
  profile: "/profiles/child1.png",
  name: "김민준",
  age: "5세 (만 4세)",
  interests: "하추핑, 태권도 ...",
});

function addEvent() {
  // 더미: alert
  alert("일정 추가 모달을 구현해주세요.");
}
</script>

<style scoped>
/* 필요 시 추가 스타일 삽입 */
</style>

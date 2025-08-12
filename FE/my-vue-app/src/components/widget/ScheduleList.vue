<!-- ScheduleList.vue -->
<template>
  <div class="space-y-4">
    <ScheduleCard
      v-for="(schedule, index) in schedules"
      :key="index"
      :date="schedule.date"
      :title="schedule.title"
    />
  </div>
</template>

<script setup>
import ScheduleCard from '@/components/card/ScheduleCard.vue'

const props = defineProps({
  schedules: {
    type: Array,
    required: true
  }
})
</script>

<style scoped>
/* 필요 시 스타일 작성 */
</style>

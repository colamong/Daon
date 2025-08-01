<template>
  <div class="bg-transition-blue/20 px-6 py-4 rounded-2xl w-full">
    <p class="text-base font-paperBold text-black">
      {{ question }}
    </p>
  </div>
</template>

<script setup>
const props = defineProps({
  question: {
    type: String,
    required: true
  }
})
</script>

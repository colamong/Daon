<!-- src/components/layout/BaseAppLayout.vue -->
<template>
  <div class="min-h-screen flex flex-col bg-white">
    <Header />
    <main class="flex-grow">
      <router-view />
    </main>
    <Footer />
  </div>
</template>

<script setup>
import Header from '@/components/layout/Header.vue'
import Footer from '@/components/layout/Footer.vue'
</script>

<style scoped>
/* 전역 스타일 필요 시 */
</style>

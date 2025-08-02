<template>
  <section
    class="relative w-full h-screen bg-cover bg-center"
    :style="{ backgroundImage: `url(${bgImage})` }"
  >
    <!-- 어두운 오버레이 -->
    <div
      class="absolute inset-0 w-[1200px] h-96 opacity-80 bg-white/60 backdrop-blur-[30px]"
    >
      <div
        class="relative z-10 container opacity-100 mx-auto px-6 md:px-12 h-full flex flex-col md:flex-row items-center justify-center w-1200"
      >
        <!-- 왼쪽 텍스트 영역 -->
        <div class="text-black max-w-xl space-y-6 font-paper">
          <h1 class="text-4xl md:text-5xl font-bold">
            온 마음을 잇는 다리,<br />
            <span class="text-blue-700">다온(DA:ON)</span>
          </h1>
          <p class="text-xl">
            다온은 다문화 가정의 모든 순간에 따뜻하게 다가갑니다.<br />
            언제나 곁에서 든든하게,<br />다온이 여러분의 이야기를 기다리고
            있어요.
          </p>
        </div>
        <div></div>

        <!-- 오른쪽 버튼 영역 -->
        <div class="mt-8 md:mt-0 md:ml-20 flex space-x-4">
          <router-link
            to="/register"
            class="px-6 py-3 bg-blue-600 rounded-lg text-white font-medium hover:bg-blue-700 transition font-paper"
          >
            회원가입
          </router-link>
          <router-link
            to="/login"
            class="px-6 py-3 border-2 border-blue-600 rounded-lg text-blue-600 font-medium hover:bg-blue-50 transition font-paper"
          >
            로그인
          </router-link>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import bgImage from "@/assets/images/landing-bg.png"; // Figma 배경 이미지 경로
</script>

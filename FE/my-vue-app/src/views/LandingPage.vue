<template>
  <!-- 우측 하단 배경 이미지 (section 밖에 별도 배치) -->
  <div class="fixed bottom-0 right-10" style="z-index: 10 !important">
    <img
      :src="landingImg"
      alt="랜딩 페이지 배경 이미지"
      class="opacity-90 w-64 h-auto"
    />
  </div>

  <section class="relative w-full h-screen bg-blue-50 overflow-hidden">
    <!-- 그라데이션 배경 (전체 화면) -->
    <div
      class="w-[1920px] h-[1080px] left-[-2.15px] top-0 absolute bg-gradient-to-b from-blue-400/50 via-blue-100/50 to-blue-50/50 backdrop-blur-[1.50px]"
    ></div>

    <!-- 기존 어두운 오버레이 (흰색 콘텐츠 박스) -->
    <div
      class="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 w-[1000px] h-96 opacity-90 bg-white/30 backdrop-blur-[10px] rounded-2xl"
      style="z-index: 20 !important"
    >
      <div
        class="relative z-10 container mx-auto px-6 md:px-12 h-full flex flex-col md:flex-row items-center justify-center w-1200"
        style="opacity: 1 !important"
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
            :to="{ name: 'signup' }"
            class="px-6 py-3 bg-blue-600 rounded-lg text-white font-medium hover:bg-blue-700 transition font-paper"
          >
            회원가입
          </router-link>
          <router-link
            :to="{ name: 'login' }"
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
import landingImg from "@/assets/images/landing_img.png"; // 우측 하단 배경 이미지
</script>

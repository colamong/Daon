<template>
  <header class="w-full h-28 bg-background-header flex items-center justify-between px-8">
    <!-- 왼쪽: 로고 -->
    <div class="flex items-center gap-4">
      <div class="w-[63px] h-[63px] bg-[#7C7C7C] rounded-full"></div>
      <div class="text-2xl font-paperSemi text-[#526049]">다온(DA:ON)</div>
    </div>

    <!-- 가운데: 네비게이션 -->
    <nav class="flex gap-8">
      <span class="text-xl font-paper text-black text-center">펭구랑 놀자</span>
      <span class="text-xl font-paper text-black text-center">아이 프로필</span>
      <span class="text-xl font-paper text-black text-center">문서 도우미</span>
      <span class="text-xl font-paper text-black text-center">온동네</span>
      <span class="text-xl font-paper text-black text-center">상황별 학습</span>
    </nav>

    <!-- 오른쪽: 유저 정보 -->
    <div class="flex items-center gap-4">
      <img src="https://placehold.co/53x53" alt="프로필" class="w-[53px] h-[53px]" />
      <span class="text-xl font-paper text-black">응우옌 반 안</span>
      <div class="w-[45px] h-[45px] bg-white rounded-full shadow-[2px_2px_10px_rgba(0,0,0,0.2)] flex items-center justify-center">
        <img src="@/assets/images/logout-icon.svg" alt="logout" class="w-5 h-5" />
      </div>
    </div>

  </header>
</template>

<script setup>
// 향후 props, emits 추가 가능
</script>

<style scoped>
/* 커스텀 스타일 필요 시 여기에 작성 */
</style>

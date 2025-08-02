<template>
  <footer class="w-full bg-footer px-8 py-12 text-black font-paper">
    <div class="max-w-screen-xl mx-auto flex flex-col md:flex-row justify-between items-start gap-x-24 gap-y-10">

      <!-- 왼쪽: 브랜드 정보 -->
      <div class="flex flex-col gap-5 w-full md:w-[200px]">
        <div class="text-xl font-bold">다온(DA:ON)</div>
        <p class="text-sm leading-snug">
          다문화 가정이 한국 사회에 잘 적응할 수 있도록<br />
          돕는 AI 기반 생활 지원 플랫폼 다온입니다.
        </p>
        <div class="flex gap-3 mt-2">
          <img src="@/assets/images/facebook.png" alt="Facebook" class="w-7 h-7 cursor-pointer" />
          <img src="@/assets/images/twitter.png" alt="Twitter" class="w-7 h-7 cursor-pointer" />
          <img src="@/assets/images/instagram.png" alt="Instagram" class="w-7 h-7 cursor-pointer" />
        </div>
      </div>  

      <!-- 서비스 -->
      <div class="flex flex-col gap-3 w-full md:w-[160px]">
        <div class="text-base font-semibold">서비스</div>
        <ul class="text-sm leading-snug space-y-1">
          <li><a href="#">펭구랑 놀자</a></li>
          <li><a href="#">아이 프로필</a></li>
          <li><a href="#">문서 도우미</a></li>
          <li><a href="#">온동네</a></li>
          <li><a href="#">상황별 학습</a></li>
        </ul>
      </div>

      <!-- 고객지원 -->
      <div class="flex flex-col gap-3 w-full md:w-[160px]">
        <div class="text-base font-semibold">고객지원</div>
        <ul class="text-sm leading-snug space-y-1">
          <li><a href="#">자주 묻는 질문</a></li>
          <li><a href="#">이용 가이드</a></li>
          <li><a href="#">공지사항</a></li>
          <li><a href="#">문의하기</a></li>
        </ul>
      </div>

      <!-- 연락처 -->
      <div class="flex flex-col gap-3 w-full md:w-[200px]">
        <div class="text-base font-semibold">연락처</div>
        <ul class="text-sm leading-snug space-y-2">
          <li class="flex items-center gap-2">
            <img src="@/assets/images/location.png" alt="주소" class="w-4 h-4" />
            <span>서울시 강남구 테헤란로</span>
          </li>
          <li class="flex items-center gap-2">
            <img src="@/assets/images/letter.png" alt="이메일" class="w-4 h-4" />
            <span>support@daon.co.kr</span>
          </li>
          <li class="flex items-center gap-2">
            <img src="@/assets/images/phone.png" alt="전화" class="w-4 h-4" />
            <span>1588-0706</span>
          </li>
        </ul>
      </div>

      <!-- 오른쪽: 일러스트 + 저작권 -->
      <div class="hidden md:flex flex-col items-end w-[260px]">
        <img
        src="@/assets/images/footer-family.png" 
        alt="가족 일러스트"
        class="w-[280px] h-auto object-contain"
        />
      <div class="mt-1 text-xs text-gray-500 font-medium flex items-center gap-1">
    <span>© 2025 다온 All right reserved</span>
  </div>
</div>

    </div>
  </footer>
</template>

<script setup>
// No script needed
</script>

<style scoped>
/* 여기에 필요 시 추가 커스텀 가능 */
</style>

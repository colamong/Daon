<template>
  <component
    :is="link ? 'router-link' : 'button'"
    :to="link"
    :class="[
      // 둥근모서리 호버시 색상 전환 부드럽게
      'rounded-lg transition-colors duration-200',
      // retry는 굵은폰트에 그림자, 나머지는 기본
      variant === 'retry' ? 'font-paperBold px-6 py-3 shadow' : 'font-paper px-4 py-2',
      variant === 'myprofile' // 내 프로필
        ? 'bg-hover-purple text-white'
        : variant === 'comunity' // 커뮤니티 핫한순 최신순 버튼
        ? 'bg-box text-black hover:bg-hover-blue hover:text-white'
        : variant === 'chat' // 채팅 전송 버튼
        ? 'bg-background-footer text-black'
        : variant === 'kidsprofile' // 아이 프로필 관련 버튼
        ? 'bg-purpleLight text-black'
        : variant === 'retry' // 한국어 공부 다시시도 버튼
        ? 'bg-box text-black'
        : 'bg-background-footer text-black hover:bg-hover-blue hover:text-white' // 기본 파란 배경에 검은글씨
    ]"
  >
    <slot />
  </component>
</template>

<script setup>
defineProps({
  variant: {
    type: String,
    default: 'default',
  },
  link: {
    type: String,
    default: '',
  },
})
</script>

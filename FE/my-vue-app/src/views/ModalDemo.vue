<!-- src/views/ModalDemo.vue -->
<template>
  <div class="p-8">
    <button
      @click="show = true"
      class="px-4 py-2 bg-blue-500 text-white rounded"
    >
      모달 열기
    </button>

    <!-- BaseModal을 여기서 바로 써 봅니다 -->
    <BaseModal v-model="show">
      <template #header> 데모용 모달 </template>

      <div class="space-y-2">
        <p>이곳이 모달의 본문 영역입니다.</p>
        <p>
          원하는 내용을 마음껏 넣어서 확인해 보세요.<br /><br /><br /><br /><br /><br /><br /><br /><br />
          dfdasfsdfddfd
        </p>
      </div>
    </BaseModal>
  </div>
</template>

<script setup>
import { ref } from "vue";
import BaseModal from "@/components/modal/BaseModal.vue";

const show = ref(false);
</script>

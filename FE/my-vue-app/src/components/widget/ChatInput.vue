<template>
  <div class="flex gap-2">
    <input
      v-model="inputText"
      @keydown.enter.prevent="handleEnter"
      @compositionstart="isComposing = true"
      @compositionend="isComposing = false"
      placeholder="메세지를 입력하세요."
      class="flex-1 p-2 border rounded-lg dark:bg-zinc-700 dark:text-white dark:border-zinc-600 text-sm font-paper"
      id="chatInput"
      type="text"
    />
    <button
      @click="emitSend"
      class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-1.5 px-5 rounded-lg transition duration-300 ease-in-out text-sm !font-paper"
      id="sendButton"
    >
      전송
    </button>
  </div>
</template>

<script setup>
import { ref, watch } from "vue";

const emit = defineEmits(["update:modelValue", "send"]);
const props = defineProps({
  modelValue: String,
});

const inputText = ref(props.modelValue);

watch(
  () => props.modelValue,
  (val) => {
    inputText.value = val;
  }
);

watch(inputText, (val) => {
  emit("update:modelValue", val);
});

let lastSendTime = 0;
const isComposing = ref(false);

function handleEnter() {
  if (isComposing.value) {
    // 한글 조합 중이면 잠시 대기
    setTimeout(() => {
      emitSend();
    }, 10);
  } else {
    emitSend();
  }
}

function emitSend() {
  const message = inputText.value.trim();
  if (!message) return;
  
  const now = Date.now();
  if (now - lastSendTime < 150) {
    return; // 150ms 내 중복 전송 차단
  }
  
  lastSendTime = now;
  
  // 전송하고 나서 입력창 비우기
  emit("send", message);
  inputText.value = "";
}
</script>

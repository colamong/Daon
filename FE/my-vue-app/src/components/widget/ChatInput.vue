<template>
  <div class="flex gap-2">
    <input
      v-model="inputText"
      @keydown.enter.prevent="emitSend"
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

const isSending = ref(false);

function emitSend() {
  if (!inputText.value.trim() || isSending.value) return;
  
  isSending.value = true;
  const message = inputText.value;
  inputText.value = "";
  
  emit("send", message);
  
  // 짧은 지연 후 플래그 해제
  setTimeout(() => {
    isSending.value = false;
  }, 100);
}
</script>

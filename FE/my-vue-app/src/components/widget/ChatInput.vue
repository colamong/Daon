<template>
  <div class="flex items-center gap-2 p-4 border-t">
    <input
      v-model="inputText"
      @keydown.enter="emitSend"
      type="text"
      placeholder="채팅을 입력해주세요"
      class="flex-1 border border-black rounded-xl px-4 py-2 outline-none font-paper"
    />
    <button
      @click="emitSend"
      class="bg-[#F2F5FD] px-4 py-2 rounded-xl text-sm font-paper"
    >
      전송
    </button>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'

const emit = defineEmits(['update:modelValue', 'send'])
const props = defineProps({
  modelValue: String,
})

const inputText = ref(props.modelValue)

watch(() => props.modelValue, (val) => {
  inputText.value = val
})

watch(inputText, (val) => {
  emit('update:modelValue', val)
})

function emitSend() {
  if (!inputText.value.trim()) return
  emit('send', inputText.value)
  inputText.value = ''
}
</script>

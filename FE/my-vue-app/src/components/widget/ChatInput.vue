<template>
  <div class="flex gap-2">
    <input
      v-model="inputText"
      @keydown.enter="emitSend"
      placeholder="Type your message..."
      class="flex-1 p-2 border rounded-lg dark:bg-zinc-700 dark:text-white dark:border-zinc-600 text-sm font-paper"
      id="chatInput"
      type="text"
    />
    <button
      @click="emitSend"
      class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-1.5 px-3 rounded-lg transition duration-300 ease-in-out text-sm font-paper"
      id="sendButton"
    >
      Send
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

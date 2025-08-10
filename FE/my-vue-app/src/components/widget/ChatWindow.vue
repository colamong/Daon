<template>
  <div
    class="flex flex-col w-full !h-[510px] border rounded-2xl overflow-hidden shadow-lg bg-white"
  >
    <!-- 메시지 영역 -->
    <div ref="messageContainer" class="flex-1 overflow-y-auto p-4 space-y-2">
      <ChatBubble
        v-for="msg in messages"
        :key="msg.id"
        :isMine="msg.userId === currentUserId"
        :message="msg.message"
        :timestamp="msg.sentAt"
      />
    </div>

    <!-- 입력창 -->
    <ChatInput v-model="newMessage" @send="handleSend" />
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted, watch } from "vue";
import ChatBubble from "./ChatBubble.vue";
import ChatInput from "./ChatInput.vue";

// Props 정의
const props = defineProps({
  messages: {
    type: Array,
    default: () => []
  },
  currentUserId: {
    type: Number,
    required: true
  }
});

// Emit 정의
const emit = defineEmits(['sendMessage']);

const newMessage = ref("");
const messageContainer = ref(null);

// 메시지 전송 핸들러
function handleSend(text) {
  emit('sendMessage', text);
  newMessage.value = "";
}

// 스크롤을 맨 아래로 이동
function scrollToBottom() {
  nextTick(() => {
    const el = messageContainer.value;
    if (el) {
      el.scrollTop = el.scrollHeight;
    }
  });
}

// 메시지가 변경될 때마다 스크롤을 맨 아래로 이동
watch(() => props.messages, () => {
  scrollToBottom();
}, { deep: true });

onMounted(() => {
  scrollToBottom();
});
</script>

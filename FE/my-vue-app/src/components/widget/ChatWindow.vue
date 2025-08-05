<template>
  <div
    class="flex flex-col w-full !h-[510px] border rounded-2xl overflow-hidden shadow-lg bg-white"
  >
    <!-- 메시지 영역 -->
    <div ref="messageContainer" class="flex-1 overflow-y-auto p-4 space-y-2">
      <ChatBubble
        v-for="msg in messages"
        :key="msg.id"
        :isMine="msg.isMine"
        :message="msg.text"
        :timestamp="msg.time"
      />
    </div>

    <!-- 입력창 -->
    <ChatInput v-model="newMessage" @send="handleSend" />
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted } from "vue";
import ChatBubble from "./ChatBubble.vue";
import ChatInput from "./ChatInput.vue";

const newMessage = ref("");
const messageContainer = ref(null);

const messages = ref([
  { id: 1, text: "오늘 마트 쉰대요", isMine: false, time: "오전 10:00" },
  {
    id: 2,
    text: "그러면 오늘 시장 가야겠네요",
    isMine: true,
    time: "오전 10:01",
  },
  { id: 3, text: "내일 장 봐야겠어요", isMine: false, time: "오전 10:02" },
  { id: 4, text: "다들 그거 들으셨어요?", isMine: false, time: "오전 10:03" },
]);

function handleSend(text) {
  messages.value.push({
    id: Date.now(),
    text,
    isMine: true,
    time: "오전 10:10",
  });

  scrollToBottom();
}

function scrollToBottom() {
  nextTick(() => {
    const el = messageContainer.value;
    if (el) {
      el.scrollTop = el.scrollHeight;
    }
  });
}

onMounted(() => {
  scrollToBottom();
});
</script>

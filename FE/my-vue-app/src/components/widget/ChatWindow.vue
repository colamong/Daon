<template>
  <div
    class="w-full bg-white dark:bg-zinc-800 shadow-md rounded-lg overflow-hidden"
  >
    <div class="flex flex-col h-[600px]">
      <div class="px-4 py-3 border-b dark:border-zinc-700">
        <div class="flex justify-between items-center">
          <div class="flex items-center space-x-3">
            <img
              v-if="roomImage"
              :src="roomImage"
              :alt="roomTitle"
              class="w-8 h-8 rounded-full object-cover border border-gray-200 ml-2"
            />
            <h2 class="text-lg font-semibold text-zinc-800 dark:text-white font-paper">
              {{ roomTitle }}
            </h2>
          </div>
          <div class="flex items-center space-x-2">
            <button
              v-if="!showChatList"
              @click="handleGoToList"
              class="bg-gray-500 hover:bg-gray-600 text-white text-xs px-3 py-1.5 rounded-lg transition-colors font-paper"
            >
              전체 목록
            </button>
            <button
              v-if="showLeaveButton"
              @click="handleLeave"
              class="bg-red-500 hover:bg-red-600 text-white text-xs px-3 py-1.5 rounded-lg transition-colors font-paper"
            >
              나가기
            </button>
            <button
              @click="handleToggleList"
              class="bg-blue-500 hover:bg-blue-600 text-white p-2 rounded-lg transition-colors flex items-center justify-center"
              title="목록"
            >
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M3 6H21" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M3 12H21" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M3 18H21" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </button>
          </div>
        </div>
      </div>
      <div
        ref="messageContainer"
        class="flex-1 overflow-y-auto bg-white px-4 py-3"
        id="chatDisplay"
      >
        <div class="bg-blue-50 rounded-lg p-4 min-h-full">
          <div class="flex flex-col space-y-2">
            <ChatBubble
              v-for="(msg, index) in messages"
              :key="msg.id"
              :isMine="msg.userId === currentUserId"
              :message="msg.message"
              :timestamp="msg.sentAt"
              :userName="msg.userName"
              :userProfileImg="msg.userProfileImg"
              :isAlternate="index % 2 === 1"
            />
          </div>
        </div>
      </div>
      <div class="px-4 py-2 border-t dark:border-zinc-700">
        <ChatInput v-model="newMessage" @send="handleSend" />
      </div>
    </div>
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
  },
  roomTitle: {
    type: String,
    default: 'Chatbot Assistant'
  },
  roomImage: {
    type: String,
    default: null
  },
  showLeaveButton: {
    type: Boolean,
    default: false
  },
  showChatList: {
    type: Boolean,
    default: false
  }
});

// Emit 정의
const emit = defineEmits(['sendMessage', 'leave', 'toggleList', 'goToList']);

const newMessage = ref("");
const messageContainer = ref(null);

// 메시지 전송 핸들러
function handleSend(text) {
  emit('sendMessage', text);
  newMessage.value = "";
}

// 나가기 핸들러
function handleLeave() {
  emit('leave');
}

// 목록 토글 핸들러
function handleToggleList() {
  emit('toggleList');
}

// 전체 목록으로 이동 핸들러
function handleGoToList() {
  emit('goToList');
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

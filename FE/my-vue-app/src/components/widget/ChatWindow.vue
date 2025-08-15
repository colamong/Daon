<template>
  <div
    class="w-full bg-white dark:bg-zinc-800 shadow-md rounded-lg overflow-hidden"
  >
    <div class="flex flex-col h-[600px]">
      <div class="px-4 py-3 border-b dark:border-zinc-700">
        <div class="flex justify-between items-center">
          <div class="flex items-center space-x-3 flex-1 min-w-0">
            <img
              v-if="roomImage"
              :src="roomImage"
              :alt="roomTitle"
              class="w-8 h-8 rounded-full object-cover border border-gray-200 ml-2 flex-shrink-0"
            />
            <div class="min-w-0 flex-1">
              <h2 class="text-sm font-semibold text-zinc-800 dark:text-white font-paper leading-tight">
                {{ roomTitle }}
              </h2>
            </div>
          </div>
          <div class="flex items-center space-x-1 flex-shrink-0">
            <button
              v-if="!showChatList"
              @click="handleGoToList"
              class="bg-gray-500 hover:bg-gray-600 text-white text-xs px-2 py-1 rounded transition-colors font-paper"
            >
              전체목록
            </button>
            <button
              v-if="showLeaveButton"
              @click="handleLeave"
              class="relative flex items-center justify-start w-[32px] h-[32px] bg-white rounded-full shadow-[2px_2px_10px_rgba(0,0,0,0.2)] overflow-hidden transition-[width] duration-300 ease-in-out hover:w-[80px] hover:bg-red-500 hover:rounded-[32px] active:translate-x-[1px] active:translate-y-[1px]"
            >
              <!-- 아이콘 영역 -->
              <div
                class="flex items-center justify-center w-full transition-all duration-300 ease-in-out hover:w-[30%] hover:pl-3"
              >
                <svg
                  class="w-[12px] fill-current text-red-500 hover:text-white"
                  viewBox="0 0 512 512"
                >
                  <path
                    d="M377.9 105.9L500.7 228.7c7.2 7.2 11.3 17.1 11.3 27.3s-4.1 20.1-11.3 27.3L377.9 406.1c-6.4 6.4-15 9.9-24 9.9c-18.7 0-33.9-15.2-33.9-33.9l0-62.1-128 0c-17.7 0-32-14.3-32-32l0-64c0-17.7 14.3-32 32-32l128 0 0-62.1c0-18.7 15.2-33.9 33.9-33.9c9 0 17.6 3.6 24 9.9zM160 96L96 96c-17.7 0-32 14.3-32 32l0 256c0 17.7 14.3 32 32 32l64 0c17.7 0 32 14.3 32 32s-14.3 32-32 32l-64 0c-53 0-96-43-96-96L0 128C0 75 43 32 96 32l64 0c17.7 0 32 14.3 32 32s-14.3 32-32 32z"
                  />
                </svg>
              </div>
              <!-- 텍스트 영역 -->
              <span
                class="absolute right-0 w-0 opacity-0 text-white text-[0.8em] font-paper transition-[width,opacity] duration-300 ease-in-out hover:opacity-100 hover:w-[70%] hover:pr-2"
              >
                나가기
              </span>
            </button>
            <button
              @click="handleToggleList"
              class="bg-blue-500 hover:bg-blue-600 text-white p-1.5 rounded transition-colors flex items-center justify-center"
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
              :messageType="msg.messageType"
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

<template>
  <div class="max-w-4xl mx-auto px-4 py-2 font-paper mb-10">
    <div class="flex items-start space-x-6">
      <!-- 왼쪽: Header + ChatWindow -->
      <div class="flex-1 space-y-6">
        <!-- Header -->
        <div
          v-if="currentRoom"
          class="flex justify-between items-center p-4 bg-white rounded-lg shadow"
        >
          <h1 class="text-2xl font-bold">{{ currentRoom.location }}</h1>
          <div class="flex items-center space-x-3">
            <button
              v-if="currentRoom.userJoined"
              @click="showLeaveConfirm(currentRoom)"
              class="bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded-lg transition-colors text-sm font-medium"
            >
              나가기
            </button>
          </div>
        </div>

        <!-- ChatWindow -->
        <ChatWindow
          class="w-full"
          :messages="chatMessages"
          :currentUserId="authStore.user?.id || 0"
          @sendMessage="handleSendMessage"
        />
      </div>

      <!-- 오른쪽: 참가중인 채팅방 목록 -->
      <div
        class="w-80 h-[600px] px-6 py-4 bg-gradient-to-b from-blue-400 via-indigo-300 to-white rounded-lg shadow-[0_4px_4px_0_rgba(0,0,0,0.25)] inline-flex flex-col justify-start items-start overflow-y-auto"
      >
        <div class="self-stretch flex justify-between items-center mb-2">
          <div class="text-white text-2xl font-paperSemi">
            나의 채팅방 목록
          </div>
          <button
            @click="$router.push('/dashboard/community')"
            class="text-white text-sm bg-white/20 hover:bg-white/30 px-3 py-1 rounded-lg transition-colors"
          >
            목록
          </button>
        </div>
        <div class="flex-1 overflow-y-auto space-y-4 pr-2">
          <ChatListCard
            v-for="room in joinedRooms"
            :key="room.id"
            :location="room.location"
            :image="room.image"
            :link="`/dashboard/community/${room.id}`"
            class="mb-3"
          />
        </div>
      </div>
    </div>

    <!-- 나가기 확인 모달 -->
    <ConfirmModal
      v-model="showConfirmModal"
      :title="`'${selectedRoom?.location}' 채팅방에서 나가시겠습니까?`"
      message="나가시면 채팅방 목록에서 사라지며, 다시 참가하려면 새로 입장해야 합니다."
      confirm-text="나가기"
      cancel-text="취소"
      @confirm="confirmLeave"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import ChatWindow from "@/components/widget/ChatWindow.vue";
import ChatListCard from "@/components/card/ChatListCard.vue";
import ConfirmModal from "@/components/modal/ConfirmModal.vue";
import { useCommunityStore } from "@/store/community.js";
import { useAuthStore } from "@/store/auth.js";
import websocketService from "@/services/websocketService.js";
import { communityService } from "@/services/communityService.js";

// 라우터 & 스토어
const route = useRoute();
const $router = useRouter();
const communityStore = useCommunityStore();
const authStore = useAuthStore();

// 상태
const currentCommunity = ref(null);
const chatMessages = ref([]);
const isConnected = ref(false);

// 현재 방 정보
const currentRoom = computed(() => {
  if (!currentCommunity.value) return null;
  return {
    id: currentCommunity.value.id,
    location: currentCommunity.value.title,
    userJoined: true, // 임시: 모두 나가기 버튼 표시
  };
});

// 참가 중인 방 목록
const joinedRooms = computed(() => {
  return communityStore.joinedCommunities.map((community) => ({
    id: community.id,
    location: community.title,
    image: "", // 기본 이미지 사용
  }));
});

// 모달 상태
const showConfirmModal = ref(false);
const selectedRoom = ref(null);

// 나가기 모달 열기
const showLeaveConfirm = (room) => {
  selectedRoom.value = room;
  showConfirmModal.value = true;
};

// 나가기 확정
const confirmLeave = () => {
  if (selectedRoom.value) {
    leaveRoom(selectedRoom.value.id);
    selectedRoom.value = null;
  }
};

// 채팅방 나가기
const leaveRoom = async (roomId) => {
  try {
    await communityStore.leaveCommunity(roomId, authStore.user.id);
    if (currentRoom.value && currentRoom.value.id === roomId) {
      $router.push("/dashboard/community");
    }
  } catch (error) {
    console.error("채팅방 나가기 실패:", error);
    alert("채팅방 나가기에 실패했습니다.");
  }
};

// 메시지 전송(WebSocket)
const handleSendMessage = async (message) => {
  if (!currentCommunity.value || !authStore.user?.id) {
    console.error("커뮤니티 또는 사용자 정보가 없습니다.");
    return;
  }

  try {
    if (isConnected.value) {
      websocketService.sendMessage(
        currentCommunity.value.id,
        message,
        authStore.user.id
      );
    } else {
      console.error("WebSocket이 연결되지 않았습니다.");
      alert("연결이 끊어졌습니다. 페이지를 새로고침해주세요.");
    }
  } catch (error) {
    console.error("메시지 전송 실패:", error);
    alert("메시지 전송에 실패했습니다. 다시 시도해주세요.");
  }
};

// 메시지 히스토리 로드 (초기 로딩용)
const loadMessages = async () => {
  if (!currentCommunity.value) return;
  try {
    const messages = await communityService.getMessages(currentCommunity.value.id);
    chatMessages.value = messages || [];
    // WebSocket 서비스에도 메시지 설정
    websocketService.setMessages(messages || []);
    console.log("메시지 히스토리 로드 완료:", chatMessages.value.length, "개");
  } catch (error) {
    console.error("메시지 목록 로드 실패:", error);
  }
};

// WebSocket 연결 및 구독
const connectWebSocket = async () => {
  try {
    await websocketService.connect();
    isConnected.value = true;
    
    // 연결 상태 모니터링
    websocketService.onConnect((connected) => {
      isConnected.value = connected;
    });
    
    // 새 메시지 수신 처리
    websocketService.onMessage((newMessage) => {
      // 중복 체크
      const exists = chatMessages.value.some(msg => msg.id === newMessage.id);
      if (!exists) {
        chatMessages.value.push({
          id: newMessage.id,
          message: newMessage.message,
          userId: newMessage.userId,
          userName: newMessage.userName,
          sentAt: newMessage.sentAt
        });
      }
    });
    
    console.log("WebSocket 연결 완료");
  } catch (error) {
    console.error("WebSocket 연결 실패:", error);
    isConnected.value = false;
  }
};

const disconnectWebSocket = () => {
  websocketService.disconnect();
  isConnected.value = false;
};

// 초기 데이터 로드
const loadCommunityData = async () => {
  const communityId = Number(route.params.id);
  if (!communityId) {
    $router.push("/dashboard/community");
    return;
  }

  try {
    // 커뮤니티 목록/현재 커뮤니티
    await communityStore.fetchAllCommunities();
    currentCommunity.value = communityStore.communities.find(
      (c) => c.id === communityId
    );

    if (!currentCommunity.value) {
      console.error("해당 커뮤니티를 찾을 수 없습니다.");
      $router.push("/dashboard/community");
      return;
    }

    // 참여 목록 먼저 → 필요 시 join
    await communityStore.fetchJoinedCommunities(authStore.user.id);
    if (!communityStore.isJoined(communityId)) {
      await communityStore.joinCommunity(communityId, authStore.user.id);
      await communityStore.fetchJoinedCommunities(authStore.user.id);
    }

    // WebSocket 연결 및 채팅방 구독
    await connectWebSocket();
    
    // 히스토리 로드
    chatMessages.value = [];
    await loadMessages();
    
    // 현재 커뮤니티 구독
    websocketService.subscribeToCommunity(communityId);
    console.log(`커뮤니티 ${communityId} 구독 완료`);
  } catch (error) {
    console.error("채팅방 로드 실패:", error);
    alert("채팅방을 불러오는데 실패했습니다.");
    $router.push("/dashboard/community");
  }
};

onMounted(() => {
  loadCommunityData();
});

onBeforeUnmount(() => {
  disconnectWebSocket();
});

// 라우트 변경 시 재로딩 + WebSocket 재구독
watch(
  () => route.params.id,
  async (newId, oldId) => {
    if (newId && newId !== oldId) {
      // 방 이동 시 WebSocket 리셋
      chatMessages.value = [];
      await loadCommunityData();
    }
  }
);
</script>

<style scoped>
/* 추가 스타일 필요 시 여기에 */
</style>

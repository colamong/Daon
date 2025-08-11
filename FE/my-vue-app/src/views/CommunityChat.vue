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
import axiosInstance from "@/utils/axios.js";

// 라우터 & 스토어
const route = useRoute();
const $router = useRouter();
const communityStore = useCommunityStore();
const authStore = useAuthStore();

// 상태
const currentCommunity = ref(null);
const chatMessages = ref([]);

// 폴링 타이머 & 중복 방지
let pollTimer = null;
const knownIds = new Set();

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

// 메시지 전송(REST)
const handleSendMessage = async (message) => {
  if (!currentCommunity.value || !authStore.user?.id) {
    console.error("커뮤니티 또는 사용자 정보가 없습니다.");
    return;
  }

  try {
    const requestDto = {
      userId: authStore.user.id,
      message,
    };
    await axiosInstance.post(
      `/api/community/${currentCommunity.value.id}/messages`,
      requestDto
    );
    // 브로드캐스트가 없다면 폴링 간격 전에 반영하려면 즉시 한번 갱신
    await loadMessages();
  } catch (error) {
    console.error("메시지 전송 실패:", error);
    alert("메시지 전송에 실패했습니다. 다시 시도해주세요.");
  }
};

// 메시지 목록 조회(REST) - 새로운 것만 append (깜빡임/스크롤 튐 방지)
const loadMessages = async () => {
  if (!currentCommunity.value) return;
  try {
    const res = await axiosInstance.get(
      `/api/community/${currentCommunity.value.id}/messages`
    );
    const list = res.data.data || [];

    if (chatMessages.value.length === 0) {
      chatMessages.value = list;
      knownIds.clear();
      list.forEach((m) => knownIds.add(m.id));
    } else {
      for (const m of list) {
        if (!knownIds.has(m.id)) {
          chatMessages.value.push(m);
          knownIds.add(m.id);
        }
      }
    }
    // console.log("메시지 목록 로드 완료:", chatMessages.value.length, "개");
  } catch (error) {
    console.error("메시지 목록 로드 실패:", error);
  }
};

// 폴링 제어
const startPolling = (intervalMs = 2000) => {
  stopPolling();
  pollTimer = setInterval(async () => {
    try {
      await loadMessages();
    } catch (e) {
      console.error("폴링 중 오류:", e);
    }
  }, intervalMs);
  document.addEventListener("visibilitychange", onVisibilityChange);
};

const stopPolling = () => {
  if (pollTimer) {
    clearInterval(pollTimer);
    pollTimer = null;
  }
  document.removeEventListener("visibilitychange", onVisibilityChange);
};

const onVisibilityChange = () => {
  if (document.hidden) {
    stopPolling();
  } else {
    startPolling();
  }
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

    // ✅ 새로고침 시 500 방지: 참여 목록 먼저 → 필요 시 join
    await communityStore.fetchJoinedCommunities(authStore.user.id);
    if (!communityStore.isJoined(communityId)) {
      await communityStore.joinCommunity(communityId, authStore.user.id);
      await communityStore.fetchJoinedCommunities(authStore.user.id);
    }

    // 히스토리 로드 후 폴링 시작
    knownIds.clear();
    chatMessages.value = [];
    await loadMessages();
    startPolling(2000); // 2초 간격 (원하면 1000~3000 조정)
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
  stopPolling();
});

// 라우트 변경 시 재로딩 + 폴링 재시작
watch(
  () => route.params.id,
  async (newId, oldId) => {
    if (newId && newId !== oldId) {
      // 방 이동 시 폴링 리셋
      stopPolling();
      knownIds.clear();
      chatMessages.value = [];
      await loadCommunityData();
    }
  }
);
</script>

<style scoped>
/* 추가 스타일 필요 시 여기에 */
</style>

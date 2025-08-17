<template>
  <div class="max-w-4xl mx-auto px-4 py-2 font-paper mb-10">
    <div
      :class="
        showChatList ? 'md:flex md:items-start md:space-x-6' : 'flex justify-center'
      "
    >
      <!-- ChatWindow -->
      <div :class="showChatList ? 'md:flex-1' : 'w-full max-w-xl'">
        <ChatWindow
          class="w-full"
          :messages="chatMessages"
          :currentUserId="authStore.user?.id || 0"
          :roomTitle="currentRoom?.location || 'Chatbot Assistant'"
          :roomImage="currentRoom?.location ? getRegionImage(currentRoom.location) : null"
          :showLeaveButton="!!currentRoom?.userJoined"
          :showChatList="showChatList"
          @sendMessage="handleSendMessage"
          @leave="showLeaveConfirm(currentRoom)"
          @toggleList="toggleChatList"
          @goToList="goToFullList"
        />
      </div>

      <!-- 데스크톱: 오른쪽 참가중인 채팅방 목록 -->
      <div
        v-if="showChatList"
        class="hidden md:block w-88 h-[600px] px-6 py-4 bg-gradient-to-b from-blue-400 via-indigo-300 to-white rounded-lg shadow-[0_4px_4px_0_rgba(0,0,0,0.25)] flex flex-col justify-start items-start overflow-y-auto"
      >
        <div class="self-stretch flex justify-between items-center mb-2">
          <div class="text-white text-2xl font-paperSemi">나의 채팅방 목록</div>
          <button
            @click="$router.push('/dashboard/community')"
            class="text-white text-sm bg-white/20 hover:bg-white/30 px-3 py-1 rounded-lg transition-colors"
          >
            전체 목록
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

    <!-- 모바일: 사이드바 형태의 채팅방 목록 -->
    <div
      v-if="showChatList"
      class="md:hidden fixed inset-0 z-50 flex"
    >
      <!-- 배경 오버레이 -->
      <div 
        class="absolute inset-0 bg-black bg-opacity-50"
        @click="toggleChatList"
      ></div>
      
      <!-- 사이드바 -->
      <div
        class="relative w-80 h-full bg-gradient-to-b from-blue-400 via-indigo-300 to-white shadow-xl flex flex-col px-4 py-6"
      >
        <!-- 헤더 -->
        <div class="flex justify-between items-center mb-4">
          <div class="text-white text-lg font-paperSemi">나의 채팅방 목록</div>
          <button
            @click="toggleChatList"
            class="text-white hover:bg-white/20 p-2 rounded-lg transition-colors"
          >
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M18 6L6 18M6 6L18 18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </button>
        </div>
        
        <!-- 전체 목록 버튼 -->
        <button
          @click="$router.push('/dashboard/community')"
          class="w-full text-white text-sm bg-white/20 hover:bg-white/30 px-3 py-2 rounded-lg transition-colors mb-4"
        >
          전체 목록
        </button>
        
        <!-- 채팅방 목록 -->
        <div class="flex-1 overflow-y-auto space-y-3">
          <ChatListCard
            v-for="room in joinedRooms"
            :key="room.id"
            :location="room.location"
            :image="room.image"
            :link="`/dashboard/community/${room.id}`"
            @click="toggleChatList"
          />
        </div>
      </div>
    </div>

    <!-- 나가기 확인 모달 -->
    <ConfirmModal
      v-model="showConfirmModal"
      title="현재 채팅방에서 나가시겠습니까?"
      message="나가시면 채팅방 목록에서 사라지며,<br>다시 참가하려면 새로 입장해야 합니다."
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

// 지역명 매핑 (실제 파일명에 맞춤)
const regionNameMap = {
  // 서울특별시 구 (서울은 구별로 세분화)
  강남구: { name: "gangnam", ext: "png" },
  강동구: { name: "gangdong", ext: "png" },
  강북구: { name: "gangbuk", ext: "png" },
  강서구: { name: "gangseo", ext: "png" },
  관악구: { name: "gwanak", ext: "png" },
  광진구: { name: "gwangjin", ext: "png" },
  구로구: { name: "guro", ext: "jpg" },
  금천구: { name: "geumcheon", ext: "png" },
  노원구: { name: "nowon", ext: "png" },
  도봉구: { name: "dobong", ext: "png" },
  동대문구: { name: "dongdaemun", ext: "png" },
  동작구: { name: "dongjak", ext: "png" },
  마포구: { name: "mapo", ext: "png" },
  서대문구: { name: "seodaemun", ext: "png" },
  서초구: { name: "seocho", ext: "png" },
  성동구: { name: "seongdong", ext: "png" },
  성북구: { name: "seongbuk", ext: "png" },
  송파구: { name: "songpa", ext: "png" },
  양천구: { name: "yangcheon", ext: "png" },
  영등포구: { name: "yeongdeungpo", ext: "png" },
  용산구: { name: "yongsan", ext: "png" },
  은평구: { name: "eunpyeong", ext: "png" },
  종로구: { name: "jongno", ext: "png" },
  중구: { name: "junggu", ext: "png" },
  중랑구: { name: "jungnang", ext: "png" },

  // 광역시/도 (시도 단위로 통일)
  부산광역시: { name: "busan", ext: "png" },
  대구광역시: { name: "daegu", ext: "png" },
  인천광역시: { name: "incheon", ext: "png" },
  광주광역시: { name: "gwangju", ext: "jpg" },
  대전광역시: { name: "daegeon", ext: "png" },
  울산광역시: { name: "ulsan", ext: "svg" },
  세종시: { name: "sejong", ext: "jpg" },
  경기도: { name: "gyeongido", ext: "jpg" },
  강원특별자치도: { name: "gangwon", ext: "jpg" },
  충청북도: { name: "chungbuk", ext: "png" },
  충청남도: { name: "chungnam", ext: "png" },
  전북특별자치도: { name: "jeonbuk", ext: "png" },
  전라남도: { name: "jeonnam", ext: "png" },
  경상북도: { name: "gyeongbuk", ext: "png" },
  경상남도: { name: "gyeongnam", ext: "svg" },
  제주특별자치도: { name: "jeju", ext: "svg" },
};

// 지역별 이미지 매핑 함수
const getRegionImage = (location) => {
  const parts = location.split(" ");
  const region = parts[0].trim();
  const district = parts[1] ? parts[1].trim() : "";

  try {
    let fileInfo;

    // 서울특별시는 구별로 세분화
    if (region === "서울특별시" && district && regionNameMap[district]) {
      fileInfo = regionNameMap[district];
    }
    // 다른 지역은 시/도 단위
    else if (regionNameMap.hasOwnProperty(region)) {
      fileInfo = regionNameMap[region];
    } else {
      throw new Error("이미지 없음");
    }

    // Vite의 정적 자원 import 방식 사용
    return new URL(
      `../assets/images/re/${fileInfo.name}.${fileInfo.ext}`,
      import.meta.url
    ).href;
  } catch (error) {
    return new URL("../assets/icons/image-placeholder.svg", import.meta.url)
      .href;
  }
};

// 상태
const currentCommunity = ref(null);
const chatMessages = ref([]);
const isConnected = ref(false);
const showChatList = ref(false);

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
    image: getRegionImage(community.title), // 지역별 이미지 매핑
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

// 채팅방 목록 토글
const toggleChatList = () => {
  showChatList.value = !showChatList.value;
};

// 전체 목록으로 이동
const goToFullList = () => {
  $router.push("/dashboard/community");
};

// 채팅방 나가기
const leaveRoom = async (roomId) => {
  try {
    await communityStore.leaveCommunity(roomId, authStore.user.id);
    if (currentRoom.value && currentRoom.value.id === roomId) {
      $router.push("/dashboard/community");
    }
  } catch (error) {
    alert("채팅방 나가기에 실패했습니다.");
  }
};

// 메시지 전송(WebSocket)
const handleSendMessage = async (message) => {
  if (!currentCommunity.value || !authStore.user?.id) {
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
      alert("연결이 끊어졌습니다. 페이지를 새로고침해주세요.");
    }
  } catch (error) {
    alert("메시지 전송에 실패했습니다. 다시 시도해주세요.");
  }
};

// 메시지 히스토리 로드 (초기 로딩용)
const loadMessages = async () => {
  if (!currentCommunity.value || !authStore.user?.id) return;
  try {
    const messages = await communityService.getMessages(
      currentCommunity.value.id,
      authStore.user.id
    );
    chatMessages.value = messages || [];
    // WebSocket 서비스에도 메시지 설정
    websocketService.setMessages(messages || []);
  } catch (error) {
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

    // WebSocket 메시지 콜백으로 직접 처리 (중복 방지)
    websocketService.onMessage((chatMessage) => {
      const exists = chatMessages.value.some((msg) => msg.id === chatMessage.id);
      if (!exists) {
        chatMessages.value.push({
          id: chatMessage.id,
          message: chatMessage.message,
          userId: chatMessage.userId,
          userName: chatMessage.userName,
          userProfileImg: chatMessage.userProfileImg,
          sentAt: chatMessage.sentAt,
          messageType: chatMessage.messageType || 'USER',
        });
      }
    });
  } catch (error) {
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
  } catch (error) {
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
      // 목록 토글 닫기
      showChatList.value = false;
      await loadCommunityData();
    }
  }
);
</script>

<style scoped>
/* 추가 스타일 필요 시 여기에 */
</style>

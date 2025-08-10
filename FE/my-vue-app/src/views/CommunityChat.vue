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
import { ref, computed, onMounted, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import ChatWindow from "@/components/widget/ChatWindow.vue";
import ChatListCard from "@/components/card/ChatListCard.vue";
import ConfirmModal from "@/components/modal/ConfirmModal.vue";
import { useCommunityStore } from "@/store/community.js";
import { useAuthStore } from "@/store/auth.js";
import axiosInstance from "@/utils/axios.js";

// 1) 라우트 파라미터 및 라우터
const route = useRoute();
const $router = useRouter();
const communityStore = useCommunityStore();
const authStore = useAuthStore();

// 2) 현재 커뮤니티 정보
const currentCommunity = ref(null);
const chatMessages = ref([]);

// 3) 현재 방 객체
const currentRoom = computed(() => {
  if (!currentCommunity.value) return null;
  
  return {
    id: currentCommunity.value.id,
    location: currentCommunity.value.title,
    userJoined: true // 임시로 모든 채팅방에서 나가기 버튼 표시
  };
});


// 6) 참가중인 방들
const joinedRooms = computed(() => {
  console.log('joinedCommunities:', communityStore.joinedCommunities);
  const rooms = communityStore.joinedCommunities.map(community => ({
    id: community.id,
    location: community.title,
    image: '', // 기본 이미지 사용
  }));
  console.log('joinedRooms with IDs:', rooms);
  return rooms;
});

// 기본 이미지
const defaultImage = new URL("@/assets/icons/image-placeholder.svg", import.meta.url).href;

// 모달 상태
const showConfirmModal = ref(false);
const selectedRoom = ref(null);

// 나가기 확인 모달 표시
const showLeaveConfirm = (room) => {
  selectedRoom.value = room;
  showConfirmModal.value = true;
};

// 나가기 확인
const confirmLeave = () => {
  if (selectedRoom.value) {
    leaveRoom(selectedRoom.value.id);
    selectedRoom.value = null;
  }
};

// 채팅방 나가기 함수
const leaveRoom = async (roomId) => {
  try {
    await communityStore.leaveCommunity(roomId, authStore.user.id);
    
    // 현재 보고 있던 방에서 나간 경우 커뮤니티 목록으로 이동
    if (currentRoom.value && currentRoom.value.id === roomId) {
      $router.push('/dashboard/community');
    }
  } catch (error) {
    console.error('채팅방 나가기 실패:', error);
    alert('채팅방 나가기에 실패했습니다.');
  }
};

// 메시지 전송 핸들러
const handleSendMessage = async (message) => {
  if (!currentCommunity.value || !authStore.user?.id) {
    console.error('커뮤니티 또는 사용자 정보가 없습니다.');
    return;
  }

  try {
    const requestDto = {
      userId: authStore.user.id,
      message: message
    };

    const response = await axiosInstance.post(`/api/community/${currentCommunity.value.id}/messages`, requestDto);
    
    // 성공적으로 전송되면 메시지 목록 새로고침
    await loadMessages();
    
  } catch (error) {
    console.error('메시지 전송 실패:', error);
    alert('메시지 전송에 실패했습니다. 다시 시도해주세요.');
  }
};

// 메시지 목록 로드 함수
const loadMessages = async () => {
  if (!currentCommunity.value) return;
  
  try {
    const response = await axiosInstance.get(`/api/community/${currentCommunity.value.id}/messages`);
    chatMessages.value = response.data.data || [];
    console.log('메시지 목록 로드 완료:', chatMessages.value.length, '개');
  } catch (error) {
    console.error('메시지 목록 로드 실패:', error);
  }
};

// 데이터 로드 함수
const loadCommunityData = async () => {
  const communityId = Number(route.params.id);
  if (!communityId) {
    $router.push('/dashboard/community');
    return;
  }
  
  try {
    // 커뮤니티 정보 조회
    await communityStore.fetchAllCommunities();
    currentCommunity.value = communityStore.communities.find(c => c.id === communityId);
    
    if (!currentCommunity.value) {
      console.error('해당 커뮤니티를 찾을 수 없습니다.');
      $router.push('/dashboard/community');
      return;
    }
    
    // 자동으로 채팅방에 참가
    if (!communityStore.isJoined(communityId)) {
      console.log('커뮤니티 참가 시도:', communityId, 'userId:', authStore.user.id);
      await communityStore.joinCommunity(communityId, authStore.user.id);
    }
    
    // 참가중인 커뮤니티 목록 조회
    await communityStore.fetchJoinedCommunities(authStore.user.id);
    
    // 메시지 목록 로드
    await loadMessages();
    
  } catch (error) {
    console.error('채팅방 로드 실패:', error);
    alert('채팅방을 불러오는데 실패했습니다.');
    $router.push('/dashboard/community');
  }
};

// 컴포넌트 마운트 시 데이터 로드
onMounted(() => {
  loadCommunityData();
});

// 라우트 파라미터 변경 감지
watch(() => route.params.id, (newId, oldId) => {
  if (newId && newId !== oldId) {
    console.log('채팅방 변경:', oldId, '→', newId);
    loadCommunityData();
  }
});
</script>

<style scoped>
/* 추가 스타일 필요 시 여기에 */
</style>

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
            <button @click="toggleFavorite" class="flex items-center space-x-1">
              <!-- userFavorited가 true면 노랑색 채워진 별, 아니면 회색 빈 별 -->
              <span
                class="text-2xl"
                :class="
                  currentRoom.userFavorited ? 'text-yellow-400' : 'text-gray-400'
                "
              >
                {{ currentRoom.userFavorited ? "★" : "☆" }}
              </span>
              <span class="text-lg">{{ currentRoom.favorites }}</span>
            </button>
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
        <ChatWindow class="w-full" />
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
import { ref, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import ChatWindow from "@/components/widget/ChatWindow.vue";
import ChatListCard from "@/components/card/ChatListCard.vue";
import ConfirmModal from "@/components/modal/ConfirmModal.vue";
import { communities as dummyData } from "@/data/communities.js";

// 1) 라우트 파라미터 및 라우터
const route = useRoute();
const $router = useRouter();

// 2) 방 목록 (reactive)
const rooms = ref(dummyData);

// 3) 현재 방 객체
const currentRoom = computed(() => {
  const id = Number(route.params.id);
  const room = rooms.value.find((r) => r.id === id);
  
  // 채팅방에 입장할 때 자동으로 참가 상태로 설정
  if (room && !room.userJoined) {
    room.userJoined = true;
  }
  
  return room;
});

// rooms 배열의 userFavorited 를 직접 토글
function toggleFavorite() {
  if (!currentRoom.value) return;
  // 1) 즐겨찾기 여부에 따라 숫자 증감
  if (currentRoom.value.userFavorited) {
    currentRoom.value.favorites--;
  } else {
    currentRoom.value.favorites++;
  }
  // 2) userFavorited 플래그 토글
  currentRoom.value.userFavorited = !currentRoom.value.userFavorited;
}
// 6) 참가중인 방들 (userJoined 속성 기반으로 필터링)
const joinedRooms = computed(() =>
  rooms.value.filter((r) => r.userJoined)
);

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
const leaveRoom = (roomId) => {
  const roomIndex = rooms.value.findIndex(r => r.id === roomId);
  if (roomIndex !== -1) {
    // userJoined를 false로 변경
    rooms.value[roomIndex].userJoined = false;
    
    // 현재 보고 있던 방에서 나간 경우 커뮤니티 목록으로 이동
    if (currentRoom.value && currentRoom.value.id === roomId) {
      $router.push('/dashboard/community');
    }
  }
};
</script>

<style scoped>
/* 추가 스타일 필요 시 여기에 */
</style>

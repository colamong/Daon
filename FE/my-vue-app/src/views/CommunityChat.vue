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
        </div>

        <!-- ChatWindow -->
        <ChatWindow class="w-full" />
      </div>

      <!-- 오른쪽: 즐겨찾기 목록 -->
      <div
        class="w-80 h-[600px] px-6 py-4 bg-gradient-to-b from-blue-400 via-indigo-300 to-white rounded-lg shadow-[0_4px_4px_0_rgba(0,0,0,0.25)] inline-flex flex-col justify-start items-start overflow-y-auto"
      >
        <div class="self-stretch text-white text-2xl font-paperSemi mb-2">
          나의 채팅방 목록
        </div>
        <div class="flex-1 overflow-y-auto space-y-4 pr-2">
          <ChatListCard
            v-for="room in favoriteRooms"
            :key="room.id"
            :location="room.location"
            :image="room.image"
            :link="`/dashboard/community/${room.id}`"
            class="mb-3"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import { useRoute } from "vue-router";
import ChatWindow from "@/components/widget/ChatWindow.vue";
import ChatListCard from "@/components/card/ChatListCard.vue";
import { communities as dummyData } from "@/data/communities.js";

// 1) 라우트 파라미터
const route = useRoute();

// 2) 방 목록 (reactive)
const rooms = ref(dummyData);

// 3) 현재 방 객체
const currentRoom = computed(() => {
  const id = Number(route.params.id);
  return rooms.value.find((r) => r.id === id);
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
// 6) 즐겨찾기 된 방들만 필터
const favoriteRooms = computed(() =>
  rooms.value.filter((r) => r.userFavorited)
);
</script>

<style scoped>
/* 추가 스타일 필요 시 여기에 */
</style>

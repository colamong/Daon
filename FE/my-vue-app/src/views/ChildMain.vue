<template>
  <div
    :style="{ backgroundImage: `url(${bgImage})` }"
    class="h-screen relative bg-cover bg-center"
  >
    <header class="px-4 py-4 flex items-center justify-between">
      <button
        @click="goBack"
        aria-label="뒤로가기"
        class="w-20 h-20 flex items-center justify-center bg-white rounded-lg shadow"
      >
        <img :src="outIcon" alt="뒤로가기" class="w-20 h-20 object-contain" />
      </button>

      <!-- 선택된 아이 이름 표시 -->
      <div
        v-if="currentChild && currentChild.name"
        class="bg-white/70 backdrop-blur-sm rounded-2xl px-6 py-3 shadow-lg"
      >
        <div class="flex items-center gap-3">
          <img
            :src="currentChild.profileImage || 'https://placehold.co/40x40'"
            :alt="`${currentChild.name} 프로필`"
            class="w-12 h-12 rounded-full object-cover border-2 border-white"
          />
          <p class="text-4xl text-gray-800 font-shark">
            {{ currentChild.name }}
          </p>
        </div>
      </div>
    </header>

    <main
      class="absolute inset-0 flex items-center justify-center pointer-events-none"
    >
      <div
        class="pointer-events-auto grid grid-cols-1 sm:grid-cols-2 gap-y-6 gap-x-16 w-full max-w-[900px] px-4"
      >
        <div
          @click="goToPenguin"
          class="block bg-purple-300 rounded-2xl overflow-hidden shadow hover:shadow-lg transition cursor-pointer"
        >
          <div class="p-4">
            <h2
              class="text-4xl mb-2 text-black text-center font-shark text-outline-white"
            >
              <span class="text-blue-600 font-shark">펭귄</span>과 대화하기
            </h2>
            <img
              src="../assets/images/penguin.png"
              alt="펭귄과 대화하기"
              class="w-full h-full object-contain"
            />
          </div>
        </div>

        <div
          @click="goToDrawing"
          class="block bg-blue-400 rounded-2xl overflow-hidden shadow hover:shadow-lg transition cursor-pointer"
        >
          <div class="p-4">
            <h2 class="text-4xl font-shark mb-2 text-center text-outline-white">
              나의 <span class="font-shark text-red-500">그림</span> 일기
            </h2>
            <img
              src="../assets/images/drawing.png"
              alt="나의 그림 일기"
              class="w-full h-full object-contain py-5"
            />
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { useRouter } from "vue-router";
import { useChildStore } from "@/store/child";
import outIcon from "../assets/images/out.png";
import bgImage from "../assets/images/child_main.png";

// props 정의
const props = defineProps({
  childId: {
    type: [String, Number],
    default: null,
  },
});

const router = useRouter();
const childStore = useChildStore();

// 현재 선택된 아이 정보
const currentChild = computed(() => childStore.selectedChild);

// 컴포넌트 마운트 시 실행
onMounted(() => {
  childStore.initialize();

  // URL에서 childId가 전달된 경우 해당 아이를 선택
  if (props.childId) {
    const childId = parseInt(props.childId);
    if (childStore.children.find((child) => child.id === childId)) {
      childStore.selectChild(childId);
    }
  }
});

function goBack() {
  router.back();
}

// 펭귀과 대화하기 클릭 시 바로 이동 (이미 선택된 아이 정보가 있음)
function goToPenguin() {
  // 현재 선택된 아이가 있으면 바로 펭귄 페이지로
  if (currentChild.value) {
    router.push({ 
      name: "ChildPet", 
      params: { childId: currentChild.value.id }
    });
  }
}

// 그림일기 클릭 시 바로 이동 (이미 선택된 아이 정보가 있음)
function goToDrawing() {
  // 현재 선택된 아이가 있으면 바로 그림일기 페이지로
  if (currentChild.value) {
    router.push({ 
      name: "ChildDrawing", 
      params: { childId: currentChild.value.id }
    });
  }
}
</script>

<style scoped>
/* 필요 시 스타일 작성 */
</style>

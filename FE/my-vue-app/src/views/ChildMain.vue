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
       <img
          :src="outIcon"
          alt="뒤로가기"
          class="w-20 h-20 object-contain"
        />
    </img>
      </button>
    </header>


    <main class="absolute inset-0 flex items-center justify-center pointer-events-none">
      <div
        class="pointer-events-auto
               grid grid-cols-1 sm:grid-cols-2 gap-y-6 gap-x-16
               w-full max-w-[900px] px-4"
      >
      
          <router-link
          to="/child/pet"
          class="block bg-purple-300 rounded-2xl overflow-hidden shadow hover:shadow-lg transition"
          >
          <div class="p-4">
            <h2 class="text-4xl mb-2 text-black text-center font-shark text-outline-white">
              <span class="text-blue-600 font-shark">펭귄</span>과 대화하기
            </h2>
            <img
            src="../assets/images/penguin.png"
            alt="펭귄과 대화하기"
            class="w-full h-full object-contain "
            />
          </div>
        </router-link>
        
        <router-link
        to="/child/drawing"
        class="block bg-blue-400 rounded-2xl overflow-hidden shadow hover:shadow-lg transition"
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
      </router-link>
    </div>
  
    </main>
  </div>
</template>

<script setup>
import { useRouter } from "vue-router";
import outIcon from "../assets/images/out.png";
import bgImage from "../assets/images/child_main.png";
const router = useRouter();
function goBack() {
  router.back();
}

</script>

<style scoped>
/* 필요 시 스타일 작성 */
</style>

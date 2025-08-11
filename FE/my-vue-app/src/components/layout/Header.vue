<!-- src/components/layout/Header.vue -->
<template>
  <header
    class="relative w-full h-28 bg-background-header flex items-center justify-between px-8"
  >
    <!-- 왼쪽 로고 -->
    <div @click="goDashboard" class="flex items-center gap-4 cursor-pointer">
      <div class="w-16 h-16 bg-gray-500 rounded-full"></div>
      <div class="text-2xl font-paperSemi text-[#526049]">다온(DA:ON)</div>
    </div>

    <!-- 가운데 내비게이션 -->
    <nav class="flex gap-8">
      <button @click="goChildMain" class="text-xl font-paper text-black">
        펭구랑 놀자
      </button>
      <button @click="goChildProfile" class="text-xl font-paper text-black">
        아이 프로필
      </button>
      <button @click="goOCRTool" class="text-xl font-paper text-black">
        문서 도우미
      </button>
      <button @click="goCommunityChat" class="text-xl font-paper text-black">
        온동네
      </button>
      <button @click="goLearningHelper" class="text-xl font-paper text-black">
        상황별 학습
      </button>
    </nav>

    <!-- 오른쪽: 프로필 토글 + 로그아웃 -->
    <div class="flex items-center gap-4" ref="wrapper">
      <img
        @click="toggleProfile"
        :src="auth.user?.profileImage || 'https://placehold.co/53x53'"
        alt="프로필"
        class="w-14 h-14 rounded-full cursor-pointer object-cover"
      />
      <span
        @click="toggleProfile"
        class="cursor-pointer text-xl font-paper text-black"
      >
        {{ auth.user?.nickname || "게스트" }}
      </span>

      <!-- Tailwind 로직만으로 재구현한 로그아웃 버튼 -->
      <button
        @click="logout"
        class="relative flex items-center justify-start w-[45px] h-[45px] bg-white rounded-full shadow-[2px_2px_10px_rgba(0,0,0,0.2)] overflow-hidden transition-[width] duration-300 ease-in-out hover:w-[125px] hover:bg-black hover:rounded-[40px] active:translate-x-[2px] active:translate-y-[2px]"
      >
        <!-- 아이콘 영역 -->
        <div
          class="flex items-center justify-center w-full transition-all duration-300 ease-in-out hover:w-[30%] hover:pl-5"
        >
          <svg
            class="w-[17px] fill-current text-black hover:text-white"
            viewBox="0 0 512 512"
          >
            <path
              d="M377.9 105.9L500.7 228.7c7.2 7.2 11.3 17.1 11.3 27.3s-4.1 20.1-11.3 27.3L377.9 406.1c-6.4 6.4-15 9.9-24 9.9c-18.7 0-33.9-15.2-33.9-33.9l0-62.1-128 0c-17.7 0-32-14.3-32-32l0-64c0-17.7 14.3-32 32-32l128 0 0-62.1c0-18.7 15.2-33.9 33.9-33.9c9 0 17.6 3.6 24 9.9zM160 96L96 96c-17.7 0-32 14.3-32 32l0 256c0 17.7 14.3 32 32 32l64 0c17.7 0 32 14.3 32 32s-14.3 32-32 32l-64 0c-53 0-96-43-96-96L0 128C0 75 43 32 96 32l64 0c17.7 0 32 14.3 32 32s-14.3 32-32 32z"
            />
          </svg>
        </div>
        <!-- 텍스트 영역 -->
        <span
          class="absolute right-0 w-0 opacity-0 text-white text-[1.2em] font-paper transition-[width,opacity] duration-300 ease-in-out hover:opacity-100 hover:w-[70%] hover:pr-2.5"
        >
          Logout
        </span>
      </button>
    </div>

    <!-- 프로필 카드 (fixed 위치) -->
    <div
      v-if="showProfile"
      ref="profileCard"
      class="font-paper fixed top-28 right-8 h-96 w-80 bg-purple-200 rounded-xl shadow-lg z-[1000]"
    >
      <!-- 외곽 테두리 -->
      <div
        class="absolute inset-0 h-96 w-80 rounded-xl border-2 border-violet-900"
      ></div>

      <!-- 컨텐츠 래퍼 -->
      <div
        class="relative flex flex-col items-center p-8 pt-12 space-y-4 text-center h-full overflow-visible"
      >
        <!-- 1) 프로필 사진 (오타 수정: class="\" 제거) -->
        <div class="">
          <img
            :src="auth.user?.profileImage || 'https://placehold.co/96x96'"
            alt="프로필"
            class="w-24 h-24 rounded-full object-cover border-4 border-white"
          />
        </div>

        <!-- 2) 닉네임 -->
        <h3 class="mt-4 text-2xl font-bold text-violet-900">
          {{ auth.user?.nickname }}
        </h3>

        <!-- 3) 이메일 -->
        <p class="text-m text-violet-900 truncate w-full px-4">
          {{ auth.user?.email }}
        </p>

        <!-- 4) 국가 아이콘 + 나라명 -->
        <div class="flex items-center space-x-2">
          <img
            src="@/assets/icons/country_flag.svg"
            alt="국가 아이콘"
            class="w-5 h-5"
          />
          <span class="text-m text-violet-900">{{ auth.user?.country }}</span>
        </div>

        <!-- 5) 프로필 수정 버튼 -->
        <BaseButton
          variant="myprofile"
          link="/profile/edit"
          class="mt-auto w-36"
        >
          프로필 수정
        </BaseButton>
      </div>
    </div>

    <!-- 펭구랑 놀자 - 아이 선택 모달 -->
    <ChildSelectModal 
      v-model="showPenguinChildSelectModal"
      :children="childStore.children"
      action-text="펭귄과 놀"
      @select="onPenguinChildSelected"
      @register="goToChildRegister"
    />
    
    <!-- 아이 등록 확인 모달 -->
    <ConfirmChildRegistrationModal 
      v-model="showChildRegistrationModal"
      @confirm="handleChildRegistrationConfirm"
      @cancel="handleChildRegistrationCancel"
    />
  </header>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/store/auth";
import { useChildStore } from "@/store/child";
import BaseButton from "@/components/button/BaseButton.vue";
import ChildSelectModal from "@/components/modal/ChildSelectModal.vue";
import ConfirmChildRegistrationModal from "@/components/modal/ConfirmChildRegistrationModal.vue";

const router = useRouter();
const auth = useAuthStore();
const childStore = useChildStore();

const showProfile = ref(false);
const profileCard = ref(null);
const wrapper = ref(null);
const showPenguinChildSelectModal = ref(false);
const showChildRegistrationModal = ref(false);

function toggleProfile() {
  showProfile.value = !showProfile.value;
}

function logout() {
  auth.logout();
  router.push({ name: "Landing" });
}

const goDashboard = () => router.push({ name: "Dashboard" });

//비동기 처리: initialize 완료 후 분기 
const goChildMain = async () => {
  await childStore.initialize();

  if (!childStore.hasChildren) {
    showChildRegistrationModal.value = true;
    return;
  }

  if (childStore.children.length === 1) {
    childStore.selectChild(childStore.children[0].id);
    router.push({
      name: "ChildMain",
      params: { childId: childStore.children[0].id },
    });
    return;
  }

  showPenguinChildSelectModal.value = true;
};

const goChildProfile = async () => {
  await childStore.initialize();
  router.push({
    name: childStore.hasChildren ? "ChildProfile" : "RegisterChild",
  });
};

const goOCRTool = () => router.push({ name: "OCRTool" });
const goCommunityChat = () => router.push({ name: "Community" });
const goLearningHelper = () => router.push({ name: "LearningHelper" });

// 펭구랑 놀자에서 아이 선택됐을 때
function onPenguinChildSelected(child) {
  childStore.selectChild(child.id);
  router.push({
    name: "ChildMain",
    params: { childId: child.id },
  });
}

// 아이 등록하러 가기
function goToChildRegister() {
  router.push({ name: "RegisterChild" });
}

// 아이 등록 모달 핸들러
function handleChildRegistrationConfirm() {
  router.push({ name: "RegisterChild" });
}
function handleChildRegistrationCancel() {
  // 취소 시 아무것도 하지 않음
}

function handleClickOutside(e) {
  if (
    showProfile.value &&
    profileCard.value &&
    wrapper.value &&
    !profileCard.value.contains(e.target) &&
    !wrapper.value.contains(e.target)
  ) {
    showProfile.value = false;
  }
}

onMounted(() => document.addEventListener("click", handleClickOutside));
onBeforeUnmount(() =>
  document.removeEventListener("click", handleClickOutside)
);
</script>

<style scoped></style>

<!-- src/components/layout/Header.vue -->
<template>
  <header
    class="relative w-full !h-24 bg-background-header flex items-center justify-between px-4 md:px-8"
  >
    <!-- 왼쪽 로고 -->
    <div @click="goDashboard" class="cursor-pointer flex items-center gap-2">
      <img 
        src="@/assets/images/daon_logo.png" 
        alt="다온 로고" 
        class="w-10 h-10 md:w-12 md:h-12 object-contain"
      />
      <h3 class="text-lg md:text-2xl font-paper font-bold text-black hover:text-blue-600">
        다온(DA:ON)
      </h3>
    </div>

    <!-- 모바일 헤더 우측 버튼들 -->
    <div class="md:hidden flex items-center gap-3">
      <!-- 이용 가이드 버튼 (?) -->
      <button
        @click="showTutorial"
        class="w-8 h-8 flex items-center justify-center text-blue-600 hover:text-blue-800 transition-colors"
        title="이용 가이드"
      >
        <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8.228 9c.549-1.165 2.03-2 3.772-2 2.21 0 4 1.343 4 3 0 1.4-1.278 2.575-3.006 2.907-.542.104-.994.54-.994 1.093m0 3h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
        </svg>
      </button>
      
      <!-- 햄버거 메뉴 버튼 -->
      <button
        @click="toggleMobileMenu"
        data-mobile-menu-toggle
        class="flex flex-col items-center justify-center w-8 h-8 space-y-1"
      >
        <span class="w-6 h-0.5 bg-black transition-all duration-300" :class="{ 'rotate-45 translate-y-2': showMobileMenu }"></span>
        <span class="w-6 h-0.5 bg-black transition-all duration-300" :class="{ 'opacity-0': showMobileMenu }"></span>
        <span class="w-6 h-0.5 bg-black transition-all duration-300" :class="{ '-rotate-45 -translate-y-2': showMobileMenu }"></span>
      </button>
    </div>

    <!-- 데스크톱 내비게이션 -->
    <nav class="hidden md:flex gap-8">
      <button
        @click="goChildMain"
        class="text-xl font-paper text-black hover:font-paperBold hover:text-blue-600"
        data-tutorial="penguin"
      >
        펭구랑 놀자
      </button>
      <button
        @click="goOCRTool"
        class="text-xl font-paper text-black hover:font-paperBold hover:text-blue-600"
        data-tutorial="document"
      >
        문서 도우미
      </button>
      <button
        @click="goCommunityChat"
        class="text-xl font-paper text-black hover:font-paperBold hover:text-blue-600"
        data-tutorial="community"
      >
        온동네
      </button>
      <button
        @click="goLearningHelper"
        class="text-xl font-paper text-black hover:font-paperBold hover:text-blue-600"
        data-tutorial="growth"
      >
        상황별 학습
      </button>
    </nav>

    <!-- 데스크톱 오른쪽: 프로필 토글 + 로그아웃 -->
    <div class="hidden md:flex items-center gap-4" ref="wrapper">
      <div
        class="flex items-center"
        ref="wrapper"
        @mouseenter="showProfileCard"
        @mouseleave="hideProfileCard"
      >
        <img
          @click="toggleProfile"
          :src="auth.user?.profileImage || 'https://placehold.co/53x53'"
          alt="프로필"
          class="w-9 h-9 lg:w-11 lg:h-11 rounded-full cursor-pointer object-cover transition-transform hover:scale-105"
        />
        <span
          @click="toggleProfile"
          class="ml-2 cursor-pointer text-lg lg:text-xl font-paper text-black hover:text-blue-600 transition-colors"
        >
          {{ auth.user?.nickname || "게스트" }}
        </span>
      </div>

      <!-- Tailwind 로직만으로 재구현한 로그아웃 버튼 -->
      <button
        @click="logout"
        class="relative flex items-center justify-start w-[40px] h-[40px] lg:w-[45px] lg:h-[45px] bg-white rounded-full shadow-[2px_2px_10px_rgba(0,0,0,0.2)] overflow-hidden transition-[width] duration-300 ease-in-out hover:w-[110px] lg:hover:w-[125px] hover:bg-black hover:rounded-[40px] active:translate-x-[2px] active:translate-y-[2px]"
      >
        <!-- 아이콘 영역 -->
        <div
          class="flex items-center justify-center w-full transition-all duration-300 ease-in-out hover:w-[30%] hover:pl-4 lg:hover:pl-5"
        >
          <svg
            class="w-[15px] lg:w-[17px] fill-current text-black hover:text-white"
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

    <!-- 모바일 메뉴 -->
    <div
      v-if="showMobileMenu"
      class="mobile-menu md:hidden fixed top-24 left-0 right-0 bg-white border-t border-gray-200 shadow-lg z-50 max-h-[calc(100vh-96px)] overflow-y-auto"
    >
      <div class="px-4 py-6 space-y-6">
        <!-- 모바일 네비게이션 -->
        <div class="space-y-1">
          <div
            @click="handleMobileChildMain"
            class="flex items-center gap-4 w-full text-left py-4 px-4 text-lg font-paper text-gray-800 hover:bg-blue-50 hover:text-blue-600 rounded-lg transition-all cursor-pointer border border-transparent hover:border-blue-200"
            data-tutorial="penguin"
          >
            <div class="w-12 h-12 bg-blue-100 rounded-full flex items-center justify-center">
              🐧
            </div>
            <div>
              <div class="font-paperBold">펭구랑 놀자</div>
              <div class="text-sm text-gray-500">펭구와 함께하는 재미있는 활동</div>
            </div>
          </div>
          
          <div
            @click="handleMobileOCRTool"
            class="flex items-center gap-4 w-full text-left py-4 px-4 text-lg font-paper text-gray-800 hover:bg-green-50 hover:text-green-600 rounded-lg transition-all cursor-pointer border border-transparent hover:border-green-200"
            data-tutorial="document"
          >
            <div class="w-12 h-12 bg-green-100 rounded-full flex items-center justify-center">
              📄
            </div>
            <div>
              <div class="font-paperBold">문서 도우미</div>
              <div class="text-sm text-gray-500">문서를 쉽게 번역하고 요약</div>
            </div>
          </div>
          
          <div
            @click="handleMobileCommunity"
            class="flex items-center gap-4 w-full text-left py-4 px-4 text-lg font-paper text-gray-800 hover:bg-purple-50 hover:text-purple-600 rounded-lg transition-all cursor-pointer border border-transparent hover:border-purple-200"
            data-tutorial="community"
          >
            <div class="w-12 h-12 bg-purple-100 rounded-full flex items-center justify-center">
              🏘️
            </div>
            <div>
              <div class="font-paperBold">온동네</div>
              <div class="text-sm text-gray-500">지역 이웃들과 소통하는 공간</div>
            </div>
          </div>
          
          <div
            @click="handleMobileLearning"
            class="flex items-center gap-4 w-full text-left py-4 px-4 text-lg font-paper text-gray-800 hover:bg-orange-50 hover:text-orange-600 rounded-lg transition-all cursor-pointer border border-transparent hover:border-orange-200"
            data-tutorial="growth"
          >
            <div class="w-12 h-12 bg-orange-100 rounded-full flex items-center justify-center">
              📚
            </div>
            <div>
              <div class="font-paperBold">상황별 학습</div>
              <div class="text-sm text-gray-500">유용한 다양한 상황별 학습</div>
            </div>
          </div>
        </div>
        
        <!-- 구분선 -->
        <hr class="border-gray-200">
        
        <!-- 모바일 프로필 -->
        <div class="bg-gray-50 rounded-xl p-4">
          <div class="flex items-center justify-between">
            <div class="flex items-center gap-3 cursor-pointer" @click="handleMobileProfileEdit">
              <img
                :src="auth.user?.profileImage || 'https://placehold.co/48x48'"
                alt="프로필"
                class="w-12 h-12 rounded-full object-cover border-2 border-white shadow-sm"
              />
              <div>
                <div class="text-base font-paperBold text-gray-800">
                  {{ auth.user?.nickname || "게스트" }} ({{ auth.userNationName || "국가정보없음" }})
                </div>
                <div class="text-sm text-gray-500">
                  {{ auth.user?.email || "guest@example.com" }}
                </div>
              </div>
            </div>
            <button
              @click="handleMobileLogout"
              class="flex items-center justify-center w-10 h-10 bg-red-500 hover:bg-red-600 text-white rounded-full transition-colors shadow-sm"
              title="로그아웃"
            >
              <svg
                class="w-4 h-4 fill-current"
                viewBox="0 0 512 512"
              >
                <path
                  d="M377.9 105.9L500.7 228.7c7.2 7.2 11.3 17.1 11.3 27.3s-4.1 20.1-11.3 27.3L377.9 406.1c-6.4 6.4-15 9.9-24 9.9c-18.7 0-33.9-15.2-33.9-33.9l0-62.1-128 0c-17.7 0-32-14.3-32-32l0-64c0-17.7 14.3-32 32-32l128 0 0-62.1c0-18.7 15.2-33.9 33.9-33.9c9 0 17.6 3.6 24 9.9zM160 96L96 96c-17.7 0-32 14.3-32 32l0 256c0 17.7 14.3 32 32 32l64 0c17.7 0 32 14.3 32 32s-14.3 32-32 32l-64 0c-53 0-96-43-96-96L0 128C0 75 43 32 96 32l64 0c17.7 0 32 14.3 32 32s-14.3 32-32 32z"
                />
              </svg>
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 프로필 카드 (fixed 위치) -->
    <div
      v-if="showProfile"
      ref="profileCard"
      @mouseenter="showProfileCard"
      @mouseleave="hideProfileCard"
      class="font-paper fixed top-20 right-4 md:right-8 h-80 w-72 md:w-76 bg-purple-200 rounded-xl shadow-lg z-[1000] border-2 border-violet-900"
    >
      <!-- 컨텐츠 래퍼 -->
      <div
        class="relative flex flex-col items-center p-8 pt-2 space-y-4 text-center h-full overflow-visible"
      >
        <!-- 1) 프로필 사진 (오타 수정: class="\" 제거) -->
        <div class="mt-2">
          <img
            :src="auth.user?.profileImage || 'https://placehold.co/96x96'"
            alt="프로필"
            class="w-20 h-20 rounded-full object-cover border-4 border-white"
          />
        </div>

        <!-- 2) 닉네임 -->
        <h3 class="mt-1 text-xl font-bold text-violet-900">
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
          <span class="text-m text-violet-900">{{ auth.userNationName }}</span>
        </div>

        <!-- 5) 프로필 수정 버튼 -->
        <BaseButton
          variant="myprofile"
          link="/profile/edit"
          class="w-36 hover:border-2 hover:border-violet-900 hover:bg-white hover:text-violet-900"
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

    <!-- 네비게이션 튜토리얼 모달 -->
    <NavigationTutorialModal v-model="showTutorialModal" />
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
import NavigationTutorialModal from "@/components/modal/NavigationTutorialModal.vue";

const router = useRouter();
const auth = useAuthStore();
const childStore = useChildStore();

const showProfile = ref(false);
const profileCard = ref(null);
const wrapper = ref(null);
const showPenguinChildSelectModal = ref(false);
const showChildRegistrationModal = ref(false);
const hoverTimer = ref(null);
const showMobileMenu = ref(false);
const showTutorialModal = ref(false);

function toggleProfile() {
  showProfile.value = !showProfile.value;
}

function showProfileCard() {
  // 기존 타이머가 있다면 취소
  if (hoverTimer.value) {
    clearTimeout(hoverTimer.value);
    hoverTimer.value = null;
  }
  showProfile.value = true;
}

function hideProfileCard() {
  // 200ms 딜레이를 두어 마우스가 카드로 이동할 시간을 줌
  hoverTimer.value = setTimeout(() => {
    showProfile.value = false;
    hoverTimer.value = null;
  }, 200);
}

function logout() {
  auth.logout();
  router.push({ name: "Landing" });
}

// 모바일 메뉴 관련 함수들
function toggleMobileMenu() {
  showMobileMenu.value = !showMobileMenu.value;
  
  // 모바일 메뉴가 열릴 때 스크롤 방지, 닫힐 때 스크롤 허용
  if (showMobileMenu.value) {
    document.body.style.overflow = 'hidden';
  } else {
    document.body.style.overflow = '';
  }
}

function closeMobileMenu() {
  showMobileMenu.value = false;
  document.body.style.overflow = '';
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

// 모바일 메뉴 핸들러 함수들
async function handleMobileChildMain() {
  closeMobileMenu();
  await goChildMain();
}

function handleMobileOCRTool() {
  closeMobileMenu();
  goOCRTool();
}

function handleMobileCommunity() {
  closeMobileMenu();
  goCommunityChat();
}

function handleMobileLearning() {
  closeMobileMenu();
  goLearningHelper();
}

function handleMobileLogout() {
  closeMobileMenu();
  logout();
}

function handleMobileProfileEdit() {
  closeMobileMenu();
  goToProfileEdit();
}

function goToProfileEdit() {
  router.push({ name: "ProfileEdit" });
}

// 튜토리얼 모달 표시
function showTutorial() {
  // 페이지 상단으로 스크롤
  window.scrollTo({ top: 0, behavior: "smooth" });

  // 스크롤 완료 후 모달 표시
  setTimeout(() => {
    showTutorialModal.value = true;
  }, 800);
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
  
  // 모바일 메뉴 외부 클릭 시 닫기
  if (showMobileMenu.value && !e.target.closest('.mobile-menu') && !e.target.closest('[data-mobile-menu-toggle]')) {
    showMobileMenu.value = false;
    document.body.style.overflow = '';
  }
}

onMounted(async () => {
  document.addEventListener("click", handleClickOutside);
  // 국가 목록 로드 (프로필 카드의 국가 정보 표시를 위해)
  await auth.loadNations();
});
onBeforeUnmount(() => {
  document.removeEventListener("click", handleClickOutside);
  // 컴포넌트 언마운트 시 스크롤 복원
  document.body.style.overflow = '';
});
</script>

<style scoped></style>

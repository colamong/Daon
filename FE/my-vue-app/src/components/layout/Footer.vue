<template>
  <footer class="w-full bg-footer px-8 py-12 text-black font-paper">
    <div
      class="max-w-screen-xl mx-auto flex flex-col md:flex-row justify-between items-start gap-x-24 gap-y-10"
    >
      <!-- 왼쪽: 브랜드 정보 -->
      <div class="flex flex-col gap-5 w-full md:w-[200px]">
        <div class="text-xl font-paperBold">다온(DA:ON)</div>
        <p class="text-sm leading-snug">
          다문화 가정이 한국 사회에 <br />
          잘 적응할 수 있도록 돕는 <br />
          AI 기반 통합 지원 플랫폼
        </p>
        <div class="flex gap-3 mt-2">
          <img
            src="@/assets/images/facebook.png"
            alt="Facebook"
            class="w-7 h-7 cursor-pointer"
          />
          <img
            src="@/assets/images/twitter.png"
            alt="Twitter"
            class="w-7 h-7 cursor-pointer"
          />
          <img
            src="@/assets/images/instagram.png"
            alt="Instagram"
            class="w-7 h-7 cursor-pointer"
          />
        </div>
      </div>

      <!-- 서비스 -->
      <div class="flex flex-col gap-3 w-full md:w-[160px]">
        <div class="text-base font-semibold">서비스</div>
        <ul class="text-sm leading-snug space-y-1">
          <li>
            <button
              @click="goChildMain"
              class="font-paper text-black hover:font-paperBold"
            >
              펭구랑 놀자
            </button>
          </li>
          <li>
            <button
              @click="goChildProfile"
              class="font-paper text-black hover:font-paperBold"
            >
              아이 프로필
            </button>
          </li>
          <li>
            <button
              @click="goOCRTool"
              class="font-paper text-black hover:font-paperBold"
            >
              문서 도우미
            </button>
          </li>
          <li>
            <button
              @click="goCommunityChat"
              class="font-paper text-black hover:font-paperBold"
            >
              온동네
            </button>
          </li>
          <li>
            <button
              @click="goLearningHelper"
              class="font-paper text-black hover:font-paperBold"
            >
              상황별 학습
            </button>
          </li>
        </ul>
      </div>

      <!-- 고객지원 -->
      <div class="flex flex-col gap-3 w-full md:w-[160px]">
        <div class="text-base font-semibold">고객지원</div>
        <ul class="text-sm leading-snug space-y-1">
          <li><span class="cursor-default">자주 묻는 질문</span></li>
          <li>
            <button
              @click="showTutorial"
              class="font-paper text-black hover:font-paperBold"
            >
              이용 가이드
            </button>
          </li>
          <li><span class="cursor-default">공지사항</span></li>
          <li><span class="cursor-default">문의하기</span></li>
        </ul>
      </div>

      <!-- 연락처 -->
      <div class="flex flex-col gap-3 w-full md:w-[200px]">
        <div class="text-base font-semibold">연락처</div>
        <ul class="text-sm leading-snug space-y-2">
          <li class="flex items-center gap-2">
            <img
              src="@/assets/images/location.png"
              alt="주소"
              class="w-4 h-4"
            />
            <span>서울시 강남구 테헤란로</span>
          </li>
          <li class="flex items-center gap-2">
            <img
              src="@/assets/images/letter.png"
              alt="이메일"
              class="w-4 h-4"
            />
            <span>support@daon.co.kr</span>
          </li>
          <li class="flex items-center gap-2">
            <img src="@/assets/images/phone.png" alt="전화" class="w-4 h-4" />
            <span>1588-0706</span>
          </li>
        </ul>
      </div>

      <!-- 오른쪽: 일러스트 + 저작권 -->
      <div class="hidden md:flex flex-col items-end w-[260px]">
        <img
          src="@/assets/images/footer-family.png"
          alt="가족 일러스트"
          class="w-[280px] h-auto object-contain"
        />
        <div
          class="mt-1 text-xs text-gray-500 font-medium flex items-center gap-1"
        >
          <span>© 2025 다온 All right reserved</span>
        </div>
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
  </footer>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/store/auth";
import { useChildStore } from "@/store/child";
import ChildSelectModal from "@/components/modal/ChildSelectModal.vue";
import ConfirmChildRegistrationModal from "@/components/modal/ConfirmChildRegistrationModal.vue";
import NavigationTutorialModal from "@/components/modal/NavigationTutorialModal.vue";

const router = useRouter();
const auth = useAuthStore();
const childStore = useChildStore();

const showPenguinChildSelectModal = ref(false);
const showChildRegistrationModal = ref(false);
const showTutorialModal = ref(false);

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

// 튜토리얼 모달 표시
function showTutorial() {
  // 페이지 상단으로 스크롤
  window.scrollTo({ top: 0, behavior: "smooth" });

  // 스크롤 완료 후 모달 표시
  setTimeout(() => {
    showTutorialModal.value = true;
  }, 800); // 시간을 좀 더 늘려서 스크롤이 완전히 끝난 후에 실행
}
</script>

<style scoped>
/* 여기에 필요 시 추가 커스텀 가능 */
</style>

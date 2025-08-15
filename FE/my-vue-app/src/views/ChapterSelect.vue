<template>
  <div class="flex justify-center min-h-screen py-4 md:py-10">
    <div
      class="w-full max-w-4xl mx-4 md:mx-10 px-2 md:px-4 py-4 md:py-8 font-paper bg-white rounded-xl"
    >
    <!-- 헤더 -->
    <div v-if="currentTheme">
      <div class="text-center mb-4 md:mb-8">
        <!-- 모바일: 제목과 버튼이 같은 라인 -->
        <div class="flex items-center justify-between mb-2 md:hidden">
          <h1 class="text-xl font-paperBold text-gray-800 flex-1 text-center">
            {{ currentTheme.title }}에서
          </h1>
          <div class="flex space-x-1">
            <IconButton
              variant="left-arrow"
              label="이전 테마"
              @click="navigateTheme('prev')"
              class="hover:bg-green-500 text-xs"
            />
            <IconButton
              variant="right-arrow"
              label="다음 테마"
              @click="navigateTheme('next')"
              class="bg-blue-400/80 hover:bg-blue-500/80 text-white text-xs"
            />
          </div>
        </div>
        
        <!-- 데스크톱: 기존 레이아웃 -->
        <div class="hidden md:block">
          <h1 class="text-4xl font-paperBold text-gray-800 mb-4">
            {{ currentTheme.title }}에서
          </h1>
          <div class="flex justify-between items-center mb-6">
            <div class="flex-1"></div>
            <p class="text-lg text-gray-600 text-center flex-1 whitespace-nowrap">
              {{ currentTheme.description }}
            </p>
            <!-- 테마 간 이동 화살표 -->
            <div class="flex space-x-2 flex-1 justify-end">
              <IconButton
                variant="left-arrow"
                label="이전 테마"
                @click="navigateTheme('prev')"
                class="hover:bg-green-500"
              />
              <IconButton
                variant="right-arrow"
                label="다음 테마"
                @click="navigateTheme('next')"
                class="bg-blue-400/80 hover:bg-blue-500/80 text-white"
              />
            </div>
          </div>
        </div>
        
        <!-- 모바일: 설명 -->
        <p class="text-sm text-gray-600 text-center mb-4 md:hidden">
          {{ currentTheme.description }}
        </p>
        <hr class="border-gray-300 max-w-4xl mx-auto" />
      </div>
    </div>

    <!-- 콘텐츠 영역 -->
    <div class="flex flex-col md:grid md:grid-cols-2 gap-4 md:gap-8 mt-4 md:mt-8" v-if="currentTheme">
      <!-- 이미지 영역 -->
      <div
        class="relative bg-blue-100/50 rounded-xl overflow-hidden w-full h-48 md:h-80 flex items-center justify-center order-1 md:order-1"
      >
        <img
          :src="currentTheme.image"
          :alt="currentTheme.title + ' 이미지'"
          class="w-full h-full object-contain"
        />
      </div>

      <!-- 챕터 목록 -->
      <div class="space-y-3 md:space-y-4 order-2 md:order-2">
        <div class="h-64 md:h-80 overflow-y-auto space-y-2 md:space-y-4 pr-1 md:pr-2">
          <div
            v-for="(chapter, index) in chapters"
            :key="chapter.id"
            class="bg-blue-100 rounded-lg p-3 md:p-5 hover:bg-blue-200 transition-colors cursor-pointer relative"
            @click="startChapter(chapter)"
          >
            <div class="flex justify-between items-center">
              <div class="flex-1 pr-2 md:pr-4">
                <h3 class="text-sm md:text-lg font-paperBold text-gray-800 mb-1">
                  Ch.{{ index + 1 }} {{ chapter.title }}
                </h3>
                <p class="text-xs md:text-sm text-gray-600 line-clamp-2">
                  {{ chapter.description }}
                </p>
              </div>
              <div
                class="flex-shrink-0 text-gray-500 text-lg md:text-2xl font-bold flex items-center justify-center"
              >
                &gt;
              </div>
            </div>
          </div>
        </div>

        <!-- 챕터 없음 처리 -->
        <p v-if="!chapters.length" class="text-sm md:text-base text-gray-500">
          이 테마에 등록된 챕터가 없습니다.
        </p>
      </div>
    </div>

    <!-- 테마를 찾을 수 없는 경우 -->
    <div v-else class="text-center py-8 md:py-12">
      <h2 class="text-lg md:text-2xl font-paperBold text-gray-600 mb-3 md:mb-4">
        테마를 찾을 수 없습니다
      </h2>
      <button
        @click="$router.push('/dashboard/learning')"
        class="bg-blue-500 hover:bg-blue-600 text-white px-4 md:px-6 py-2 md:py-3 rounded-lg transition-colors text-sm md:text-base"
      >
        테마 선택으로 돌아가기
      </button>
    </div>
  </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useNotification } from "@/composables/useNotification";
import IconButton from "@/components/button/IconButton.vue";
import learningService from "@/services/learningService"; // ✅ 서비스 사용

const route = useRoute();
const router = useRouter();
const { showInfo } = useNotification();

// 상태
const themes = ref([]); // 전체 테마 (API)
const chapters = ref([]); // 현재 테마의 챕터 목록 (API)

// 현재 테마
const themeId = computed(() => Number(route.params.id));
const currentTheme = computed(() =>
  themes.value.find((t) => t.id === themeId.value)
);

// 현재 테마의 인덱스 (좌우 이동용)
const currentThemeIndex = computed(() => {
  if (!currentTheme.value) return -1;
  return themes.value.findIndex((t) => t.id === currentTheme.value.id);
});

// 데이터 로딩
const loadThemes = async () => {
  const list = await learningService.getThemes();
  themes.value = list;
};

const loadChapters = async () => {
  if (!themeId.value) return;
  const list = await learningService.getChaptersByTheme(themeId.value);
  chapters.value = list;
};

onMounted(async () => {
  await loadThemes();
  await loadChapters();
});

// 테마 변경 시 챕터 재로딩
watch(
  () => themeId.value,
  async () => {
    await loadChapters();
  }
);

// 테마 간 이동 (순환)
const navigateTheme = (direction) => {
  if (!themes.value.length || currentThemeIndex.value < 0) return;
  const last = themes.value.length - 1;
  let idx = currentThemeIndex.value;
  idx =
    direction === "prev"
      ? idx === 0
        ? last
        : idx - 1
      : idx === last
      ? 0
      : idx + 1;

  const newTheme = themes.value[idx];
  router.push(`/dashboard/learning/theme/${newTheme.id}`);
};

// 챕터 시작하기
const startChapter = (chapter) => {
  const chapterIndex = chapters.value.findIndex((c) => c.id === chapter.id) + 1;
  showInfo(
    `Ch.${chapterIndex} ${chapter.title} 학습을 시작합니다!`,
    "학습 시작",
    { duration: 2000 }
  );
  setTimeout(() => {
    router.push(
      `/dashboard/learning/theme/${themeId.value}/chapter/${chapter.id}/question/1`
    );
  }, 1000);
};
</script>

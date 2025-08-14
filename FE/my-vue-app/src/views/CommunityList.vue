<template>
  <div class="max-w-4xl mx-auto px-4 py-8 space-y-12 font-paper">
    <!-- Hero 섹션 -->
    <section class="space-y-6">
      <h2 class="text-3xl font-bold text-center mb-10">온 동네</h2>
      <div
        class="flex flex-col md:flex-row bg-white rounded-lg shadow-lg overflow-hidden"
      >
        <div class="md:w-1/2">
          <img
            :src="communityHero"
            alt="온 동네 히어로 이미지"
            class="w-full h-full object-cover"
          />
        </div>
        <div class="md:w-1/2 p-6 flex flex-col justify-center text-xl">
          <p class="text-gray-700 mb-2">
            같은 지역 이웃들과 바로 연결되는 채팅방<br />
            동네 맛집부터 생활 꿀팁까지,
            <br />
            실시간으로 나누고 물어보세요.
          </p>
        </div>
      </div>
    </section>
    <!-- 검색창 래퍼 -->
    <div
      ref="wrapper"
      class="relative w-full max-w-6xl mx-auto overflow-visible"
    >
      <SearchBar
        ref="searchInput"
        v-model="searchQuery"
        placeholder="지역명을 입력해주세요"
        @focus="openDropdown"
        @input="openDropdown"
      />
      <teleport to="body">
        <div
          v-show="showDropdown && filteredSuggestions.length"
          ref="popperEl"
          class="suggestion-popper bg-white rounded-md shadow-lg z-50"
        >
          <SuggestionList
            :suggestions="filteredSuggestions"
            @select="handleSelect"
          />
        </div>
      </teleport>
    </div>

    <!-- 리스트 제목 -->
    <div class="text-center mb-8">
      <h3 class="text-2xl font-bold">
        {{ getListTitle() }}
      </h3>
    </div>

    <!-- 탭 메뉴 & 필터 바 -->
    <div class="flex items-center justify-between mb-8">
      <!-- 탭 메뉴 (좌측) -->
      <div class="bg-gray-100 p-1 rounded-lg">
        <button
          @click="activeTab = 'all'"
          :class="[
            'px-6 py-2 rounded-md font-medium transition-colors',
            activeTab === 'all'
              ? 'bg-white text-blue-600 shadow-sm'
              : 'text-gray-600 hover:text-gray-900',
          ]"
        >
          전체 채팅방
        </button>
        <button
          @click="activeTab = 'joined'"
          :class="[
            'px-6 py-2 rounded-md font-medium transition-colors',
            activeTab === 'joined'
              ? 'bg-white text-blue-600 shadow-sm'
              : 'text-gray-600 hover:text-gray-900',
          ]"
        >
          참여중인 채팅방
        </button>
      </div>

      <!-- 정렬 & 필터 바 (우측) -->
      <div v-if="activeTab === 'all'" class="flex items-center space-x-4">
        <button
          @click="sortOption = 'popularity'"
          :class="
            sortOption === 'popularity'
              ? 'bg-blue-600 text-white'
              : 'bg-gray-200 text-gray-700'
          "
          class="px-4 py-2 rounded"
        >
          인기순
        </button>
        <button
          @click="sortOption = 'alpha'"
          :class="
            sortOption === 'alpha'
              ? 'bg-blue-600 text-white'
              : 'bg-gray-200 text-gray-700'
          "
          class="px-4 py-2 rounded"
        >
          가나다순
        </button>
        <select
          v-model="selectedRegion"
          class="px-4 py-2 border border-gray-300 rounded"
        >
          <option value="">전체 지역</option>
          <option v-for="r in regionOptions" :key="r" :value="r">
            {{ r }}
          </option>
        </select>
      </div>
      <div v-else class="flex items-center space-x-4">
        <button
          @click="joinedSortOption = 'recent'"
          :class="
            joinedSortOption === 'recent'
              ? 'bg-blue-600 text-white'
              : 'bg-gray-200 text-gray-700'
          "
          class="px-4 py-2 rounded"
        >
          최근 참여순
        </button>
        <button
          @click="joinedSortOption = 'alpha'"
          :class="
            joinedSortOption === 'alpha'
              ? 'bg-blue-600 text-white'
              : 'bg-gray-200 text-gray-700'
          "
          class="px-4 py-2 rounded"
        >
          가나다순
        </button>
      </div>
    </div>

    <!-- 커뮤니티 카드 그리드 + 페이징 -->
    <section>
      <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-6">
        <div
          v-for="post in currentList.slice(page * 12, (page + 1) * 12)"
          :key="post.id"
          class="cursor-pointer"
          @click="goChat(post.id)"
        >
          <CommunityCard
            :location="post.title"
            :date="''"
            :image="getRegionImage(post.title)"
            :favorites="post.currentParticipants"
          />
        </div>
      </div>

      <div
        v-if="totalPages > 1"
        class="flex justify-center items-center space-x-4 mt-8"
      >
        <button
          @click="prevPage"
          :disabled="page === 0"
          class="px-4 py-2 bg-gray-200 rounded disabled:opacity-50"
        >
          이전
        </button>
        <span class="text-gray-600">{{ page + 1 }} / {{ totalPages }}</span>
        <button
          @click="nextPage"
          :disabled="page + 1 >= totalPages"
          class="px-4 py-2 bg-gray-200 rounded disabled:opacity-50"
        >
          다음
        </button>
      </div>
    </section>
  </div>
</template>

<script setup>
import {
  ref,
  computed,
  watch,
  onMounted,
  onBeforeUnmount,
  nextTick,
} from "vue";
import { useRouter } from "vue-router";
import { createPopper } from "@popperjs/core";

import SearchBar from "@/components/widget/SearchBar.vue";
import SuggestionList from "@/components/widget/SuggestionList.vue";
import CommunityCard from "@/components/card/CommunityCard.vue";
import communityHero from "@/assets/images/community-hero.png";
import { useCommunityStore } from "@/store/community.js";
import { useAuthStore } from "@/store/auth.js";

const router = useRouter();
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

    return new URL(
      `../assets/images/re/${fileInfo.name}.${fileInfo.ext}`,
      import.meta.url
    ).href;
  } catch (error) {
    return new URL("../assets/icons/image-placeholder.svg", import.meta.url)
      .href;
  }
};

const searchQuery = ref("");
const showDropdown = ref(false);
const activeTab = ref("all"); // 'all' 또는 'joined'
const wrapper = ref(null);
const searchInput = ref(null);
const popperEl = ref(null);
let popperInstance = null;

// 검색 제안용
const filteredSuggestions = computed(
  () =>
    communityStore.communities
      .map((c) => ({
        title: c.title,
        subtitle: "",
        image: getRegionImage(c.title), // 지역별 이미지 매핑
        link: `/dashboard/community/${c.id}`, // 실제 채팅방 링크로 변경
      }))
      .filter((r) => r.title.includes(searchQuery.value))
      .slice(0, 50) // 최대 50개까지 표시
);

// Popper 인스턴스 생성 및 데이터 로드
onMounted(async () => {
  // 커뮤니티 데이터 로드
  await communityStore.fetchAllCommunities();

  // 사용자 ID가 있으면 참여중인 채팅방도 로드
  if (authStore.user?.id) {
    await communityStore.fetchJoinedCommunities(authStore.user.id);
  }

  await nextTick();
  popperInstance = createPopper(
    searchInput.value.$el || searchInput.value,
    popperEl.value,
    {
      placement: "bottom-start",
      modifiers: [
        { name: "offset", options: { offset: [0, 4] } },
        { name: "preventOverflow", options: { boundary: "viewport" } },
        {
          name: "sameWidth",
          enabled: true,
          phase: "beforeWrite",
          requires: ["computeStyles"],
          fn({ state }) {
            state.styles.popper.width = `${state.rects.reference.width}px`;
          },
          effect({ state }) {
            state.elements.popper.style.width = `${state.elements.reference.offsetWidth}px`;
          },
        },
      ],
    }
  );
  document.addEventListener("click", onClickOutside);
});
onBeforeUnmount(() => {
  popperInstance?.destroy();
  document.removeEventListener("click", onClickOutside);
});
function onClickOutside(e) {
  if (
    wrapper.value &&
    !wrapper.value.contains(e.target) &&
    popperEl.value &&
    !popperEl.value.contains(e.target)
  ) {
    showDropdown.value = false;
  }
}

// 전체 채팅방 정렬 옵션: 'popularity' | 'alpha'
const sortOption = ref("popularity");

// 참여중인 채팅방 정렬 옵션: 'recent' | 'alpha'
const joinedSortOption = ref("recent");

// 지역 필터 옵션
const selectedRegion = ref("");

// 지역 선택 드롭다운
const regionOptions = computed(() =>
  Array.from(
    new Set(communityStore.communities.map((c) => c.title.split(" ")[0]))
  )
);

// 전체 채팅방: 필터 & 정렬된 리스트
const processedAllCommunities = computed(() => {
  let list = communityStore.communities.slice();

  // 실시간 검색 필터링
  if (searchQuery.value.trim()) {
    list = list.filter((c) => c.title.includes(searchQuery.value.trim()));
  }

  // 지역 드롭다운 필터링
  if (selectedRegion.value) {
    list = list.filter((c) => c.title.startsWith(selectedRegion.value));
  }

  // 정렬
  if (sortOption.value === "popularity") {
    list.sort((a, b) => b.currentParticipants - a.currentParticipants);
  } else if (sortOption.value === "alpha") {
    list.sort((a, b) => a.title.localeCompare(b.title, "ko"));
  }

  return list;
});

// 참여중인 채팅방: 필터 & 정렬된 리스트
const processedJoinedCommunities = computed(() => {
  let list = communityStore.joinedCommunities.slice();
  if (joinedSortOption.value === "alpha") {
    list.sort((a, b) => a.title.localeCompare(b.title, "ko"));
  } else if (joinedSortOption.value === "recent") {
    // TODO: 실제로는 참여 시간 기준 정렬해야 함
    list = list; // 현재는 기본 순서 유지
  }
  return list;
});

// 현재 탭에 따른 표시할 리스트
const currentList = computed(() => {
  return activeTab.value === "all"
    ? processedAllCommunities.value
    : processedJoinedCommunities.value;
});

// 페이징
const page = ref(0);
const itemsPerPage = 12;
const totalPages = computed(() =>
  Math.ceil(currentList.value.length / itemsPerPage)
);
const displayedCommunities = computed(() =>
  currentList.value.slice(
    page.value * itemsPerPage,
    (page.value + 1) * itemsPerPage
  )
);
function prevPage() {
  if (page.value > 0) page.value--;
}
function nextPage() {
  if (page.value + 1 < totalPages.value) page.value++;
}

// 검색창 토글 & Popper 업데이트
watch(searchQuery, async (v) => {
  if (v) {
    showDropdown.value = true;
    await nextTick();
    popperInstance.update();
  } else {
    showDropdown.value = false;
  }
});

// 필터링/정렬/탭 변경 시 페이지 리셋
watch(
  [searchQuery, selectedRegion, sortOption, joinedSortOption, activeTab],
  () => {
    page.value = 0;
  }
);
async function openDropdown() {
  if (searchQuery.value) {
    showDropdown.value = true;
    await nextTick();
    popperInstance.update();
  }
}
async function handleSelect(item) {
  searchQuery.value = item.title;
  showDropdown.value = false;

  // 선택된 채팅방으로 직접 이동
  if (item.link && item.link !== "#") {
    router.push(item.link);
  }
}
function goChat(id) {
  router.push({
    name: "CommunityChat",
    params: { id },
  });
}

// 리스트 제목 동적 생성
const getListTitle = () => {
  const baseTitle =
    activeTab.value === "all" ? "온동네 커뮤니티" : "참여중인 채팅방";

  if (activeTab.value === "all" && searchQuery.value.trim()) {
    return `"${searchQuery.value}" 검색 결과 (${currentList.value.length}개)`;
  }

  return baseTitle;
};
</script>

<style scoped>
.suggestion-popper {
  max-height: 300px;
  overflow-y: auto;
}
</style>

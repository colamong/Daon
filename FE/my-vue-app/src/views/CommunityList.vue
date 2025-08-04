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
            이웃 간의 작은 이야기부터 따뜻한 나눔까지,
          </p>
          <p class="text-gray-700 mb-2">온 동네 사람들의 마음을 잇는</p>
          <p class="text-gray-700 mb-5">따뜻한 커뮤니티 공간입니다.</p>
          <p class="text-gray-700">
            우리 동네에 숨겨진 일상의 온기를 만나보세요.
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

    <!-- 정렬 & 필터 바 -->
    <div class="flex items-center justify-between">
      <h3 class="text-2xl font-bold">온동네 커뮤니티</h3>
      <div class="flex items-center space-x-4">
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
    </div>

    <!-- 커뮤니티 카드 그리드 + 페이징 -->
    <section>
      <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-6">
        <CommunityCard
          v-for="post in pagedCommunities"
          :key="post.id"
          :location="post.location"
          :date="post.date"
          :image="post.image"
          :link="post.link"
        />
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
import { createPopper } from "@popperjs/core";

import SearchBar from "@/components/widget/SearchBar.vue";
import SuggestionList from "@/components/widget/SuggestionList.vue";
import CommunityCard from "@/components/card/CommunityCard.vue";
import communityHero from "@/assets/images/community-hero.png";

const searchQuery = ref("");
const showDropdown = ref(false);
const wrapper = ref(null);
const searchInput = ref(null);
const popperEl = ref(null);
let popperInstance = null;

// 더미 커뮤니티 포스트 18개
const communities = [
  {
    id: 1,
    location: "서울특별시 종로구",
    date: "2024-01-24",
    image: "",
    link: "/community/1",
    favorites: 12,
  },
  {
    id: 2,
    location: "서울특별시 중구",
    date: "2024-01-22",
    image: "",
    link: "/community/2",
    favorites: 8,
  },
  {
    id: 3,
    location: "서울특별시 용산구",
    date: "2024-01-20",
    image: "",
    link: "/community/3",
    favorites: 5,
  },
  {
    id: 4,
    location: "서울특별시 성동구",
    date: "2024-01-18",
    image: "",
    link: "/community/4",
    favorites: 15,
  },
  {
    id: 5,
    location: "서울특별시 광진구",
    date: "2024-01-16",
    image: "",
    link: "/community/5",
    favorites: 3,
  },
  {
    id: 6,
    location: "서울특별시 성북구",
    date: "2024-01-14",
    image: "",
    link: "/community/6",
    favorites: 7,
  },
  {
    id: 7,
    location: "서울특별시 강남구",
    date: "2024-01-12",
    image: "",
    link: "/community/7",
    favorites: 25,
  },
  {
    id: 8,
    location: "서울특별시 서초구",
    date: "2024-01-10",
    image: "",
    link: "/community/8",
    favorites: 10,
  },
  {
    id: 9,
    location: "서울특별시 송파구",
    date: "2024-01-08",
    image: "",
    link: "/community/9",
    favorites: 2,
  },
  {
    id: 10,
    location: "서울특별시 강동구",
    date: "2024-01-06",
    image: "",
    link: "/community/10",
    favorites: 6,
  },
  {
    id: 11,
    location: "인천광역시 미추홀구",
    date: "2024-01-04",
    image: "",
    link: "/community/11",
    favorites: 9,
  },
  {
    id: 12,
    location: "인천광역시 연수구",
    date: "2024-01-02",
    image: "",
    link: "/community/12",
    favorites: 4,
  },
  {
    id: 13,
    location: "부산광역시 해운대구",
    date: "2023-12-30",
    image: "",
    link: "/community/13",
    favorites: 20,
  },
  {
    id: 14,
    location: "부산광역시 수영구",
    date: "2023-12-28",
    image: "",
    link: "/community/14",
    favorites: 1,
  },
  {
    id: 15,
    location: "대구광역시 중구",
    date: "2023-12-26",
    image: "",
    link: "/community/15",
    favorites: 11,
  },
  {
    id: 16,
    location: "대구광역시 수성구",
    date: "2023-12-24",
    image: "",
    link: "/community/16",
    favorites: 13,
  },
  {
    id: 17,
    location: "광주광역시 동구",
    date: "2023-12-22",
    image: "",
    link: "/community/17",
    favorites: 0,
  },
  {
    id: 18,
    location: "전라남도 순천시",
    date: "2023-12-20",
    image: "",
    link: "/community/18",
    favorites: 14,
  },
];

// 검색 제안용
const filteredSuggestions = computed(() =>
  communities
    .map((c) => ({ title: c.location, subtitle: "", image: "", link: "#" }))
    .filter((r) => r.title.includes(searchQuery.value))
);

// Popper 인스턴스 생성
onMounted(async () => {
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

// 정렬 옵션: 'popularity' | 'alpha'
const sortOption = ref("popularity");

// 지역 필터 옵션
const selectedRegion = ref("");

// 지역 선택 드롭다운
const regionOptions = computed(() =>
  Array.from(new Set(communities.map((c) => c.location.split(" ")[0])))
);

// 필터 & 정렬된 리스트
const processed = computed(() => {
  let list = communities.slice();
  if (selectedRegion.value) {
    list = list.filter((c) => c.location.startsWith(selectedRegion.value));
  }
  if (sortOption.value === "popularity") {
    list.sort((a, b) => b.favorites - a.favorites);
  } else if (sortOption.value === "alpha") {
    list.sort((a, b) => a.location.localeCompare(b.location, "ko"));
  }
  return list;
});

// 페이징
const page = ref(0);
const itemsPerPage = 6;
const totalPages = computed(() =>
  Math.ceil(processed.value.length / itemsPerPage)
);
const pagedCommunities = computed(() =>
  processed.value.slice(
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
async function openDropdown() {
  if (searchQuery.value) {
    showDropdown.value = true;
    await nextTick();
    popperInstance.update();
  }
}
function handleSelect(item) {
  searchQuery.value = item.title;
  showDropdown.value = false;
  // 선택된 지역으로 필터
  selectedRegion.value = item.title.split(" ")[0];
}
</script>

<style scoped>
.suggestion-popper {
  max-height: 300px;
  overflow-y: auto;
}
</style>

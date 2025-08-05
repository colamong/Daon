<template>
  <div class="py-8 px-4">
    <div class="mx-auto max-w-6xl bg-white pt-10 pb-10 rounded-2xl mb-10">
      <!-- 상단 타이틀 -->
      <div class="text-center mb-12">
        <h1 class="text-4xl font-paperBold text-gray-800">아이 프로필 수정</h1>
      </div>

      <!-- 아이가 없는 경우 -->
      <div v-if="!hasChild" class="text-center py-20">
        <p class="text-xl text-gray-600 mb-8">등록된 아이가 없습니다.</p>
        <button
          @click="goToRegister"
          class="bg-purple-500 text-white px-8 py-4 rounded-lg text-lg font-paperBold hover:bg-purple-600 transition-colors"
        >
          아이 등록하기
        </button>
      </div>

      <!-- 아이가 있는 경우 -->
      <div v-else class="space-y-8">
        <!-- 아이 선택 탭 (여러 명인 경우) -->
        <div v-if="childrenList.length > 1" class="flex justify-center space-x-4 mb-8">
          <button
            v-for="(child, index) in childrenList"
            :key="child.id"
            @click="selectChild(index)"
            class="px-6 py-3 rounded-lg font-paperBold transition-colors"
            :class="{
              'bg-purple-500 text-white': selectedChildIndex === index,
              'bg-gray-200 text-gray-700 hover:bg-gray-300': selectedChildIndex !== index
            }"
          >
            {{ child.name }}
          </button>
        </div>

        <!-- 수정 폼 -->
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-16 px-8">
          <!-- 좌측: 이미지 업로드 -->
          <div class="flex flex-col space-y-6">
            <BaseImageUpload @upload:image="handleImageUpload" :initial-image="childData.profileImage" />
          </div>

          <!-- 우측: 폼 필드들 -->
          <div class="space-y-8">
            <form @submit.prevent="handleUpdateChild" class="space-y-8">
              <!-- 이름 -->
              <div>
                <label for="childName" class="block text-lg font-paperBold text-black mb-3">
                  이름
                </label>
                <input
                  id="childName"
                  v-model="childData.name"
                  type="text"
                  required
                  placeholder="아이의 이름을 입력하세요"
                  class="w-full py-3 px-4 border-2 border-gray-300 rounded-lg focus:outline-none focus:border-purple-500 font-paper text-lg"
                />
              </div>

              <!-- 생년월일 -->
              <div>
                <label for="birthDate" class="block text-lg font-paperBold text-black mb-3">
                  생년월일
                </label>
                <div class="flex gap-2">
                  <select
                    v-model="selectedYear"
                    class="flex-1 py-3 px-4 border-2 border-gray-300 rounded-lg focus:outline-none focus:border-purple-500 font-paper text-lg bg-white"
                  >
                    <option value="">년도</option>
                    <option v-for="year in years" :key="year" :value="year">
                      {{ year }}년
                    </option>
                  </select>
                  <select
                    v-model="selectedMonth"
                    class="flex-1 py-3 px-4 border-2 border-gray-300 rounded-lg focus:outline-none focus:border-purple-500 font-paper text-lg bg-white"
                  >
                    <option value="">월</option>
                    <option v-for="month in 12" :key="month" :value="month">
                      {{ month }}월
                    </option>
                  </select>
                  <select
                    v-model="selectedDay"
                    class="flex-1 py-3 px-4 border-2 border-gray-300 rounded-lg focus:outline-none focus:border-purple-500 font-paper text-lg bg-white"
                  >
                    <option value="">일</option>
                    <option v-for="day in daysInMonth" :key="day" :value="day">
                      {{ day }}일
                    </option>
                  </select>
                </div>
              </div>

              <!-- 성별 -->
              <div>
                <BaseRadioGroup
                  v-model="childData.gender"
                  label="성별"
                  name="gender"
                  :options="genderOptions"
                />
              </div>

              <!-- 관심사 -->
              <div>
                <BaseCheckboxGroup
                  v-model="childData.interests"
                  label="관심사"
                  :options="interestOptions"
                />
              </div>

              <!-- 추가하고 싶은 관심사 -->
              <div>
                <label for="newInterest" class="block text-lg font-paperBold text-black mb-3">
                  추가하고 싶은 관심사
                </label>
                <div class="flex gap-2">
                  <input
                    id="newInterest"
                    v-model="newInterest"
                    type="text"
                    placeholder="새로운 관심사를 입력하세요"
                    class="flex-1 py-3 px-4 border-2 border-gray-300 rounded-lg focus:outline-none focus:border-purple-500 font-paper text-lg"
                    @keypress.enter.prevent="addNewInterest"
                  />
                  <button
                    type="button"
                    @click="addNewInterest"
                    class="px-6 py-3 bg-purple-500 text-white font-paperBold text-lg rounded-lg hover:bg-purple-600 transition-colors"
                  >
                    추가
                  </button>
                </div>
              </div>

              <!-- 수정/삭제 버튼 -->
              <div class="flex gap-4 pt-6">
                <button
                  type="submit"
                  :disabled="loading"
                  class="flex-1 py-4 bg-purple-500 text-white font-paperBold text-lg rounded-lg hover:bg-purple-600 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
                >
                  {{ loading ? "수정 중..." : "수정하기" }}
                </button>
                <button
                  type="button"
                  @click="confirmDelete"
                  class="px-8 py-4 bg-red-500 text-white font-paperBold text-lg rounded-lg hover:bg-red-600 transition-colors"
                >
                  삭제
                </button>
              </div>
            </form>

            <!-- 취소 버튼 -->
            <div class="text-center">
              <button
                @click="goBack"
                class="px-8 py-3 bg-gray-300 text-gray-700 font-paperBold text-lg rounded-lg hover:bg-gray-400 transition-colors"
              >
                취소
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/store/auth'
import BaseImageUpload from '@/components/form/BaseImageUpload.vue'
import BaseRadioGroup from '@/components/form/BaseRadioGroup.vue'
import BaseCheckboxGroup from '@/components/form/BaseCheckboxGroup.vue'

const router = useRouter()
const auth = useAuthStore()

const loading = ref(false)
const childrenList = ref([])
const selectedChildIndex = ref(0)
const selectedYear = ref('')
const selectedMonth = ref('')
const selectedDay = ref('')
const newInterest = ref('')

// 아이 데이터
const childData = reactive({
  id: null,
  name: '',
  birthDate: '',
  gender: '',
  interests: [],
  profileImage: null
})

// 년도 옵션 (현재년도부터 20년 전까지)
const currentYear = new Date().getFullYear()
const years = Array.from({ length: 20 }, (_, i) => currentYear - i)

// 해당 월의 일수 계산
const daysInMonth = computed(() => {
  if (!selectedYear.value || !selectedMonth.value) return 31
  return new Date(selectedYear.value, selectedMonth.value, 0).getDate()
})

// 생년월일 업데이트 감시
watch([selectedYear, selectedMonth, selectedDay], () => {
  if (selectedYear.value && selectedMonth.value && selectedDay.value) {
    const month = selectedMonth.value.toString().padStart(2, '0')
    const day = selectedDay.value.toString().padStart(2, '0')
    childData.birthDate = `${selectedYear.value}-${month}-${day}`
  }
})

// 성별 옵션
const genderOptions = [
  { label: '남자', value: '남자' },
  { label: '여자', value: '여자' }
]

// 관심사 옵션
const interestOptions = ref([
  { label: '스포츠', value: '스포츠' },
  { label: '음식', value: '음식' },
  { label: '여행', value: '여행' },
  { label: '동물', value: '동물' },
  { label: '음악', value: '음악' },
  { label: '춤', value: '춤' },
  { label: '게임', value: '게임' },
  { label: '책읽기', value: '책읽기' },
  { label: '요리', value: '요리' }
])

const hasChild = computed(() => childrenList.value.length > 0)
const selectedChild = computed(() => childrenList.value[selectedChildIndex.value] || {})

// 아이 정보 로드
function loadChildren() {
  const children = JSON.parse(localStorage.getItem('children') || '[]')
  childrenList.value = children
  
  if (children.length > 0) {
    loadChildData(children[0])
  }
}

// 특정 아이 데이터 로드
function loadChildData(child) {
  Object.assign(childData, child)
  
  // 생년월일 파싱
  if (child.birthDate) {
    const [year, month, day] = child.birthDate.split('-')
    selectedYear.value = parseInt(year)
    selectedMonth.value = parseInt(month)
    selectedDay.value = parseInt(day)
  }
}

// 아이 선택
function selectChild(index) {
  selectedChildIndex.value = index
  loadChildData(selectedChild.value)
}

// 이미지 업로드 처리
function handleImageUpload(file) {
  if (file) {
    const reader = new FileReader()
    reader.onload = (e) => {
      childData.profileImage = e.target.result
    }
    reader.readAsDataURL(file)
  }
}

// 새로운 관심사 추가
function addNewInterest() {
  if (!newInterest.value.trim()) {
    alert('관심사를 입력해주세요.')
    return
  }

  const exists = interestOptions.value.find(option => 
    option.value.toLowerCase() === newInterest.value.trim().toLowerCase()
  )

  if (exists) {
    alert('이미 존재하는 관심사입니다.')
    newInterest.value = ''
    return
  }

  const newInterestOption = {
    label: newInterest.value.trim(),
    value: newInterest.value.trim()
  }
  
  interestOptions.value.push(newInterestOption)
  
  if (!childData.interests.includes(newInterestOption.value)) {
    childData.interests.push(newInterestOption.value)
  }
  
  newInterest.value = ''
}

// 아이 정보 수정
async function handleUpdateChild() {
  // 필수 필드 검증
  if (!childData.name.trim()) {
    alert('아이의 이름을 입력해주세요.')
    return
  }

  if (!childData.birthDate) {
    alert('생년월일을 선택해주세요.')
    return
  }

  if (!childData.gender) {
    alert('성별을 선택해주세요.')
    return
  }

  loading.value = true

  try {
    await new Promise(resolve => setTimeout(resolve, 1000))

    // localStorage에서 기존 데이터 업데이트
    const existingChildren = JSON.parse(localStorage.getItem('children') || '[]')
    const childIndex = existingChildren.findIndex(child => child.id === childData.id)
    
    if (childIndex !== -1) {
      existingChildren[childIndex] = {
        ...childData,
        updatedAt: new Date().toISOString()
      }
      localStorage.setItem('children', JSON.stringify(existingChildren))
      
      // auth 스토어에도 업데이트
      if (auth.user) {
        auth.user.children = existingChildren
        localStorage.setItem('auth_user', JSON.stringify(auth.user))
      }
      
      alert(`${childData.name}의 정보가 성공적으로 수정되었습니다! 🎉`)
      router.push({ name: 'ChildProfile' })
    }

  } catch (error) {
    console.error('아이 정보 수정 실패:', error)
    alert('아이 정보 수정에 실패했습니다. 다시 시도해주세요.')
  } finally {
    loading.value = false
  }
}

// 아이 삭제 확인
function confirmDelete() {
  if (confirm(`정말로 ${childData.name}의 정보를 삭제하시겠습니까?\n이 작업은 되돌릴 수 없습니다.`)) {
    deleteChild()
  }
}

// 아이 삭제
function deleteChild() {
  const existingChildren = JSON.parse(localStorage.getItem('children') || '[]')
  const updatedChildren = existingChildren.filter(child => child.id !== childData.id)
  
  localStorage.setItem('children', JSON.stringify(updatedChildren))
  
  // auth 스토어에도 업데이트
  if (auth.user) {
    auth.user.children = updatedChildren
    localStorage.setItem('auth_user', JSON.stringify(auth.user))
  }
  
  alert(`${childData.name}의 정보가 삭제되었습니다.`)
  
  if (updatedChildren.length > 0) {
    router.push({ name: 'ChildProfile' })
  } else {
    router.push({ name: 'Dashboard' })
  }
}

// 페이지 이동 함수들
function goToRegister() {
  router.push({ name: 'RegisterChild' })
}

function goBack() {
  router.push({ name: 'ChildProfile' })
}

// 컴포넌트 마운트 시 실행
onMounted(() => {
  loadChildren()
})
</script>

<style scoped>
/* 필요시 추가 스타일 */
</style>

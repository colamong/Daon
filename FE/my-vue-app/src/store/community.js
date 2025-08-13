import { defineStore } from 'pinia'
import { ref, computed, reactive } from 'vue'
import communityService from '@/services/communityService.js'
import websocketService from '@/services/websocketService.js'

export const useCommunityStore = defineStore('community', () => {
  // ---------- 기본 상태 ----------
  const communities = ref([])
  const activeCommunities = ref([])
  const currentCommunity = ref(null)
  const participants = ref([])
  const messages = ref([]) // (기존 유지: 호환용)
  const loading = ref(false)
  const error = ref(null)

  // 임시 사용자 ID (추후 auth 연동)
  const currentUserId = ref(1)

  // ---------- 실시간용 상태 ----------
  const currentRoomId = ref(null)
  // 방별 메시지 저장소: { [roomId]: Array<msg> }
  const messagesByRoom = reactive({})
  // 방별 중복 방지용 Set: { [roomId]: Set<id> }
  const knownIdsByRoom = reactive({})

  // ---------- Getters ----------
  const isWebSocketConnected = computed(() => websocketService.isConnected.value)
  // 화면에서 바로 바인딩할 수 있게 현재 방 메시지를 노출
  const websocketMessages = computed(() => {
    const rid = currentRoomId.value
    return rid ? (messagesByRoom[rid] || []) : []
  })
  const joinedCommunities = computed(() => activeCommunities.value)

  // 사용자가 특정 커뮤니티에 참여 중인지 확인
  const isJoined = (communityId) =>
    activeCommunities.value.some((c) => c.id === communityId)

  // ---------- 커뮤니티 목록 ----------
  const fetchAllCommunities = async () => {
    try {
      loading.value = true
      error.value = null
      const data = await communityService.getAllCommunities()
      communities.value = data.communities || []
    } catch (err) {
      error.value = '커뮤니티 목록을 불러오는데 실패했습니다.'
    } finally {
      loading.value = false
    }
  }

  const searchCommunities = async (title) => {
    try {
      loading.value = true
      error.value = null
      const data = await communityService.searchCommunities(title)
      return data.communities || []
    } catch (err) {
      error.value = '커뮤니티 검색에 실패했습니다.'
      return []
    } finally {
      loading.value = false
    }
  }

  const fetchActiveCommunities = async (userId = currentUserId.value) => {
    try {
      loading.value = true
      error.value = null
      const data = await communityService.getActiveCommunities(userId)
      activeCommunities.value = data.communities || []
    } catch (err) {
      error.value = '참여 중인 커뮤니티 목록을 불러오는데 실패했습니다.'
    } finally {
      loading.value = false
    }
  }
  // 별칭
  const fetchJoinedCommunities = fetchActiveCommunities

  // ---------- 참여/나가기 ----------
  const joinCommunity = async (communityId, userId = currentUserId.value) => {
    try {
      loading.value = true
      error.value = null
      const data = await communityService.joinCommunity(communityId, userId)
      await fetchActiveCommunities(userId) // 목록 갱신
      return data
    } catch (err) {
      if (err.response?.data?.message?.includes('already participating')) {
        error.value = '이미 참여 중인 커뮤니티입니다.'
      } else {
        error.value = '커뮤니티 참여에 실패했습니다.'
      }
      throw err
    } finally {
      loading.value = false
    }
  }

  const leaveCommunity = async (communityId, userId = currentUserId.value) => {
    try {
      loading.value = true
      error.value = null
      await communityService.leaveCommunity(communityId, userId)
      await fetchActiveCommunities(userId)

      if (currentCommunity.value?.id === communityId) {
        currentCommunity.value = null
        participants.value = []
        messages.value = []
        messagesByRoom[communityId] = []
        knownIdsByRoom[communityId] = new Set()
      }
    } catch (err) {
      if (err.response?.data?.message?.includes('not participating')) {
        error.value = '참여하지 않은 커뮤니티입니다.'
      } else {
        error.value = '커뮤니티 나가기에 실패했습니다.'
      }
      throw err
    } finally {
      loading.value = false
    }
  }

  // ---------- 정보 ----------
  const fetchParticipants = async (communityId) => {
    try {
      loading.value = true
      error.value = null
      const data = await communityService.getParticipants(communityId)
      participants.value = data || []
    } catch (err) {
      error.value = '참여자 목록을 불러오는데 실패했습니다.'
    } finally {
      loading.value = false
    }
  }

  const fetchMessages = async (communityId) => {
    try {
      loading.value = true
      error.value = null
      const data = await communityService.getMessages(communityId)
      messages.value = data || [] // 호환용
      // 방별 저장소 채우기 + 중복세트 초기화
      messagesByRoom[communityId] = data || []
      knownIdsByRoom[communityId] = new Set((data || []).map((m) => m.id))

      // (참고) 이전에 websocketService.setMessages(data, currentUserId) 쓰던 것 제거
      // 현재 websocketService.setMessages는 (messages)만 받는 시그니처입니다.
      websocketService.setMessages(data || [])
    } catch (err) {
      error.value = '메시지 히스토리를 불러오는데 실패했습니다.'
    } finally {
      loading.value = false
    }
  }

  // ---------- WebSocket ----------
  const connectWebSocket = async () => {
    try {
      await websocketService.connect()
      bindReconnectAutoResubscribe() // 연결되면 재구독 바인딩
      bindOnMessageOnce()            // 수신 콜백 1회 바인딩
    } catch (err) {
      error.value = 'WebSocket 연결에 실패했습니다.'
      throw err
    }
  }

  const disconnectWebSocket = () => {
    websocketService.disconnect()
  }

  const subscribeToCommunity = (communityId) => {
    try {
      currentRoomId.value = communityId
      // 저장소 준비
      if (!messagesByRoom[communityId]) messagesByRoom[communityId] = []
      if (!knownIdsByRoom[communityId]) knownIdsByRoom[communityId] = new Set()
      websocketService.subscribeToCommunity(communityId)
    } catch (err) {
      error.value = '커뮤니티 구독에 실패했습니다.'
      throw err
    }
  }

  // 새 메시지 전송 (WS 사용 시)
  const sendMessage = (communityId, message, userId = currentUserId.value) => {
    try {
      // ⚠️ 인자 순서 복구: (communityId, message, userId)
      websocketService.sendMessage(communityId, message, userId)
    } catch (err) {
      error.value = '메시지 전송에 실패했습니다.'
      throw err
    }
  }

  // ---------- 내부 헬퍼 ----------
  // 재연결 시 자동 재구독
  const bindReconnectAutoResubscribe = () => {
    if (bindReconnectAutoResubscribe._bound) return
    websocketService.onConnect((ok) => {
      if (ok && currentRoomId.value) {
        try {
          websocketService.subscribeToCommunity(currentRoomId.value)
        } catch (e) {
        }
      }
    })
    bindReconnectAutoResubscribe._bound = true
  }

  // 수신 콜백을 한 번만 바인딩해서 현재 방 메시지에 즉시 반영
  const bindOnMessageOnce = () => {
    if (bindOnMessageOnce._bound) return
    websocketService.onMessage((raw) => {
      const rid = currentRoomId.value
      if (!rid) return
      if (!messagesByRoom[rid]) messagesByRoom[rid] = []
      if (!knownIdsByRoom[rid]) knownIdsByRoom[rid] = new Set()

      const msg = {
        id: raw.id,
        userId: raw.userId,
        message: raw.message ?? raw.text,
        sentAt: raw.sentAt ?? raw.timestamp,
      }
      if (!knownIdsByRoom[rid].has(msg.id)) {
        messagesByRoom[rid].push(msg)
        knownIdsByRoom[rid].add(msg.id)
      }
    })
    bindOnMessageOnce._bound = true
  }

  // ---------- 기타 ----------
  const setCurrentCommunity = (community) => {
    currentCommunity.value = community
  }

  const clearError = () => {
    error.value = null
  }

  const clearMessages = () => {
    messages.value = []
    const rid = currentRoomId.value
    if (rid) {
      messagesByRoom[rid] = []
      knownIdsByRoom[rid] = new Set()
    }
    websocketService.clearMessages()
  }

  const setCurrentUserId = (userId) => {
    currentUserId.value = userId
  }

  // ---------- 노출 ----------
  return {
    // State
    communities,
    activeCommunities,
    currentCommunity,
    participants,
    messages, // (호환용)
    loading,
    error,
    currentUserId,

    // Realtime State
    currentRoomId,
    websocketMessages, // 화면에서 바로 바인딩 가능

    // Getters
    isWebSocketConnected,
    joinedCommunities,
    isJoined,

    // Actions
    fetchAllCommunities,
    searchCommunities,
    fetchActiveCommunities,
    fetchJoinedCommunities,
    joinCommunity,
    leaveCommunity,
    fetchParticipants,
    fetchMessages,

    connectWebSocket,
    disconnectWebSocket,
    subscribeToCommunity,
    sendMessage,

    setCurrentCommunity,
    clearError,
    clearMessages,
    setCurrentUserId,
  }
})

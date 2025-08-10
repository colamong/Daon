import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import communityService from '@/services/communityService.js'
import websocketService from '@/services/websocketService.js'

export const useCommunityStore = defineStore('community', () => {
  // 상태
  const communities = ref([])
  const activeCommunities = ref([])
  const currentCommunity = ref(null)
  const participants = ref([])
  const messages = ref([])
  const loading = ref(false)
  const error = ref(null)

  // 현재 사용자 ID (임시로 1로 설정, 나중에 auth store에서 가져올 예정)
  const currentUserId = ref(1)

  // Getters
  const isWebSocketConnected = computed(() => websocketService.isConnected.value)
  const websocketMessages = computed(() => websocketService.messages)
  const joinedCommunities = computed(() => activeCommunities.value)
  
  // 사용자가 특정 커뮤니티에 참여 중인지 확인
  const isJoined = (communityId) => {
    return activeCommunities.value.some(community => community.id === communityId)
  }

  // Actions - 커뮤니티 목록 관련
  const fetchAllCommunities = async () => {
    try {
      loading.value = true
      error.value = null
      const data = await communityService.getAllCommunities()
      communities.value = data.communities || []
    } catch (err) {
      error.value = '커뮤니티 목록을 불러오는데 실패했습니다.'
      console.error('Failed to fetch communities:', err)
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
      console.error('Failed to search communities:', err)
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
      console.error('Failed to fetch active communities:', err)
    } finally {
      loading.value = false
    }
  }
  
  // fetchJoinedCommunities 별칭 추가 (CommunityChat에서 사용)
  const fetchJoinedCommunities = fetchActiveCommunities

  // Actions - 커뮤니티 참여 관련
  const joinCommunity = async (communityId, userId = currentUserId.value) => {
    try {
      loading.value = true
      error.value = null
      const data = await communityService.joinCommunity(communityId, userId)
      
      // 성공하면 활성 커뮤니티 목록 새로고침
      await fetchActiveCommunities(userId)
      
      return data
    } catch (err) {
      if (err.response?.data?.message?.includes('already participating')) {
        error.value = '이미 참여 중인 커뮤니티입니다.'
      } else {
        error.value = '커뮤니티 참여에 실패했습니다.'
      }
      console.error('Failed to join community:', err)
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
      
      // 성공하면 활성 커뮤니티 목록 새로고침
      await fetchActiveCommunities(userId)
      
      // 현재 보고 있던 커뮤니티에서 나간 경우 초기화
      if (currentCommunity.value?.id === communityId) {
        currentCommunity.value = null
        participants.value = []
        messages.value = []
      }
    } catch (err) {
      if (err.response?.data?.message?.includes('not participating')) {
        error.value = '참여하지 않은 커뮤니티입니다.'
      } else {
        error.value = '커뮤니티 나가기에 실패했습니다.'
      }
      console.error('Failed to leave community:', err)
      throw err
    } finally {
      loading.value = false
    }
  }

  // Actions - 커뮤니티 정보 관련
  const fetchParticipants = async (communityId) => {
    try {
      loading.value = true
      error.value = null
      const data = await communityService.getParticipants(communityId)
      participants.value = data || []
    } catch (err) {
      error.value = '참여자 목록을 불러오는데 실패했습니다.'
      console.error('Failed to fetch participants:', err)
    } finally {
      loading.value = false
    }
  }

  const fetchMessages = async (communityId) => {
    try {
      loading.value = true
      error.value = null
      const data = await communityService.getMessages(communityId)
      messages.value = data || []
      
      // WebSocket 서비스에 메시지 히스토리 설정
      websocketService.setMessages(data || [], currentUserId.value)
    } catch (err) {
      error.value = '메시지 히스토리를 불러오는데 실패했습니다.'
      console.error('Failed to fetch messages:', err)
    } finally {
      loading.value = false
    }
  }

  // Actions - WebSocket 관련
  const connectWebSocket = async () => {
    try {
      await websocketService.connect()
      console.log('WebSocket connected successfully')
    } catch (err) {
      error.value = 'WebSocket 연결에 실패했습니다.'
      console.error('Failed to connect WebSocket:', err)
      throw err
    }
  }

  const disconnectWebSocket = () => {
    websocketService.disconnect()
    console.log('WebSocket disconnected')
  }

  const subscribeToCommunity = (communityId) => {
    try {
      websocketService.subscribeToCommunity(communityId)
      console.log(`Subscribed to community ${communityId}`)
    } catch (err) {
      error.value = '커뮤니티 구독에 실패했습니다.'
      console.error('Failed to subscribe to community:', err)
      throw err
    }
  }

  const sendMessage = (communityId, message, userId = currentUserId.value) => {
    try {
      websocketService.sendMessage(communityId, userId, message)
    } catch (err) {
      error.value = '메시지 전송에 실패했습니다.'
      console.error('Failed to send message:', err)
      throw err
    }
  }

  // Actions - 현재 커뮤니티 설정
  const setCurrentCommunity = (community) => {
    currentCommunity.value = community
  }

  const clearError = () => {
    error.value = null
  }

  const clearMessages = () => {
    messages.value = []
    websocketService.clearMessages()
  }

  // 현재 사용자 ID 설정 (나중에 auth와 연동 시 사용)
  const setCurrentUserId = (userId) => {
    currentUserId.value = userId
    websocketService.setCurrentUserId(userId)
  }

  return {
    // State
    communities,
    activeCommunities,
    currentCommunity,
    participants,
    messages,
    loading,
    error,
    currentUserId,

    // Getters
    isWebSocketConnected,
    websocketMessages,
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
    setCurrentUserId
  }
})
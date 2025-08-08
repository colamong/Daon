import axiosInstance from '@/utils/axios.js'

const BASE_URL = '/api/community'

export const communityService = {
  // 전체 커뮤니티 목록 조회
  async getAllCommunities() {
    try {
      const response = await axiosInstance.get(BASE_URL)
      return response.data.data
    } catch (error) {
      console.error('Failed to fetch communities:', error)
      throw error
    }
  },

  // 커뮤니티 검색 (지역명)
  async searchCommunities(title) {
    try {
      const encodedTitle = encodeURIComponent(title)
      const response = await axiosInstance.get(`${BASE_URL}/search?title=${encodedTitle}`)
      return response.data.data
    } catch (error) {
      console.error('Failed to search communities:', error)
      throw error
    }
  },

  // 참여 중인 커뮤니티 목록 조회
  async getActiveCommunities(userId) {
    try {
      const response = await axiosInstance.get(`${BASE_URL}/active?userId=${userId}`)
      return response.data.data
    } catch (error) {
      console.error('Failed to fetch active communities:', error)
      throw error
    }
  },

  // 커뮤니티 입장
  async joinCommunity(communityId, userId) {
    try {
      const response = await axiosInstance.post(`${BASE_URL}/${communityId}/join`, {
        userId
      })
      return response.data.data
    } catch (error) {
      console.error('Failed to join community:', error)
      throw error
    }
  },

  // 커뮤니티 퇴장
  async leaveCommunity(communityId, userId) {
    try {
      const response = await axiosInstance.delete(`${BASE_URL}/${communityId}/leave`, {
        data: { userId }
      })
      return response.data
    } catch (error) {
      console.error('Failed to leave community:', error)
      throw error
    }
  },

  // 커뮤니티 참여자 목록 조회
  async getParticipants(communityId) {
    try {
      const response = await axiosInstance.get(`${BASE_URL}/${communityId}/participants`)
      return response.data.data
    } catch (error) {
      console.error('Failed to fetch participants:', error)
      throw error
    }
  },

  // 커뮤니티 메시지 히스토리 조회
  async getMessages(communityId) {
    try {
      const response = await axiosInstance.get(`${BASE_URL}/${communityId}/messages`)
      return response.data.data
    } catch (error) {
      console.error('Failed to fetch messages:', error)
      throw error
    }
  }
}

export default communityService
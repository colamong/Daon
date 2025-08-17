import { reactive, ref } from 'vue'
import SockJS from 'sockjs-client'
import { Client } from '@stomp/stompjs'

class WebSocketService {
  constructor() {
    this.client = null
    this.isConnected = ref(false)
    this.currentCommunityId = ref(null)
    this.messages = reactive([])
    this.connectionCallbacks = []
    this.messageCallbacks = []
    this.currentSubscription = null
  }

  // WebSocket 연결
  async connect() {
    if (this.client && this.client.connected) {
      return
    }

    try {
      // 환경에 따른 WebSocket URL 자동 설정
      const USE_PROXY = import.meta?.env?.VITE_USE_PROXY === 'true'
      
      let wsUrl
      if (USE_PROXY) {
        // 개발 환경: Vite 프록시를 통해 연결
        wsUrl = `${window.location.protocol}//${window.location.host}/ws`
      } else {
        // 프로덕션 환경: 직접 백엔드로 연결
        const WS_BASE_URL = import.meta?.env?.VITE_API_BASE_URL || 'https://i13a706.p.ssafy.io/stt'
        wsUrl = `${WS_BASE_URL}/ws`
      }
      
      
      const socket = new SockJS(wsUrl)

      this.client = new Client({
        webSocketFactory: () => socket,
        debug: () => {},  // STOMP debug 로그 비활성화
        reconnectDelay: 5000,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000,
      })

      this.client.onConnect = (frame) => {
        this.isConnected.value = true
        this.connectionCallbacks.forEach((cb) => cb(true))
      }

      this.client.onStompError = (frame) => {
        this.isConnected.value = false
        this.connectionCallbacks.forEach((cb) => cb(false))
      }

      this.client.onDisconnect = () => {
        this.isConnected.value = false
        this.currentCommunityId.value = null
      }

      this.client.activate()

      return new Promise((resolve, reject) => {
        const timeout = setTimeout(() => reject(new Error('WebSocket connection timeout')), 10000)
        this.onConnect((success) => {
          clearTimeout(timeout)
          success ? resolve() : reject(new Error('Failed to connect to WebSocket'))
        })
      })
    } catch (error) {
      throw error
    }
  }

  // WebSocket 연결 해제
  disconnect() {
    if (this.client) {
      if (this.currentSubscription) {
        this.currentSubscription.unsubscribe()
        this.currentSubscription = null
      }
      this.client.deactivate()
      this.client = null
      this.isConnected.value = false
      this.currentCommunityId.value = null
      this.messages.length = 0
    }
  }

  // 커뮤니티 채팅방 구독
  subscribeToCommunity(communityId) {
    if (!this.client || !this.client.connected) {
      throw new Error('WebSocket is not connected')
    }

    // 이전 구독 해제
    if (this.currentSubscription) {
      this.currentSubscription.unsubscribe()
    }

    this.currentCommunityId.value = communityId

    // 새로운 커뮤니티 구독
    this.currentSubscription = this.client.subscribe(`/topic/community/${communityId}`, (message) => {
      try {
        const chatMessage = JSON.parse(message.body)

        // ✅ 원래 형식으로 복구: text / timestamp 사용, isMine 고정(false 또는 기존 기본값)
        this.messages.push({
          id: chatMessage.id,
          text: chatMessage.message,
          userId: chatMessage.userId,
          userName: chatMessage.userName,
          userProfileImg: chatMessage.userProfileImg,
          timestamp: chatMessage.sentAt,
          messageType: chatMessage.messageType || 'USER',
          isMine: false,
        })

        this.messageCallbacks.forEach((cb) => cb(chatMessage))
      } catch (error) {
      }
    })

  }

  // 메시지 전송 (시그니처 원래대로: communityId, message, userId)
  sendMessage(communityId, message, userId) {
    if (!this.client || !this.client.connected) {
      throw new Error('WebSocket is not connected')
    }

    const messageData = {
      userId: parseInt(userId),
      message: String(message || '').trim(),
    }

    this.client.publish({
      destination: `/app/community/${communityId}`,
      body: JSON.stringify(messageData),
    })

  }

  // 연결 상태 콜백 등록
  onConnect(callback) {
    this.connectionCallbacks.push(callback)
  }

  // 메시지 수신 콜백 등록
  onMessage(callback) {
    this.messageCallbacks.push(callback)
  }

  // 메시지 초기화
  clearMessages() {
    this.messages.length = 0
  }

  // 메시지 설정 (히스토리 로드 시 사용) — ✅ 원래 시그니처로 복구
  setMessages(messages) {
    this.messages.length = 0
    messages.forEach((msg) => {
      this.messages.push({
        id: msg.id,
        text: msg.message,      // ← text
        userId: msg.userId,
        userName: msg.userName,
        userProfileImg: msg.userProfileImg,
        timestamp: msg.sentAt,  // ← timestamp
        messageType: msg.messageType || 'USER',
        isMine: false,
      })
    })
  }
}

// 싱글톤 인스턴스
const websocketService = new WebSocketService()
export default websocketService

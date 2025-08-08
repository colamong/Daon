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
  }

  // WebSocket 연결
  async connect() {
    if (this.client && this.client.connected) {
      console.log('Already connected to WebSocket')
      return
    }

    try {
      // SockJS 소켓 생성
      const socket = new SockJS('http://localhost:8080/ws')
      
      // STOMP 클라이언트 생성
      this.client = new Client({
        webSocketFactory: () => socket,
        debug: (str) => {
          console.log('STOMP Debug:', str)
        },
        reconnectDelay: 5000,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000,
      })

      // 연결 성공 콜백
      this.client.onConnect = (frame) => {
        console.log('WebSocket Connected:', frame)
        this.isConnected.value = true
        
        // 연결 성공 콜백 실행
        this.connectionCallbacks.forEach(callback => callback(true))
      }

      // 연결 실패 콜백
      this.client.onStompError = (frame) => {
        console.error('WebSocket STOMP Error:', frame.headers['message'])
        console.error('Error details:', frame.body)
        this.isConnected.value = false
        
        // 연결 실패 콜백 실행
        this.connectionCallbacks.forEach(callback => callback(false))
      }

      // 연결 해제 콜백
      this.client.onDisconnect = () => {
        console.log('WebSocket Disconnected')
        this.isConnected.value = false
        this.currentCommunityId.value = null
      }

      // 연결 시작
      this.client.activate()
      
      return new Promise((resolve, reject) => {
        const timeout = setTimeout(() => {
          reject(new Error('WebSocket connection timeout'))
        }, 10000)

        this.onConnect((success) => {
          clearTimeout(timeout)
          if (success) {
            resolve()
          } else {
            reject(new Error('Failed to connect to WebSocket'))
          }
        })
      })
    } catch (error) {
      console.error('WebSocket connection error:', error)
      throw error
    }
  }

  // WebSocket 연결 해제
  disconnect() {
    if (this.client) {
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
        
        // 메시지를 reactive 배열에 추가
        this.messages.push({
          id: chatMessage.id,
          text: chatMessage.message,
          userId: chatMessage.userId,
          userName: chatMessage.userName,
          timestamp: chatMessage.sentAt,
          isMine: false // 나중에 현재 유저와 비교해서 설정
        })

        // 메시지 콜백 실행
        this.messageCallbacks.forEach(callback => callback(chatMessage))
        
      } catch (error) {
        console.error('Error parsing message:', error)
      }
    })

    console.log(`Subscribed to community ${communityId}`)
  }

  // 메시지 전송
  sendMessage(communityId, userId, message) {
    if (!this.client || !this.client.connected) {
      throw new Error('WebSocket is not connected')
    }

    const messageData = {
      userId: parseInt(userId),
      message: message.trim()
    }

    this.client.publish({
      destination: `/app/community/${communityId}`,
      body: JSON.stringify(messageData)
    })

    console.log('Message sent:', messageData)
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

  // 메시지 설정 (히스토리 로드 시 사용)
  setMessages(messages, currentUserId) {
    this.messages.length = 0
    messages.forEach(msg => {
      this.messages.push({
        id: msg.id,
        text: msg.message,
        userId: msg.userId,
        userName: msg.userName,
        timestamp: msg.sentAt,
        isMine: msg.userId === currentUserId
      })
    })
  }
}

// 싱글톤 인스턴스 생성
const websocketService = new WebSocketService()

export default websocketService
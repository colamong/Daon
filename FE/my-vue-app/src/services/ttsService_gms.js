// src/services/ttsService_gms.js
import apiClient from '@/utils/axios.js'
import { useAuthStore } from '@/store/auth'

/** 내부 상태 (중복 재생 방지용) */
let currentController = null
let currentObjectUrl = null

/** 선택: 로그인 사용자 ID 필요하면 사용 */
function getUserId() {
  const auth = useAuthStore()
  return auth.user?.id ?? null
}

/** 텍스트 길이 보정(최대 4096자) */
function clampText(text = '') {
  const s = String(text).trim()
  return s.length > 4096 ? s.slice(0, 4096) : s
}

/** 스트림 URL 생성기 - 서버의 ConversationTtsController 규격을 따름 */
export function buildStreamUrl(text, {
  voice = 'nova',
  format = 'wav',
  speed = 1.0,
  evalMode = false, // 발음평가 동시 전송시 true (백엔드 지원 시)
} = {}) {
  const qs = new URLSearchParams({
    text: clampText(text),
    voice,
    format,
    speed: String(speed),
    eval: String(Boolean(evalMode)),
  })
  return `/api/conversation/tts/stream?${qs.toString()}`
}

/** 내부: fetch로 blob 받아서 ObjectURL 반환 (axios 가능하지만 fetch가 스트림/Abort에 안전) */
async function fetchAudioObjectUrl(url, { signal } = {}) {
  // apiClient에 Authorization이 있을 수 있으므로 그걸 그대로 복사
  const headers = {}
  const auth = apiClient.defaults.headers.common?.Authorization
  if (auth) headers.Authorization = auth

  const res = await fetch(url, {
    method: 'GET',
    credentials: 'include',   // 쿠키 인증 사용 시
    headers,                  // 토큰 인증 사용 시
    signal,
  })
  if (!res.ok) {
    const msg = await safeReadText(res)
    throw new Error(`TTS 실패(${res.status}): ${msg}`)
  }
  const blob = await res.blob() // audio/wav
  return URL.createObjectURL(blob)
}

async function safeReadText(res) {
  try { return await res.text() } catch { return '' }
}

/** 재생 중단/리소스 정리 */
export function stop(audioEl) {
  if (currentController) currentController.abort()
  if (audioEl && !audioEl.paused) audioEl.pause()
  if (currentObjectUrl) {
    URL.revokeObjectURL(currentObjectUrl)
    currentObjectUrl = null
  }
}

/** 텍스트를 읽어줘 (WAV 기본). audioEl이 없으면 내부 Audio로 재생 후 반환 */
export async function playText(text, {
  voice = 'nova',
  format = 'wav',
  speed = 1.0,
  evalMode = false,
} = {}, audioEl = null) {
  const url = buildStreamUrl(text, { voice, format, speed, evalMode })
  return playByUrl(url, audioEl)
}

/** 이미 만든 audioUrl을 재생 */
export async function playByUrl(audioUrl, audioEl = null) {
  // 이전 재생 중단
  stop(audioEl)

  currentController = new AbortController()
  const objectUrl = await fetchAudioObjectUrl(audioUrl, { signal: currentController.signal })
  currentObjectUrl = objectUrl

  // 오디오 엘리먼트 준비
  const el = audioEl ?? new Audio()
  el.src = objectUrl

  // 모바일 자동재생 정책 회피: 호출측에서 반드시 사용자 제스처 내에서 호출 권장
  await el.play()

  // 재생이 끝나면 리소스 정리
  const onEnded = () => { cleanup(el) }
  const onError = () => { cleanup(el) }
  el.addEventListener('ended', onEnded, { once: true })
  el.addEventListener('error', onError, { once: true })

  return el
}

function cleanup(el) {
  if (currentObjectUrl) {
    URL.revokeObjectURL(currentObjectUrl)
    currentObjectUrl = null
  }
  // src 정리(원하면 유지해도 됨)
  if (el) el.removeAttribute('src')
}

/** 텍스트를 미리 받아서 오디오 URL만 만들고 싶을 때 (프리로드/프리페치) */
export async function preloadObjectUrl(text, opts = {}) {
  stop() // 기존 것 중단
  currentController = new AbortController()
  currentObjectUrl = await fetchAudioObjectUrl(buildStreamUrl(text, opts), { signal: currentController.signal })
  return currentObjectUrl
}

const ttsService = {
  getUserId,          // 필요하면 외부에서 사용
  buildStreamUrl,
  playText,
  playByUrl,
  preloadObjectUrl,
  stop,
}

export default ttsService

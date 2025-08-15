// 기존 speechService.js 건드리지 않기 위해 분리한 TTS 전용 서비스

class TTSService {
  constructor(synthesis = (typeof window !== 'undefined' ? window.speechSynthesis : null)) {
    this.synthesis = synthesis
    this.voices = []
    this._onVoicesChanged = null
    if (this.synthesis) this._init()
  }

  get supported() {
    return !!this.synthesis
  }

  _init() {
    this._loadVoices()
    this._onVoicesChanged = this._loadVoices.bind(this)
    this.synthesis.addEventListener('voiceschanged', this._onVoicesChanged)
  }

  _loadVoices() {
    if (!this.synthesis) return
    const list = this.synthesis.getVoices()
    if (list?.length) this.voices = list
  }

  async ensureVoicesLoaded(timeout = 1500) {
    if (!this.synthesis) return
    this._loadVoices()
    if (this.voices.length) return
    await new Promise((resolve) => {
      const handler = () => {
        this._loadVoices()
        this.synthesis.removeEventListener('voiceschanged', handler)
        resolve()
      }
      this.synthesis.addEventListener('voiceschanged', handler)
      setTimeout(() => {
        this.synthesis.removeEventListener('voiceschanged', handler)
        resolve()
      }, timeout)
    })
  }

  getKoreanVoice(preferredName) {
    if (!this.voices.length) return null
    if (preferredName) {
      const exact = this.voices.find(v => v.name === preferredName)
      if (exact) return exact
      const loose = this.voices.find(v => v.name?.toLowerCase().includes(preferredName.toLowerCase()))
      if (loose) return loose
    }
    
    // 모바일 환경에서 더 나은 한국어 음성 선택
    const isMobile = /Android|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent)
    
    if (isMobile) {
      // 여성 음성 우선 선택 (더 자연스러운 목소리)
      const femaleVoices = this.voices.filter(v => 
        (v.lang || '').toLowerCase().startsWith('ko') && 
        (v.name?.toLowerCase().includes('female') || 
         v.name?.toLowerCase().includes('heami') ||
         v.name?.toLowerCase().includes('standard-a') ||
         v.name?.includes('여성') ||
         v.name?.includes('Female'))
      )
      
      if (femaleVoices.length > 0) {
        return femaleVoices[0]
      }
      
      // 일반 한국어 음성 중에서 품질 좋은 것 선택
      const koVoices = this.voices.filter(v => (v.lang || '').toLowerCase().startsWith('ko'))
      if (koVoices.length > 0) {
        // 시스템 기본 음성보다는 Google이나 Microsoft 음성 선호
        const preferredVoice = koVoices.find(v => 
          v.name?.includes('Google') || v.name?.includes('Microsoft')
        )
        return preferredVoice || koVoices[0]
      }
    }
    
    const ko = this.voices.find(v => (v.lang || '').toLowerCase().startsWith('ko'))
    return ko || this.voices[0] || null
  }

  cancel() {
    if (this.synthesis) this.synthesis.cancel()
  }

  // 긴 문장 안전하게 분할
  chunkText(text, maxLen = 180) {
    const t = String(text || '').trim()
    if (!t) return []
    if (t.length <= maxLen) return [t]

    const parts = t
      .split(/([\.!?…]|[\n])/)
      .reduce((acc, cur) => {
        if (!acc.length) return [cur]
        if (/^[\.!?…\n]$/.test(cur)) acc[acc.length - 1] += cur
        else acc.push(cur)
        return acc
      }, [])
      .map(s => s.trim())
      .filter(Boolean)

    const chunks = []
    let buf = ''
    for (const p of parts) {
      const next = (buf ? buf + ' ' : '') + p
      if (next.length > maxLen) {
        if (buf) chunks.push(buf)
        if (p.length > maxLen) {
          for (let i = 0; i < p.length; i += maxLen) chunks.push(p.slice(i, i + maxLen))
          buf = ''
        } else {
          buf = p
        }
      } else {
        buf = next
      }
    }
    if (buf) chunks.push(buf)
    return chunks
  }

  async speakText(text, opts = {}) {
    if (!this.supported) throw new Error('SpeechSynthesis not supported')

    const {
      lang = 'ko-KR',
      rate = 1,
      pitch = 1,
      volume = 1,
      voiceName,
      interrupt = true,
    } = opts

    // 모바일 환경 감지 및 설정 조정
    const isMobile = /Android|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent)
    const finalRate = isMobile ? Math.min(rate * 0.9, 1) : rate  // 모바일에서 속도 약간 조정
    const finalPitch = isMobile ? Math.max(pitch * 1.1, 1) : pitch  // 모바일에서 음높이 약간 높게

    if (interrupt) this.cancel()
    await this.ensureVoicesLoaded()

    const voice = this.getKoreanVoice(voiceName)
    const chunks = this.chunkText(text)
    if (!chunks.length) return

    for (const c of chunks) {
      await new Promise((resolve, reject) => {
        const u = new SpeechSynthesisUtterance(c)
        u.lang = lang
        u.rate = finalRate
        u.pitch = finalPitch
        u.volume = volume
        if (voice) u.voice = voice
        u.onend = resolve
        u.onerror = (e) => reject(e?.error || e)
        this.synthesis.speak(u)
      })
    }
  }

  dispose() {
    try {
      if (this.synthesis && this._onVoicesChanged) {
        this.synthesis.removeEventListener('voiceschanged', this._onVoicesChanged)
      }
    } finally {
      this.cancel()
    }
  }
}

export const ttsService = new TTSService()
export default ttsService

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

    if (interrupt) this.cancel()
    await this.ensureVoicesLoaded()

    const voice = this.getKoreanVoice(voiceName)
    const chunks = this.chunkText(text)
    if (!chunks.length) return

    for (const c of chunks) {
      await new Promise((resolve, reject) => {
        const u = new SpeechSynthesisUtterance(c)
        u.lang = lang
        u.rate = rate
        u.pitch = pitch
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

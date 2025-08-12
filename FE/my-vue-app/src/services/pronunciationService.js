import axios from 'axios'
const API_BASE = import.meta.env.VITE_PRONUN_API_BASE || 'https://i13a706.p.ssafy.io/stt'

export async function evaluatePronunciation(questionId, file) {
  const form = new FormData()
  const name = file.name || 'speech.webm'
  const type = file.type || 'audio/webm'
  form.append('audio', new File([file], name, { type }))

  const { data } = await axios.post(
    `${API_BASE}/api/evaluate/${questionId}`,
    form,
    { headers: { 'Content-Type': 'multipart/form-data' } }
  )
  return data // { score, transcribed, reference }
}

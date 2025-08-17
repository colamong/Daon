
import axios from 'axios';
const API_BASE = import.meta.env.VITE_PRONUN_API_BASE || 'https://i13a706.p.ssafy.io';

export async function evaluatePronunciation(questionId, fileOrBlob) {
  const wavBlob = await ensureMono16kWav(fileOrBlob);  

  const meta = await readWavMeta(wavBlob); 
  
  const form = new FormData();
  form.append('audio', new File([wavBlob], 'speech.wav', { type: 'audio/wav' }));
  const { data } = await axios.post(`${API_BASE}/api/evaluate/${questionId}`, form);
  return data; // { score, transcribed, reference }
}

/* ===== Helper: mono/16k WAV 보장 ===== */
// file이 이미 audio/wav라도 샘플레이트/채널 확신 못하므로 안전하게 재정규화
async function ensureMono16kWav(file) {
  if (file.type === 'audio/wav' || file.type === 'audio/x-wav') {
    return convertAnyToMono16kWav(file);
  }
  return convertAnyToMono16kWav(file);
}

async function convertAnyToMono16kWav(file) {
  const arrayBuf = await file.arrayBuffer();

  // 1) 디코드 (브라우저 내장 디코더 이용)
  const AC = window.AudioContext || window.webkitAudioContext;
  if (!AC) throw new Error('AudioContext not supported');
  const ctx = new AC();

  // Safari 호환: slice(0)로 ArrayBuffer 복제
  const audioBuf = await ctx.decodeAudioData(arrayBuf.slice(0));

  // 2) 모노화
  const ch = audioBuf.numberOfChannels;
  const len = audioBuf.length;
  const mono = new Float32Array(len);
  if (ch === 1) {
    audioBuf.copyFromChannel(mono, 0);
  } else {
    const tmp = new Float32Array(len);
    audioBuf.copyFromChannel(mono, 0);
    for (let c = 1; c < ch; c++) {
      audioBuf.copyFromChannel(tmp, c);
      for (let i = 0; i < len; i++) mono[i] = (mono[i] + tmp[i]) * 0.5;
    }
  }

  // 3) 16kHz로 리샘플 (선형보간)
  const srcRate = audioBuf.sampleRate;
  const targetRate = 16000;
  const resampled = resampleLinear(mono, srcRate, targetRate);

  // 4) PCM16으로 변환 후 WAV 패키징
  const pcm16 = float32ToInt16(resampled);
  const wav = encodeWavPcm16(pcm16, targetRate, 1);

  // 5) 리소스 정리
  if (typeof ctx.close === 'function') {
    try { await ctx.close(); } catch (_) {}
  }

  return new Blob([wav], { type: 'audio/wav' });
}

/* ===== 작은 유틸들 ===== */
function resampleLinear(input, srcRate, dstRate) {
  if (srcRate === dstRate) return input;
  const ratio = srcRate / dstRate;
  const outLen = Math.round(input.length / ratio);
  const out = new Float32Array(outLen);
  for (let i = 0; i < outLen; i++) {
    const idx = i * ratio;
    const i0 = Math.floor(idx);
    const i1 = Math.min(i0 + 1, input.length - 1);
    const t = idx - i0;
    out[i] = input[i0] * (1 - t) + input[i1] * t;
  }
  return out;
}

function float32ToInt16(f32) {
  const out = new Int16Array(f32.length);
  for (let i = 0; i < f32.length; i++) {
    let s = Math.max(-1, Math.min(1, f32[i]));
    out[i] = s < 0 ? s * 0x8000 : s * 0x7fff;
  }
  return out;
}

function encodeWavPcm16(pcm16, sampleRate, channels) {
  const byteRate = sampleRate * channels * 2;
  const blockAlign = channels * 2;
  const dataSize = pcm16.byteLength;
  const buffer = new ArrayBuffer(44 + dataSize);
  const view = new DataView(buffer);

  writeStr(view, 0, 'RIFF');
  view.setUint32(4, 36 + dataSize, true);
  writeStr(view, 8, 'WAVE');
  writeStr(view, 12, 'fmt ');
  view.setUint32(16, 16, true);     // PCM chunk size
  view.setUint16(20, 1, true);      // PCM
  view.setUint16(22, channels, true);
  view.setUint32(24, sampleRate, true);
  view.setUint32(28, byteRate, true);
  view.setUint16(32, blockAlign, true);
  view.setUint16(34, 16, true);     // bits per sample
  writeStr(view, 36, 'data');
  view.setUint32(40, dataSize, true);
  new Int16Array(buffer, 44).set(pcm16);
  return buffer;
}

function writeStr(v, off, s) {
  for (let i = 0; i < s.length; i++) v.setUint8(off + i, s.charCodeAt(i));
}

async function readWavMeta(blob) {
  const buf = await blob.arrayBuffer();
  const v = new DataView(buf);
  const channels = v.getUint16(22, true);
  const sampleRate = v.getUint32(24, true);
  const bitsPerSample = v.getUint16(34, true);
  return { channels, sampleRate, bitsPerSample };
}
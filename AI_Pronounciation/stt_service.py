import requests
from transformers import Wav2Vec2Processor, Wav2Vec2ForCTC
import torch
import torchaudio
from difflib import SequenceMatcher
import torchaudio
torchaudio.set_audio_backend("soundfile")

SPRING_API_BASE = "http://localhost:8080"  # 또는 EC2 주소

def get_ref_text(question_id: int) -> str:
    url = f"{SPRING_API_BASE}/api/study/choice/correct/{question_id}"
    res = requests.get(url)
    if res.status_code != 200:
        raise Exception(f"Spring에서 정답 문장을 가져오지 못했습니다. Status: {res.status_code}")
    return res.text

# 모델 및 processor 로딩 (사전 학습된 한국어 모델)
processor = Wav2Vec2Processor.from_pretrained("kresnik/wav2vec2-large-xlsr-korean")
model = Wav2Vec2ForCTC.from_pretrained("kresnik/wav2vec2-large-xlsr-korean")
model.eval()

def transcribe_wav(file_path: str) -> str:
    waveform, sample_rate = torchaudio.load(file_path)

    # 샘플레이트가 16000이 아니면 변환
    if sample_rate != 16000:
        resampler = torchaudio.transforms.Resample(orig_freq=sample_rate, new_freq=16000)
        waveform = resampler(waveform)

    inputs = processor(waveform.squeeze().numpy(), return_tensors="pt", sampling_rate=16000)
    with torch.no_grad():
        logits = model(inputs.input_values).logits
    predicted_ids = torch.argmax(logits, dim=-1)
    transcription = processor.batch_decode(predicted_ids)[0]
    return transcription.strip()

def calc_similarity(stt_text: str, ref_text: str) -> int:
    stt_text = stt_text.replace(" ", "")
    ref_text = ref_text.replace(" ", "")
    score = SequenceMatcher(None, stt_text, ref_text).ratio()
    return int(score * 100)

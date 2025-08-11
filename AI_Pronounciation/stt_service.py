import re
import unicodedata
import requests
from difflib import SequenceMatcher

import torch
import torchaudio
from transformers import Wav2Vec2Processor, Wav2Vec2ForCTC

# torchaudio backend 설정 (libsndfile)
torchaudio.set_audio_backend("soundfile")

# Spring API (정답 문장 조회)
SPRING_API_BASE = "http://localhost:8080"  # 또는 EC2 주소

def get_ref_text(question_id: int) -> str:
    """정답 문장을 Spring에서 조회"""
    url = f"{SPRING_API_BASE}/api/study/choice/correct/{question_id}"
    res = requests.get(url)
    if res.status_code != 200:
        raise Exception(f"Spring에서 정답 문장을 가져오지 못했습니다. Status: {res.status_code}")
    return res.text

# 모델 로딩 
processor = Wav2Vec2Processor.from_pretrained("kresnik/wav2vec2-large-xlsr-korean")
model = Wav2Vec2ForCTC.from_pretrained("kresnik/wav2vec2-large-xlsr-korean")
model.eval()

def transcribe_wav(file_path: str) -> str:
    """WAV 파일을 16kHz mono로 맞춰 ASR 수행"""
    # 1) 로드
    waveform, sample_rate = torchaudio.load(file_path)  # [ch, time]

    # 2) mono 변환 (필요 시)
    if waveform.shape[0] > 1:
        waveform = torch.mean(waveform, dim=0, keepdim=True)

    # 3) 16kHz 리샘플
    if sample_rate != 16000:
        resampler = torchaudio.transforms.Resample(orig_freq=sample_rate, new_freq=16000)
        waveform = resampler(waveform)

    # 4) 모델 입력
    inputs = processor(waveform.squeeze().numpy(), return_tensors="pt", sampling_rate=16000)

    # 5) 추론
    with torch.no_grad():
        logits = model(inputs.input_values).logits
    predicted_ids = torch.argmax(logits, dim=-1)
    transcription = processor.batch_decode(predicted_ids)[0]

    return transcription.strip()


# 유사도 채점
def _normalize(text: str) -> str:
    """한글 NFC, 소문자, 특수문자 제거, 공백 정리"""
    t = unicodedata.normalize("NFC", (text or "").strip()).lower()
    t = re.sub(r"[^0-9a-z가-힣\s]", " ", t)
    t = re.sub(r"\s+", " ", t)
    return t.strip()

# 흔한 조사/정중어미
_PARTICLES = r"(은|는|이|가|을|를|에|에서|으로|와|과|에게|께|한테|도|만|까지|부터|조차|라도|마저)"
_POLITE_ENDINGS = r"(입니다|습니까|습니다|어요|에요|예요|였어요|였습니까|다)$"

def _soft_korean_simplify(text: str) -> str:
    """
    의미 손상 최소화하며 채점 완화:
    - 단어 경계로 조사 제거
    - 단어별 정중/평서형 어미 약하게 제거
    """
    t = _normalize(text)
    t = re.sub(fr"\b{_PARTICLES}\b", " ", t)
    words = []
    for w in t.split():
        w2 = re.sub(_POLITE_ENDINGS, "", w)
        if w2:
            words.append(w2)
    return " ".join(words)

def _char_sim(a: str, b: str) -> float:
    """문자 단위(공백 제거) 시퀀스 유사도"""
    return SequenceMatcher(None, a.replace(" ", ""), b.replace(" ", "")).ratio()

def _word_sim(a: str, b: str) -> float:
    """단어 시퀀스 유사도"""
    return SequenceMatcher(None, a.split(), b.split()).ratio()

def _jaccard_ngram(a: str, b: str, n: int = 2) -> float:
    """문자 n-gram 자카드 (작은 오탈자에 관대)"""
    def grams(s: str):
        s2 = s.replace(" ", "")
        return {s2[i:i+n] for i in range(max(len(s2) - n + 1, 0))}
    A, B = grams(a), grams(b)
    if not A and not B: return 1.0
    if not A or not B:  return 0.0
    return len(A & B) / len(A | B)

CHO = [chr(c) for c in range(0x1100, 0x1113)]       # 19
JUNG = [chr(c) for c in range(0x1161, 0x1176)]      # 21
JONG = [""] + [chr(c) for c in range(0x11A8, 0x11C3)]  # 28 (빈 종성 포함)

def _to_jamo(s: str) -> str:
    """가-힣을 초/중/종성으로 분해하여 문자열로 변환"""
    out = []
    for ch in s:
        code = ord(ch)
        if 0xAC00 <= code <= 0xD7A3:
            n = code - 0xAC00
            cho = n // 588
            jung = (n % 588) // 28
            jong = n % 28
            out.append(CHO[cho])
            out.append(JUNG[jung])
            if JONG[jong]:
                out.append(JONG[jong])
        else:
            out.append(ch)
    return "".join(out)

def _jamo_sim(a: str, b: str) -> float:
    """자모 단위 시퀀스 유사도(발음유사에 관대)"""
    ja = _to_jamo(a.replace(" ", ""))
    jb = _to_jamo(b.replace(" ", ""))
    return SequenceMatcher(None, ja, jb).ratio()

def _word_coverage(ref: str, hyp: str) -> float:
    """참고문장(ref) 대비 인식문장(hyp)의 '단어 일치 커버리지'(순서 감안)"""
    r, h = ref.split(), hyp.split()
    if not r:
        return 0.0
    sm = SequenceMatcher(None, r, h)
    matched = sum(b.size for b in sm.get_matching_blocks())
    return matched / len(r)

def _char_coverage(ref: str, hyp: str) -> float:
    """공백 무시한 '문자 LCS 커버리지' (띄어쓰기 영향 0)"""
    r, h = list(ref.replace(" ", "")), list(hyp.replace(" ", ""))
    if not r:
        return 0.0
    sm = SequenceMatcher(None, r, h)
    matched = sum(b.size for b in sm.get_matching_blocks())
    return matched / len(r)

def calc_similarity(stt_text: str, ref_text: str) -> int:
    """
    띄어쓰기 감점 제거 버전:
    - 모든 지표에서 공백 영향 제거
    - 문자/bi-gram/자모 혼합(단어 유사도는 제외)
    - 공백 무시 LCS 커버리지로 soft floor
    - 감마 스케일
    """
    if not stt_text or not ref_text:
        return 0

    # 전처리 (조사/정중어미 제거)
    stt = _soft_korean_simplify(stt_text)
    ref = _soft_korean_simplify(ref_text)

    # 공백 제거본
    stt_ns = stt.replace(" ", "")
    ref_ns = ref.replace(" ", "")

    # 지표 (모두 공백 영향 없음)
    c  = _char_sim(stt, ref)            # 내부에서 공백 제거
    j2 = _jaccard_ngram(stt, ref, 2)    # 내부에서 공백 제거
    js = _jamo_sim(stt, ref)            # 내부에서 공백 제거

    # 가중 평균 (자모 비중↑: 발음 유사에 관대)
    score = 0.30 * c + 0.20 * j2 + 0.50 * js

    # 문두/문미 보너스도 공백 무시 기준으로
    if ref_ns and stt_ns:
        if stt_ns[0] == ref_ns[0]:
            score += 0.02
        if stt_ns[-1] == ref_ns[-1]:
            score += 0.02

    # 공백 무시 LCS 커버리지 기반 최저점 보장 (띄어쓰기와 무관)
    cov = _char_coverage(ref, stt)   # ref 대비 매칭 비율
    if cov >= 2/3:
        score = max(score, 0.75)
    elif cov >= 0.5:
        score = max(score, 0.65)

    score = min(1.0, score)

    # 감마(γ)로 완만 상향 — 필요시 0.8~0.7로 내려 더 올릴 수 있음
    GAMMA = 0.6
    score = score ** GAMMA

    return int(round(score * 100))
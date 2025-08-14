from __future__ import annotations

import os
import re
import unicodedata
from typing import List, Sequence, Tuple

import requests
import torch
import torchaudio as ta
from transformers import Wav2Vec2Processor, Wav2Vec2ForCTC
from g2pk import G2p


SPRING_API_BASE = os.getenv("SPRING_API_BASE", "https://i13a706.p.ssafy.io")
STT_MODEL_NAME = os.getenv("KOR_STT_MODEL", "kresnik/wav2vec2-large-xlsr-korean")
TARGET_SR = 16_000  # 모델 학습 샘플레이트

# lazy-singleton
_processor: Wav2Vec2Processor | None = None
_model: Wav2Vec2ForCTC | None = None
_g2p = G2p()  # 경량, 즉시 생성해도 무방


def _load_stt_model() -> tuple[Wav2Vec2Processor, Wav2Vec2ForCTC]:
    """
    팩토리 메서드: Wav2Vec2 모델/프로세서를 1회 로딩.
    """
    global _processor, _model
    if _processor is None or _model is None:
        _processor = Wav2Vec2Processor.from_pretrained(STT_MODEL_NAME)
        _model = Wav2Vec2ForCTC.from_pretrained(STT_MODEL_NAME)
        _model.eval()
    return _processor, _model


# 1) STT (음성→텍스트)
def transcribe_wav(wav_path: str) -> str:
    if not os.path.exists(wav_path):
        raise FileNotFoundError(f"음성 파일이 존재하지 않습니다: {wav_path}")

    proc, mdl = _load_stt_model()

    # torchaudio는 backend=soundfile이면 비압축/다수 포맷 로딩 가능
    wav, sr = ta.load(wav_path)  # shape: [channels, time]
    if wav.size(0) > 1:
        wav = wav.mean(dim=0, keepdim=True)  # 모노화
    if sr != TARGET_SR:
        wav = ta.functional.resample(wav, orig_freq=sr, new_freq=TARGET_SR)

    # 모델 입력
    audio = wav.squeeze(0).numpy()
    with torch.no_grad():
        inputs = proc(audio, sampling_rate=TARGET_SR, return_tensors="pt")
        logits = mdl(**inputs).logits
        pred_ids = torch.argmax(logits, dim=-1)
        text = proc.batch_decode(pred_ids)[0]

    # 약간의 정규화(소문자, 특수문자 정리)
    text = unicodedata.normalize("NFC", text.strip())
    text = re.sub(r"[^0-9a-z가-힣\s]", " ", text.lower())
    text = re.sub(r"\s+", " ", text).strip()
    return text


# 2) 참조 문장 조회
def get_ref_text(question_id: int) -> str:
    """
    Spring API에서 문제ID의 정답 문장을 조회.
    - GET {SPRING_API_BASE}/api/study/choice/correct/{question_id}
    """
    url = f"{SPRING_API_BASE}/api/study/choice/correct/{question_id}"
    res = requests.get(url, timeout=5)
    if res.status_code != 200:
        raise RuntimeError(f"정답 문장 조회 실패 (status={res.status_code})")
    return res.text or ""

# 3) 발음 유사도(도메인 로직)
CHO: List[str] = [
    "ᄀ","ᄁ","ᄂ","ᄃ","ᄄ","ᄅ","ᄆ","ᄇ","ᄈ","ᄉ","ᄊ","ᄋ","ᄌ","ᄍ","ᄎ","ᄏ","ᄐ","ᄑ","ᄒ"
]
JUNG: List[str] = [
    "ᅡ","ᅢ","ᅣ","ᅤ","ᅥ","ᅦ","ᅧ","ᅨ","ᅩ","ᅪ","ᅫ","ᅬ","ᅭ","ᅮ","ᅯ","ᅰ","ᅱ","ᅲ","ᅳ","ᅴ","ᅵ"
]
JONG: List[str] = [
    "", "ᆨ","ᆩ","ᆪ","ᆫ","ᆬ","ᆭ","ᆮ","ᆯ","ᆰ","ᆱ","ᆲ","ᆳ","ᆴ","ᆵ","ᆶ",
    "ᆷ","ᆸ","ᆹ","ᆺ","ᆻ","ᆼ","ᆽ","ᆾ","ᆿ","ᇀ","ᇁ","ᇂ"
]

CHO_SET = set(CHO)
JUNG_SET = set(JUNG)
JONG_SET = set(JONG[1:]) 

def is_cho(ch: str) -> bool: return ch in CHO_SET
def is_jung(ch: str) -> bool: return ch in JUNG_SET
def is_jong(ch: str) -> bool: return ch in JONG_SET

# 종성 7자 중화 (ㄱ/ㄴ/ㄷ/ㄹ/ㅁ/ㅂ/ㅇ)
JONG_BASE = {
    "": "",
    "ᆨ":"ᆨ","ᆩ":"ᆨ","ᆪ":"ᆨ","ᆿ":"ᆨ",
    "ᆫ":"ᆫ","ᆬ":"ᆫ","ᆭ":"ᆫ",
    "ᆮ":"ᆮ","ᆺ":"ᆮ","ᆻ":"ᆮ","ᆽ":"ᆮ","ᆾ":"ᆮ","ᇀ":"ᆮ",
    "ᆯ":"ᆯ","ᆰ":"ᆯ","ᆱ":"ᆯ","ᆲ":"ᆯ","ᆳ":"ᆯ","ᆴ":"ᆯ","ᆵ":"ᆯ","ᆶ":"ᆯ",
    "ᆷ":"ᆷ",
    "ᆸ":"ᆸ","ᆹ":"ᆸ","ᇁ":"ᆸ",
    "ᆼ":"ᆼ",
    "ᇂ":"ᇂ"
}

# 초성 계열 기저(평/경/격 통합)
CHO_BASE = {
    "ᄀ":"ᄀ","ᄁ":"ᄀ","ᄏ":"ᄀ",
    "ᄃ":"ᄃ","ᄄ":"ᄃ","ᄐ":"ᄃ",
    "ᄇ":"ᄇ","ᄈ":"ᄇ","ᄑ":"ᄇ",
    "ᄉ":"ᄉ","ᄊ":"ᄉ",
    "ᄌ":"ᄌ","ᄍ":"ᄌ","ᄎ":"ᄌ",
    "ᄂ":"ᄂ","ᄅ":"ᄅ","ᄆ":"ᄆ","ᄋ":"ᄋ","ᄒ":"ᄒ"
}

# 모음 근접군
VOWEL_NEAR = [
    {"ᅢ","ᅦ"},
    {"ᅬ","ᅫ","ᅰ"},
    {"ᅴ","ᅵ"},
]

def _normalize_pron(s: str) -> str:
    """G2P로 발음열 생성 → 불필요 기호 제거/공백 정리"""
    s = _g2p(s or "")
    s = unicodedata.normalize("NFC", s.strip())
    s = re.sub(r"[^0-9a-z가-힣\s]", " ", s.lower())
    s = re.sub(r"\s+", " ", s)
    return s.strip()

def _to_jamo_triplets(s: str) -> List[Tuple[str, str, str]]:
    """한글 문자열 → [(초,중,종)] 목록. 한글 외 문자는 중성 칸으로."""
    triplets: List[Tuple[str, str, str]] = []
    for ch in s:
        code = ord(ch)
        if 0xAC00 <= code <= 0xD7A3:
            n = code - 0xAC00
            ci = n // 588
            vi = (n % 588) // 28
            ti = n % 28
            c = CHO[ci]
            v = JUNG[vi]
            t = JONG[ti]
            t = JONG_BASE.get(t, t)
            triplets.append((c, v, t))
        else:
            if ch.strip():
                triplets.append(("", ch, ""))
    return triplets

def _vowel_sim_cost(a: str, b: str) -> float:
    if a == b: return 0.0
    for g in VOWEL_NEAR:
        if a in g and b in g:
            return 0.25
    return 1.0

def _cons_sim_cost(a: str, b: str, pos: str) -> float:
    if a == b:
        return 0.0
    if a in {"ᄒ","ᇂ"} or b in {"ᄒ","ᇂ"}:
        return 0.35
    if {a, b} == {"ᄂ","ᄅ"} or {a, b} == {"ᆫ","ᆯ"}:
        return 0.4
    base_a = CHO_BASE.get(a, a) if pos == "C" else a
    base_b = CHO_BASE.get(b, b) if pos == "C" else b
    if base_a == base_b:
        return 0.25 if pos == "C" else 0.35
    return 0.8 if pos == "T" else 1.0

def _ins_del_cost(symbol: str, pos: str) -> float:
    if pos == "V":
        return 0.9
    if symbol in {"ᄒ","ᇂ"}:
        return 0.35
    return 0.7 if pos == "T" else 0.8

def _weighted_levenshtein(a_tris: Sequence[Tuple[str, str, str]],
                          b_tris: Sequence[Tuple[str, str, str]]) -> float:
    """음절 단위 DP, 음절 내 자모치환 비용 합산 → 정규화 편집거리(0~1)"""
    n, m = len(a_tris), len(b_tris)
    dp = [[0.0]*(m+1) for _ in range(n+1)]

    def syl_del_cost(t: Tuple[str, str, str]) -> float:
        c, v, tt = t
        return _ins_del_cost(c, "C") + _ins_del_cost(v, "V") + _ins_del_cost(tt, "T")

    for i in range(1, n+1):
        dp[i][0] = dp[i-1][0] + syl_del_cost(a_tris[i-1])
    for j in range(1, m+1):
        dp[0][j] = dp[0][j-1] + syl_del_cost(b_tris[j-1])

    for i in range(1, n+1):
        for j in range(1, m+1):
            ac, av, at = a_tris[i-1]
            bc, bv, bt = b_tris[j-1]
            sub_cost = (
                _cons_sim_cost(ac, bc, "C") +
                _vowel_sim_cost(av, bv) +
                _cons_sim_cost(at, bt, "T")
            )
            dp[i][j] = min(
                dp[i-1][j] + syl_del_cost(a_tris[i-1]),
                dp[i][j-1] + syl_del_cost(b_tris[j-1]),
                dp[i-1][j-1] + sub_cost
            )

    if n == 0 and m == 0:
        return 0.0
    base = a_tris if n >= m else b_tris
    denom = sum(syl_del_cost(t) for t in base)
    return (dp[n][m] / denom) if denom > 0 else 0.0

def calc_pronunciation_score(stt_text: str, ref_text: str) -> int:
    """발음 점수 0~100"""
    if not stt_text or not ref_text:
        return 0
    stt_pron = _normalize_pron(stt_text)
    ref_pron = _normalize_pron(ref_text)
    a_tris = _to_jamo_triplets(stt_pron)
    b_tris = _to_jamo_triplets(ref_pron)
    dist = _weighted_levenshtein(a_tris, b_tris)
    sim = 1.0 - dist
    score = (sim ** 0.6) * 100.0
    if stt_pron and ref_pron:
        if stt_pron[0] == ref_pron[0]: score += 1.0
        if stt_pron[-1] == ref_pron[-1]: score += 1.0
    return int(max(0, min(100, round(score))))

def calc_similarity(stt_text: str, ref_text: str) -> int:
    """기존 호환 래퍼"""
    return calc_pronunciation_score(stt_text, ref_text)


# 명시적 내보내기
__all__ = [
    "transcribe_wav",
    "calc_similarity",
    "get_ref_text",
    "calc_pronunciation_score",
]

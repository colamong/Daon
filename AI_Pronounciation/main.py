# main.py
"""
- WAV만 허용(프론트에서 RecordRTC 등으로 16kHz mono WAV 업로드 전제)
- Spring에서 정답 문장 조회 → STT → 유연 채점 → 결과 반환
"""

import os
import tempfile
import logging
from fastapi import FastAPI, UploadFile, File, HTTPException
from fastapi.middleware.cors import CORSMiddleware

from stt_service import transcribe_wav, calc_similarity, get_ref_text

app = FastAPI()

# CORS (프론트 도메인 추가)
app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:5173", "http://localhost:3000", "http://localhost:8080"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# WAV만 받음 (FFmpeg 없이 운영)
ALLOWED_MIMES = {"audio/wav", "audio/x-wav"}

logger = logging.getLogger("uvicorn.error")

@app.post("/api/evaluate/{question_id}")
async def evaluate_pronunciation(
    question_id: int,
    audio: UploadFile = File(...)
):
    # 0) 입력 포맷 검증
    if audio.content_type not in ALLOWED_MIMES:
        raise HTTPException(status_code=415, detail=f"WAV only. got {audio.content_type}")

    # 1) 정답 문장 조회
    try:
        ref_text = get_ref_text(question_id)
    except Exception as e:
        raise HTTPException(status_code=502, detail=f"정답 문장 불러오기 실패: {str(e)}")

    tmp_path = None
    try:
        # 2) 파일 저장
        chunk = await audio.read()
        if not chunk:
            raise HTTPException(status_code=400, detail="빈 파일입니다.")
        with tempfile.NamedTemporaryFile(delete=False, suffix=".wav") as tmp:
            tmp.write(chunk)
            tmp_path = tmp.name

        # 3) STT
        stt_text = transcribe_wav(tmp_path)

        # 4) 유사도 채점(유연)
        score = calc_similarity(stt_text, ref_text)

        # 5) 서버 로그 (운영 시 민감정보 마스킹 고려)
        try:
            logger.info(f"[evaluate] q={question_id} score={score} stt='{stt_text}' ref='{ref_text}'")
        except Exception:
            print(f"[evaluate] q={question_id} score={score} stt='{stt_text}' ref='{ref_text}'")

        # 6) 결과 반환
        return {
            "score": score,             # int 0~100
            "transcribed": stt_text,
            "reference": ref_text
        }
    finally:
        # 7) 임시파일 정리
        if tmp_path and os.path.exists(tmp_path):
            try:
                os.remove(tmp_path)
            except:
                pass

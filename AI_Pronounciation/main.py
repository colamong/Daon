from fastapi import FastAPI, UploadFile, File, Form
from stt_service import transcribe_wav, calc_similarity
from stt_service import get_ref_text
import tempfile, os

app = FastAPI()

@app.post("/api/evaluate/{question_id}")
async def evaluate_pronunciation(
    question_id: int,
    audio: UploadFile = File(...)
):
    # 1. Spring Boot에서 정답 문장 가져오기
    try:
        ref_text = get_ref_text(question_id)
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"정답 문장 불러오기 실패: {str(e)}")

    # 2. 업로드된 음성 파일 임시 저장
    with tempfile.NamedTemporaryFile(delete=False, suffix=".wav") as tmp:
        tmp.write(await audio.read())
        tmp_path = tmp.name

    try:
        # 3. 음성 -> 텍스트 변환 (STT)
        stt_text = transcribe_wav(tmp_path)

        # 4. 발음 유사도 평가
        score = calc_similarity(stt_text, ref_text)

        # 5. 결과 반환
        return {
            "score": score,
            "transcribed": stt_text,
            "reference": ref_text
        }
    finally:
        os.remove(tmp_path)
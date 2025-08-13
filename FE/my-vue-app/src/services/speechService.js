// Web Speech API 서비스
export class SpeechService {
  constructor() {
    this.recognition = null;
    this.synthesis = window.speechSynthesis;
    this.isListening = false;
    this.isSupported = this.checkSupport();
    this.voices = [];
    this.loadVoices();
  }

  // 사용 가능한 음성 목록 로드
  loadVoices() {
    if (this.synthesis) {
      this.voices = this.synthesis.getVoices();

      // 음성이 로드되지 않았다면 이벤트 리스너로 재시도
      if (this.voices.length === 0) {
        this.synthesis.addEventListener("voiceschanged", () => {
          this.voices = this.synthesis.getVoices();
        });
      }
    }
  }

  // 한국어 음성 필터링
  getKoreanVoices() {
    return this.voices.filter((voice) => voice.lang.includes("ko"));
  }

  // 특정 음성 찾기 (이름으로)
  findVoice(voiceName) {
    return this.voices.find((voice) => voice.name.includes(voiceName));
  }

  // 브라우저 지원 확인
  checkSupport() {
    return "webkitSpeechRecognition" in window || "SpeechRecognition" in window;
  }

  // TTS: 텍스트를 음성으로 출력
  speak(text, options = {}) {
    return new Promise((resolve, reject) => {
      if (!this.synthesis) {
        reject(new Error("Speech synthesis not supported"));
        return;
      }

      // 진행 중인 음성 중단
      this.synthesis.cancel();

      const utterance = new SpeechSynthesisUtterance(text);

      // 기본 설정
      utterance.lang = options.lang || "ko-KR";
      utterance.rate = options.rate || 0.9; // 조금 느리게
      utterance.pitch = options.pitch || 1.9; // 조금 높게
      utterance.volume = options.volume || 1;

      // 음성 선택
      if (options.voiceName) {
        const selectedVoice = this.findVoice(options.voiceName);
        if (selectedVoice) {
          utterance.voice = selectedVoice;
        }
      } else {
        // 기본으로 한국어 음성 중 첫 번째 선택
        const koreanVoices = this.getKoreanVoices();
        if (koreanVoices.length > 0) {
          utterance.voice = koreanVoices[0];
        }
      }

      utterance.onend = () => {
        log("TTS 재생 완료:", text);
        resolve();
      };

      utterance.onerror = (event) => {
        error("TTS 오류:", event);
        reject(new Error("Speech synthesis failed"));
      };

      this.synthesis.speak(utterance);
      log("TTS 시작:", text);
    });
  }

  // STT: 음성을 텍스트로 변환
  listen(options = {}) {
    return new Promise((resolve, reject) => {
      if (!this.isSupported) {
        reject(new Error("Speech recognition not supported"));
        return;
      }

      // 이미 듣고 있으면 중단
      if (this.isListening) {
        this.stopListening();
      }

      // 음성 인식 설정
      const SpeechRecognition =
        window.SpeechRecognition || window.webkitSpeechRecognition;
      this.recognition = new SpeechRecognition();

      this.recognition.lang = options.lang || "ko-KR";
      this.recognition.continuous = options.continuous || false;
      this.recognition.interimResults = options.interimResults || false;
      this.recognition.maxAlternatives = options.maxAlternatives || 1;

      let hasResolved = false;

      this.recognition.onstart = () => {
        this.isListening = true;
        log("STT 시작");
      };

      this.recognition.onresult = (event) => {
        const transcript = event.results[0][0].transcript;
        log("STT 결과:", transcript);
        if (!hasResolved) {
          hasResolved = true;
          resolve(transcript);
        }
      };

      this.recognition.onerror = (event) => {
        this.isListening = false;
        error("STT 오류:", event.error);
        if (!hasResolved) {
          hasResolved = true;
          reject(new Error(`Speech recognition failed: ${event.error}`));
        }
      };

      this.recognition.onend = () => {
        this.isListening = false;
        log("STT 종료");
        // 결과 없이 종료된 경우 기본 응답으로 처리
        if (!hasResolved) {
          hasResolved = true;
          log("STT 인식 오류로 기본 응답 처리");
          resolve("음성 인식 오류입니다");
        }
      };

      // 음성 인식 시작
      this.recognition.start();
    });
  }

  // 음성 인식 중단
  stopListening() {
    if (this.recognition && this.isListening) {
      this.recognition.stop();
      this.isListening = false;
    }
  }

  // TTS 중단
  stopSpeaking() {
    if (this.synthesis) {
      this.synthesis.cancel();
    }
  }

  // 리소스 정리
  cleanup() {
    this.stopListening();
    this.stopSpeaking();
  }
}

// 싱글톤 인스턴스 생성
export const speechService = new SpeechService();

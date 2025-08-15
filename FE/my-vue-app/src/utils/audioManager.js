/**
 * 전역 오디오 관리자
 * 모든 오디오 인스턴스를 중앙에서 관리하여 겹침 방지
 */
class AudioManager {
  constructor() {
    this.audioInstances = new Map();
    this.currentBGM = null;
  }

  /**
   * BGM 등록 및 재생
   */
  async playBGM(pageId, audioSrc) {
    try {
      // 기존 BGM 완전 정지
      this.stopAllBGM();

      console.log(`[AudioManager] BGM 재생 시도: ${pageId}`);
      
      const audio = new Audio(audioSrc);
      audio.loop = true;
      audio.volume = 0.3;
      audio.preload = "auto";

      // 오디오 인스턴스 등록
      this.audioInstances.set(pageId, audio);
      this.currentBGM = { pageId, audio };

      // 강제 재생 시도
      const playPromise = audio.play();

      if (playPromise !== undefined) {
        playPromise
          .then(() => {
            console.log(`[AudioManager] BGM 재생 성공: ${pageId}`);
          })
          .catch((error) => {
            console.log(`[AudioManager] 자동 재생 차단됨: ${pageId}`, error);
            // 차단된 경우 첫 번째 클릭/터치 이벤트에서 재생
            document.addEventListener("click", () => this.playBGM(pageId, audioSrc), { once: true });
            document.addEventListener("touchstart", () => this.playBGM(pageId, audioSrc), { once: true });
          });
      }

      return audio;
    } catch (error) {
      console.error(`[AudioManager] BGM 생성 실패: ${pageId}`, error);
    }
  }

  /**
   * 특정 페이지의 BGM 정지
   */
  stopBGM(pageId) {
    const audio = this.audioInstances.get(pageId);
    if (audio) {
      console.log(`[AudioManager] BGM 정지: ${pageId}`);
      audio.pause();
      audio.currentTime = 0;
      audio.src = '';
      this.audioInstances.delete(pageId);
      
      if (this.currentBGM && this.currentBGM.pageId === pageId) {
        this.currentBGM = null;
      }
    }
  }

  /**
   * 모든 BGM 강제 정지
   */
  stopAllBGM() {
    console.log(`[AudioManager] 모든 BGM 정지 - 총 ${this.audioInstances.size}개`);
    
    this.audioInstances.forEach((audio, pageId) => {
      console.log(`[AudioManager] 정지 중: ${pageId}`);
      audio.pause();
      audio.currentTime = 0;
      audio.src = '';
    });
    
    this.audioInstances.clear();
    this.currentBGM = null;
  }

  /**
   * 페이지 전환 시 호출
   */
  onPageLeave(pageId) {
    console.log(`[AudioManager] 페이지 떠남: ${pageId}`);
    this.stopBGM(pageId);
  }

  /**
   * 브라우저 탭/창 닫기 시 모든 오디오 정리
   */
  cleanup() {
    console.log('[AudioManager] 전체 정리');
    this.stopAllBGM();
  }

  /**
   * 효과음 재생 (BGM과 별개)
   */
  async playEffect(effectSrc, volume = 0.4) {
    try {
      const audio = new Audio(effectSrc);
      audio.volume = volume;
      audio.preload = "auto";
      
      await audio.play();
      
      // 효과음은 재생 완료 후 자동으로 정리됨
      audio.addEventListener('ended', () => {
        audio.src = '';
      });
      
      return audio;
    } catch (error) {
      console.warn('[AudioManager] 효과음 재생 실패:', error);
    }
  }

  /**
   * 현재 재생 중인 BGM 정보
   */
  getCurrentBGM() {
    return this.currentBGM;
  }
}

// 싱글톤 인스턴스 생성
const audioManager = new AudioManager();

// 브라우저 탭/창 닫기 이벤트 리스너
window.addEventListener('beforeunload', () => {
  audioManager.cleanup();
});

window.addEventListener('pagehide', () => {
  audioManager.cleanup();
});

export default audioManager;
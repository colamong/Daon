<template>
  <div class="situation-card">
    <div class="situation-header">
      <h2 class="situation-title font-paperBold">상황</h2>
    </div>
    <div class="situation-content">
      <div class="question-container">
        <div class="question-text">
          <span
            class="typing-text"
            :data-text="safeText"
            :key="safeText"
          ></span>
          <span
            class="typing-cursor"
            :class="{ 'cursor-after-typing': typingComplete }"
          ></span>
        </div>
        <button v-if="showAudioButton" @click="playAudio" class="audio-button">
          🔊
        </button>
      </div>
      <div v-if="pronunciation && showPronunciation" class="pronunciation-text">
        {{ pronunciation }}
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from "vue";

const props = defineProps({
  text: {
    type: String,
    default: "",
  },
  pronunciation: {
    type: String,
    default: "",
  },
  showAudioButton: {
    type: Boolean,
    default: true,
  },
  showPronunciation: {
    type: Boolean,
    default: true,
  },
  typingSpeed: {
    type: Number,
    default: 100,
  },
  autoStart: {
    type: Boolean,
    default: true,
  },
});

const emit = defineEmits(["typing-complete", "play-audio"]);

const typingComplete = ref(false);
const safeText = computed(() => props.text || "");

const animationDuration = computed(() => {
  const charCount = safeText.value.length;
  return Math.max(2, (charCount * props.typingSpeed) / 1000);
});

const playAudio = () => {
  emit("play-audio");
};

onMounted(() => {
  if (props.autoStart) {
    setTimeout(() => {
      typingComplete.value = true;
      emit("typing-complete");
    }, animationDuration.value * 1000);
  }
});

watch(
  () => props.text,
  () => {
    typingComplete.value = false;
    if (props.autoStart && props.text) {
      setTimeout(() => {
        typingComplete.value = true;
        emit("typing-complete");
      }, animationDuration.value * 1000);
    }
  },
  { immediate: false }
);
</script>

<style scoped>
.situation-card {
  background-color: #dbeafe;
  border-radius: 0.75rem;
  padding: 1.5rem;
  margin-bottom: 2rem;
}

.situation-header {
  margin-bottom: 0.75rem;
}

.situation-title {
  font-size: 1.25rem;
  color: #1f2937;
  margin: 0;
}

.situation-content {
  position: relative;
}

.question-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
}

.question-text {
  color: #374151;
  line-height: 1.6;
  margin: 0;
  font-family: inherit;
  font-size: 1rem;
  min-height: 1.5rem;
  flex: 1;
  white-space: nowrap;
  overflow: hidden;
}

.audio-button {
  background-color: white;
  border: 1px solid #d1d5db;
  border-radius: 0.5rem;
  padding: 0.5rem;
  width: 2rem;
  height: 2rem;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
  flex-shrink: 0;
}

.audio-button:hover {
  background-color: #f9fafb;
}

.pronunciation-text {
  color: #6b7280;
  font-size: 0.875rem;
  margin-top: 0.5rem;
  font-style: italic;
}

.typing-text::before {
  content: attr(data-text);
  display: inline-block;
  overflow: hidden;
  white-space: nowrap;
  width: 0;
  animation: typing v-bind(animationDuration + "s")
    steps(v-bind("safeText.length")) forwards;
}

.typing-cursor {
  display: inline-block;
  width: 2px;
  height: 1.2em;
  background-color: #3b82f6;
  margin-left: 4px;
  animation: blink 1s infinite;
  vertical-align: text-top;
}

.cursor-after-typing {
  position: relative;
  left: 8px;
}

@keyframes typing {
  0% {
    width: 0;
  }
  100% {
    width: 100%;
  }
}

@keyframes blink {
  0%,
  50% {
    opacity: 1;
  }
  51%,
  100% {
    opacity: 0;
  }
}
</style>

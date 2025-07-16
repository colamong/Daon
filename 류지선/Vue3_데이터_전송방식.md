# Vue 3 데이터 전송 방법 정리

Vue 3에서는 컴포넌트 간 데이터 전송을 위해 다음과 같은 3가지 방식이 자주 사용됩니다.

---

## 1. Props / Emit (부모 ↔ 자식)

가장 기본적인 방식이며, Vue의 공식적인 데이터 흐름입니다.

| 방향 | 설명 |
|------|------|
| ⬇️ 부모 → 자식 | `props`를 통해 데이터 전달 |
| ⬆️ 자식 → 부모 | `emit`을 통해 이벤트와 함께 데이터 전달 |

### 사용 예시

```vue
<!-- ParentComponent.vue -->
<template>
  <ChildComponent :msg="message" @child-event="handleChildEvent" />
</template>

<script>
import ChildComponent from './ChildComponent.vue'

export default {
  components: { ChildComponent },
  data() {
    return { message: '안녕하세요!' }
  },
  methods: {
    handleChildEvent(payload) {
      console.log('자식으로부터 받은 데이터:', payload)
    }
  }
}
</script>
```

```vue
<!-- ChildComponent.vue -->
<template>
  <button @click="$emit('child-event', 'Hello from Child!')">보내기</button>
</template>

<script>
export default {
  props: ['msg'],
  mounted() {
    console.log(this.msg) // 부모에게 받은 메시지
  }
}
</script>
```

### 장점
- Vue 기본 기능으로 가볍고 직관적

### 단점
- 다단계 컴포넌트 전송 시 구조가 복잡해짐

---

## 2. Mitt (이벤트 버스 방식)

형제 또는 멀리 떨어진 컴포넌트 간 통신에 적합한 방식입니다.  
`mitt` 라이브러리를 통해 전역 이벤트 시스템을 구성합니다.

### 설치
```bash
npm install mitt
```

### 설정 (mitt.js)
```js
// src/mitt/index.js
import mitt from 'mitt';
const emitter = mitt();
export default emitter;
```

### 사용 예시
```vue
<!-- A.vue -->
<script setup>
import emitter from '@/mitt'
emitter.emit('changeResult', '⭕')
</script>
```

```vue
<!-- B.vue -->
<script setup>
import { onMounted } from 'vue'
import emitter from '@/mitt'

onMounted(() => {
  emitter.on('changeResult', (data) => {
    console.log('A에서 받은 데이터:', data)
  })
})
</script>
```

### 장점
- 형제 또는 비연결된 컴포넌트 간 손쉬운 통신

### 단점
- 이벤트 네이밍 관리 필요
- 상태 추적이 어려울 수 있음

---

## 3. Pinia (전역 상태 관리)

Vue 3 공식 상태 관리 라이브러리이며, Vuex의 후속입니다.  
컴포넌트 간 상태를 공유하고 반응형으로 관리할 수 있습니다.

### 설치
```bash
npm install pinia
```

### 설정 (main.js)
```js
import { createPinia } from 'pinia'
app.use(createPinia())
```

### store 생성 (useResultStore.js)
```js
import { defineStore } from 'pinia'

export const useResultStore = defineStore('result', {
  state: () => ({ result: '' }),
  actions: {
    setResult(val) {
      this.result = val
    }
  }
})
```

### 사용 예시
```vue
<!-- A.vue -->
<script setup>
import { useResultStore } from '@/stores/useResultStore'
const store = useResultStore()
store.setResult('⭕')
</script>
```

```vue
<!-- B.vue -->
<script setup>
import { useResultStore } from '@/stores/useResultStore'
const store = useResultStore()
console.log('A에서 설정한 값:', store.result)
</script>
```

### 장점
- 상태 추적이 쉽고 구조적 개발에 적합

### 단점
- 초기 셋업 필요
- 작은 프로젝트에는 다소 과할 수 있음

---

## 정리 표

| 구분            | 방식            | 전송 대상               | 특징                                   |
|-----------------|-----------------|--------------------------|----------------------------------------|
| 1. Props/Emit   | Vue 기본 방식   | 부모 ↔ 자식             | 단방향, 간단한 구조                    |
| 2. Mitt         | 이벤트 버스     | 형제, 떨어진 컴포넌트   | 이벤트 기반, 가볍고 빠름              |
| 3. Pinia        | 전역 상태 관리 | 전체 컴포넌트           | 상태 공유, 대규모 프로젝트에 적합     |

---

## 추천 폴더 구조

```
src/
├── components/
│   ├── A.vue
│   └── B.vue
├── mitt/
│   └── index.js        # mitt 설정
├── stores/
│   └── useResultStore.js  # Pinia store
└── App.vue
```


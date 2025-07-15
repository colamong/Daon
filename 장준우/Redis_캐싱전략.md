# 1일차

# Redis란? / Redis의 장점

### ✅ Redis란?

Redis의 의미를 인터넷에 검색해보면 아래와 같이 나온다.

> 레디스(Redis)는 Remote Dictionary Server의 약자로서, “키-값” 구조의 비정형 데이터를 저장하고 관리하기 위한 오픈 소스 기반의 비관계형 데이터베이스 관리 시스템(DBMS)이다. 
- 위키백과 -
> 

너무 어렵게 적혀져있다. First Word 법칙에 따라 쉽게 바꿔서 이해해보자. 

> **Redis는 데이터 처리 속도가 엄청 빠른 NoSQL 데이터베이스**이다.
> 

이렇게 기억하고 있어도 충분하다. NoSQL 데이터베이스를 풀어서 얘기하자면 Key-Value의 형태로 저장하는 데이터베이스라고 생각하면 된다. 

### ✅ Redis의 장점

Redis는 다양한 장점을 가지고 있다. 여러 특징 중 딱 1가지만 확실하게 기억해라.

> **레디스(Redis)는 인메모리(in-memory)에 모든 데이터를 저장한다. 
그래서 데이터의 처리 성능이 굉장히 빠르다.**
> 

MySQL과 같은 RDBMS의 데이터베이스는 대부분 디스크(Disk)에 데이터를 저장한다. 하지만 Redis는 메모리(RAM)에 데이터를 저장한다. 디스크(Disk)보다 메모리(RAM)에서의 데이터 처리속도가 월등하게 빠르다. 이 때문에 Redis의 데이터 처리 속도가 RDBMS에 비해 훨씬 빠르다. 

# Redis 주요 사용 사례

Redis의 사용 사례를 검색해보면 아주 다양하다. 검색했을 때 아래와 같은 사용 사례를 확인할 수 있다. 

- 캐싱 (Caching)
- 세션 관리 (Session Management)
- 실시간 분석 및 통계 (Real-time Analystics)
- 메시지 큐 (Message Queue)
- 지리공간 인덱싱 (Geospatial Indexing)
- 속도 제한 (Rate Limiting)
- 실시간 채팅 및 메시징 (Real-time Chat And Messaging)

레디스(Redis)에 내장된 기능이 다양하다보니 여러 용도로 사용된다. 레디스를 처음 배우는 입장에서 이 모든 사례를 익히려고 하면 막막할 수 밖에 없다. 따라서 파레토의 법칙에 의해 현업에서 많이 사용되는 ‘**캐싱(데이터 조회 성능 향상)**’만 집중적으로 살펴볼 것이다. 

‘캐싱(데이터 조회 성능 향상)’의 사례를 학습하다보면 자연스럽게 레디스의 기본 기능들이 익혀진다. 이렇게 배우면 레디스를 정말 빨리 익힐 수 있다. 그런 뒤에 레디스의 다른 사례에 대해서도 조금씩 학습하면서 살을 붙여나가면 된다. 

# 백엔드 채용 공고에 종종 등장하는 ‘대용량 트래픽 처리 경험’, ‘Redis 사용 경험’

### ✅ 최근 백엔드 개발자 채용 공고에 자주 등장하는 스펙

백엔드 개발자 채용 공고를 살펴보면 ‘**대용량 트래픽**’에 관한 얘기와 ‘**NoSQL**’에 관한 얘기가 자주 나온다. 이 역량이 서비스가 점점 고도화 될 수록 빼놓을 수 없는 역량이기 때문이다. 이 2가지 역량을 채울 수 있는 방법 중 하나가 Redis이다. 

대용량 트래픽을 처리하기 위해서 필수적으로 사용되는 기능이 Redis의 캐싱(Caching) 기능이다. 또한 Redis는 NoSQL의 일종이다. 따라서 Redis를 학습함으로써 최근 회사에서 요구하는 자격 요건을 맞출 수 있도록 노력해보자. 

# 캐시(Cache), 캐싱(Caching)이란?

### ✅ 캐시(Cache)란?

**캐시(Cache)**란, **원본 저장소보다 빠르게 가져올 수 있는 임시 데이터 저장소**를 의미한다.

참고로 캐시(Cache)라는 단어는 Redis에서만 쓰이는 용어는 아니고 전반적인 개발 분야에서 통용돼서 쓰인다. 

### ✅ 캐싱(Caching)이란?

**캐싱(Caching)**이란 **캐시(Cache, 임시 데이터 저장소)에 접근해서 데이터를 빠르게 가져오는 방식**을 의미한다.

현업에서는 아래와 같이 얘기하는 편이다. 

> “이 API는 응답 속도가 너무 느린데? 이 응답 데이터는 **캐싱(Cahing)** 해두고 쓰는 게 어때?’
> 

이 말을 풀어서 설명하자면 **‘API 응답 결과를 원본 저장소보다 빠르게 가져올 수 있는 임시 데이터 저장소에 저장해두고, 빠르게 조회할 수 있게 만드는 게 어때?’**라는 의미이다. 

## Redis의 주요 활용 사례

### 1. **캐싱 (Caching)**

- **설명**: DB나 외부 API에서 자주 조회되는 데이터를 Redis에 저장해두고, 빠르게 응답
- **예시**:
    - 로그인한 사용자 정보
    - GPT 응답 결과 (ex. 관광지 추천 결과)
    - 인기 게시글 목록
- **장점**:
    - DB 부하 감소
    - API 응답 속도 향상
- **TTL(Time To Live)** 설정으로 일정 시간 후 자동 만료 가능

---

### 2. **세션 저장소**

- **설명**: 웹서버가 세션 정보를 Redis에 저장
- **예시**:
    - 로그인 유지 (Spring에서 `spring-session-data-redis` 사용)
    - 다중 서버 간 세션 공유 가능

---

### 3. **실시간 랭킹 시스템 (Sorted Set)**

- **설명**: 점수 기반으로 정렬된 데이터를 저장하고, 실시간 랭킹을 만듦
- **예시**:
    - 실시간 게임 랭킹
    - 게시글 좋아요 수 기반 인기 글 TOP 10

---

### 4. **분산 락 (Distributed Lock)**

- **설명**: 여러 서버에서 동시에 자원 접근 시 충돌 방지
- **라이브러리**: Redisson (Java), Lettuce, Jedis 등
- **예시**:
    - 결제 중복 처리 방지
    - 재고 수량 감소 시 동시성 제어

---

### 5. **메시지 큐 (Pub/Sub)**

- **설명**: 채널 개념을 통해 메시지를 게시(publish)하고, 여러 구독자(subscriber)에게 전달
- **예시**:
    - 실시간 채팅
    - 알림 시스템

---

### 6. **지표 카운팅 / 트래픽 수집**

- **설명**: 방문자 수, 좋아요 수 등을 Redis의 원자적 증가 연산으로 빠르게 처리

### 1. 캐싱 예시

```java
java
@Cacheable(value = "guide", key = "#location")
public String getGuide(String location) {
    return externalGptService.getGuide(location); // GPT 호출
}

```

- `@Cacheable`: Redis에 저장
- `value`: Redis key의 prefix
- `key`: 특정 파라미터 기반 저장

---

### 🔹 2. 직접 RedisTemplate 사용

```java
java
@Autowired
private RedisTemplate<String, String> redisTemplate;

public void saveValue(String key, String value) {
    redisTemplate.opsForValue().set(key, value, Duration.ofMinutes(30));
}

public String getValue(String key) {
    return redisTemplate.opsForValue().get(key);
}

```

# TIL – Redis & Kafka

## 1. Redis

### 개념 및 특징
- Redis는 인메모리 기반의 고속 Key-Value 저장소이다.
- 비정형 데이터 저장이 가능하며 NoSQL DB의 일종이다.
- 높은 속도, 다양한 자료구조, pub/sub 기능, Persistence 지원 등이 주요 특징.
- 단일 스레드 기반 아키텍처이지만 I/O 처리 병렬성이 뛰어남.

### 주요 자료구조
| 자료구조 | 설명 | 활용 예 |
|----------|------|---------|
| String | 기본 자료형 | 캐시, 카운터, 세션 |
| List | 양방향 연결 리스트 | 채팅 로그 저장, 대기열 |
| Set | 중복 불가 집합 | 유저 태그, 팔로잉 목록 |
| Sorted Set | 점수 기반 정렬 | 랭킹 시스템 |
| Hash | Key-Value의 집합 | 유저 프로필 저장 |
| Stream | 로그 스트리밍 | Kafka-like 기능 제공 |

### 주요 활용 사례
- 세션 스토리지 (Spring Session Redis)
- 로그인 시도 횟수 제한
- 페이지 뷰 카운터
- 실시간 순위 집계
- 실시간 알림 시스템 (Redis Pub/Sub)
- 분산락 (Redlock Algorithm)

### 명령어 예시
```bash
SET key value
GET key
DEL key
INCR page:view
LPUSH queue task1
ZADD ranking 200 user1
ZREVRANGE ranking 0 10 WITHSCORES
```

### Spring Boot 연동 예시

#### Redis 의존성 및 설정
```groovy
implementation 'org.springframework.boot:spring-boot-starter-data-redis'
```

```yaml
spring:
  redis:
    host: localhost
    port: 6379
```

#### 서비스 예제
```java
@Service
public class CacheService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void setUserCache(String userId, String username) {
        redisTemplate.opsForValue().set("user:" + userId, username);
    }

    public String getUserCache(String userId) {
        return (String) redisTemplate.opsForValue().get("user:" + userId);
    }
}
```

---

## 🟦 2. Kafka

### 개념 및 특징
- Kafka는 대용량의 로그 데이터 처리에 특화된 **분산 메시징 시스템**이다.
- Apache Software Foundation에서 관리.
- 고속, 내구성, 확장성, 이벤트 재처리 기능이 강점.
- 메시지를 디스크에 저장해 내구성 보장.

### 구조 및 핵심 요소
| 구성요소 | 설명 |
|----------|------|
| Producer | 메시지를 Kafka에 전송 |
| Consumer | 메시지를 구독하여 처리 |
| Broker | 메시지를 저장 및 관리 |
| Topic | 메시지의 논리적 그룹 |
| Partition | Topic의 물리적 분할 단위 |
| Offset | 메시지의 고유 위치값 |
| Zookeeper | 클러스터 메타데이터 관리 |

### 주요 활용 사례
- 실시간 로그 수집 및 분석 (ELK Stack과 연동)
- 주문 처리 시스템
- 마이크로서비스 간 이벤트 연동
- 데이터 스트리밍 처리 (Kafka Streams, Flink)
- 실시간 알림/모니터링 시스템

### CLI 사용 예
```bash
# 토픽 생성
kafka-topics.sh --create --topic logs --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1

# 메시지 전송
kafka-console-producer.sh --topic logs --bootstrap-server localhost:9092

# 메시지 수신
kafka-console-consumer.sh --topic logs --bootstrap-server localhost:9092 --from-beginning
```

### Spring Boot 연동 예시

#### Kafka 설정
```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: test-group
      auto-offset-reset: earliest
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer
```

#### Kafka 서비스
```java
@Service
public class KafkaProducer {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String topic, String message) {
        kafkaTemplate.send(topic, message);
    }
}

@Component
public class KafkaConsumer {
    @KafkaListener(topics = "logs", groupId = "test-group")
    public void listen(String message) {
        System.out.println("Received: " + message);
    }
}
```

---

## 3. Redis vs Kafka 비교

| 항목 | Redis | Kafka |
|------|-------|-------|
| 주 용도 | 캐시, 세션, 랭킹, pub/sub | 스트리밍, 비동기 처리, 이벤트 큐 |
| 처리 방식 | 메모리 기반 | 디스크 기반 |
| 메시징 방식 | 실시간 브로드캐스트 (비영속) | 영속적 메시지 큐 (재처리 가능) |
| 확장성 | 단일 노드 중심 (Cluster 지원) | 분산 처리에 최적화 |
| 내구성 | 낮음 (옵션 설정 가능) | 높음 (디스크 저장) |
| 주 사용처 | 실시간 캐싱, 제한, 간단한 pub/sub | 마이크로서비스 간 통신, 실시간 로그 |

---

## 4. 실무 시나리오 & 병행 활용 전략

### 시나리오: 주문 처리 시스템

1. 고객이 주문 → Kafka로 메시지 전송 (비동기)
2. Consumer가 Kafka 메시지 수신 → 주문 처리 서비스로 전달
3. 처리 결과를 Redis에 저장 (처리 상태 캐싱)
4. 관리자 화면에서 Redis 통해 실시간 상태 확인

### 시나리오: 실시간 게임 순위 시스템
- Kafka: 게임 이벤트 수집 (킬 수, 점수)
- Redis: 실시간 랭킹 캐싱 (ZADD 사용)
- Kafka → Flink로 처리 → Redis로 반영

---

## 5. 결론 및 회고

- Redis와 Kafka는 실시간성과 확장성을 각각 보완할 수 있는 기술이다.
- Redis는 빠른 응답과 간단한 pub/sub에 적합하며,
- Kafka는 안정성과 대규모 이벤트 처리에 적합하다.
- 둘을 함께 사용하면 실시간성과 내구성을 동시에 확보할 수 있다.

> Redis는 속도를, Kafka는 확장성과 안정성을 담당한다.  
> 시스템의 성격에 맞춰 적절한 조합을 선택하자!

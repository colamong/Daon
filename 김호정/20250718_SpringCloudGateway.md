
# API Gateway
- 여러 마이크로서비스에 대한 통합 진입점(Entry Point) 역할을 수행하는 서버

> ## API Gateway 사용 이유
### 1. 단일 진입점(Single Entry Point) 제공
- 모든 클라이언트 요청이 다양한 마이크로서비스로 분산되기 전에 API Gateway를 통해 들어오므로 각 서비스의 엔드포인트를 노출하지 않아도 됨
- 클라이언트는 하나의 엔드포인트만 알면 되고, 서비스별로 일일이 직접 호출할 필요 X
### 2. 라우팅 및 집계
- 요청을 서비스별로 적절히 라우팅하여 서비스 분리에 따른 복잡성 해소
- 여러 마이크로서비스의 응답을 집계하여 단일 응답으로 반환 가능
	ex) 상품 정보와 재고 등 다양한 정보를 한 번에 제공
### 3. 보안 및 인증/인가
- 인증, 인가, 암호화 등 보안 정책을 중앙에서 일관성 있게 적용 가능
- 서비스 개별 구현이 아닌 Gateway 차원에서 API Key, JWT, OAuth 등 인증 로직을 통합 관리
### 4. 로드밸런싱 및 트래픽 관리
- 여러 인스턴스에 들어오는 트래픽을 효율적으로 분산(Load Balancing)
- 요청 제한, Rate limiting, Circuit Breaker 등 트래픽 제어 및 장애 대응 기능을 기본으로 제공합니다.
### 5. 프로토콜 및 데이터 변환
클라이언트-서비스 간 서로 다른 프로토콜(REST, gRPC 등)이나 데이터 형식(JSON, XML 등) 변환이 필요할 때 유용


# Spring Cloud Gateway

- Spring 진영에서 제공하는 경량 API Gateway로 Spring Boot와 매우 잘 통합
- 반응형(Non-blocking) 아키텍처로 WebFlux 및 Reactor 기반, 동시성에 강하고 높은 처리량 제공

## 주요 기능

1. 다양한 라우팅 기능
- 경로, 헤더, 쿠키, 쿼리 스트링, HTTP 메서드, HOST 등 여러 조건 기준 라우팅
- Static(YAML 등 정적) & Dynamic(코드 기반) 라우팅 모두 지원

2. 필터 체인

- 요청 전(Pre-Filter)/** 후(Post-Filter)** 단계로 나눠, 인증/인가, 트래픽 제어, 변환 등 가공 가능
- Global Filter(전체 적용)와 Route Filter(특정 라우트에만 적용)로 구분

3. 비동기 & 반응형 프로그래밍

- Spring WebFlux & Reactor-Netty 기반. I/O 처리 효율이 높아 대량 트래픽에 적합

4. 서비스 디스커버리 연동

- Eureka, Consul 등과 연동하여 동적으로 서비스 위치 탐색(예: lb://서비스명 URI)

5. 강력한 확장성

- 커스텀 Predicate/Filter/에러처리/라우트 생성 지원
- Spring Security 등 보안 확장 용이

6. 부하 분산 및 장애 허용

- 라우트 내에서 여러 인스턴스 간 로드밸런싱 수행
- Circuit Breaker, Rate Limit, Retry(재시도) 내장

7. 모니터링 & 메트릭

- Spring Boot Actuator, Prometheus 등으로 실시간 상태, 트래픽 모니터링

 

## 환경 설정

### 공통 의존성 (Gradle)
```groovy
dependencies {
  implementation 'org.springframework.cloud:spring-cloud-starter-gateway'
  implementation 'org.springframework.cloud:spring-cloud-starter-netflix-eureka-client'
}

```

### Eureka Server (application.yml)
```yaml
server:
  port: 8761

eureka:
  client:
    register-with-eureka: false
    fetch-registry: false

```

## 각 마이크로서비스 설정

### application.yml (예 : user-service)

```yaml
spring:
  application:
    name: user-service
server:
  port: 8081

eureka:
  instance:
    prefer-ip-address: true
  client:
    service-url:
      defaultZone: http://[eureka-public-ip]:8761/eureka

```
- **prefer-ip-address : true**를 반드시 설정해줘야 EC2 퍼블릭 환경에서 접속 가능

## API Gateway 설정
### application.yml
```yaml
server:
  port: 8000

spring:
  application:
    name: api-gateway

eureka:
  client:
    service-url:
      defaultZone: http://[eureka-public-ip]:8761/eureka

cloud:
  gateway:
    routes:
      - id: user-service
        uri: lb://user-service
        predicates:
          - Path=/users/**

```
- lb://서비스명은 **Eureka의 로드 밸런싱** 주소

### Java 코드 기반 라우팅 (동적 설정 가능)

```java
@Configuration
public class GatewayConfig {

  @Bean
  public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
    return builder.routes()
        .route("user-service", r -> r.path("/users/**")
            .uri("lb://user-service"))
        .route("order-service", r -> r.path("/orders/**")
            .uri("lb://order-service"))
        .build();
  }
}

```

- Java 코드 방식은 조건부 필터, 인증, 동적 제어 로직 삽입 시 필수


#### 참고 자료
- https://alswns7984.tistory.com/122

# 스프링 데이터(Spring Data)
- 스프링 생태계에서 데이터 접근을 추상화하는 프로젝트

### 목적
- 다양한 데이터 저장소 기술(RDB, NoSQL 등)에 대한 공통된 접근 방식 제공
- 반복되는 CRUD 코드를 줄이고, 개발자의 생산성 향상

### 특징
- CrudRepository, PagingAndSortingRepository, Repository 등 공통 인터페이스 제공
- JPA, MongoDB, Redis, Elasticsearch 등 여러 저장소 모듈 존재

# Spring Data JPA

### 개요

Spring Data JPA는 Spring Data 프로젝트의 하위 모듈 중 하나로,  
**JPA(Hibernate 등)를 기반으로 한 데이터 접근을 추상화하고 자동화**하는 라이브러리

> `JpaRepository` 인터페이스를 정의하는 것만으로 대부분의 CRUD와 페이징/정렬 기능 구현 가능

---

### 목적

| 목적 | 설명 |
|------|------|
| 생산성 | 반복되는 CRUD, 페이징, 정렬 코드 제거 |
| 추상화 | Repository 패턴 기반으로 DB 접근 코드 통합 |
| 확장성 | QueryDSL, EntityGraph, Auditing 등 통합 가능 |

---

## 주요 기능

### 1. 메서드 이름 기반 쿼리 자동 생성

```java
List<Member> findByUsernameAndAgeGreaterThan(String username, int age);
```

→ `SELECT * FROM member WHERE username = ? AND age > ?`

---

### 2. @Query로 JPQL 명시

```java
@Query("select m from Member m where m.username = :username")
Member findByUsername(@Param("username") String username);
```

---

### 3. 페이징 & 정렬

```java
Page<Member> findByAgeGreaterThan(int age, Pageable pageable);
```

```java
PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("username").descending());
Page<Member> result = memberRepository.findByAgeGreaterThan(20, pageRequest);
```

---

### 4. EntityGraph – N+1 문제 해결

```java
@EntityGraph(attributePaths = {"team"})
@Query("select m from Member m")
List<Member> findAllWithTeam();
```

→ 지연 로딩(LAZY)을 fetch join처럼 즉시 로딩(EAGER)로 전환


## 구조 이해

```java
public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findByUsername(String username);
}
```

- 구현체는 Spring Data JPA가 자동 생성
- JpaRepository는 PagingAndSortingRepository, CrudRepository 계층 포함


### 실무 사용 전략

| 항목 | 전략 |
|------|------|
| 커스텀 쿼리 | `@Query`, QueryDSL, Native Query |
| 성능 최적화 | `EntityGraph`, `@BatchSize`, `join fetch` |
| 확장성 | 커스텀 Repository 구현 분리 |
| 공통 필드 관리 | `BaseEntity`, `@MappedSuperclass` |
| Repository 명명 규칙 | `도메인 + Repository`, ex) `MemberRepository` |


## 장점 vs 단점

| 장점 | 단점 |
|------|------|
| 코드 작성량 감소 | 추상화로 인해 성능 튜닝 어려울 수 있음 |
| JPA와 Spring 통합 | 복잡한 쿼리는 QueryDSL 등과 병행 필요 |
| 테스트 용이 | 실무에서는 복잡도 ↑ 시 한계 존재 |


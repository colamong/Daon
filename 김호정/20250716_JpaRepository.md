# JpaRepository<T, ID>

Spring Data JPA에서 제공하는 인터페이스로,  
`CrudRepository` + `PagingAndSortingRepository` + JPA-specific 기능까지 모두 포함합니다.

```java
public interface JpaRepository<T, ID extends Serializable>
       extends PagingAndSortingRepository<T, ID>, QueryByExampleExecutor<T> {
    // ...
}
```

### 상속 구조

```
Repository
 └─ CrudRepository
     └─ PagingAndSortingRepository
         └─ JpaRepository
```

---

## 제공 기능

| 범주 | 메서드 | 설명 |
|------|--------|------|
| **CRUD** | `save`, `findById`, `delete` | 기본 CRUD 제공 |
| **페이징/정렬** | `findAll(Pageable)`, `findAll(Sort)` | 페이지네이션/정렬 |
| **배치 처리** | `saveAll`, `deleteInBatch`, `deleteAllInBatch` | 성능 향상 |
| **JPQL 지원** | `@Query`, 메서드 명 기반 쿼리 | 동적 쿼리 정의 |
| **flush** | `flush()` | 영속성 컨텍스트 → DB 강제 반영 |
| **트랜잭션 제어** | Spring `@Transactional` | 읽기 전용 최적화 등 |

---

## JpaRepository를 사용해야 되는 이유

- JPA의 모든 기능을 사용할 수 있으면서도
- 반복되는 CRUD/조회 코드 없이
- 비즈니스 로직에만 집중할 수 있게 해주기 때문입니다.

---

## 주요 메서드 상세 설명

### 1. `save(S entity)` / `saveAll(Iterable<S> entities)`

```java
memberRepository.save(new Member("kim", 30));
```

- 새로운 Entity 저장하거나 기존 Entity 수정할 때 사용 (insert or update)
- saveAll()은 여러 개를 한 번에 처리할 때 사용.

---

### 2. `findById(ID id)` / `findAll()` / `findAllById(Iterable<ID>)`

```java
Optional<Member> member = memberRepository.findById(1L);
List<Member> members = memberRepository.findAllById(List.of(1L, 2L));
```

- 기본 조회 메서드
- ID로 하나 조회하거나 전체/일부 조회할 수 있음
- Optional로 감싸서 null 방지

---

### 3. `delete(T entity)` / `deleteById(ID id)` / `deleteAll()`

```java
memberRepository.deleteById(1L);
memberRepository.deleteAllInBatch();
```

- Entity 삭제
- ID로 삭제하거나, 전체 삭제 가능
- deleteAllInBatch()는 한 번에 처리해 성능 ↑

---

### 4. `findAll(Pageable pageable)`

```java
PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("username").descending());
Page<Member> page = memberRepository.findAll(pageRequest);
```

- 페이징된 데이터를 조회할 때 사용
- PageRequest로 페이지 번호, 크기, 정렬 조건 지정

---

### 5. `flush()`, `saveAndFlush()`

```java
memberRepository.saveAndFlush(member);
memberRepository.flush();
```

- 영속성 컨텍스트의 변경 사항을 DB에 즉시 반영
- saveAndFlush()는 저장 후 바로 반영할 때 사용

> ### 영속성 컨텍스트
 - JPA가 엔티티 객체를 관리하고 상태 변화를 추적하는 **메모리 상의 저장소**
- Entity가 이 컨텍스트에 등록되면 JPA는 해당 객체를 1차 캐시에 저장하고 내부적으로 스냅샷을 생성하여 상태 변화를 추적
- 값이 변경되면 JPA는 이를 감지(Dirty Checking)하고, 
트랜잭션 커밋 시점에만 필요한 SQL을 생성해 DB에 반영
- 동일한 Entity를 다시 조회할 경우 DB에 쿼리를 보내지 않고 **1차 캐시에서 재사용**
→ **성능 향상, 엔티티 동일성(==)** 보장 가능
#### 영속성 컨텍스트는 자동 업데이트, 성능 최적화, 데이터 일관성 보장을 가능하게 해주는 JPA의 핵심 원리



---

### 6. `@Query` → 직접 JPQL 사용

```java
@Query("select m from Member m where m.username = :username")
Member findByUsername(@Param("username") String username);
```

- 복잡하거나 커스텀한 조건이 필요한 경우 JPQL로 직접 쿼리 작성

---

### 7. 정렬 + 페이징 조합

```java
Page<Member> findByAgeGreaterThan(int age, Pageable pageable);
```

- 조건 기반으로 조회하면서 동시에 페이징/정렬까지 적용

---

## JpaRepository 설계 전략

### 1. 도메인별 Repository로 구성

```java
public interface MemberRepository extends JpaRepository<Member, Long> { }
```

---

### 2. 커스텀 Repository 분리

```java
public interface MemberRepositoryCustom {
    List<Member> searchWithCondition(...);
}

public class MemberRepositoryImpl implements MemberRepositoryCustom {
    // QueryDSL이나 JPQL 작성
}
```

---

## 정리

| 항목 | 설명 |
|------|------|
| `JpaRepository` | Spring Data JPA의 핵심 인터페이스 |
| 장점 | 코드 작성 최소화, 생산성 향상 |
| 사용 방식 | 인터페이스 정의 시 자동 구현 |
| 확장 | 커스텀 구현, Native Query 등 가능 |

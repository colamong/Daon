# 2일차

# JWT+Spring Security

<aside>
💡

**Spring Security 기반의 JWT 인증 시스템.**

로그인 요청 시 사용자 정보를 확인한 뒤 JWT를 생성하여 응답 헤더에 전달하고,

이후 클라이언트가 요청 시 해당 토큰을 포함하여 인증 필터에서 검증을 거쳐 보호된 리소스에 접근할 수 있도록 구성

**단일 AccessToken 처리하는 방식 → Access Token + Refresh Token 조합 방식으로 고도화**

</aside>

# 기존 프로젝트 전체 구조

```java
auth_project/
├── **controller**/               REST API 엔드포인트
│   └── AuthController.java
├── **dto**/                      요청/응답 객체
│   ├── LoginRequestDTO.java
│   └── LoginResponseDTO.java
├── **exception**/                인증 관련 예외 정의
│   └── AuthException.java
├── **jwt**/                      JWT 관련 유틸과 필터
│   ├── JwtAuthenticationFilter.java
│   ├── JwtProvider.java
│   └── UserPrincipal.java
└── **service**/                  비즈니스 로직 처리
    ├── AuthService.java
    ├── AuthServiceImpl.java
    └── CustomUserDetailsService.java
```

### 동작 흐름

```java

[1] 클라이언트 → /auth/login (email, password)
      ↓
[2] AuthController.login()
      ↓
[3] AuthServiceImpl.login()
      → DB에서 사용자 정보 조회
      → 비밀번호 비교
      → JwtProvider.createToken() 호출
      ↓
[4] JWT 생성 후 Authorization 헤더에 담아 응답
      ↓
[5] 이후 클라이언트는 모든 요청 시:
    → Authorization: Bearer <JWT>
      ↓
[6] JwtAuthenticationFilter
      → 헤더에서 JWT 추출 및 유효성 검증
      → userId로 사용자 정보 로드
      → SecurityContextHolder에 인증 등록
      ↓
[7] 인증된 사용자로 인가(Authorization)된 API 접근 가능

```

# 전체 JWT 인증 흐름

<aside>
💡

### 1단계: 로그인 시 토큰 발급

: 로그인 (/auth/login) → JWT 생성 → 헤더에 담아 응답

### 2단계: 인증된 요청 시 JWT 검증 → 인증 객체 등록

: 이후 매 요청마다 → JWT 검증 → 인증 객체 생성 → SecurityContext 등록

</aside>

### 1단계: 로그인 시 토큰 발급

```java

로그인 (/auth/login)
→ JWT 생성: JwtProvider.createToken()
→ 응답 헤더에 Authorization: Bearer <token>

[1단계] 로그인
└── AuthController.login()
    └── AuthServiceImpl.login()
        ├── UserMapper.selectByEmail()
        ├── PasswordEncoder.matches()
        └── JwtProvider.createToken()
    └── ResponseEntity.ok().header("Authorization", "Bearer <token>")
```

### JwtProvider.createToken()

```java
public String createToken(String userId, List<String> roles) {
    // JWT의 payload(Claims) 생성 및 설정
    Claims claims = Jwts.claims().setSubject(userId); // 'sub' 필드에 userId 저장 (토큰의 주체)
    claims.put("roles", roles);                        // 사용자 권한 정보를 roles 필드에 추가 (커스텀 claim)

    // 현재 시각과 만료 시각 설정
    Date now = new Date();                             // 발급 시각(iat)
    Date expiry = new Date(now.getTime() + accessTokenExpiration); // 만료 시각(exp): 현재 시간 + 3시간

    // JWT 최종 생성
    return Jwts.builder()
            .setClaims(claims)                         // 설정한 payload(claims) 등록
            .setIssuedAt(now)                          // 토큰 발급 시간 설정
            .setExpiration(expiry)                     // 토큰 만료 시간 설정
            .signWith(SignatureAlgorithm.HS256, secretKey.getBytes()) // 서명 알고리즘 + 비밀 키로 서명
            .compact();                                // 모든 정보를 인코딩하여 최종 JWT 문자열 생성
}
```

### 생성된 JWT 구조

```java
HEADER:
{
  "alg": "HS256",
  "typ": "JWT"
}

PAYLOAD:
{
  "sub": "1",
  "roles": ["ROLE_USER"],
  "iat": 1718000000,
  "exp": 1718010800
}

SIGNATURE:
HMACSHA256(base64UrlEncode(header) + "." + base64UrlEncode(payload), secretKey)
```

## Controller 패키지

```java
auth_project/
├── **controller**/               REST API 엔드포인트
│   └── AuthController.java   → 로그인 및 회원가입 요청을 처리하는 진입점
│                             → /auth/login, /auth/signup API 제공
│                             → 로그인 성공 시 JWT를 Authorization 헤더에 담아 반환

```

**controller/AuthController.java**

```java
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<UserIdResponse> register(@RequestBody User user) {
        UserIdResponse response = userService.registerUser(user);
        URI location = URI.create("/user/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
        String token = authService.login(request);

        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + token)
                .build();  // 바디 없음
    }
}

```

- 토큰은 **HTTP Header의 Authorization 필드**에 담아 응답

<aside>
💡

**토큰은 헤더에만?**

LoginResponseDTO 같은 DTO로 응답 바디에 포함할 수 있음

```java

  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "email": "user@example.com"
  }
}
```

**단점**:

- 요청마다 클라이언트가 body 파싱 → 토큰 저장 → 헤더에 다시 수동으로 넣어야 함
- 보안 도구(API Gateway, Spring Security, Swagger 등**)** 과의 연동이 어렵고 추가 설정 필요

**헤더**

- 보안 구조와 HTTP 규약에 가장 부합함
- 토큰은 **요청 메타정보**에 해당하므로, 헤더가 위치적으로 올바름
- 토큰만 별도로 전달하면 클라이언트가 저장/전송 로직을 자유롭게 구성할 수 있음
- 클라이언트가 이후 요청에 Authorization 헤더로 다시 보낼 수 있음

**→ 헤더에 JWT를 주는 방식은 실무에서 가장 흔하고 바람직한 방식**

</aside>

### 2단계: 인증된 요청 시 JWT 검증 → 인증 객체 등록

```java

이후 요청마다:
→ JwtAuthenticationFilter.doFilterInternal()
→ 헤더에서 JWT 추출 및 검증
→ 인증 객체 생성 → SecurityContext에 등록

[2단계] 요청 시 인증 처리
└── JwtAuthenticationFilter.doFilterInternal()
    ├── extractToken()
    ├── JwtProvider.validateToken()
    ├── JwtProvider.getUserId()
    ├── CustomUserDetailsService.loadUserById()
    ├── UserPrincipal.from(user)
    └── SecurityContextHolder.setAuthentication()
```

**JWT 검증**

### JwtProvider. validateToken(String token)

```java
// 토큰이 유효한지 검사
public boolean validateToken(String token) {
    try {
        parseClaims(token); // 내부적으로 파싱 & 서명/만료 검사 수행
        return true;        // 유효한 토큰
    } catch (JwtException | IllegalArgumentException e) {
        return false;       // 만료 or 서명 오류 등 → 유효하지 않음
    }
}
```

- parseClaims(token)
    - JWT를 파싱하고, 서명을 검증하며, 만료 여부까지 확인
    - 실패 시 JwtException 또는 IllegalArgumentException 발생
- 예외가 발생하지 않으면 `true`
    
    → **정상적이고 유효한 토큰**
    
- 예외가 발생하면 `false`
    
    → **만료되었거나 위조된 토큰**
    

 **인증 객체 등록**

- Authorization 헤더에서 JWT 추출
- JWT 유효성 검사
- 사용자 ID 추출 (sub)
- DB에서 사용자 정보 로드 (UserDetails)
- 인증 객체 생성
- SecurityContextHolder에 등록
- 필터 체인 다음 단계로 진행

### SecurityContextHolder.setAuthentication()

```java
@Override
protected void doFilterInternal(HttpServletRequest request,
                                HttpServletResponse response,
                                FilterChain filterChain)
        throws ServletException, IOException {

    // 요청 헤더에서 JWT 추출 (Authorization: Bearer xxx...)
    String token = extractToken(request);

    //  토큰이 존재하고, 유효한지 검사 (서명, 만료시간 포함)
    if (token != null && jwtProvider.validateToken(token)) {

        //  토큰에서 사용자 식별자(userId)를 추출 (sub 클레임)
        Long userId = Long.valueOf(jwtProvider.getUserId(token));

        //  DB에서 사용자 정보 조회 (UserDetails 형태로 반환)
        UserDetails userDetails = userDetailsService.loadUserById(userId);

        //  인증 객체 생성 (JWT 기반이라 credentials는 null)
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

        //  추가 정보 설정 (IP, 세션 등 요청 기반 부가정보)
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        //  Spring Security 컨텍스트에 인증 객체 등록
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    //  다음 필터로 요청을 전달 (컨트롤러로 이어짐)
    filterChain.doFilter(request, response);
}

```

- 

# Access Token + Refresh Token 도입

## **[1단계]** 1.Refresh Token 도입

---

**1. Access Token은 짧게**

- JWT는 클라이언트가 보관하는 토큰이기 때문에, **노출되었을 때 되돌릴 수 없다.**
- 따라서 노출되더라도 피해를 줄이기 위해 **유효시간을 짧게** 설정해야 한다. (예: 5~15분)
- → 하지만 이렇게 하면 사용자는 금방 로그아웃되어야 하고,
    
    계속 로그인을 반복하게 됨 → UX가 매우 나빠짐
    

**2. Refresh Token이 로그인 상태를 유지**

- Access Token이 만료되면, Refresh Token을 통해 **다시 Access Token을 발급받을 수 있다.**
- 이때 Refresh Token은 보통 **7일~14일 등 긴 유효기간**을 갖고,
    
    서버나 Redis에 **사용자 ID와 함께 저장**된다.
    
- 클라이언트는 Access Token이 만료되었을 때, **Refresh Token을 통해 로그인 상태를 연장**할 수 있다.

**3. 서버는 Refresh Token을 "조절 가능한 토큰"으로 활용**

- 서버는 Refresh Token을 DB 또는 Redis에 저장해두고, 다음과 같은 유연한 처리가 가능하다:
    - 로그아웃 시 → Refresh Token을 삭제해서 무효화
    - 중복 로그인 차단, 강제 로그아웃도 가능
    - Refresh Token 탈취 감지 시 → 해당 계정만 차단 가능

## **[2단계]** 토큰 발급 구조 변경

```java
[기존]
로그인 → Access Token만 발급 → 클라이언트 저장 → 만료되면 재로그인

[개선]
로그인 → Access + Refresh Token 발급
        → Access Token은 바디로, Refresh Token는 쿠키로 전달
        → Access Token만료 시, Refresh Token로 /auth/refresh-token 호출
        → 새 Access Token 발급 → 로그인 유지
```

## 기존 구조 vs 개선 구조 (Refresh Token 포함)

| 항목 | 기존 구조 (Access Token만) | 개선된 구조 (Access + Refresh Token) |
| --- | --- | --- |
| **발급 토큰** | Access Token만 발급 | Access Token + Refresh Token 동시 발급 |
| **Access Token 전달 방식** | 응답 바디 또는 응답 헤더 | 응답 **바디(JSON)** |
| **Refresh Token 전달 방식** |  없음 | **HttpOnly + Secure Cookie** |
| **Access Token 저장 위치 (클라이언트)** | LocalStorage | LocalStorage |
| **Refresh Token 저장 위치 (서버)** |  없음 | Redis(userId → Refresh Token) |
| **Access Token 만료 후** | 재로그인 필요 | **/auth/refresh-token**으로 재발급 가능 |
| **로그아웃 처리 방식** | 클라이언트 측 Access Token삭제만 | 서버에서 Refresh Token 삭제 (완전한 무효화 가능) |

### **Access Token 전달 방식**

처음 구조에서는 **Access Token만 존재하고**, **Refresh Token이 없기 때문에 토큰이 매우 중요하고 민감**했음.

- 그래서 토큰을 바디보다 **헤더에서만 주고받는 게 더 안전**
- 그러나 실무에서는 어차피  **Access Token**는 **클라이언트 측 저장소(LocalStorage 등)** 에 보관됨
- **발급 시 바디에 포함하고, 이후 요청 시 헤더로 보내는 패턴**이 가장 흔함

**결론적으로 발급 시에는 응답 바디로 내려주는 것이 더 일반적**

- 클라이언트에서 `fetch()`나 `axios`로 쉽게 파싱해서 저장 가능
- 예: `{ accessToken: "...", userInfo: {...} }`

## **[3단계] Access Token 재발급 로직 구현**

```java
[클라이언트]
 ↳ /auth/refresh-token 요청
     (Refresh Token은 HttpOnly + Secure 쿠키로 자동 전송됨)

[서버]
 1. 쿠키에서 Refresh Token 추출
 2. Refresh Token의 유효성 검증
     - JWT 형식 검증 (서명, 만료)
     - Redis에 해당 사용자의 Refresh Token이 존재하고 일치하는지 확인
 3. 유효하다면 → 새로운 Access Token 생성 후 응답 바디로 전송
 4. Refresh Token이 유효하지 않거나 만료되었으면 → 401 Unauthorized 반환
```

### **1. 요청에서 Refresh Token 쿠키 추출**

- 클라이언트는 로그인 시 발급받은 **Refresh Token을 HttpOnly 쿠키**로 저장하고 있음.
- `/auth/refresh-token` 요청을 보낼 때 브라우저는 자동으로 이 쿠키를 함께 전송함.
- 서버는 `HttpServletRequest`에서 모든 쿠키를 확인하고,
    
    이름이 `"refreshToken"`인 쿠키를 찾아 값을 추출함.
    
- 이 쿠키가 없다면 인증 갱신 요청 자체를 인정할 수 없기 때문에 `401 Unauthorized`를 반환해야 함.

### **2. Refresh Token의 형식 및 서명 유효성 검증**

- 추출한 Refresh Token이 유효한 JWT인지 검증함.
    - 서명이 올바른지 (secretKey로 서명한 것인지)
    - 만료되지는 않았는지 (`exp` 시간 초과 여부)
- 이 검사는 `JwtProvider.validateToken()` 메서드를 통해 수행함.
- 이 단계에서 토큰이 위조되었거나, 너무 오래돼 만료된 경우에는 무조건 거절 (`401 Unauthorized`).
- 이렇게 하면 **만료되었거나 조작된 토큰**에 대한 방어가 가능함.

### **3. Refresh Token에 포함된 사용자 식별값(userId) 추출**

- 유효한 JWT라면 내부 Claims에서 `sub` 필드를 꺼낼 수 있음.
- `JwtProvider.getUserId()`를 호출해서 `subject` 값으로 넣어둔 userId를 추출함.
- 이 userId는 나중에 Redis 키 조회 시 `"refresh:user:{userId}"` 형식으로 사용될 예정.
- 여기까지 확인되면 해당 토큰은 최소한 “누구의 것인지”를 식별할 수 있음.

### 4. **Redis에 저장된 사용자별 Refresh Token과 비교**

- Redis에서 `refresh:user:{userId}`라는 키를 통해 저장된 기존 Refresh Token을 꺼냄.
- 요청에서 받은 Refresh Token과 Redis에서 꺼낸 값이 **정확히 일치하는지 비교**함.
- 둘이 다르면 무조건 위조 또는 탈취된 토큰이라고 판단하고 거부함.
- 이 구조 덕분에:
    - 로그아웃 시 Redis에서 삭제만 하면 해당 토큰은 즉시 무효화됨.
    - 탈취된 토큰이더라도 Redis에 없으면 재사용을 막을 수 있음.

### **5. Access Token 새로 생성 및 응답 구성**

- 모든 검증이 통과되었으면, 해당 사용자 ID로 새로운 **Access Token**을 생성함.
- 이때 `JwtProvider.createAccessToken(userId, roles)` 메서드를 사용함.
    - `roles` 정보는 Refresh Token에는 없기 때문에, 필요하면 DB 또는 UserDetailsService에서 불러와야 함.
- 새로 생성된 Access Token은 JSON 응답 바디에 담아 클라이언트에 전달함.
- 클라이언트는 이 Access Token을 다시 저장하거나 Authorization 헤더에 넣어 이후 요청에 사용하게 됨.

## [4단계] 로그아웃 처리

```java
[클라이언트]
 ↳ /auth/logout 요청 (Authorization: Bearer <Access Token> 포함)

[서버]
 ↳ Access Token → 사용자 ID 추출
 ↳ Redis에서 "refresh:user:{userId}" 삭제
 ↳ 응답 시 refreshToken 쿠키 제거

[결과]
 → 서버-클라이언트 모두 토큰 제거 → 인증 완전 종료
```

### **클라이언트가 /auth/logout 엔드포인트 호출**

- 사용자가 로그아웃 버튼을 누르면,
- 프론트엔드는 **현재 Access Token을 Authorization 헤더에 담고**,
    
    `/auth/logout` API를 호출한다.
    
- 이때 브라우저는 함께 저장된 **Refresh Token 쿠키도 함께 전송**한다 (자동).

### **서버는 Access Token으로부터 사용자 식별**

- 서버는 Authorization 헤더에서 Access Token을 꺼내고,
- `JwtProvider.getUserId(token)` 또는 Spring Security의 `SecurityContext`를 통해
    
    **현재 로그인된 사용자의 ID**를 추출한다.
    

### **Redis에서 해당 사용자의 Refresh Token 삭제**

- 서버는 Redis에 저장된 `"refresh:user:{userId}"` 키를 삭제한다.
- 이를 통해 **현재 Refresh Token은 더 이상 유효하지 않게 된다.**
- 향후 이 사용자가 동일한 Refresh Token으로 `/refresh-token`을 호출해도 실패하게 됨.

### **클라이언트 측 쿠키와 토큰 삭제 지시**

- 응답 시, `"refreshToken"` 쿠키를 삭제하는 `Set-Cookie` 헤더를 함께 내려보낸다.
    - 이때 `Set-Cookie: refreshToken=; Max-Age=0;` 와 같이 설정하면 쿠키가 제거됨.
- 클라이언트에서도 로컬 스토리지 등에 저장된 Access Token을 삭제한다.
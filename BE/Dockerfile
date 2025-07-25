# 1단계: 빌드용 이미지
FROM gradle:8.7.0-jdk17 AS build

# 작업 디렉토리 설정
WORKDIR /app

# 의존성 캐시를 위해 gradle 파일만 먼저 복사
COPY build.gradle .
COPY settings.gradle .
RUN gradle dependencies --no-daemon
# 나머지 소스 복사 후 빌드
COPY . .
RUN gradle build --no-daemon -x test

# 2단계: 경량화된 JRE 이미지로 실행
FROM eclipse-temurin:17-jdk
WORKDIR /app


# 빌드 결과 jar 파일 복사 (target 폴더 내 jar 이름 확인 필요)
COPY --from=build /app/build/libs/*.jar app.jar

# 포트 노출
EXPOSE 8080

# 실행 명령어
ENTRYPOINT ["java","-jar","app.jar"]

#실행하려면
# docker build -t my-spring-app .
# docker run -p 8080:8080 my-spring-app
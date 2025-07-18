# 5일차 과제

이번 글에서는 AWS EC2와 Docker를 조합하여 Spring Boot 애플리케이션을 효율적으로 배포하는 방법을 단계별로 알아보겠습니다. 특히 AWS ECR(Elastic Container Registry)을 활용한 이미지 관리부터 Docker Compose를 통한 멀티 컨테이너 환경 구축까지 다룰 예정입니다.

## **EC2 환경 준비하기**

### **Docker와 Docker Compose 설치**

먼저 Ubuntu EC2 인스턴스에 Docker 환경을 구축해야 합니다. 다음 명령어로 한 번에 설치할 수 있습니다.

```bash
sudo apt-get update && \
sudo apt-get install -y apt-transport-https ca-certificates curl software-properties-common && \
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add - && \
sudo apt-key fingerprint 0EBFCD88 && \
sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" && \
sudo apt-get update && \
sudo apt-get install -y docker-ce && \
sudo usermod -aG docker ubuntu && \
newgrp docker
```

이어서 Docker Compose도 설치합니다.

```bash
sudo curl -L "https://github.com/docker/compose/releases/download/2.27.1/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose && \
sudo chmod +x /usr/local/bin/docker-compose && \
sudo ln -s /usr/local/bin/docker-compose /usr/bin/docker-compose
```

설치가 완료되면 버전을 확인하여 정상 설치를 검증합니다.

```
docker -v
docker compose version

```

### **EC2에 AWS CLI 설치**

AWS 서비스와의 연동을 위해 AWS CLI를 설치합니다.

```
sudo apt install unzip
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip awscliv2.zip
sudo ./aws/install
aws --version

```

## **AWS ECR을 활용한 이미지 관리**

### **ECR이 필요한 이유**

![](https://blog.kakaocdn.net/dna/lJfxl/btsOLDCgxSR/AAAAAAAAAAAAAAAAAAAAANJhO2c2bY6zVIWYa6niF9moSqR--rUbrQN9IJEwOvag/img.png?credential=yqXZFxpELC7KVnFOS48ylbz2pIh7yKj8&expires=1753973999&allow_ip=&allow_referer=&signature=hY7a85GvuKcM3YJSS45MB9%2BcEo0%3D)

전통적인 배포 방식

전통적인 배포 방식에서는 GitHub에 코드를 푸시하고 EC2에서 전체 프로젝트를 클론받아 실행했습니다. 이 방식은 몇 가지 문제점이 있습니다.

- 전체 소스코드가 서버에 노출됨
- 실행 환경 구성이 복잡함
- 의존성 관리의 어려움

Docker와 ECR을 활용하면 이러한 문제를 해결할 수 있습니다. 필요한 코드와 환경만 이미지로 패키징하여 저장하고, 배포 시에는 해당 이미지만 다운로드하여 실행하는 방식입니다.

![](https://blog.kakaocdn.net/dna/GIgnV/btsOLCiX8Qz/AAAAAAAAAAAAAAAAAAAAAKIv6CpCHdoiDgEe0h0vlD2QTkHgFeb5AtB5Ba2tJPWO/img.png?credential=yqXZFxpELC7KVnFOS48ylbz2pIh7yKj8&expires=1753973999&allow_ip=&allow_referer=&signature=BEfiWFWrRtDPEIHjrBf%2BVjSSiB8%3D)

ECR을 활용한 방식

AWS ECR은 Docker Hub와 같은 역할을 하는 AWS의 컨테이너 레지스트리 서비스입니다. AWS 생태계와의 연동이 원활하고 보안성이 뛰어나다는 장점이 있습니다.

### **IAM 사용자 생성 및 권한 설정**

ECR을 사용하기 위해서는 먼저 IAM 사용자를 생성하고 적절한 권한을 부여해야 합니다.

1. AWS 콘솔에서 IAM 서비스로 이동
2. 사용자 생성 시 "ECR-User"와 같은 명확한 이름 설정
3. 권한 정책에서 "AmazonEC2ContainerRegistryFullAccess" 정책 연결
4. 사용자 생성 완료

![](https://blog.kakaocdn.net/dna/o9YKE/btsOL4sNvRI/AAAAAAAAAAAAAAAAAAAAADKElo-39tE1RQPb8xeae_0U1yEJc7zeapxpT9WfIW4y/img.png?credential=yqXZFxpELC7KVnFOS48ylbz2pIh7yKj8&expires=1753973999&allow_ip=&allow_referer=&signature=KtwcIWcZsBXLhk7ASuVfxVsfOyw%3D)

권한 정책 설정

생성된 사용자의 Access Key를 발급받아야 합니다.

1. 생성된 사용자 선택 후 "보안 자격 증명" 탭 이동
2. "액세스 키 만들기" 선택
3. "외부에서 실행되는 애플리케이션" 선택
4. 액세스 키와 시크릿 액세스 키 저장

![](https://blog.kakaocdn.net/dna/tb4fz/btsOKDbInJt/AAAAAAAAAAAAAAAAAAAAAHToZ81172Ca3sjp-eis2CoIbJ62bOS1DqnFeJms4W_s/img.png?credential=yqXZFxpELC7KVnFOS48ylbz2pIh7yKj8&expires=1753973999&allow_ip=&allow_referer=&signature=EmxTpPlnnUsEPatUnJndkIpTJx4%3D)

외부에서 실행되는 애플리케이션으로 엑세스 키 생성

### **AWS CLI 설정**

발급받은 액세스 키로 AWS CLI를 설정합니다.

```bash
aws configure
```

다음 정보를 입력합니다.

- AWS Access Key ID: 발급받은 액세스 키
- AWS Secret Access Key: 발급받은 시크릿 액세스 키
- Default region name: ap-northeast-2 (서울 리전)
- Default output format: (엔터로 스킵)

![](https://blog.kakaocdn.net/dna/cedhwW/btsOMQ8kyge/AAAAAAAAAAAAAAAAAAAAAG7L0BO4Va0rRI16-1SBW9FNcxucbUBYPh-EEGgiQaZv/img.png?credential=yqXZFxpELC7KVnFOS48ylbz2pIh7yKj8&expires=1753973999&allow_ip=&allow_referer=&signature=iYeUGTf2BKHpsIs32uPOKMP8jk4%3D)

발급 받은 엑세스 키 확인

### **ECR 레포지토리 생성**

AWS 콘솔에서 ECR 서비스로 이동하여 새 레포지토리를 생성합니다.

1. "레포지토리 생성" 클릭
2. 가시성 설정을 "프라이빗"으로 선택 (보안상 권장)
3. 레포지토리 이름 입력 (예: "my-spring-app")
4. 레포지토리 생성 완료

![](https://blog.kakaocdn.net/dna/bnYmQX/btsOK99eAx5/AAAAAAAAAAAAAAAAAAAAAKiUkD6szNwlZ8xdd8VR2ZnCOdBytdsfWvbMLdB5HtX_/img.png?credential=yqXZFxpELC7KVnFOS48ylbz2pIh7yKj8&expires=1753973999&allow_ip=&allow_referer=&signature=0pJc8YwbRvUg1lGSH0WML8z4BkA%3D)

프라이빗으로 생성

## **Spring Boot 애플리케이션 배포**

### **기본 배포 방식**

### **Dockerfile 작성**

프로젝트 루트에 Dockerfile을 생성합니다.

```coffeescript
FROM openjdk:17-jdk

COPY build/libs/*SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]

```

### **이미지 빌드 및 ECR 업로드**

먼저 Spring Boot 애플리케이션을 빌드합니다.

```
./gradlew clean build

```

ECR에 로그인하고 이미지를 빌드한 후 업로드합니다.

![](https://blog.kakaocdn.net/dna/wx2Tk/btsOLQBeHYq/AAAAAAAAAAAAAAAAAAAAAPTkPti4fqkf8bfM1XucfixGZp6SpDmSp-g8rbJvMW8p/img.png?credential=yqXZFxpELC7KVnFOS48ylbz2pIh7yKj8&expires=1753973999&allow_ip=&allow_referer=&signature=h8rDrzh90Q7J%2B1Rw%2Bf6Ipgg4mFQ%3D)

푸시 명령 보기에서 가이드 따라가기

```bash
# ECR 로그인
aws ecr get-login-password --region ap-northeast-2 | docker login --username AWS --password-stdin [ECR URI]

# EC2 아키텍처에 맞게 이미지 빌드
docker build --platform linux/amd64 -t my-spring-app .

# 이미지 태깅
docker tag my-spring-app:latest [ECR URI]/my-spring-app:latest

# ECR에 이미지 푸시
docker push [ECR URI]/my-spring-app:latest
```

### **EC2에서 컨테이너 실행**

EC2 인스턴스에 접속하여 이미지를 다운로드하고 실행합니다.

![](https://blog.kakaocdn.net/dna/dg1lf5/btsOLc52tSh/AAAAAAAAAAAAAAAAAAAAACWtPoJY_kWo-i318dLvYM62KZqHe2kXIFkipq4v46O8/img.png?credential=yqXZFxpELC7KVnFOS48ylbz2pIh7yKj8&expires=1753973999&allow_ip=&allow_referer=&signature=L2nnHGUKwlGHW58sjvqrnzr4Gek%3D)

ECR URL 확인

```bash
# ECR 로그인
aws ecr get-login-password --region ap-northeast-2 | docker login --username AWS --password-stdin [ECR URI]

# 이미지 다운로드
docker pull [ECR URI]/my-spring-app

# 컨테이너 실행
docker run -d -p 8080:8080 [ECR URI]/my-spring-app
```

### **Docker Compose를 활용한 배포**

### **compose.yml 작성**

단일 컨테이너 배포도 Docker Compose로 관리하면 더 체계적입니다.

```less
services:
  my-spring-app:
    image: [ECR URI]/my-spring-app:latest
    ports:
      - 8080:8080

```

### **Compose 실행**

```bash
docker compose up --build -d
```

### **애플리케이션 업데이트**

코드 변경 후 업데이트하는 과정입니다.

```bash
# 로컬에서 새 버전 빌드 및 푸시
./gradlew clean build
docker build --platform linux/amd64 -t my-spring-app .
docker tag my-spring-app:latest [ECR URI]/my-spring-app:latest
docker push [ECR URI]/my-spring-app:latest

# EC2에서 새 이미지 적용
docker compose pull
docker compose up --build -d
```

## **멀티 컨테이너 환경 구축**

실제 운영 환경에서는 애플리케이션 서버뿐만 아니라 데이터베이스, 캐시 서버 등 여러 컨테이너가 함께 동작해야 합니다. Docker Compose를 활용하면 이러한 복잡한 환경을 쉽게 관리할 수 있습니다.

### **Spring Boot + MySQL + Redis 구성**

다음과 같은 compose.yml을 작성합니다.

```yaml
services:
  my-spring-app:
    image: [ECR URI]/my-spring-app:latest
    ports:
      - 8080:8080
    depends_on:
      mysql-db:
        condition: service_healthy
      redis-cache:
        condition: service_healthy
    environment:
      - SPRING_DATASOURCE_URL=jdbc:mysql://mysql-db:3306/mydb
      - SPRING_REDIS_HOST=redis-cache

  mysql-db:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: pwd1234
      MYSQL_DATABASE: mydb
    volumes:
      - ./mysql_data:/var/lib/mysql
    ports:
      - 3306:3306
    healthcheck:
      test: ["CMD", "mysqladmin", "ping"]
      interval: 5s
      retries: 10

  redis-cache:
    image: redis:alpine
    ports:
      - 6379:6379
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 5s
      retries: 10

```

### **헬스체크와 의존성 관리**

위 설정에서 주목할 점은 depends_on과 healthcheck 설정입니다.

healthcheck는 각 서비스가 정상적으로 시작되었는지 확인하는 방법을 정의합니다. MySQL의 경우 mysqladmin ping 명령으로, Redis의 경우 redis-cli ping 명령으로 상태를 확인합니다.

depends_on의 condition: service_healthy 설정은 의존하는 서비스의 헬스체크가 성공한 후에 해당 서비스를 시작하도록 보장합니다. 이를 통해 데이터베이스가 완전히 준비된 후에 애플리케이션이 시작되도록 할 수 있습니다.

### **볼륨을 통한 데이터 영속화**

MySQL 서비스에는 볼륨 설정이 포함되어 있습니다.

```jsx
volumes:
  - ./mysql_data:/var/lib/mysql

```

이 설정을 통해 컨테이너가 재시작되어도 데이터베이스 데이터가 보존됩니다. EC2 인스턴스의 ./mysql_data 디렉토리에 실제 데이터가 저장되므로, 컨테이너 관리와 독립적으로 데이터를 유지할 수 있습니다.

### **전체 시스템 실행**

모든 설정이 완료되면 다음 명령으로 전체 시스템을 실행합니다.

```bash
docker compose up -d --build
```

실행 상태를 확인하려면 다음 명령들을 사용합니다.

```
# 모든 컨테이너 상태 확인docker ps

# Compose 서비스별 상태 확인docker compose ps

# 전체 로그 확인docker compose logs

# 특정 서비스 로그 확인docker compose logs my-spring-app

```

## **마무리**

AWS EC2와 Docker를 활용한 배포는 현대적인 애플리케이션 운영의 표준이 되었습니다. ECR을 통한 이미지 관리부터 Docker Compose를 활용한 멀티 컨테이너 환경 구축까지, 이러한 기술들을 조합하면 확장 가능하고 유지보수가 용이한 배포 시스템을 구축할 수 있습니다.
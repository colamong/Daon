# 4일차 과제

## [**CI/CD란**](https://leve68.tistory.com/entry/CICD-%EA%B8%B0%EB%B3%B8-%EA%B0%9C%EB%85%90#CI%2FCD%EB%9E%80-1)

CI/CD는 **Continuous Integration(지속적 통합)**과 **Continuous Deployment(지속적 배포)**의 줄임말로, 소프트웨어 개발 과정에서 테스트, 통합, 배포의 과정을 자동화하는 개발 방법론입니다.

### [**전통적인 개발 방식의 문제점**](https://leve68.tistory.com/entry/CICD-%EA%B8%B0%EB%B3%B8-%EA%B0%9C%EB%85%90#%EC%A0%84%ED%86%B5%EC%A0%81%EC%9D%B8%20%EA%B0%9C%EB%B0%9C%20%EB%B0%A9%EC%8B%9D%EC%9D%98%20%EB%AC%B8%EC%A0%9C%EC%A0%90-1-1)

기존의 수동 배포 방식에서는 다음과 같은 문제점들이 있었습니다. 개발자가 코드를 완성한 후 직접 테스트를 실행해야 하고, 여러 개발자의 코드를 수동으로 통합하는 과정에서 충돌이 발생할 가능성이 높습니다. 또한 배포 과정에서 인간의 실수로 인한 오류가 발생할 수 있으며, 이 모든 과정이 시간 소모적입니다.

### [**CI/CD가 제공하는 해결책**](https://leve68.tistory.com/entry/CICD-%EA%B8%B0%EB%B3%B8-%EA%B0%9C%EB%85%90#CI%2FCD%EA%B0%80%20%EC%A0%9C%EA%B3%B5%ED%95%98%EB%8A%94%20%ED%95%B4%EA%B2%B0%EC%B1%85-1-2)

CI/CD를 도입하면 이러한 문제들을 체계적으로 해결할 수 있습니다. 코드 변경사항이 발생할 때마다 자동으로 빌드와 테스트가 실행되어 문제를 조기에 발견할 수 있고, 테스트를 통과한 코드만 자동으로 배포되어 안정성이 보장됩니다. 또한 반복적인 작업이 자동화되어 개발자는 핵심 비즈니스 로직 개발에 더 집중할 수 있습니다.

## [**일반적인 CI/CD 파이프라인 흐름**](https://leve68.tistory.com/entry/CICD-%EA%B8%B0%EB%B3%B8-%EA%B0%9C%EB%85%90#%EC%9D%BC%EB%B0%98%EC%A0%81%EC%9D%B8%20CI%2FCD%20%ED%8C%8C%EC%9D%B4%ED%94%84%EB%9D%BC%EC%9D%B8%20%ED%9D%90%EB%A6%84-1-3)

CI/CD 파이프라인은 일반적으로 다음과 같은 순서로 진행됩니다.

![](https://blog.kakaocdn.net/dna/GelnH/btsO789ddUx/AAAAAAAAAAAAAAAAAAAAAGsqEZrOCrdyPnAAauK0NtxQ08RyVrQWWs8vhAc0OJSL/img.png?credential=yqXZFxpELC7KVnFOS48ylbz2pIh7yKj8&expires=1753973999&allow_ip=&allow_referer=&signature=pTRkn%2FXqvZ8owii5JqbcU%2BMhl2w%3D)

### [**1단계: 코드 작성 및 커밋**](https://leve68.tistory.com/entry/CICD-%EA%B8%B0%EB%B3%B8-%EA%B0%9C%EB%85%90#1%EB%8B%A8%EA%B3%84%3A%20%EC%BD%94%EB%93%9C%20%EC%9E%91%EC%84%B1%20%EB%B0%8F%20%EC%BB%A4%EB%B0%8B-1-4)

개발자가 새로운 기능을 개발하거나 버그를 수정한 후 코드를 커밋합니다. 이때 커밋은 CI/CD 파이프라인의 시작점이 됩니다.

### [**2단계: 자동 빌드**](https://leve68.tistory.com/entry/CICD-%EA%B8%B0%EB%B3%B8-%EA%B0%9C%EB%85%90#2%EB%8B%A8%EA%B3%84%3A%20%EC%9E%90%EB%8F%99%20%EB%B9%8C%EB%93%9C-1-5)

커밋이 감지되면 자동으로 빌드 프로세스가 시작됩니다. 소스 코드가 컴파일되고 실행 가능한 형태로 변환됩니다.

### [**3단계: 자동 테스트**](https://leve68.tistory.com/entry/CICD-%EA%B8%B0%EB%B3%B8-%EA%B0%9C%EB%85%90#3%EB%8B%A8%EA%B3%84%3A%20%EC%9E%90%EB%8F%99%20%ED%85%8C%EC%8A%A4%ED%8A%B8-1-6)

빌드가 성공적으로 완료되면 미리 작성된 테스트 코드들이 자동으로 실행됩니다. 단위 테스트, 통합 테스트 등 다양한 수준의 테스트가 포함될 수 있습니다. 테스트 코드가 없는 프로젝트에서는 이 단계를 생략하기도 합니다.

### [**4단계: 자동 배포**](https://leve68.tistory.com/entry/CICD-%EA%B8%B0%EB%B3%B8-%EA%B0%9C%EB%85%90#4%EB%8B%A8%EA%B3%84%3A%20%EC%9E%90%EB%8F%99%20%EB%B0%B0%ED%8F%AC-1-7)

모든 테스트가 성공적으로 통과하면 최신 코드가 실제 서버 환경에 자동으로 배포됩니다. 이 과정에서 서버는 새로운 버전의 코드로 업데이트되고 재시작됩니다.

## [**CI/CD 도구의 선택지**](https://leve68.tistory.com/entry/CICD-%EA%B8%B0%EB%B3%B8-%EA%B0%9C%EB%85%90#CI%2FCD%20%EB%8F%84%EA%B5%AC%EC%9D%98%20%EC%84%A0%ED%83%9D%EC%A7%80-1-8)

CI/CD를 구현할 수 있는 다양한 도구들이 있으며, 각각 고유한 특징과 장단점을 가지고 있습니다.

### [**GitHub Actions**](https://leve68.tistory.com/entry/CICD-%EA%B8%B0%EB%B3%B8-%EA%B0%9C%EB%85%90#GitHub%20Actions-1-9)

GitHub이 제공하는 CI/CD 플랫폼으로, GitHub 저장소와 완벽하게 통합되어 있습니다. 가장 큰 장점은 별도의 서버 구축 없이 무료로 사용할 수 있다는 점입니다. GitHub에 코드를 호스팅하고 있다면 추가 설정 없이 바로 CI/CD를 구축할 수 있습니다.

### [**Jenkins**](https://leve68.tistory.com/entry/CICD-%EA%B8%B0%EB%B3%B8-%EA%B0%9C%EB%85%90#Jenkins-1-10)

오픈소스 자동화 서버로 매우 유연하고 확장 가능한 플랫폼입니다. 다양한 플러그인을 통해 거의 모든 개발 환경에 적용할 수 있습니다. 하지만 별도의 서버에 Jenkins를 설치하고 관리해야 한다는 단점이 있습니다.

### [**기타 도구들**](https://leve68.tistory.com/entry/CICD-%EA%B8%B0%EB%B3%B8-%EA%B0%9C%EB%85%90#%EA%B8%B0%ED%83%80%20%EB%8F%84%EA%B5%AC%EB%93%A4-1-11)

Circle CI, Travis CI 등도 널리 사용되는 CI/CD 도구들입니다. 각각 고유한 특징을 가지고 있으며, 프로젝트의 요구사항에 따라 적절한 도구를 선택할 수 있습니다.

## [**GitHub Actions를 통한 CI/CD 구현**](https://leve68.tistory.com/entry/CICD-%EA%B8%B0%EB%B3%B8-%EA%B0%9C%EB%85%90#GitHub%20Actions%EB%A5%BC%20%ED%86%B5%ED%95%9C%20CI%2FCD%20%EA%B5%AC%ED%98%84-1-12)

GitHub Actions는 현재 가장 접근하기 쉬운 CI/CD 도구 중 하나입니다. GitHub Actions를 일종의 클라우드 컴퓨터라고 생각하면 이해하기 쉽습니다.

### [**GitHub Actions 동작 흐름**](https://leve68.tistory.com/entry/CICD-%EA%B8%B0%EB%B3%B8-%EA%B0%9C%EB%85%90#GitHub%20Actions%20%EB%8F%99%EC%9E%91%20%ED%9D%90%EB%A6%84-1-13)

GitHub Actions를 활용한 CI/CD의 전체 흐름은 다음과 같습니다.

![](https://blog.kakaocdn.net/dna/zRiMB/btsO6w4iTxr/AAAAAAAAAAAAAAAAAAAAAJfNks-ZVvsWAeu2IRSTt17ykZX5vOaVuB18ro1qx3x-/img.png?credential=yqXZFxpELC7KVnFOS48ylbz2pIh7yKj8&expires=1753973999&allow_ip=&allow_referer=&signature=qW%2FDEFblXOy%2Bj30f46JYH9IZhCA%3D)

1. 개발자가 코드를 작성하고 로컬에서 커밋을 생성
2. 커밋을 GitHub 저장소에 푸시
3. 푸시 이벤트를 GitHub Actions가 감지하여 미리 정의된 워크플로우를 실행
4. 워크플로우에 정의된 빌드, 테스트, 배포 작업이 순차적으로 진행
5. 최종적으로 서버에 새로운 코드가 배포되고 서비스 재시작

## [**GitHub Actions 기본 문법과 구조**](https://leve68.tistory.com/entry/CICD-%EA%B8%B0%EB%B3%B8-%EA%B0%9C%EB%85%90#GitHub%20Actions%20%EA%B8%B0%EB%B3%B8%20%EB%AC%B8%EB%B2%95%EA%B3%BC%20%EA%B5%AC%EC%A1%B0-1-14)

GitHub Actions는 YAML 형식의 설정 파일을 통해 워크플로우를 정의합니다. 이 파일은 프로젝트 루트의 .github/workflows/ 디렉토리에 위치해야 합니다.

### [**프로젝트 설정하기**](https://leve68.tistory.com/entry/CICD-%EA%B8%B0%EB%B3%B8-%EA%B0%9C%EB%85%90#%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8%20%EC%84%A4%EC%A0%95%ED%95%98%EA%B8%B0-1-15)

먼저 새로운 프로젝트를 생성하고 다음 구조로 디렉토리를 만듭니다.

```bash
프로젝트명/
├── .github/
│   └── workflows/
│       └── deploy.yml
└── (기타 프로젝트 파일들)
```

### [**워크플로우 파일 작성**](https://leve68.tistory.com/entry/CICD-%EA%B8%B0%EB%B3%B8-%EA%B0%9C%EB%85%90#%EC%9B%8C%ED%81%AC%ED%94%8C%EB%A1%9C%EC%9A%B0%20%ED%8C%8C%EC%9D%BC%20%EC%9E%91%EC%84%B1-1-16)

deploy.yml 파일에는 다음과 같은 구조로 워크플로우를 정의합니다.

```yaml
# 워크플로우의 이름을 지정합니다name: Github Actions 실행시켜보기

# 언제 이 워크플로우가 실행될지 이벤트를 정의합니다on:
  push:
    branches:
      - main

# 실행할 작업들을 정의합니다jobs:
  My-Deploy-Job:
# 어떤 운영체제에서 실행할지 지정합니다runs-on: ubuntu-latest

# 순차적으로 실행될 단계들을 정의합니다steps:
      - name: Hello World 출력하기
        run: echo "Hello World"

      - name: 여러 명령어 실행하기
        run: |
          echo "Good"
          echo "Morning"

      - name: GitHub Actions 기본 변수 사용하기
        run: |
          echo $GITHUB_SHA
          echo $GITHUB_REPOSITORY

      - name: Secret 변수 사용하기
        run: |
          echo ${{ secrets.MY_NAME }}
          echo ${{ secrets.MY_HOBBY }}

```

### [**핵심 구성 요소 이해하기**](https://leve68.tistory.com/entry/CICD-%EA%B8%B0%EB%B3%B8-%EA%B0%9C%EB%85%90#%ED%95%B5%EC%8B%AC%20%EA%B5%AC%EC%84%B1%20%EC%9A%94%EC%86%8C%20%EC%9D%B4%ED%95%B4%ED%95%98%EA%B8%B0-1-17)

- **Workflow**는 하나의 YAML 파일에 정의된 전체 자동화 과정을 의미합니다. 하나의 저장소에 여러 개의 워크플로우를 만들 수 있습니다.
- **Event**는 워크플로우가 언제 실행될지를 결정하는 트리거입니다. 푸시, 풀 리퀘스트, 스케줄 등 다양한 이벤트를 설정할 수 있습니다.
- **Job**은 워크플로우의 구성 단위로, 하나의 워크플로우는 여러 개의 Job으로 구성될 수 있습니다. 기본적으로 Job들은 병렬로 실행됩니다.
- **Step**은 Job 내에서 순차적으로 실행되는 개별 작업 단위입니다. 각 Step은 명령어를 실행하거나 미리 만들어진 Action을 사용할 수 있습니다.

### [**저장소에 코드 업로드하기**](https://leve68.tistory.com/entry/CICD-%EA%B8%B0%EB%B3%B8-%EA%B0%9C%EB%85%90#%EC%A0%80%EC%9E%A5%EC%86%8C%EC%97%90%20%EC%BD%94%EB%93%9C%20%EC%97%85%EB%A1%9C%EB%93%9C%ED%95%98%EA%B8%B0-1-18)

워크플로우 파일을 작성한 후 GitHub 저장소에 코드를 업로드하면 자동으로 CI/CD가 실행됩니다.

```bash
git init
git add .
git commit -m "GitHub Actions 워크플로우 추가"
git branch -M main
git remote add origin {저장소 주소}
git push -u origin main
```

푸시가 완료되면 GitHub 저장소의 Actions 탭에서 워크플로우 실행 결과를 확인할 수 있습니다.

## [**환경 변수와 시크릿 관리**](https://leve68.tistory.com/entry/CICD-%EA%B8%B0%EB%B3%B8-%EA%B0%9C%EB%85%90#%ED%99%98%EA%B2%BD%20%EB%B3%80%EC%88%98%EC%99%80%20%EC%8B%9C%ED%81%AC%EB%A6%BF%20%EA%B4%80%EB%A6%AC-1-19)

실제 프로젝트에서는 API 키, 데이터베이스 접속 정보 등 민감한 정보를 안전하게 관리해야 합니다. GitHub Actions는 이를 위해 다양한 변수 시스템을 제공합니다.

### [**기본 환경 변수**](https://leve68.tistory.com/entry/CICD-%EA%B8%B0%EB%B3%B8-%EA%B0%9C%EB%85%90#%EA%B8%B0%EB%B3%B8%20%ED%99%98%EA%B2%BD%20%EB%B3%80%EC%88%98-1-20)

GitHub Actions는 워크플로우 실행 중 사용할 수 있는 다양한 기본 환경 변수를 제공합니다. $GITHUB_SHA는 현재 커밋의 해시값을, $GITHUB_REPOSITORY는 저장소 이름을 담고 있습니다.

### [**시크릿 변수**](https://leve68.tistory.com/entry/CICD-%EA%B8%B0%EB%B3%B8-%EA%B0%9C%EB%85%90#%EC%8B%9C%ED%81%AC%EB%A6%BF%20%EB%B3%80%EC%88%98-1-21)

민감한 정보는 저장소 설정의 Secrets 섹션에서 안전하게 관리할 수 있습니다. 워크플로우에서는 ${{ secrets.변수명 }} 형태로 사용할 수 있으며, 실행 로그에서는 값이 마스킹되어 보안이 유지됩니다.

## [**마무리**](https://leve68.tistory.com/entry/CICD-%EA%B8%B0%EB%B3%B8-%EA%B0%9C%EB%85%90#%EB%A7%88%EB%AC%B4%EB%A6%AC-1-22)

앞으로 CI/CD에 대해 학습하면서, 관련 내용들을 차례대로 정리해 나갈 예정입니다. 이번 글을 포함해 CI/CD 카테고리의 포스트들은 다음 강의를 수강하며 학습한 내용을 정리한 것입니다.
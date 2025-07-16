- # Docker Volume
    
    ### ✅ 컨테이너가 가진 문제점
    
    Docker를 활용하면 특정 프로그램을 컨테이너로 띄울 수 있다. 프로그램에 기능이 추가되면 새로운 이미지를 만들어서 컨테이너를 실행시켜야 한다. 이 때, Docker는 기존 컨테이너에서 변경된 부분을 수정하지 않고, 새로운 컨테이너를 만들어서 통째로 갈아끼우는 방식으로 교체를 한다. 이게 효율적이라고 생각했던 것이다.
    
    이런 특징 때문에 기존 컨테이너를 새로운 컨테이너로 교체하면, 기존 컨테이너 내부에 있던 데이터도 같이 삭제된다. 만약 이 컨테이너가 MySQL을 실행시키는 컨테이너 였다면 MySQL에 저장된 데이터도 같이 삭제 된다.
    
    따라서 컨테이너 내부에 저장된 데이턱가 삭제되면 안되는 경우에는 **볼륨(Volume)** 이라는 개념을 활용해야 한다.
    
    ### ✅ Docker Volume(도커 볼륨)이란?
    
    도커의 볼륨이란 도커 컨테이너에서 데이터를 영속적으로 저장하기 위한 방법이다. 볼륨은 컨테이너 자체의 저장 공간을 사용하지 않고, 호스트 자체의 저장 공간을 공유해서 사용하는 형태이다.
    
    ### ✅ 볼륨을 사용하는 명령어
    
    ```bash
    $ docker run -v [호스트의 디렉토리 절대경로]:[컨테이너의 디렉토리 절대경로] [이미지명]:[태그명]
    ```
    
    - [호스트의 디렉토리 절대 경로]에 디렉토리가 이미 존재할 경우, 호스트의 디렉토리가 컨테이너의 디렉토리를 덮어 씌운다.

    
    - [호스트의 디렉토리 절대 경로]에 디렉토리가 존재하지 않을 경우, 호스트의 디렉토리 절대 경로에 디렉토리를 새로 만들고 컨테이너의 디렉토리에 있는 파일들을 호스트의 디렉토리로 복사해온다.

- # [실습] Docker로 MySQL 실행시켜보기 -1
    
    ### ✅ Docker로 MySQL 실행시켜보기
    
    1. MySQL 이미지를 바탕으로 컨테이너 실행시키기
    
    ```bash
    $ docker run -e MYSQL-ROOT_PASSWORD=password123 -p 3306:3306 -d mysql
    ```
    
    - -e MYSQL_ROOT-PASSWORD=password123 : -e 옵션은 컨테이너의 환경 변수를 설정하는 옵션이다.
    - Dockerhub의 MySQL 공식 문서를 보면 환경 변수로 MYSQL_ROOT_PASSWORD를 정해주어야만 정상적으로 컨테이너가 실행된다고 적혀있다.
    - 아래의 명령어로 컨테이너로 들어가서 환경 변수 확인 가능
    
    ```bash
    $ docker exec -it [MySQL 컨테이너 ID] bash
    
    $ echo $MYSQL_ROOT_PASSWORD # MYSQL_ROOT_PASWORD라는 환경 변수 값 출력
    $ export # 설정되어 있는 모든 환경 변수 출력
    ```
    
    2. 컨테니어가 잘 실행되고 있는 지 체크
    
    ```bash
    $ docker ps
    ```
    
    3. 컨테이너 실행시킬 때 에러 없이 잘 실행됐는 지 로그 체크
    
    ```bash
    $ docker logs [컨테이너 ID 또는 컨테이너 명]
    ```
    
    4. DBeaver에도 연결시켜보기

- # 에러 상황
- ### 포트 충돌
    
    ```bash
    $ docker run -p 3306:3306 -d mysql
    ```
    
    - 입력 후 해당 에러 발견
    
    docker: Error response from daemon: ports are not available: exposing port TCP 0.0.0.0:3306 -> 127.0.0.1:0: listen tcp 0.0.0.0:3306: bind: Only one usage of each socket address (protocol/network address/port) is normally permitted.
    
    수업에서는 mtsql이 실행중이라 sudo 로 해결했는데 난 잘 안됨
    
    ```bash
    $ netstat -ano | findstr :3306
    ```
    
    - 해당 코드 입력 시
    
    3306에 해당하는 PID 번호가 맨 끝에 나온다.
    
    ```bash
    $ taskkill /F /PID [해당PID 번호]
    ```
    
    - 해당 코드 입력 시 종료 가능

- ### DBeaver 테스트 커넥션 에러
Docker에서 Mysql을 실행하고 3306으로 포트도 맞춘 상태에서 DBeaver에서 연결 테스트를 했을 때 

**Public Key Retrieval is not allowed**

라는 문구가 나오는 상황

MySQL 8.0 이상 버전을 도커에 설치하고 DBeaver로 접속 시도할 때, **`Public Key Retrieval is not allowed`** 오류가 자주 발생합니다. 이는 MySQL의 보안 강화 조치로 인해 공개 키를 클라이언트가 서버에서 받아오지 못할 때 발생하는 오류입니다.

## 원인

- MySQL 8.0 이상 버전부터는 인증 시 공개 키를 안전하게 서버에서 받아오기 위한 설정이 필요합니다.
- 기본적으로 **`allowPublicKeyRetrieval`** 옵션이 **`false`**로 설정되어 있어, 클라이언트가 공개 키를 요청하면 거부됩니다.
- 보안 상 SSL 연결 설정(**`useSSL`**)과 **`allowPublicKeyRetrieval`** 옵션이 적절히 설정되어 있지 않으면 오류가 발생합니다.

## 해결 방법

## 1. DBeaver에서 연결 설정 변경

1. DBeaver에서 해당 MySQL 연결을 우클릭 → **Edit Connection** 클릭
2. **Driver Properties** 탭으로 이동
3. 다음 두 개의 속성을 추가하거나 수정
    - **`allowPublicKeyRetrieval`** : **`true`**
    - **`useSSL`** : **`false`** (또는 필요에 따라 **`true`**로 설정)
4. 설정 저장 후 다시 연결 시도
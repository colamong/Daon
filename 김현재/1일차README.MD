- # 이미지(Image) 다운로드
    
    ### 이미지 다운로드
    
    ```bash
    # docker pull 이미지명
    $ docker pull nginx # docker pull nginx:latest 와 동일하게 작동
    ```
    
    이미지를 다운로드 할 때 Dockerhub 라는 곳에서 이미지를 다운 받는다.
    
    Github과 같다고 생각하면됨
    
    **Dockerhub는 Github 처럼 이미지를 저장 및 다운받을 수 있는 저장소 역할을 한다.**
    
    ### 특정 버전 이미지 다운로드
    
    ```bash
    # docker pull 이미지명:태그명
    $ docker pull nginx:stable-perl
    ```
    
- 이미지(Image) 조회 / 삭제
    
    ### 다운받은 모든 이미지 조회
    
    ```bash
    $ docker image ls
    ```
    
    - ls : list 의 약자
    - REPOSITORY : 이미지 이름(이미지명)
    - TAG : 이미지 태그명
    - IMAGE ID : 이미지 ID
    - CREATED : 이미지가 생성된 날짜 (다운받은 날짜 X)
    - SIZE : 이미지 크기(용량)
    
    ### 이미지 삭제
    
    [특정 이미지 삭제]
    
    ```bash
    $ docker image rm [이미지 ID 또는 이미지명]
    ```
    
    - rm : remove의 약자
    - 이미지 ID를 입력할 때 전체 ID를 다 입력하지 않고 ID 의 일부만 입력 가능(단, ID의 일부만 입력했을 때, 입력한 ID의 일부를 가진 이미지가 단 1개여햐 한다.)
    - 컨테이너에서 사용하고 있지 않은 이미지만 삭제가 가능하다.
    
    [중지된 컨테이너에서 사용하고 있는 이미지 강제 삭제하기]
    
    ```bash
    $ docker image rm -f [이미지 ID 또는 이미지명]
    ```
    
    - 실행 중인 컨테이너에서 사용하고 있는 이미지는 강제로 삭제할 수 없다.
    
    [전체 이미지 삭제]
    
    ```bash
    # 컨테이너에서 사용하고 있지 않은 이미지만 전체 삭제
    $ docker image rm $(docker images -q)
    
    # 컨테이너에서 사용하고 있는 이미지를 포함해서 전체 이미지 삭제
    $ docker image rm -f $(docker images -q)
    ```
    
    - docker images -q : 시스템에 있는 모든 이미지의 ID를 반환한다. -q 옵션은 quite를 의미하며 상제 정보 대신에 각 이미지의 고유한 ID만 표시하도록 지시한다.
    <br>
    <br>
    
    # 컨테이너(Container) 생성 / 실행
    
    ### 컨테이너 생성
    
    이미지를 바탕으로 컨테이너를 생성한다. 이 때, 컨테이너를 실행시키지는 않는다.
    
    (컨테이너를 실행하지 않고 생성만 하는 경우가 잘 없어서, 이 명령어는 잘 사용하지 않음)
    
    ```bash
    # docker create 이미지명[:태그명]
    $ docker create nginx
    
    $ docker ps -a # 모든 컨테이너 조회
    ```
    
    - 로컬 환경에 다운받은 이미지가 없다면 Dockerhub로부터 이미지를 다운 (docker pull) 받아서 컨테이너를 생성한다.
    
    ### 컨테이너 실행
    
    정지되어 있는 컨테이너를 실행시킨다.
    
    ```bash
    # docker start 컨테이너명[또는 컨테이너 ID]
    $ docker start 컨테이너명[또는 컨테이너 ID]
    
    $ docker ps # 실행중인 컨테이너 조회
    
    # Nginx 컨테이너 중단 후 삭제하기
    $ docker ps # 실행 중인 컨테이너 조회
    $ docker stop {nignx를 실행시킨 컨테이너 ID} # 컨테이너 중단
    $ docker rm {nginx를 실행시킨 컨테이너 ID} # 컨테이너 삭제
    $ docker image rm nginx # Nginx 이미지 삭제
    ```    
    <br>

    ### 컨테이너 생성 + 실행
    
    이미지를 바탕으로 컨테이너를 생성한 뒤, 컨테이너를 실행까지 시킨다.
    
    (처음에 이미지를 바탕으로 컨테이너를 실행시키고 싶을 때, 이 명령어를 자주 사용한다.)
    
    ```bash
    # docker run 이미지명[:태그명]
    $ docker run nginx # 포그라운드에서 실행 (추가적인 명령어 조작을 할 수 없음)
    
    # ctrl + c 로 종료 가능
    ```
    
    - 로컬 환경에 다운받은 이미지가 없다면 Dockerhub로부터 이미지를 다운 받아서 실행 시킴
    - Dockerhub로부터 새롭게 갱신된 이미지를 다운 받고 싶다면 **docker pull** 명령어 활용

    <br>

    [컨테이너를 백그라운드에서 실행 시키기]
    
    - 포 그라운드(foreground)
        - 내가 실행시킨 프로그램의 내용이 화면에서 실행되고 출력되는 상태를 뜻함. 그러니 다른 프로그램을 조작할 수 없다.
    - 백 그라운드(background)
        - 내가 실행시킨 프로그램이 컴퓨터 내부적으로 실행되는 상태를 의미. 그래서 프로그램이 어떻게 실행되고 있는 지에 대한 정보를 화면에서 확인 불가. 이런 특성 때문에 다른 명령어를 추가로 입력할 수 있고, 새로운 프로그램을 조작 가능
    
    ```bash
    # docker run -d 이미지명[:태그명] (백그라운드 실행)
    $ docker run -d nginx
    
    # Nginx 컨테이너 중단 후 삭제
    $ docker ps # 실행 중인 컨테이너 조회
    $ docker stop {nginx를 실행시킨 컨테이너 ID} # 컨테이너 중단
    $ docker rm {nginx를 실행시킨 컨테이너 ID} # 컨테이너 삭제
    $ docker image rm niginx # Nginx 이미지 삭제
    ```
    <br>
    [컨테이너에 이름 붙여서 생성 및 실행하기]
    
    ```bash
    # docker run -d --name [컨테이너 이름] 이미지명[:태그명]
    $ docker run -d --name my-web-server nginx
    
    # Nginx 컨테이너 중단 후 삭제하기
    $ docker ps # 실행 중인 컨테이너 조회
    $ docker stop {nginx를 실행시킨 컨테이너 ID} # 컨테이너 중단
    $ docker rm {nginx를 실행시킨 컨테이너 ID} # 컨테이너 삭제
    $ docker image rm nginx # Nginx 이미지 삭제
    ```
    <br>
    [호스트의 포트와 컨테이너의 포트를 연결하기]
    
    ```bash
    # docker run -d -p [호스트 포트]:[컨테이너 포트] 이미지명[:태그명]
    $ docker run -d -p 4000:80 nignx
    ```
    
    - **docker run -p 4000:80** 라고 명령어를 입력하게 되면, 도커를 실행하는 호스트의 4000번 포트를 컨테이너의 80번 포트로 연결하도록 설정한다.
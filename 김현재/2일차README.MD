- # 컨테이너(Container) 조회 / 중지 / 삭제
    
    ### 컨테이너 조회
    
    [실행 중인 컨테이너들만 조회]
    
    ```bash
    $ docker ps
    ```
    
    - ps : **process status** 의 약자
    
    [모든 컨테이너 조회 (작동 중인 컨테이너 + 작동을 멈춘 컨테이너)]
    
    ```bash
    $ docker ps -a
    ```
    
    - -a : all의 약자
    
    ### 컨테이너 중지
    
    ```bash
    $ docker stop 컨테이너명[또는 컨테이너 ID]
    $ docker kill 컨테이너명[또는 컨테이너 ID]
    ```
    
    - 집에 있는 컴퓨터로 비유하자면  stop은 시스템 종료 버튼을 통해 정상 종료 하는 것. kill은 본체 버튼을 눌러 강제 종료 하는 것
    
    ### 컨테이너 삭제
    
    [중지되어 있는 특정 컨테이너 삭제]
    
    ```bash
    $ docker rm 컨테이너명[또는 컨테이너 ID]
    ```
    
    - 실행 중인 컨테이너는 중지한 후에만 삭제가 가능하다.
    
    [실행되고 있는 특정 컨테이너 삭제]
    
    ```bash
    $ docker rm -f 컨테이너명[또는 컨테이너 ID]
    ```
    
    [중지되어 있는 모든 컨테이너 삭제]
    
    ```bash
    $ docker rm $(docker ps -qa)
    ```
    
    [실행되고 있는 모든 컨테이너 삭제]
    
    ```bash
    $ docker rm -f $(docker ps -qa)
    ```
    
- # 컨테이너(Container) 로그 조회
    
    ### 컨테이너(Container) 로그 조회
    
    [특정 컨테이너의 모든 로그 조회]
    
    ```bash
    # docker logs [컨테이너 ID 또는 컨테이너명]
    
    $ docker run -d nginx
    $ docker logs [nginx가 실행되고 있는 컨테이너 ID]
    ```
    
    [최근 로그 10줄만 조회]
    
    ```bash
    # docker logs --tail [로그 끝부터 표시할 줄 수] [컨테이너 ID 또는 컨테이너명]
    $ docker logs --tail 10 [컨테이너 ID 또는 컨테이너명]
    ```
    
    [기존 로그 조회 + 생성되는 로그를 실시간으로 보고 싶은 경우]
    
    ```bash
    # docker log -f [컨테이너 ID 또는 컨테이너명]
    
    # Nginx의 컨테아너에 실시간으로 쌓이는 로그 확인하기
    $ docker run -d -p 80:80 nginx
    $ docker logs -f
    ```
    
    - -f : follow의 약어
    
    [기존 로그는 조회하지 않기 + 생성되는 로그를 실시간으로 보고 싶은 경우]
    
    ```bash
    $ docker logs --tail 0 -f [컨테이너 ID 또는 컨테이너명]
    ```
    
- # 실행중인 컨테이너 내부에 접속하기(exec -it)
    
    ### 실행 중인 컨테이너 내부에 접속하기
    
    ```bash
    # docker exec -it 컨테이너명[또는 컨테이너 ID] bash
    
    $ docker run -d nginx # 백그라운드 nginx 실행
    $ docker exec -t [Nginx가 실행되고 있는 컨테이너 ID] bash # bash 환경으로 컨테이너 들어가기
    $ ls # 컨테이너 내부 파일 조회
    $ cd /etc/nginx
    $ cat nginx.conf
    ```
    
    - 컨테이너 내부에서 나오려면 Crtl + D 또는 exit 을 입력하면 된다.
    - bash : 쉘(Shell)의 일종
    - -it : -it 옵션을 사용해야 명령어를 입력하고 결과를 확인할 수 있다. -it 옵션을 적지 않으면 명령어를 1번만 실행시키고 종료되어 버린다. 즉 -it 옵션을 적어야 계속해서 명령어를 입력할 수 있다.
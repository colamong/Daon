- ### [실습] Docker로 MySQL 실행시켜보기 -2
    
    ### ✅ MySQL 컨테이너에 직접 접속해보기
    
    1. MySQL 컨테이너에 접속
    
    ```bash
    $ docker exec -it [MySQL 컨테이너 ID] bash
    ```
    
    1. 컨테이너에서 MySQL 접근하기
    
    ```bash
    $ mysql -u root -p
    ```
    
    1. MySQL 접근에 성공했다면 데이터베이스 조회해보기
    
    ```bash
    mysql> show databases;
    ```
    
    
    1. 데이터베이스
    
    ```bash
    mysql > create database mydb;
    mtsql > show databases;
    ```
    
    1. 컨테이너 종료 후 다시 생성해보기
    
    ```bash
    # 컨테이너 종료
    $ docker stop [MySQL 컨테이너 ID]
    $ docker rm [MySQL 컨테이너 ID]
    
    # 컨테이너 todtjd
    $ docker run -e MYSQL_ROOT_PASSWORD=password123 -p 3306:3306 -d mysql
    $ docker exec -it [MySQL 컨테이너 ID] bash
    
    $ mysql -u root -p
    mysql > show databases; # 아까 생성한 데이터베이스가 없어진 걸 확인할 수 있다.
    ```
  
- ### [실습] Docker로 MySQL 실행시켜보기 -3
    
    ### ✅ 볼륨(Volume)을 활용해 MySQL 컨테이너 띄우기
    
    1. MySQL 컨테이너 띄우기
    
    ```bash
    $ cd /mnt/c/KHJP
    $ mkdir docker-mysql # MySQL 데이터를 저장하고 싶은 폴더 만들기
    
    # docker run -e MYSQL_ROOT_PASSWORD=password123 -p 3306:3306 **-v {호스트의 절대경로}/mysql_data:/var/lib/mysql** -d mysql
    $ docker run -e MYSQL_ROOT_PASSWORD=password123 -p 3306:3306 -v /mnt/c/KHJP/docker-mysql/mysql_data:/var/lib/mysql -d mysql
    ```
    
    - pwd 명령어로 볼륨으로 사용하고자 하는 경로를 확인한 뒤 입력해주자
        - 윈도우의 경우 아래와 같이 경로가 작성될 수 있다.
        
        ```bash
        $ docker run -e MYSQL_ROOT_PASSWORD=password123 -p 3306:3306 -v **C:/KHJP/docker-mysql/mysql_data**:/var/lib/mysql -d mysql
        ```
        
    - **주의)** `mysql_data` 디렉토리를 미리 만들어 놓으면 안 된다. 그래야 처음 이미지를 실행시킬 때 mysql 내부에 있는 `/var/lib/mysql` 파일들을 호스트 컴퓨터로 공유받을 수 있다. `mysql_data` 디렉토리를 미리 만들어놓을 경우, 기존 컨테이너의 `/var/lib/mysql` 파일들을 전부 삭제한 뒤에 `mysql_data`로 덮어씌워 버린다.
    - DB에 관련된 데이터가 저장되는 곳이 `/var/lib/mysql`인지는 Dockerhub MySQL의 공식 문서에 나와있다.
        
        
    1. MySQL 컨테이너에 접속해서 데이터베이스 만들기
    
    ```bash
    $ docker exec -it [MySQL 컨테이너 ID] bash
    
    $ mysql -u root -p
    
    mysql > show databases;
    mysql > create database mydb;
    mysql > show databases;
    ```
    
    1. 컨테이너 종료 후 다시 생성해보기
    
    ```bash
    # 컨테이너 종료
    $ docker stop [MySQL 컨테이너 ID]
    $ docker rm [MySQL 컨테이너 ID]
    
    # 컨테이너 생성
    $ docker run -e MYSQL_ROOT_PASSWORD=password123 -p 3306:3306 -v **C:/KHJP/docker-mysql/mysql_data**:/var/lib/mysql -d mysql
    
    $ docker exec -it [MySQL 컨테이너 ID] bash
    $ mysql -u root -p
    mysql > show databases;
    ```
    
    ### ✅ MySQL 컨테이너 삭제하고 다시 띄워보기
    
    ```bash
    # 컨테이너 종료
    $ docker stop [MySQL 컨테이너 ID]
    $ docker rm [MySQL 컨테이너 ID]
    
    # 비밀번호 바꿔서 컨테이너 생성
    $ docker run -e MYSQL_ROOT_PASSWORD=pwd1234 -p 3306:3306 -v **C:/KHJP/docker-mysql/mysql_data**:/var/lib/mysql -d mysql
    
    $ docker exec -it [MySQL 컨테이너 ID] bash
    $ mtsql -u root -p # 접속 불가
    ```
    
    ### 안되는 이유
    
    - Volume으로 설정해둔 폴더에 이미 비밀번호 정보가 저장되버렸기 때문이다.

### 종합 실습 후기
- 맥 기준으로 실습 코드를 작성했더니 경로를 맥 방식으로 써서 한참을 해맸습니다.
- 리눅스에 저장공간을 만들고 실습을 진행했고 내일 다시 경로를 윈도우 형태에 맞춰서 해보려고 합니다.
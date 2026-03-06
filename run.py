import subprocess
import os
import time
import webbrowser
import re

def kill_process_on_port(port):
    """지정한 포트를 사용 중인 프로세스를 찾아 종료합니다."""
    print(f"[체크] {port} 포트 사용 여부를 확인 중...")
    try:
        # netstat 출력에서 포트번호와 LISTENING 상태인 라인만 정확히 필터링
        # 정규표현식 \b를 사용하여 80801 같은 포트에 오탐되지 않도록 함
        output = subprocess.check_output(f"netstat -ano | findstr /R \":{port}\\>.*LISTENING\"", shell=True, text=True)
        lines = output.strip().split('\n')
        
        pids = set()
        for line in lines:
            # 라인의 마지막 부분이 PID입니다.
            parts = line.strip().split()
            if parts:
                pids.add(parts[-1])
        
        if pids:
            print(f"[재시작] 기존 서버 프로세스를 발견했습니다. (PID: {', '.join(pids)})")
            for pid in pids:
                print(f"       >> PID {pid} 종료 시도...")
                subprocess.run(f"taskkill /F /PID {pid}", shell=True, capture_output=True)
            
            # 포트가 완전히 반환될 때까지 잠시 대기
            print("[대기] 포트 리소스가 해제될 때까지 잠시 기다립니다...")
            time.sleep(2)
            print("[완료] 기존 프로세스가 성공적으로 종료되었습니다.")
        else:
            print("[확인] 사용 중인 포트가 없습니다. 깨끗한 상태입니다.")
            
    except subprocess.CalledProcessError:
        # findstr 결과가 없으면 에러가 발생하지만, 이는 포트가 사용 중이지 않다는 의미입니다.
        print("[확인] 사용 중인 포트가 없습니다. 깨끗한 상태입니다.")
        pass
    except Exception as e:
        print(f"[경고] 프로세스 확인 중 예외 발생: {e}")

def main():
    # 현재 스크립트 파일의 경로를 기준으로 디렉터리 설정
    root_dir = os.path.dirname(os.path.abspath(__file__))
    backend_dir = os.path.join(root_dir, "backend")

    print("--- Nextstay 프로젝트 스타터 ---")

    # 0. 중복 실행 방지: 8080 포트 체크
    kill_process_on_port(8080)

    # 1. 도커 MySQL 컨테이너 확인 및 실행
    print("\n[1/2] 도커 MySQL 상태를 확인하고 실행합니다...")
    # -d 옵션으로 백그라운드에서 실행
    subprocess.run("docker-compose up -d", shell=True, cwd=root_dir)
    time.sleep(1) # 잠시 대기

    # 2. 새로운 CMD 창에서 스프링 부트 백엔드 실행
    print("\n[2/2] 새로운 창에서 스프링 부트 백엔드를 시작합니다...")
    print("팁: 서버가 뜨면 http://localhost:8080/swagger-ui/index.html 에서 API 문서를 확인하세요.")
    
    # Windows 'start' 명령어를 사용하여 새 창 띄우기
    # /K: 명령 실행 후 창을 닫지 않음 (에러 확인 용이)
    # cd /d: 드라이브 문자가 달라도 해당 디렉터리로 강제 이동
    # CMD 환경에 맞춰 ./gradlew -> gradlew 로 수정
    cmd = f'start cmd /K "cd /d {backend_dir} && title 넥스트스테이-백엔드 && gradlew bootRun"'
    
    subprocess.run(cmd, shell=True)
    
    # 3. 서버가 기동될 시간을 잠시 기다린 후 브라우저 자동 오픈
    print("\n[대기] 서버가 기동될 때까지 잠시 기다립니다 (5초)...")
    time.sleep(5)
    
    swagger_url = "http://localhost:8080/swagger-ui/index.html"
    print(f"브라우저를 엽니다: {swagger_url}")
    webbrowser.open(swagger_url)
    
    print("\n완료! 서버는 새 창에서 실행 중이며 브라우저가 열렸습니다.")

if __name__ == "__main__":
    main()

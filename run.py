import subprocess
import os
import time
import webbrowser
import re

def kill_process_on_port(port):
    """지정한 포트를 사용 중인 프로세스를 찾아 종료합니다."""
    try:
        # netstat을 사용하여 해당 포트를 사용하는 PID 찾기
        output = subprocess.check_output(f"netstat -ano | findstr :{port}", shell=True, text=True)
        lines = output.strip().split('\n')
        
        pids = set()
        for line in lines:
            if "LISTENING" in line:
                # 라인의 마지막 부분이 PID입니다.
                parts = line.split()
                if parts:
                    pids.add(parts[-1])
        
        if pids:
            print(f"\n[알림] {port} 포트를 사용 중인 기존 프로세스를 발견했습니다. (PID: {', '.join(pids)})")
            for pid in pids:
                print(f"종료 중... (PID: {pid})")
                subprocess.run(f"taskkill /F /PID {pid}", shell=True, capture_output=True)
            time.sleep(1) # 종료될 시간을 잠시 대기
            print("기존 프로세스가 정리되었습니다.")
            
    except subprocess.CalledProcessError:
        # 포트가 사용 중이지 않으면 findstr이 에러를 뱉지만, 이는 정상 상황입니다.
        pass
    except Exception as e:
        print(f"프로세스 확인 중 오류 발생: {e}")

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

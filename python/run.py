import subprocess
import os
import sys
import time
import webbrowser
import re
import importlib
import urllib.request
from urllib.error import URLError, HTTPError

script_dir = os.path.dirname(os.path.abspath(__file__))
if script_dir not in sys.path:
    sys.path.append(script_dir)

import run_front_guest # 초기 임포트

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
    # 현재 스크립트 파일(python/run.py)의 부모 경로(Nextstay 프로젝트 루트)를 기준으로 설정
    script_dir = os.path.dirname(os.path.abspath(__file__))
    root_dir = os.path.dirname(script_dir) # 한 칸 위로 올라감 (c:\portpolio\Nextstay)
    backend_dir = os.path.join(root_dir, "backend")

    while True:
        print("\n" + "="*40)
        print("--- Nextstay 프로젝트 스타터 ---")
        print("="*40)

        # 0. 중복 실행 방지: 8080 포트 체크 및 CMD 창 닫기
        print("[체크] 기존 백엔드 창이 열려있는지 확인합니다...")
        subprocess.run('taskkill /FI "WINDOWTITLE eq 넥스트스테이-백엔드*" /T /F', shell=True, capture_output=True)
        kill_process_on_port(8080)

        # 1. 도커 MySQL 컨테이너 확인 및 실행
        print("\n[1/3] 도커 MySQL 상태를 확인하고 실행합니다...")
        subprocess.run("docker-compose up -d", shell=True, cwd=root_dir)
        time.sleep(1)

        # 2. 백엔드 실행
        print("\n[2/3] 백엔드 서버를 시작합니다...")
        subprocess.Popen('start "넥스트스테이-백엔드" cmd /k "gradlew bootRun"', shell=True, cwd=backend_dir)
        
        # 3. 서버 기동 대기
        print("\n[대기] 백엔드 서버 컴파일 및 톰캣 기동을 기다립니다 (최대 60초)...")
        
        max_retries = 30
        backend_ready = False
        for i in range(max_retries):
            try:
                # 8080 포트와 Swagger 엔드포인트가 살아있는지 체크
                with urllib.request.urlopen("http://localhost:8080/swagger-ui/index.html", timeout=2) as response:
                    if response.status == 200:
                        backend_ready = True
                        break
            except (URLError, HTTPError, ConnectionResetError, ConnectionRefusedError):
                time.sleep(2)
                
        if backend_ready:
            print("[성공] 백엔드 서버가 완전히 기동되었습니다!")
            swagger_url = "http://localhost:8080/swagger-ui/index.html"
            print(f"백엔드 API 문서 창을 새 탭으로 엽니다: {swagger_url}")
            # new=2: 새로운 탭으로 열기 시도
            webbrowser.open(swagger_url, new=2)
            
            # 4. 프론트엔드 서버 실행
            print("\n[3/3] 이어서 프론트엔드(Guest) 서버를 구동합니다...")
            # 모듈이 이미 로드되어 있어도 최신 상태를 반영하기 위해 reload
            importlib.reload(run_front_guest)
            run_front_guest.main()
        else:
            print("[실패] 백엔드 서버 응답이 없습니다. 컴파일 에러나 포트 충돌 여부를 별도의 백엔드 터미널 창에서 확인해주세요.")

        print("\n" + "-"*40)
        # strip()을 추가하여 " y" 띄어쓰기 실수 등도 방지하고, lower()로 대소문자 모두 'y'로 치환
        try:
            choice = input("다시 시작하시겠습니까? (Y/N): ").strip().lower()
        except EOFError:
            break
            
        if choice != 'y':
            print("프로그램을 종료합니다.")
            break

if __name__ == "__main__":
    try:
        main()
    except KeyboardInterrupt:
        print("\n\n" + "="*40)
        print("사용자에 의해 프로그램이 중단되었습니다.")
        print("백엔드 및 프론트엔드 창은 별도로 열려있을 수 있습니다.")
        print("="*40)
        sys.exit(0)

import subprocess
import os
import time
import urllib.request
from urllib.error import URLError, HTTPError

def main():
    script_dir = os.path.dirname(os.path.abspath(__file__))
    root_dir = os.path.dirname(script_dir)
    analytics_dir = os.path.join(root_dir, "backend-analytics")

    print("[체크] 기존 Analytics 서버 창이 있는지 확인합니다...")
    # 중복 실행 방지를 위해 해당하는 타이틀을 가진 cmd 창을 종료합니다.
    subprocess.run('taskkill /FI "WINDOWTITLE eq 넥스트스테이-Analytics*" /T /F', shell=True, capture_output=True)

    print("\n[실행] Analytics 서버(Elysia)를 시작합니다 (bun dev)...")
    # Windows Terminal(wt)을 사용하여 현재 창 화면을 세로로 분할(split-pane)하여 엽니다.
    # -w 0: 현재 활성화된 터미널 창 사용
    # split-pane: 화면 분할
    # -d: 시작 디렉토리
    # --title: 창 타이틀 설정
    subprocess.Popen(f'wt -w 0 split-pane -d "{analytics_dir}" --title "넥스트스테이-Analytics" cmd /k "bun dev"', shell=True, cwd=analytics_dir)
    
    url = "http://localhost:4000"

    print(f"[대기] Analytics 서버({url})가 켜져 있는지 확인합니다...")

    # 서버가 뜰 때까지 최대 10초 대기하며 체크
    max_retries = 10
    server_up = False
    
    for i in range(max_retries):
        try:
            with urllib.request.urlopen(url, timeout=1) as response:
                if response.getcode() == 200:
                    print(f"\n[확인] Analytics 서버가 정상적으로 응답합니다! ({url})")
                    server_up = True
                    break
        except (URLError, HTTPError):
            print(f"[{i+1}/{max_retries}] 서버 기동 대기 중...", end="\r")
            time.sleep(1)

    if not server_up:
        print("\n[경고] 서버가 응답하지 않습니다. 터미널 창의 로그를 확인해 주세요.")
    
if __name__ == "__main__":
    main()

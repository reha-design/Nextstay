import subprocess
import os
import time
import webbrowser

def main():
    script_dir = os.path.dirname(os.path.abspath(__file__))
    root_dir = os.path.dirname(script_dir)
    guest_dir = os.path.join(root_dir, "frontend-guest")

    print("[체크] 기존 Guest 프론트엔드 창이 있는지 확인합니다...")
    # 중복 실행 방지를 위해 해당하는 타이틀을 가진 cmd 창을 종료합니다.
    subprocess.run('taskkill /FI "WINDOWTITLE eq 넥스트스테이-Guest프론트*" /T /F', shell=True, capture_output=True)

    print("\n[실행] Guest 프론트엔드 서버(Nuxt)를 시작합니다 (bun dev)...")
    # Windows Terminal(wt)을 사용하여 현재 창 화면을 세로로 분할(split-pane)하여 엽니다.
    subprocess.Popen(f'wt -w 0 split-pane -d "{guest_dir}" --title "넥스트스테이-Guest프론트" cmd /k "bun dev"', shell=True, cwd=guest_dir)
    
    url = "http://localhost:3000"

    print("[대기] 기존 서버(localhost:3000)가 켜져 있는지 확인합니다...")
    import urllib.request
    from urllib.error import URLError, HTTPError

    try:
        # 기존 서버가 정상적으로 응답하는지 확인
        urllib.request.urlopen(url, timeout=1)
        print("[확인] 포트 3000번이 이미 사용 중입니다. 브라우저를 추가로 띄우지 않습니다.")
    except (URLError, HTTPError):
        # 기존 서버가 없으면 새로 띄운 서버 기동을 기다림
        print("\n[대기] 새 서버가 기동될 때까지 잠시 기다립니다 (5초)...")
        time.sleep(5)
        print(f"브라우저를 엽니다: {url}")
        webbrowser.open(url, new=2)
    
if __name__ == "__main__":
    main()

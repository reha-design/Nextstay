import subprocess
import os
import sys
import time

def kill_process_on_port(port):
    """지정한 포트를 사용 중인 프로세스를 찾아 종료합니다."""
    print(f"[체크] {port} 포트 사용 여부를 확인 중...")
    try:
        output = subprocess.check_output(f"netstat -ano | findstr /R \":{port}\\>.*LISTENING\"", shell=True, text=True)
        lines = output.strip().split('\n')
        pids = set()
        for line in lines:
            parts = line.strip().split()
            if parts:
                pids.add(parts[-1])
        
        if pids:
            print(f"[재시작] 기존 프로세스를 발견했습니다. (PID: {', '.join(pids)})")
            for pid in pids:
                subprocess.run(f"taskkill /F /PID {pid} /T", shell=True, capture_output=True)
            time.sleep(1)
    except subprocess.CalledProcessError:
        pass

def main():
    # 프로젝트 루트 경로 계산
    script_dir = os.path.dirname(os.path.abspath(__file__))
    root_dir = os.path.dirname(script_dir)
    worker_dir = os.path.join(root_dir, "backend-worker-go")

    print("="*50)
    print("🚀 Nextstay Go Worker Starter (with Air Hot-Reload)")
    print("="*50)

    # 0. 중복 실행 방지
    print("[체크] 기존 워커 프로세스 및 포트 정리 중...")
    subprocess.run('taskkill /FI "WINDOWTITLE eq Nextstay-Go-Worker*" /T /F', shell=True, capture_output=True)
    kill_process_on_port(9000)

    # Air 설치 여부 재확인
    try:
        subprocess.run(["air", "-v"], capture_output=True, check=True)
    except (subprocess.CalledProcessError, FileNotFoundError):
        print("❌ 'air'가 설치되어 있지 않거나 PATH에 없습니다.")
        print("설치 방법: go install github.com/air-verse/air@latest")
        return

    print(f"📂 작업 디렉토리: {worker_dir}")
    print("🔄 소스 코드 변경 감지 시 자동으로 재빌드 및 재시작합니다.")
    print("🛑 중단하려면 이 창을 닫거나 Ctrl+C를 누르세요.")
    print("-" * 50)

    # 새 터미널 창에서 air 실행 (Windows 환경 고려)
    if os.name == 'nt':
        # 'start' 명령의 첫 번째 인자가 제목일 경우 반드시 쌍따옴표("")로 감싸야 파일명으로 오인되지 않습니다.
        subprocess.Popen('start "Nextstay-Go-Worker" cmd /k air', shell=True, cwd=worker_dir)
    else:
        # macOS/Linux용 (필요한 경우)
        subprocess.Popen(['air'], cwd=worker_dir)

    print("\n✅ Go Worker가 새로운 창에서 실행되었습니다.")

if __name__ == "__main__":
    main()

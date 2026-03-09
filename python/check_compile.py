import subprocess
import os
import sys

def main():
    # 스크립트 파일(python/check_compile.py)의 부모 디렉토리를 프로젝트 루트(base_dir)로 설정
    base_dir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    backend_dir = os.path.join(base_dir, "backend")
    frontend_dir = os.path.join(base_dir, "frontend-guest")

    # 1. 백엔드(Spring Boot/Kotlin) 컴파일 검증 명령어
    # 테스트 코드를 제외한 메인 코드의 문법/타입 에러만 빠르게 검증합니다.
    # 실행이 완료된 후 결과를 확인할 수 있도록 cmd 창을 유지(pause)합니다.
    backend_cmd = f'cd /d "{backend_dir}" && gradlew.bat classes && echo. && echo [Backend Compile Check Completed] && pause'
    
    # 2. 프론트엔드(Nuxt/TypeScript) 컴파일 검증 명령어
    # 프로젝트 환경에 맞춰 npx 대신 속도가 훨씬 빠른 bunx를 사용합니다.
    # Nuxt 3에서 제공하는 nuxi typecheck (내부적으로 vue-tsc 실행)를 호출합니다.
    frontend_cmd = f'cd /d "{frontend_dir}" && bunx nuxi typecheck && echo. && echo [Frontend Compile Check Completed] && pause'

    print("========================================")
    print("--- Nextstay 병렬 컴파일 체크 스크립트 ---")
    print("========================================")
    print("백엔드와 프론트엔드의 구문/타입 에러 여부를 검증합니다...")

    # Windows에서 각각 새로운 CMD 창을 띄워서 명령어를 병렬 실행합니다.
    try:
        # 백엔드 검증 창 띄우기
        subprocess.Popen(
            f'start "Backend Compile Check" cmd.exe /k "{backend_cmd}"',
            shell=True
        )
        print("[실행] 백엔드(Gradle) 컴파일 검증 창을 띄웠습니다.")

        # 프론트엔드 검증 창 띄우기
        subprocess.Popen(
            f'start "Frontend Compile Check" cmd.exe /k "{frontend_cmd}"',
            shell=True
        )
        print("[실행] 프론트엔드(Nuxt/Bun) 컴파일 검증 창을 띄웠습니다.")

        print("========================================")
        print("검증이 새로 뜬 CMD 창에서 독립적으로 진행 중입니다.")
        print("창의 출력 결과를 바탕으로 에러 여부를 확인해 주시기 바랍니다.")

    except Exception as e:
        print(f"오류가 발생했습니다: {e}")

if __name__ == "__main__":
    main()

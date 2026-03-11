import subprocess
import time
import os

def check_and_run_docker(root_dir):
    """
    도커 컨테이너가 실행 중인지 확인하고, 실행 중이지 않으면 시작합니다.
    실패 시(도커 미기동 등) 5초마다 재시도하여 연결을 대기합니다.
    """
    print("\n[1/3] 도커 컨테이너 상태를 확인합니다...")
    
    while True:
        try:
            # 실행 중인 컨테이너 이름 목록을 가져옵니다.
            # stderr=subprocess.STDOUT을 사용하여 도커가 꺼져있을 때의 에러 메시지도 캡처합니다.
            result = subprocess.check_output("docker ps --format \"{{.Names}}\"", shell=True, text=True, stderr=subprocess.STDOUT)
            running_containers = [c.strip() for c in result.strip().split('\n') if c.strip()]
            
            required_containers = ["nextstay-mysql", "nextstay-rabbitmq"]
            missing = [c for c in required_containers if c not in running_containers]
            
            if not missing:
                print("[확인] 모든 필수 컨테이너(MySQL, RabbitMQ)가 이미 가동 중입니다.")
                return 
            else:
                print(f"[알림] 실행 중이지 않은 컨테이너 발견: {', '.join(missing)}")
                print("       >> docker-compose up -d 실행 시도 중...")
                # docker-compose up -d 명령어가 성공(returncode 0)할 때까지 시도
                res = subprocess.run("docker-compose up -d", shell=True, cwd=root_dir, capture_output=True, text=True)
                if res.returncode == 0:
                    print("[완료] 컨테이너가 성공적으로 시작되었습니다.")
                    time.sleep(2)
                    return
                else:
                    # docker-compose 자체가 실패하는 경우 (이미지 다운로드 실패 등) 에러로 처리하여 재시도 유도
                    raise Exception(res.stderr.strip())

        except (subprocess.CalledProcessError, Exception) as e:
            print(f"[오류] 도커 연결 실패 또는 실행 오류: {e}")
            print("       >> 도커 데스크탑(Docker Desktop)이 실행 중인지 확인해주세요.")
            print("       >> 5초 후 다시 확인합니다...")
            time.sleep(5)

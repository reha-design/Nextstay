import requests
import json
import time

BASE_URL = "http://localhost:8080/api/v1/auth"
HEALTH_URL = "http://localhost:8080/actuator/health"

def print_result(case_name, response):
    status = response.status_code
    print(f"\n[테스트 케이스] {case_name}")
    print(f"상태 코드: {status}")
    try:
        print(f"응답 바디: {json.dumps(response.json(), indent=2, ensure_ascii=False)}")
    except:
        print(f"응답 바디: {response.text}")
    
    if 200 <= status < 300:
        print("✅ 성공")
    else:
        print("❌ 예상된 실패 또는 오류")

def run_tests():
    print("=== Nextstay API 통합 테스트 시작 ===\n")

    # 1. 헬스체크 확인
    try:
        resp = requests.get(HEALTH_URL)
        print_result("시스템 헬스체크", resp)
    except Exception as e:
        print(f"❌ 서버가 실행 중이 아닌 것 같습니다: {e}")
        return

    # 테스트 데이터
    test_email = f"test_{int(time.time())}@naver.com"
    test_user = {
        "role": "GUEST",
        "name": "홍길동",
        "email": test_email,
        "password": "Password123!",
        "passwordConfirm": "Password123!",
        "phone": "010-1234-5678",
        "termsAgreed": True,
        "marketingAgreed": False
    }

    # 2. 회원가입 (201 Created 예상)
    resp = requests.post(f"{BASE_URL}/signup", json=test_user)
    print_result("신규 회원가입", resp)

    # 3. 중복 가입 시도 (409 Conflict 예상)
    resp = requests.post(f"{BASE_URL}/signup", json=test_user)
    print_result("중복 이메일 가입 시도", resp)

    # 4. 로그인 - 성공 (200 OK 예상)
    login_data = {
        "email": test_email,
        "password": "Password123!"
    }
    resp = requests.post(f"{BASE_URL}/login", json=login_data)
    print_result("로그인 성공", resp)
    
    token = None
    if resp.status_code == 200:
        token = resp.json().get("accessToken")
        print(f"\n🔑 발급된 토큰: {token[:20]}...")

    # 5. 로그인 - 실패 (401 Unauthorized 예상)
    login_fail_data = {
        "email": test_email,
        "password": "WrongPassword!"
    }
    resp = requests.post(f"{BASE_URL}/login", json=login_fail_data)
    print_result("로그인 실패 (비밀번호 틀림)", resp)

    # 6. 유효성 검사 실패 테스트 (400 Bad Request 예상)
    invalid_user = test_user.copy()
    invalid_user["email"] = "not-an-email"
    resp = requests.post(f"{BASE_URL}/signup", json=invalid_user)
    print_result("유효성 검사 실패 (잘못된 이메일)", resp)

    print("\n=== 모든 테스트 완료 ===")

if __name__ == "__main__":
    run_tests()

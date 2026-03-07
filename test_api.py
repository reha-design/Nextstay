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
    timestamp = int(time.time() * 1000) # 밀리초 단위로 더 높은 유일성 보장
    test_email = f"test_{timestamp}@naver.com"
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

    # 7. HOST 계정 가입 및 숙소 등록 테스트
    print("\n--- 호스트 기능 테스트 ---")
    host_email = f"host_{int(time.time())}@naver.com"
    host_user = test_user.copy()
    host_user.update({
        "role": "HOST",
        "email": host_email,
        "name": "김호스트"
    })
    
    # 호스트 가입
    resp = requests.post(f"{BASE_URL}/signup", json=host_user)
    print_result("호스트 회원가입", resp)

    # 호스트 로그인
    resp = requests.post(f"{BASE_URL}/login", json={"email": host_email, "password": "Password123!"})
    print_result("호스트 로그인", resp)
    
    host_token = None
    if resp.status_code == 200:
        host_token = resp.json().get("accessToken")

    # 숙소 등록 (아직 구현 전이면 404 또는 403 예상)
    if host_token:
        stay_data = {
            "name": "바다 보이는 펜션",
            "description": "파도 소리가 들리는 아늑한 펜션입니다.",
            "address": "강원도 강릉시 해안로 123",
            "city": "강릉",
            "category": "PENSION",
            "discountPolicies": [
                {"minNights": 6, "discountRate": 0.33},
                {"minNights": 14, "discountRate": 0.59},
                {"minNights": 29, "discountRate": 0.71}
            ],
            "seasonPrices": [
                {
                    "seasonName": "여름 성수기",
                    "startDate": "2026-07-01",
                    "endDate": "2026-08-31",
                    "multiplier": 1.5
                },
                {
                    "seasonName": "연말 피크",
                    "startDate": "2026-12-20",
                    "endDate": "2026-12-31",
                    "multiplier": 1.3
                }
            ]
        }
        headers = {"Authorization": f"Bearer {host_token}"}
        resp = requests.post("http://localhost:8080/api/v1/stays", json=stay_data, headers=headers)
        print_result("숙소 등록 시도 (할인 정책 포함)", resp)
        
        stay_no = None
        if resp.status_code == 201:
            stay_no = resp.text
            
        if stay_no:
            room_data = {
                "stayNo": stay_no,
                "name": "오션뷰 스위트룸",
                "pricePerNight": 150000,
                "capacity": 4,
                "description": "바다가 한눈에 보이는 최고급 스위트룸입니다."
            }
            resp = requests.post("http://localhost:8080/api/v1/rooms", json=room_data, headers=headers)
            print_result("객실 등록 시도", resp)

    print("\n=== 모든 테스트 완료 ===")

if __name__ == "__main__":
    run_tests()

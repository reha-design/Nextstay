import requests
import json
import time
import sys
import io

# Windows 터미널에서 유니코드(이모지 등) 출력을 보장하기 위한 설정
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')

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
            
            room_no = None
            if resp.status_code == 201:
                room_no = resp.text
                
            if room_no:
                # 6박 (할인 적용 예상) + 성수기 (7월 1일 포함)
                price_req = {
                    "checkInDate": "2026-07-01",
                    "checkOutDate": "2026-07-07"
                }
                resp = requests.post(f"http://localhost:8080/api/v1/rooms/{room_no}/calculate-price", json=price_req)
                print_result("실시간 가격 계산 테스트 (성수기 + 6박 할인)", resp)

                # 8. 예약 (Booking) 테스트
                print("\n--- 예약 기능 테스트 ---")
                booking_req = {
                    "roomNo": room_no,
                    "checkInDate": "2026-09-01",
                    "checkOutDate": "2026-09-03"
                }
                headers_guest = {"Authorization": f"Bearer {token}"}
                resp = requests.post(f"http://localhost:8080/api/v1/rooms/{room_no}/bookings", json=booking_req, headers=headers_guest)
                print_result("단일 객실 예약 생성", resp)
                
                # 9. 내 예약 목록 조회
                resp = requests.get("http://localhost:8080/api/v1/members/me/bookings", headers=headers_guest)
                print_result("내 예약 목록 조회", resp)

                # 9-2. 중복 날짜 예약 시도 (409 예상)
                booking_fail_req = {
                    "roomNo": room_no,
                    "checkInDate": "2026-09-02", # 겹치는 날짜
                    "checkOutDate": "2026-09-04"
                }
                resp = requests.post(f"http://localhost:8080/api/v1/rooms/{room_no}/bookings", json=booking_fail_req, headers=headers_guest)
                print_result("중복 날짜 예약 시도 (실패 예상)", resp)

                # 10. 동시성 부하 테스트 (test_load 모듈 활용)
                print("\n--- 동시성 부하 테스트 준비 ---")
                guest_tokens = []
                for i in range(5):
                    u_email = f"guest_{i}_{int(time.time() * 1000)}@naver.com"
                    u_payload = test_user.copy()
                    u_payload.update({"email": u_email, "name": f"게스트{i}"})
                    s_resp = requests.post(f"{BASE_URL}/signup", json=u_payload)
                    if s_resp.status_code != 201:
                        print(f"❌ 게스트{i} 가입 실패: {s_resp.text}")
                        continue
                    l_resp = requests.post(f"{BASE_URL}/login", json={"email": u_email, "password": u_payload["password"]})
                    if l_resp.status_code == 200:
                        guest_tokens.append(l_resp.json()["accessToken"])
                    else:
                        print(f"❌ 게스트{i} 로그인 실패: {l_resp.text}")
                
                try:
                    from .test_load import run_concurrency_test
                except (ImportError, ValueError):
                    import test_load
                    run_concurrency_test = test_load.run_concurrency_test
                
                # 5명의 서로 다른 게스트가 동일한 날짜 예약 시도
                run_concurrency_test(room_no, "2026-10-01", "2026-10-03", guest_tokens)

    print("\n=== 모든 테스트 완료 ===")

if __name__ == "__main__":
    run_tests()

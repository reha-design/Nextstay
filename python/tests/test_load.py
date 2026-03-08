import threading
import requests
import json
import time

def deliberate_booking(token, room_no, payload, results):
    """
    개별 스레드에서 실행될 예약 요청 함수
    """
    headers = {
        "Authorization": f"Bearer {token}",
        "Content-Type": "application/json"
    }
    url = f"http://localhost:8080/api/v1/rooms/{room_no}/bookings"
    
    try:
        resp = requests.post(url, json=payload, headers=headers)
        results.append({
            "status": resp.status_code,
            "body": resp.text
        })
    except Exception as e:
        results.append({
            "status": 500,
            "error": str(e)
        })

def run_concurrency_test(room_no, check_in, check_out, guest_tokens):
    """
    메인 테스트 함수: 여러 스레드를 생성하여 동시 예약을 시도함
    """
    print(f"\n🚀 [동시성 테스트 시작] 객실: {room_no}, 기간: {check_in} ~ {check_out}")
    print(f"🔥 {len(guest_tokens)}개의 스레드가 동시에 예약을 시도합니다...")

    payload = {
        "roomNo": room_no,
        "checkInDate": check_in,
        "checkOutDate": check_out
    }

    threads = []
    results = []

    # 스레드 생성
    for token in guest_tokens:
        t = threading.Thread(target=deliberate_booking, args=(token, room_no, payload, results))
        threads.append(t)

    # 모든 스레드 동시 시작 (최대한 겹치게 하기 위해 순차 start 후 join)
    for t in threads:
        t.start()

    # 모든 스레드 종료 대기
    for t in threads:
        t.join()

    # 결과 분석
    success_count = 0
    conflict_count = 0
    other_count = 0

    for res in results:
        if res["status"] == 201:
            success_count += 1
        elif res["status"] == 409:
            conflict_count += 1
        else:
            other_count += 1
            print(f"⚠️ 예상치 못한 응답: {res['status']} - {res.get('body') or res.get('error')}")

    print("\n" + "="*40)
    print(f"✅ 성공(201): {success_count}건")
    print(f"❌ 충돌(409): {conflict_count}건")
    if other_count > 0:
        print(f"⚠️ 기타 에러: {other_count}건")
    
    if success_count == 1:
        print("\n✨ 결과: [성공] 비관적 락이 정상 작동하여 단 1명만 예약에 성공했습니다.")
        return True
    else:
        print(f"\n🚨 결과: [실패] {success_count}명이 중복 예약되었습니다. 락 로직 확인 필요!")
        return False

if __name__ == "__main__":
    # 단독 실행 시 테스트 (실제 토큰이 있어야 함)
    pass

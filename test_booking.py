import requests
import json

BASE_URL = "http://localhost:8080"

def test_flow():
    email = "testguest@nextstay.com"
    password = "password123"

    # 1. Login
    print(f">>> Logging in as {email}...")
    login_data = {
        "email": email,
        "password": password
    }
    response = requests.post(f"{BASE_URL}/api/v1/auth/login", json=login_data)
    
    if response.status_code != 200:
        print(f"FAILED Login: {response.status_code}")
        print(response.text)
        return

    print("SUCCESS Login")
    
    try:
        data = response.json()
        token = data.get("accessToken")
    except Exception as e:
        print(f"Error parsing JSON: {e}")
        return

    if not token:
        print("COULD NOT FIND ACCESS TOKEN")
        return

    headers = {
        "Authorization": f"Bearer {token}",
        "Content-Type": "application/json"
    }

    # 2. Creating Booking
    room_no = "r790933311" 
    print(f">>> Creating booking for room {room_no}...")
    
    booking_data = {
        "roomNo": room_no,
        "checkInDate": "2026-04-10",
        "checkOutDate": "2026-04-15",
        "guestCount": 2,
        "guestName": "Test User",
        "guestPhone": "010-0000-0000"
    }
    
    # Correct endpoint path
    response = requests.post(f"{BASE_URL}/api/v1/rooms/{room_no}/bookings", headers=headers, json=booking_data)
    
    if response.status_code in [200, 201]:
        print(f"SUCCESS Booking: {response.text}")
    else:
        print(f"FAILED Booking: {response.status_code}")
        print(response.text)

if __name__ == "__main__":
    test_flow()

# Nextstay Notification Worker (Go)

RabbitMQ로부터 알림 이벤트를 수신하여 외부 API(Firebase, 알림톡 등)로 발송을 위임하는 고성능 비동기 워커입니다.

## 🛠️ 기술 스택
- **Language**: Go (Golang)
- **Library**: `amqp091-go` (RabbitMQ Client)

## 🚀 실행 방법

### 1. RabbitMQ 실행
먼저 Docker를 통해 RabbitMQ가 실행 중이어야 합니다.
```bash
docker-compose up -d rabbitmq
```

### 2. 워커 실행
```bash
cd backend-worker-go
go run main.go
```

## 🧪 테스트 방법
1. RabbitMQ 관리 UI([http://localhost:15672](http://localhost:15672))에 접속합니다. (ID/PW: guest/guest)
2. **Queues** 탭에서 `q.notification.v1` 큐를 선택합니다.
3. **Publish message** 섹션에서 `Payload`에 테스트 메시지를 입력하고 **Publish message** 버튼을 누릅니다.
4. Go 워커 콘솔에 수신 로그와 시뮬레이션 발송 로그가 찍히는지 확인합니다.

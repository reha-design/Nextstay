package main

import (
	"log"
	"net/http"
	"os"
	"os/signal"
	"syscall"
	"time"

	amqp "github.com/rabbitmq/amqp091-go"
)

func failOnError(err error, msg string) {
	if err != nil {
		log.Panicf("%s: %s", msg, err)
	}
}

func main() {
	// 1. RabbitMQ 연결 설정
	// 환경 변수에서 URL을 읽어오고, 없으면 로컬 호스트 기본값 사용
	rabbitURL := os.Getenv("RABBITMQ_URL")
	if rabbitURL == "" {
		rabbitURL = "amqp://guest:guest@localhost:5672/"
	}
	conn, err := amqp.Dial(rabbitURL)
	failOnError(err, "Failed to connect to RabbitMQ")
	defer conn.Close()

	ch, err := conn.Channel()
	failOnError(err, "Failed to open a channel")
	defer ch.Close()

	// 2. 알림 큐 선언 (Durable: 서버 재시작 시에도 유지)
	q, err := ch.QueueDeclare(
		"q.reservation.v1", // name
		true,               // durable
		false,              // delete when unused
		false,              // exclusive
		false,              // no-wait
		amqp.Table{
			"x-dead-letter-exchange":    "reservation.dlx",
			"x-dead-letter-routing-key": "reservation.dead",
		}, // arguments
	)
	failOnError(err, "Failed to declare a queue")

	// QoS 설정 (한 번에 1개씩 처리하여 부하 분산)
	err = ch.Qos(
		1,     // prefetch count
		0,     // prefetch size
		false, // global
	)
	failOnError(err, "Failed to set QoS")

	// 3. 메시지 소비(Consume) 시작
	msgs, err := ch.Consume(
		q.Name, // queue
		"",     // consumer
		false,  // auto-ack (수동 승인을 위해 false)
		false,  // exclusive
		false,  // no-local
		false,  // no-wait
		nil,    // args
	)
	failOnError(err, "Failed to register a consumer")

	// Graceful Shutdown을 위한 채널 설정
	sigChan := make(chan os.Signal, 1)
	signal.Notify(sigChan, syscall.SIGINT, syscall.SIGTERM)

	log.Printf(" [*] Notification Worker 가동 중... 메시지를 기다리는 중입니다.")

	// 4. 헬스 체크 서버 시작 (HTTP 9000 포트)
	// 외부 모니터링 시스템이나 배포 환경(K8s/AWS)에서 서비스 상태를 확인하는 용도입니다.
	go func() {
		http.HandleFunc("/health", func(w http.ResponseWriter, r *http.Request) {
			w.WriteHeader(http.StatusOK)
			w.Write([]byte("OK"))
		})
		log.Printf(" [🌐] Health Check 서버 시작: http://localhost:9000/health")
		if err := http.ListenAndServe(":9000", nil); err != nil {
			log.Printf(" [!] Health Check 서버 오류: %v", err)
		}
	}()

	// 5. 메시지 처리 루프
	go func() {
		for d := range msgs {
			log.Printf(" [📩] 메시지 수신: %s", d.Body)

			// [시뮬레이션] 알림 발송 처리 (비즈니스 로직)
			simulateNotification(d.Body)

			// 처리 완료 후 Ack 전송
			log.Printf(" [✅] 처리 완료 (Ack)")
			d.Ack(false)
		}
	}()

	// 종료 신호 대기
	<-sigChan
	log.Printf(" [!] Worker 종료 중...")
}

// simulateNotification 은 실제 Firebase/카카오톡 발송 대신 로그로 시뮬레이션합니다.
func simulateNotification(body []byte) {
	log.Printf(" [🚀] 알림 발송 시뮬레이션 시작...")
	
	// 소요 시간 시뮬레이션 (네트워크 지연 등)
	time.Sleep(1 * time.Second)
	
	log.Printf(" [📱] 발송 완료: %s", string(body))
}

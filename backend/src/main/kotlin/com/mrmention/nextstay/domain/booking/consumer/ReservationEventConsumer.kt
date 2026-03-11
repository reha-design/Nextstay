package com.mrmention.nextstay.domain.booking.consumer

import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class ReservationEventConsumer {

    private val log = LoggerFactory.getLogger(javaClass)

    @RabbitListener(queues = ["q.reservation.v1"])
    fun consumeReservationEvent(message: Map<String, Any>) {
        val bookingNo = message["bookingNo"]
        val userNo = message["userNo"]
        val roomNo = message["roomNo"]
        
        log.info(">>> [비동기 예약 이벤트 수신] 예약번호: $bookingNo, 고객번호: $userNo, 객실번호: $roomNo")
        
        try {
            // 1. 외부 알림 시스템 연동 시뮬레이션
            sendNotification(bookingNo.toString(), userNo.toString())
            
            // 2. 예약 데이터 최종 검증 및 상태 업데이트 (시뮬레이션)
            processBooking(bookingNo.toString())
            
            log.info("<<< [비동기 예약 처리 성공] 예약번호: $bookingNo")
        } catch (e: Exception) {
            log.error("!!! [비동기 예약 처리 실패] 예약번호: $bookingNo - DLQ로 이동 가능", e)
            throw e 
        }
    }

    private fun sendNotification(bookingNo: String, userNo: String) {
        log.info("    -> [알림 서비스] 고객($userNo)에게 예약($bookingNo) 완료 알림톡 발송 중...")
        Thread.sleep(1000) // 동기 대기
        log.info("    -> [알림 서비스] 알림톡 발송 완료")
    }

    private fun processBooking(bookingNo: String) {
        log.info("    -> [예약 서비스] 예약($bookingNo) 상태를 'CONFIRMED'로 변경 중...")
        Thread.sleep(500) // 동기 대기
        log.info("    -> [예약 서비스] 상태 변경 완료")
    }
}

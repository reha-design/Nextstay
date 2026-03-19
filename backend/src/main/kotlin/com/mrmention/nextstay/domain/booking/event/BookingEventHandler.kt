package com.mrmention.nextstay.domain.booking.event

import com.mrmention.nextstay.global.config.RabbitMqConfig
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class BookingEventHandler(
    private val rabbitTemplate: RabbitTemplate
) {
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleBookingCreated(event: BookingCreatedEvent) {
        val message = mapOf(
            "bookingNo" to event.bookingNo,
            "userNo" to event.userNo,
            "roomNo" to event.roomNo,
            "guestName" to event.guestName
        )
        rabbitTemplate.convertAndSend(
            RabbitMqConfig.RESERVATION_EXCHANGE,
            RabbitMqConfig.RESERVATION_ROUTING_KEY,
            message
        )
    }
}

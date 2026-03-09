package com.mrmention.nextstay.global.config

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitMqConfig {

    companion object {
        const val RESERVATION_EXCHANGE = "reservation.exchange"
        const val RESERVATION_QUEUE = "q.reservation.v1"
        const val RESERVATION_ROUTING_KEY = "reservation.created"

        // DLQ 설정 추가
        const val RESERVATION_DLX = "reservation.dlx"
        const val RESERVATION_DLQ = "reservation.dlq"
        const val RESERVATION_DLQ_ROUTING_KEY = "reservation.dead"
    }

    @Bean
    fun reservationExchange() = DirectExchange(RESERVATION_EXCHANGE)

    @Bean
    fun reservationQueue(): Queue {
        return org.springframework.amqp.core.QueueBuilder.durable(RESERVATION_QUEUE)
            .withArgument("x-dead-letter-exchange", RESERVATION_DLX)
            .withArgument("x-dead-letter-routing-key", RESERVATION_DLQ_ROUTING_KEY)
            .build()
    }

    @Bean
    fun reservationBinding(reservationQueue: Queue, reservationExchange: DirectExchange): Binding {
        return BindingBuilder.bind(reservationQueue).to(reservationExchange).with(RESERVATION_ROUTING_KEY)
    }

    // DLQ 관련 Bean 설정
    @Bean
    fun reservationDeadLetterExchange() = DirectExchange(RESERVATION_DLX)

    @Bean
    fun reservationDeadLetterQueue() = Queue(RESERVATION_DLQ)

    @Bean
    fun reservationDeadLetterBinding(): Binding {
        return BindingBuilder.bind(reservationDeadLetterQueue())
            .to(reservationDeadLetterExchange())
            .with(RESERVATION_DLQ_ROUTING_KEY)
    }

    @Bean
    fun messageConverter(): MessageConverter {
        return Jackson2JsonMessageConverter()
    }

    @Bean
    fun rabbitTemplate(connectionFactory: ConnectionFactory, messageConverter: MessageConverter): RabbitTemplate {
        val rabbitTemplate = RabbitTemplate(connectionFactory)
        rabbitTemplate.messageConverter = messageConverter
        return rabbitTemplate
    }
}

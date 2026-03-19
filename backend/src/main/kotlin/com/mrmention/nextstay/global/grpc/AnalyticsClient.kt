package com.mrmention.nextstay.global.grpc

import com.mrmention.nextstay.grpc.AnalyticsServiceGrpcKt
import com.mrmention.nextstay.grpc.EventRequest
import com.mrmention.nextstay.grpc.VisitRequest
import io.grpc.ManagedChannelBuilder
import jakarta.annotation.PreDestroy
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class AnalyticsClient(
    @Value("\${grpc.analytics.host:localhost}") private val host: String,
    @Value("\${grpc.analytics.port:50051}") private val port: Int
) {
    private val channel = ManagedChannelBuilder.forAddress(host, port)
        .usePlaintext()
        .build()

    private val stub = AnalyticsServiceGrpcKt.AnalyticsServiceCoroutineStub(channel)

    /**
     * 이벤트 로그 전송
     */
    suspend fun logEvent(eventName: String, payloadJson: String) {
        try {
            val request = EventRequest.newBuilder()
                .setEventName(eventName)
                .setPayloadJson(payloadJson)
                .setTimestamp(System.currentTimeMillis())
                .build()

            stub.logEvent(request)
        } catch (e: Exception) {
            // 운영 환경에서는 로깅만 하고 서비스에는 지장을 주지 않도록 처리
            println("Failed to log event via gRPC: ${e.message}")
        }
    }

    /**
     * 방문 로그 전송
     */
    suspend fun logVisit(path: String, userId: String?, userAgent: String) {
        try {
            val request = VisitRequest.newBuilder()
                .setPath(path)
                .setUserId(userId ?: "")
                .setUserAgent(userAgent)
                .build()

            stub.logVisit(request)
        } catch (e: Exception) {
            println("Failed to log visit via gRPC: ${e.message}")
        }
    }

    @PreDestroy
    fun shutdown() {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)
    }
}

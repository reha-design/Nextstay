package com.mrmention.nextstay.domain.member.event

import com.mrmention.nextstay.global.grpc.AnalyticsClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class AuthEventHandler(
    private val analyticsClient: AnalyticsClient
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleSignup(event: SignupEvent) {
        scope.launch {
            analyticsClient.logEvent(
                "SIGNUP_SUCCESS",
                "{\"userNo\": \"${event.userNo}\", \"email\": \"${event.email}\", \"role\": \"${event.role}\"}"
            )
        }
    }
}

package com.mrmention.nextstay.global.util

import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime

interface TimeProvider {
    fun now(): LocalDateTime
    fun today(): LocalDate
}

@Component
class DefaultTimeProvider : TimeProvider {
    override fun now(): LocalDateTime = LocalDateTime.now()
    override fun today(): LocalDate = LocalDate.now()
}

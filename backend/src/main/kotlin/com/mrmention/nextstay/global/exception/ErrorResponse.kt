package com.mrmention.nextstay.global.exception

import java.time.LocalDateTime

/**
 * 전역 공통 에러 응답 형식
 */
data class ErrorResponse(
    val status: Int,
    val message: String,
    val timestamp: LocalDateTime = LocalDateTime.now()
)

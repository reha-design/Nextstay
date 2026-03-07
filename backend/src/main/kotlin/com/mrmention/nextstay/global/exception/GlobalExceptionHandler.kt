package com.mrmention.nextstay.global.exception

import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * 비즈니스 예외 처리
     */
    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(e: BusinessException): ResponseEntity<ErrorResponse> {
        log.warn("[BusinessException] ${e.message}")
        val errorResponse = ErrorResponse(
            status = e.status.value(),
            message = e.message
        )
        return ResponseEntity.status(e.status).body(errorResponse)
    }

    /**
     * (@Valid) 유효성 검사 실패 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(e: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val message = e.bindingResult.fieldErrors.firstOrNull()?.defaultMessage ?: "잘못된 입력값입니다."
        log.warn("[ValidationException] $message")
        val errorResponse = ErrorResponse(
            status = 400,
            message = message
        )
        return ResponseEntity.badRequest().body(errorResponse)
    }

    /**
     * 데이터 무결성 위반 처리 (중복 키 등)
     */
    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDataIntegrityViolationException(e: DataIntegrityViolationException): ResponseEntity<ErrorResponse> {
        log.warn("[DataIntegrityViolation] ${e.message}")
        val errorResponse = ErrorResponse(
            status = 409,
            message = "이미 존재하는 데이터입니다. (중복 오류)"
        )
        return ResponseEntity.status(409).body(errorResponse)
    }

    /**
     * 기타 일반 예외 처리
     */
    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ErrorResponse> {
        log.error("[Unexpected Error]", e)
        val errorResponse = ErrorResponse(
            status = 500,
            message = "서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해 주세요."
        )
        return ResponseEntity.internalServerError().body(errorResponse)
    }
}

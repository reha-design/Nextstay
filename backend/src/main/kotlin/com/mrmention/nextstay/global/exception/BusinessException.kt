package com.mrmention.nextstay.global.exception

import org.springframework.http.HttpStatus

/**
 * 비즈니스 로직 예외의 최상위 클래스
 */
open class BusinessException(
    val status: HttpStatus,
    override val message: String
) : RuntimeException(message)

/**
 * 409 Conflict: 리소스 충돌 (중복 등)
 */
class AlreadyExistsException(message: String) : BusinessException(HttpStatus.CONFLICT, message)

/**
 * 401 Unauthorized: 인증 실패
 */
class InvalidCredentialsException(message: String) : BusinessException(HttpStatus.UNAUTHORIZED, message)

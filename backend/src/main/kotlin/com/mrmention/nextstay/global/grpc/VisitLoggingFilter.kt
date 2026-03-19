package com.mrmention.nextstay.global.grpc

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class VisitLoggingFilter(
    private val analyticsClient: AnalyticsClient
) : OncePerRequestFilter() {

    private val scope = CoroutineScope(Dispatchers.IO)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val path = request.requestURI

        // 정적 리소스나 헬스 체크는 제외
        if (!path.startsWith("/api/v1")) {
            filterChain.doFilter(request, response)
            return
        }

        val userAgent = request.getHeader("User-Agent") ?: "Unknown"

        // 현재 인증된 사용자 정보 가져오기 (비로그인 시 anonymousUser)
        val authentication = SecurityContextHolder.getContext().authentication
        val userId = if (authentication != null && authentication.isAuthenticated) {
            authentication.name
        } else {
            null
        }

        // 비동기로 gRPC 로깅 전송
        scope.launch {
            analyticsClient.logVisit(path, userId, userAgent)
        }

        filterChain.doFilter(request, response)
    }
}

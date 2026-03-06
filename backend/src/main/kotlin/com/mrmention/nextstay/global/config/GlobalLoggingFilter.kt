package com.mrmention.nextstay.global.config

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class GlobalLoggingFilter : OncePerRequestFilter() {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val uri = request.requestURI
        
        // 정적 리소스 및 Swagger 관련 로그 제외 (노이즈 제거)
        if (uri.startsWith("/swagger-ui") || uri.startsWith("/v3/api-docs") || uri == "/favicon.ico") {
            filterChain.doFilter(request, response)
            return
        }

        val startTime = System.nanoTime()
        val method = request.method
        val queryString = request.queryString?.let { "?$it" } ?: ""
        
        // [REQ] 형태로 축소
        log.info("--> $method $uri$queryString")

        try {
            filterChain.doFilter(request, response)
        } finally {
            val endTime = System.nanoTime()
            val durationNs = endTime - startTime
            val status = response.status
            
            val timeDisplay = if (durationNs < 1_000_000) {
                "${durationNs / 1_000}μs"
            } else {
                "${durationNs / 1_000_000}ms"
            }
            
            // [<-- 200] 형태로 축소
            log.info("<-- $status $method $uri - $timeDisplay")
        }
    }
}

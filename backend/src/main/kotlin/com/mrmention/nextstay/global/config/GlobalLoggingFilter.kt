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
        val startTime = System.nanoTime()
        val method = request.method
        val uri = request.requestURI
        val queryString = request.queryString?.let { "?$it" } ?: ""
        
        log.info("[REQUEST] $method $uri$queryString")

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
            
            log.info("[RESPONSE] $method $uri$queryString - Status: $status, Time: $timeDisplay")
        }
    }
}

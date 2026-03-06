package com.mrmention.nextstay.global.config

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.util.StopWatch

@Component
class GlobalLoggingFilter : OncePerRequestFilter() {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val stopWatch = StopWatch()
        stopWatch.start()

        val method = request.method
        val uri = request.requestURI
        val queryString = request.queryString?.let { "?$it" } ?: ""
        
        log.info("[REQUEST] $method $uri$queryString")

        try {
            filterChain.doFilter(request, response)
        } finally {
            stopWatch.stop()
            val status = response.status
            val time = stopWatch.totalTimeMillis
            
            log.info("[RESPONSE] $method $uri$queryString - Status: $status, Time: ${time}ms")
        }
    }
}

package com.mrmention.nextstay.global.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.web.filter.OncePerRequestFilter

/**
 * 개발용 바이패스 필터.
 * 헤더에 'test-user-id'가 있으면 해당 유저로 강제 인증 처리함.
 */
class DevAuthenticationFilter : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val testUserNo = request.getHeader("test-user-id")
        
        if (!testUserNo.isNullOrBlank()) {
            val authorities = listOf(SimpleGrantedAuthority("ROLE_GUEST")) // 기본적으로 GUEST 권한 부여
            val principal = User(testUserNo, "", authorities)
            val authentication = UsernamePasswordAuthenticationToken(principal, null, authorities)
            SecurityContextHolder.getContext().authentication = authentication
        }
        
        filterChain.doFilter(request, response)
    }
}

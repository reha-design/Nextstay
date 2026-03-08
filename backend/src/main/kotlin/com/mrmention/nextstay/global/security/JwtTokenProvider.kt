package com.mrmention.nextstay.global.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtTokenProvider(
    @Value("\${jwt.secret}") private val secretKey: String,
    @Value("\${jwt.expiration}") private val expirationTime: Long
) {
    private val key: SecretKey = Keys.hmacShaKeyFor(secretKey.toByteArray())

    fun createToken(userNo: String, role: String): String {
        val now = Date()
        val expiryDate = Date(now.time + expirationTime) // Access token expiration

        return Jwts.builder()
            .subject(userNo)
            .claim("role", role)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(key)
            .compact()
    }

    // 7일 유효기간 리프레시 토큰 (밀리초 변환: 7일 * 24시간 * 60분 * 60초 * 1000)
    fun createRefreshToken(userNo: String, role: String): String {
        val now = Date()
        val refreshExpiryDate = Date(now.time + (1000L * 60 * 60 * 24 * 7)) 

        return Jwts.builder()
            .subject(userNo)
            .claim("role", role)
            .issuedAt(now)
            .expiration(refreshExpiryDate)
            .signWith(key)
            .compact()
    }

    // Refresh Token 검증 후 subject(userNo) 반환
    fun getUserNoFromToken(token: String): String {
        val claims: Claims = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload
        return claims.subject
    }
    
    // Role 반환
    fun getRoleFromToken(token: String): String {
        val claims: Claims = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload
        return claims["role"] as String
    }

    fun getAuthentication(token: String): Authentication {
        val claims: Claims = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload

        val userNo = claims.subject
        val role = claims["role"] as String
        val authorities = listOf(SimpleGrantedAuthority("ROLE_$role"))
        
        val principal = User(userNo, "", authorities)
        return UsernamePasswordAuthenticationToken(principal, token, authorities)
    }

    fun validateToken(token: String): Boolean {
        return try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token)
            true
        } catch (e: Exception) {
            false
        }
    }
}

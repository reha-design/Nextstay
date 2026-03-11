package com.mrmention.nextstay.global.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtTokenProvider: JwtTokenProvider
) {

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors { it.configurationSource(corsConfigurationSource()) }
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it.requestMatchers(
                    "/h2-console/**",
                    "/favicon.ico",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/swagger-resources/**",
                    "/api/v1/auth/**",
                    "/actuator/health",
                    "/api/v1/rooms/*/calculate-price",
                    "/api/v1/rooms/*",
                    "/api/v1/accommodations/**",
                    "/api/v1/stays/**"
                ).permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/v1/stays").hasRole("HOST")
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/v1/rooms").hasRole("HOST")
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/v1/rooms/*/bookings").hasRole("GUEST")
                .anyRequest().authenticated()
            }
            .addFilterBefore(ApiCacheControlFilter(), UsernamePasswordAuthenticationFilter::class.java)
            .addFilterBefore(DevAuthenticationFilter(), UsernamePasswordAuthenticationFilter::class.java)
            .addFilterBefore(JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter::class.java)
        
        // H2 콘솔 및 캐시 헤더 설정
        http.headers { headers ->
            headers.frameOptions { it.sameOrigin() }
            headers.cacheControl { it.disable() } // ApiCacheControlFilter에서 직접 제어하기 위해 기본값 비활성화
        }

        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOriginPatterns = listOf(
            "http://localhost:3000", 
            "https://d384c7rwalwmeb.cloudfront.net",
            "https://d2jwfd8djegkc5.cloudfront.net"
        ) // 프론트엔드 주소 허용
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
        configuration.allowedHeaders = listOf("*")
        configuration.allowCredentials = true
        configuration.maxAge = 3600L

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }
}

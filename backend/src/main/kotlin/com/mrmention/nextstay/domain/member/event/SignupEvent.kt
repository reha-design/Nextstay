package com.mrmention.nextstay.domain.member.event

data class SignupEvent(
    val userNo: String,
    val email: String,
    val role: String
)

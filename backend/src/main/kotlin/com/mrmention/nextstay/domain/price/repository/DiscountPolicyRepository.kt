package com.mrmention.nextstay.domain.price.repository

import com.mrmention.nextstay.domain.price.entity.DiscountPolicy
import org.springframework.data.jpa.repository.JpaRepository

interface DiscountPolicyRepository : JpaRepository<DiscountPolicy, Long>

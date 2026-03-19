package com.mrmention.nextstay.domain.price.repository

import com.mrmention.nextstay.domain.price.entity.DiscountPolicy
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface DiscountPolicyRepository : JpaRepository<DiscountPolicy, UUID>

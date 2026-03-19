package com.mrmention.nextstay.domain.promotion.repository

import com.mrmention.nextstay.domain.promotion.entity.PromoRule
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.UUID

@Repository
interface PromoRuleRepository : JpaRepository<PromoRule, UUID> {
    fun findAllByStartAtLessThanEqualAndEndAtGreaterThanEqual(
        now1: LocalDateTime,
        now2: LocalDateTime
    ): List<PromoRule>
}

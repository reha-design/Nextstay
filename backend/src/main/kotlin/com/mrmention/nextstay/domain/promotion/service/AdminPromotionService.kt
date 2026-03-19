package com.mrmention.nextstay.domain.promotion.service

import com.mrmention.nextstay.domain.promotion.dto.PromoRuleRequest
import com.mrmention.nextstay.domain.promotion.dto.PromoRuleResponse
import com.mrmention.nextstay.domain.promotion.entity.PromoRule
import com.mrmention.nextstay.domain.promotion.repository.PromoRuleRepository
import com.mrmention.nextstay.global.util.IdGenerator
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true)
class AdminPromotionService(
    private val promoRuleRepository: PromoRuleRepository,
    private val idGenerator: IdGenerator
) {
    @Transactional
    fun createPromoRule(request: PromoRuleRequest): String {
        val rule = PromoRule(
            id = idGenerator.generate(),
            name = request.name,
            discountType = request.discountType,
            amount = request.amount,
            minOrderPrice = request.minOrderPrice,
            startAt = request.startAt,
            endAt = request.endAt,
            maxPerUser = request.maxPerUser
        )
        return promoRuleRepository.save(rule).id.toString()
    }

    fun getAllPromoRules(): List<PromoRuleResponse> = promoRuleRepository.findAll().map { it.toResponse() }

    private fun PromoRule.toResponse() = PromoRuleResponse(
        id = this.id.toString(),
        name = this.name,
        discountType = this.discountType,
        amount = this.amount,
        minOrderPrice = this.minOrderPrice,
        startAt = this.startAt,
        endAt = this.endAt,
        maxPerUser = this.maxPerUser
    )
}

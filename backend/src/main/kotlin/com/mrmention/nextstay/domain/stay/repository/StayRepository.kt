package com.mrmention.nextstay.domain.stay.repository

import com.mrmention.nextstay.domain.stay.entity.Stay
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID


interface StayRepository : JpaRepository<Stay, UUID> {

    @EntityGraph(attributePaths = ["host", "rooms", "discountPolicies", "seasonPrices"])
    override fun findAll(): List<Stay>

    @EntityGraph(attributePaths = ["host", "rooms", "discountPolicies", "seasonPrices"])
    fun findByStayNo(stayNo: UUID): Stay?
}

package com.mrmention.nextstay.domain.summary.repository

import com.mrmention.nextstay.domain.stay.entity.Stay
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StaySummaryRepository : JpaRepository<Stay, Long> {
    fun findByCity(city: String): List<Stay>
}

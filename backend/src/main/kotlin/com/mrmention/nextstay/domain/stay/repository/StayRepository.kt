package com.mrmention.nextstay.domain.stay.repository

import com.mrmention.nextstay.domain.stay.entity.Stay
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface StayRepository : JpaRepository<Stay, Long> {
    fun findByStayNo(stayNo: String): Optional<Stay>
}

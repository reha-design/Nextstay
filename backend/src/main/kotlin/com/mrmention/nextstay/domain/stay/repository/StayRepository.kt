package com.mrmention.nextstay.domain.stay.repository

import com.mrmention.nextstay.domain.stay.entity.Stay
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository


interface StayRepository : JpaRepository<Stay, Long> {
    
    @EntityGraph(attributePaths = ["host"])
    override fun findAll(): List<Stay>

    @EntityGraph(attributePaths = ["host"])
    fun findByStayNo(stayNo: String): Stay?
}

package com.mrmention.nextstay.domain.stay.repository

import com.mrmention.nextstay.domain.stay.entity.Stay
import org.springframework.data.jpa.repository.JpaRepository

interface StayRepository : JpaRepository<Stay, Long>

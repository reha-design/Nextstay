package com.mrmention.nextstay.domain.summary.controller

import com.mrmention.nextstay.domain.stay.entity.Stay
import com.mrmention.nextstay.domain.summary.repository.StaySummaryRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/summaries/stays")
class StaySummaryController(
    private val staySummaryRepository: StaySummaryRepository
) {

    @GetMapping
    fun getAllSummaries(): List<Stay> {
        return staySummaryRepository.findAll()
    }

    @GetMapping("/search")
    fun getSummariesByCity(@RequestParam city: String): List<Stay> {
        return staySummaryRepository.findByCity(city)
    }
}

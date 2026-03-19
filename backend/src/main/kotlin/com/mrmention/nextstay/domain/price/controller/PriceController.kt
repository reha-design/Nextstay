package com.mrmention.nextstay.domain.price.controller

import com.mrmention.nextstay.domain.price.dto.PriceCalculationRequest
import com.mrmention.nextstay.domain.price.dto.PriceCalculationResponse
import com.mrmention.nextstay.domain.price.service.PriceService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@Tag(name = "Price", description = "가격 계산 API")
@RestController
@RequestMapping("/api/v1/rooms")
class PriceController(
    private val priceService: PriceService
) {

    @Operation(summary = "예상 요금 조회", description = "체크인/체크아웃 날짜를 기반으로 예상 요금 및 할인 정보를 조회합니다.")
    @GetMapping("/{roomNo}/calculate-price")
    fun calculatePrice(
        @PathVariable roomNo: UUID,
        @Valid @ModelAttribute request: PriceCalculationRequest
    ): ResponseEntity<PriceCalculationResponse> {
        val response = priceService.calculatePrice(roomNo, request)
        return ResponseEntity.ok(response)
    }
}

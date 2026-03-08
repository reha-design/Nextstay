package com.mrmention.nextstay.domain.price.controller

import com.mrmention.nextstay.domain.price.dto.PriceCalculationRequest
import com.mrmention.nextstay.domain.price.dto.PriceCalculationResponse
import com.mrmention.nextstay.domain.price.service.PriceService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "Price", description = "가격 계산 API")
@RestController
@RequestMapping("/api/v1/rooms/{roomNo}/calculate-price")
class PriceController(
    private val priceService: PriceService
) {

    @Operation(summary = "실시간 가격 계산", description = "날짜를 입력하면 성수기 및 연박 할인이 적용된 최종 금액을 계산합니다.")
    @PostMapping
    fun calculatePrice(
        @PathVariable roomNo: String,
        @RequestBody request: PriceCalculationRequest
    ): ResponseEntity<PriceCalculationResponse> {
        val response = priceService.calculatePrice(roomNo, request)
        return ResponseEntity.ok(response)
    }
}

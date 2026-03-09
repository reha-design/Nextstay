package com.mrmention.nextstay.domain.price.service

import com.mrmention.nextstay.domain.price.dto.PriceCalculationRequest
import com.mrmention.nextstay.domain.price.dto.PriceCalculationResponse
import com.mrmention.nextstay.domain.room.repository.RoomRepository
import com.mrmention.nextstay.global.exception.BusinessException
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class PriceService(
    private val roomRepository: RoomRepository,
    private val pricingEngine: PricingEngine
) {

    fun calculatePrice(roomNo: String, request: PriceCalculationRequest): PriceCalculationResponse {
        val room = roomRepository.findByRoomNo(roomNo)
            ?: throw BusinessException(HttpStatus.NOT_FOUND, "객실 정보를 찾을 수 없습니다.")

        return pricingEngine.calculate(room, request)
    }
}

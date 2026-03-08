package com.mrmention.nextstay.domain.price.service

import com.mrmention.nextstay.domain.price.dto.PriceCalculationRequest
import com.mrmention.nextstay.domain.price.dto.PriceCalculationResponse
import com.mrmention.nextstay.domain.room.repository.RoomRepository
import com.mrmention.nextstay.global.exception.BusinessException
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.temporal.ChronoUnit
import kotlin.math.roundToLong

@Service
@Transactional(readOnly = true)
class PriceService(
    private val roomRepository: RoomRepository
) {

    fun calculatePrice(roomNo: String, request: PriceCalculationRequest): PriceCalculationResponse {
        val room = roomRepository.findRoomWithPricingData(roomNo)
            ?: throw BusinessException(HttpStatus.NOT_FOUND, "객실 정보를 찾을 수 없습니다.")

        val checkIn = request.checkInDate
        val checkOut = request.checkOutDate
        
        if (checkIn.isAfter(checkOut) || checkIn == checkOut) {
            throw BusinessException(HttpStatus.BAD_REQUEST, "체크인 날짜는 체크아웃 날짜보다 빨라야 합니다.")
        }

        val totalNights = ChronoUnit.DAYS.between(checkIn, checkOut).toInt()
        val stay = room.stay
        
        var seasonalAdjustedTotal = java.math.BigDecimal.ZERO
        val appliedSeasons = mutableSetOf<String>()

        // 1. 일자별 성수기 가중치 적용
        for (i in 0 until totalNights) {
            val currentDate = checkIn.plusDays(i.toLong())
            
            // 해당 날짜에 적용되는 시즌 정책 확인 (여러 개일 경우 가장 높은 가중치 적용)
            val activeSeason = stay.seasonPrices
                .filter { (it.startDate <= currentDate) && (it.endDate >= currentDate) }
                .maxByOrNull { it.multiplier }

            if (activeSeason != null) {
                seasonalAdjustedTotal = seasonalAdjustedTotal.add(room.pricePerNight.toBigDecimal().multiply(activeSeason.multiplier))
                appliedSeasons.add("${activeSeason.seasonName}(${activeSeason.multiplier}x)")
            } else {
                seasonalAdjustedTotal = seasonalAdjustedTotal.add(room.pricePerNight.toBigDecimal())
            }
        }

        // 2. 연박 할인 적용
        // 가장 높은 연박 일수 조건을 만족하는 정책 찾기
        val applicableDiscount = stay.discountPolicies
            .filter { totalNights >= it.minNights }
            .maxByOrNull { it.minNights }

        val discountAmount = if (applicableDiscount != null) {
            seasonalAdjustedTotal.multiply(applicableDiscount.discountRate)
        } else {
            java.math.BigDecimal.ZERO
        }

        val finalPrice = seasonalAdjustedTotal.subtract(discountAmount)
        val discountInfo = applicableDiscount?.let { 
            val percent = (it.discountRate * 100.toBigDecimal()).toInt()
            "${it.minNights}박 이상 할인(${percent}%)" 
        }

        return PriceCalculationResponse(
            totalNights = totalNights,
            basePricePerNight = room.pricePerNight,
            totalBasePrice = room.pricePerNight.toLong() * totalNights,
            seasonalAdjustedPrice = seasonalAdjustedTotal.setScale(0, java.math.RoundingMode.HALF_UP).toLong(),
            discountAmount = discountAmount.setScale(0, java.math.RoundingMode.HALF_UP).toLong(),
            finalTotalPrice = finalPrice.setScale(0, java.math.RoundingMode.HALF_UP).toLong(),
            appliedSeasons = appliedSeasons.toList(),
            appliedDiscount = discountInfo
        )
    }
}

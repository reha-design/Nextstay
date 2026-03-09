package com.mrmention.nextstay.domain.price.service

import com.mrmention.nextstay.domain.price.dto.*
import com.mrmention.nextstay.domain.price.entity.DiscountPolicyType
import com.mrmention.nextstay.domain.price.repository.RoomDiscountMappingRepository
import com.mrmention.nextstay.domain.price.repository.RoomPriceScheduleRepository
import com.mrmention.nextstay.domain.room.entity.Room
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.*

@Component
class PricingEngine(
    private val priceScheduleRepository: RoomPriceScheduleRepository,
    private val discountMappingRepository: RoomDiscountMappingRepository
) {
    fun calculate(room: Room, request: PriceCalculationRequest): PriceCalculationResponse {
        val checkIn = request.checkInDate
        val checkOut = request.checkOutDate
        val totalNights = ChronoUnit.DAYS.between(checkIn, checkOut).toInt()

        // 1. 일별 요금 계산 (시즌 및 요일 반영)
        val schedules = priceScheduleRepository.findByRoomIdAndDateRange(room.id!!, checkIn, checkOut.minusDays(1))
        
        var totalOriginalPrice = 0L
        for (i in 0 until totalNights) {
            val date = checkIn.plusDays(i.toLong())
            val schedule = schedules.find { s ->
                val start = s.startDate
                val end = s.endDate
                (start <= date) && (end >= date)
            }
            
            val dailyPrice = if (schedule != null) {
                val isWeekend = date.dayOfWeek == DayOfWeek.FRIDAY || date.dayOfWeek == DayOfWeek.SATURDAY
                val weekendPrice = schedule.weekendPrice
                val peakPrice = schedule.peakPrice
                
                if (isWeekend && weekendPrice != null) {
                    weekendPrice.toLong()
                } else if (peakPrice != null) {
                    peakPrice.toLong()
                } else {
                    schedule.basePrice.toLong()
                }
            } else {
                room.pricePerNight.toLong() // 스케줄 없으면 기본가
            }
            totalOriginalPrice += dailyPrice
        }

        // 2. 할인 정책 적용
        val mappings = discountMappingRepository.findActiveByRoomId(room.id!!)
        val applicablePolicies = mappings.map { it.discountPolicy }
            .filter { it.minStayNights == null || totalNights >= it.minStayNights!! }
            .filter { policy ->
                val start = policy.startDate
                val end = policy.endDate
                (start == null || !start.isAfter(checkIn)) && (end == null || !end.isBefore(checkIn))
            }
            .sortedByDescending { it.priority }

        // 가장 유리한 정책(또는 우선순위가 높은 정책) 선택
        val bestLongStayPolicy = applicablePolicies
            .filter { it.policyType == DiscountPolicyType.LONG_STAY }
            .maxByOrNull { it.minStayNights ?: 0 }

        val finalDiscountAmount: Long
        val finalPrice: Long
        val totalDiscountRate: Int
        
        if (bestLongStayPolicy != null) {
            val discountRate = bestLongStayPolicy.discountRate ?: BigDecimal.ZERO
            val amountFromRate = totalOriginalPrice.toBigDecimal().multiply(discountRate).setScale(0, RoundingMode.HALF_UP).toLong()
            val fixedAmount = (bestLongStayPolicy.discountAmount ?: 0).toLong()
            
            finalDiscountAmount = amountFromRate + fixedAmount
            finalPrice = totalOriginalPrice - finalDiscountAmount
            totalDiscountRate = if (totalOriginalPrice > 0) ((finalDiscountAmount.toDouble() / totalOriginalPrice.toDouble()) * 100).toInt() else 0
        } else {
            finalDiscountAmount = 0L
            finalPrice = totalOriginalPrice
            totalDiscountRate = 0
        }

        val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.KOREA)
        
        val displayDto = DisplayDto(
            badgeText = bestLongStayPolicy?.badgeText,
            discountNotice = if (finalDiscountAmount > 0) "실제 가격보다 ${currencyFormatter.format(finalDiscountAmount)} 저렴하게 예약 중!" else null,
            isLongStayDiscount = bestLongStayPolicy != null,
            formattedOriginalPrice = currencyFormatter.format(totalOriginalPrice),
            formattedFinalPrice = currencyFormatter.format(finalPrice)
        )

        val pricingDetail = PricingDetailDto(
            nightlyBasePrice = room.pricePerNight,
            totalNights = totalNights,
            totalOriginalPrice = totalOriginalPrice,
            finalTotalPrice = finalPrice,
            totalDiscountAmount = finalDiscountAmount,
            totalDiscountRate = totalDiscountRate,
            pricePerNightDiscounted = if (totalNights > 0) (finalPrice / totalNights).toInt() else 0
        )

        return PriceCalculationResponse(
            pricing = pricingDetail,
            display = displayDto,
            appliedPolicies = applicablePolicies.map { 
                AppliedPolicyDto(it.policyName, it.discountRate, it.discountAmount)
            }
        )
    }
}

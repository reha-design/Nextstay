package com.mrmention.nextstay.domain.stay.service

import com.mrmention.nextstay.domain.member.repository.MemberRepository
import com.mrmention.nextstay.domain.room.repository.RoomRepository
import com.mrmention.nextstay.domain.stay.dto.DiscountPolicyResponse
import com.mrmention.nextstay.domain.stay.dto.SeasonPriceResponse
import com.mrmention.nextstay.domain.stay.dto.StayRequest
import com.mrmention.nextstay.domain.stay.dto.StayResponse
import com.mrmention.nextstay.domain.stay.dto.MainPageStayResponse
import com.mrmention.nextstay.domain.stay.dto.StayDetailResponse
import com.mrmention.nextstay.domain.stay.dto.RoomDetailResponse
import com.mrmention.nextstay.domain.stay.entity.Stay
import com.mrmention.nextstay.domain.stay.entity.StayDiscountPolicy
import com.mrmention.nextstay.domain.stay.entity.StaySeasonPrice
import com.mrmention.nextstay.domain.stay.dto.PriceTierDto
import com.mrmention.nextstay.domain.price.dto.PriceCalculationRequest
import com.mrmention.nextstay.domain.price.service.PricingEngine
import com.mrmention.nextstay.domain.stay.repository.StayRepository
import com.mrmention.nextstay.global.exception.BusinessException
import com.mrmention.nextstay.global.util.IdGenerator
import com.mrmention.nextstay.global.util.TimeProvider
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true)
class StayService(
    private val stayRepository: StayRepository,
    private val memberRepository: MemberRepository,
    private val roomRepository: RoomRepository, // 여전히 Room 상세 조회가 필요할 수 있으므로 유지
    private val pricingEngine: PricingEngine,
    private val timeProvider: TimeProvider,
    private val idGenerator: IdGenerator
) {

    /**
     * 숙소 등록
     */
    @Transactional
    fun createStay(request: StayRequest, userNo: String): String {
        val host = memberRepository.findByUserNo(UUID.fromString(userNo))
            ?: throw BusinessException(HttpStatus.NOT_FOUND, "호스트 정보를 찾을 수 없습니다.")

        val stayId = idGenerator.generate()
        val stayNo = idGenerator.generate()

        val stay = Stay(
            id = stayId,
            stayNo = stayNo,
            host = host,
            name = request.name,
            description = request.description,
            address = request.address,
            city = request.city,
            category = request.category,
            latitude = request.latitude,
            longitude = request.longitude
        )

        request.discountPolicies.forEach {
            stay.discountPolicies.add(
                StayDiscountPolicy(
                    id = idGenerator.generate(),
                    stay = stay,
                    minNights = it.minNights,
                    discountRate = it.discountRate
                )
            )
        }

        request.seasonPrices.forEach {
            stay.seasonPrices.add(
                StaySeasonPrice(
                    id = idGenerator.generate(),
                    stay = stay,
                    seasonName = it.seasonName,
                    startDate = it.startDate,
                    endDate = it.endDate,
                    multiplier = it.multiplier
                )
            )
        }

        val savedStay = stayRepository.save(stay)
        return savedStay.stayNo.toString()
    }

    /**
     * 숙소 목록 조회 (심플 버전)
     */
    fun getAllStays(): List<StayResponse> = stayRepository.findAll().map { it.toResponse() }

    fun getMainPageStays(): List<MainPageStayResponse> {
        val today = timeProvider.today()
        val stays = stayRepository.findAll() // JOIN FETCH로 이미 rooms가 포함됨

        return stays.map { stay ->
            val cheapestRoom = stay.rooms.minByOrNull { it.pricePerNight }
            val minPrice = cheapestRoom?.pricePerNight ?: 50000

            val priceTiers = cheapestRoom?.let { room ->
                listOf(6, 14, 29).map { nights ->
                    val result = pricingEngine.calculate(room, PriceCalculationRequest(today, today.plusDays(nights.toLong())))
                    PriceTierDto(
                        nights = nights,
                        price = result.pricing.finalTotalPrice,
                        originalPrice = result.pricing.totalOriginalPrice,
                        discountRate = result.pricing.totalDiscountRate
                    )
                }
            } ?: emptyList()

            MainPageStayResponse(
                stayNo = stay.stayNo.toString(),
                name = stay.name,
                address = stay.address,
                category = stay.category.name,
                minPrice = minPrice,
                thumbnailUrl = "https://picsum.photos/seed/${stay.stayNo}/400/300",
                rating = String.format("%.1f", (40..50).random() / 10.0).toDouble(),
                priceTiers = priceTiers
            )
        }
    }

    fun getStayDetail(stayNo: String): StayDetailResponse {
        val today = timeProvider.today()
        val stay = stayRepository.findByStayNo(UUID.fromString(stayNo))
            ?: throw BusinessException(HttpStatus.NOT_FOUND, "숙소 정보를 찾을 수 없습니다.")

        // stay.rooms가 이미 JOIN FETCH 됨

        return StayDetailResponse(
            stayNo = stay.stayNo.toString(),
            name = stay.name,
            description = stay.description,
            address = stay.address,
            city = stay.city,
            category = stay.category.name,
            hostName = stay.host.name,
            latitude = if (stayNo == "s260307174469") 37.7718 else stay.latitude,
            longitude = if (stayNo == "s260307174469") 128.9482 else stay.longitude,
            rating = String.format("%.1f", (40..50).random() / 10.0).toDouble(),
            images = stay.images.ifEmpty {
                listOf(
                    "https://picsum.photos/seed/${stay.stayNo}_1/1200/800",
                    "https://picsum.photos/seed/${stay.stayNo}_2/1200/800",
                    "https://picsum.photos/seed/${stay.stayNo}_3/1200/800"
                )
            },
            rooms = stay.rooms.map { room ->
                // 29박(한달살기) 기준 할인가 계산
                val result = pricingEngine.calculate(room, PriceCalculationRequest(today, today.plusDays(29)))

                RoomDetailResponse(
                    roomNo = room.roomNo.toString(),
                    name = room.name,
                    description = room.description,
                    type = "STANDARD", // 임시 타입
                    basePrice = room.pricePerNight,
                    capacity = room.capacity,
                    imageUrls = listOf("https://picsum.photos/seed/${room.roomNo}/400/300"),
                    monthlyPrice = result.pricing.finalTotalPrice,
                    discountRate = result.pricing.totalDiscountRate,
                    badgeText = result.display.badgeText
                )
            }
        )
    }

    private fun Stay.toResponse() = StayResponse(
        stayNo = this.stayNo.toString(),
        name = this.name,
        description = this.description,
        address = this.address,
        city = this.city,
        category = this.category,
        hostName = this.host.name,
        latitude = this.latitude,
        longitude = this.longitude,
        discountPolicies = this.discountPolicies.map {
            DiscountPolicyResponse(it.minNights, it.discountRate)
        },
        seasonPrices = this.seasonPrices.map {
            SeasonPriceResponse(it.seasonName, it.startDate, it.endDate, it.multiplier)
        }
    )
}

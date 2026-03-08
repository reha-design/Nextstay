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
import com.mrmention.nextstay.domain.stay.repository.StayRepository
import com.mrmention.nextstay.global.exception.BusinessException
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.format.DateTimeFormatter
import java.util.concurrent.atomic.AtomicLong

@Service
@Transactional(readOnly = true)
class StayService(
    private val stayRepository: StayRepository,
    private val memberRepository: MemberRepository,
    private val roomRepository: RoomRepository
) {
    private val sequence = AtomicLong(1)

    /**
     * 숙소 등록
     */
    @Transactional
    fun createStay(request: StayRequest, userNo: String): String {
        val host = memberRepository.findByUserNo(userNo)
            ?: throw BusinessException(HttpStatus.NOT_FOUND, "호스트 정보를 찾을 수 없습니다.")

        val stayNo = generateStayNo()
        
        val stay = Stay(
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
                    stay = stay,
                    minNights = it.minNights,
                    discountRate = it.discountRate
                )
            )
        }
        
        request.seasonPrices.forEach {
            stay.seasonPrices.add(
                StaySeasonPrice(
                    stay = stay,
                    seasonName = it.seasonName,
                    startDate = it.startDate,
                    endDate = it.endDate,
                    multiplier = it.multiplier
                )
            )
        }

        val savedStay = stayRepository.save(stay)
        return savedStay.stayNo
    }

    /**
     * 숙소 목록 조회 (심플 버전)
     */
    fun getAllStays(): List<StayResponse> {
        return stayRepository.findAll().map { it.toResponse() }
    }

    /**
     * 메인 페이지용 숙소 목록 조회 (썸네일, 최저가 포함)
     */
    fun getMainPageStays(): List<MainPageStayResponse> {
        val stays = stayRepository.findAll()
        return stays.map { stay ->
            val rooms = roomRepository.findAllByStayId(stay.id!!)
            val minPrice = rooms.minOfOrNull { it.pricePerNight } ?: 50000

            MainPageStayResponse(
                stayNo = stay.stayNo,
                name = stay.name,
                address = stay.address,
                category = stay.category.name,
                minPrice = minPrice,
                thumbnailUrl = "https://picsum.photos/seed/${stay.stayNo}/400/300",
                rating = String.format("%.1f", (40..50).random() / 10.0).toDouble()
            )
        }
    }

    /**
     * 숙소 상세 정보 조회
     */
    fun getStayDetail(stayNo: String): StayDetailResponse {
        val stay = stayRepository.findByStayNo(stayNo)
            ?: throw BusinessException(HttpStatus.NOT_FOUND, "숙소 정보를 찾을 수 없습니다.")
        
        val rooms = roomRepository.findAllByStayId(stay.id!!)
        
        return StayDetailResponse(
            stayNo = stay.stayNo,
            name = stay.name,
            description = stay.description,
            address = stay.address,
            city = stay.city,
            category = stay.category.name,
            hostName = stay.host.name,
            latitude = if (stayNo == "s260307174469") 37.7718 else stay.latitude,
            longitude = if (stayNo == "s260307174469") 128.9482 else stay.longitude,
            rating = String.format("%.1f", (40..50).random() / 10.0).toDouble(),
            images = listOf(
                "https://picsum.photos/seed/${stay.stayNo}_1/1200/800",
                "https://picsum.photos/seed/${stay.stayNo}_2/1200/800",
                "https://picsum.photos/seed/${stay.stayNo}_3/1200/800"
            ),
            rooms = rooms.map { room ->
                RoomDetailResponse(
                    roomNo = room.roomNo,
                    name = room.name,
                    description = room.description,
                    type = "STANDARD", // 임시 타입
                    basePrice = room.pricePerNight,
                    capacity = room.capacity,
                    imageUrls = listOf("https://picsum.photos/seed/${room.roomNo}/400/300")
                )
            }
        )
    }

    private fun generateStayNo(): String {
        val date = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyMMddHHmm"))
        val random = (10..99).random()
        return "s$date$random"
    }

    private fun Stay.toResponse() = StayResponse(
        stayNo = this.stayNo,
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

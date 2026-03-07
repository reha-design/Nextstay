package com.mrmention.nextstay.domain.stay.service

import com.mrmention.nextstay.domain.member.repository.MemberRepository
import com.mrmention.nextstay.domain.stay.dto.StayRequest
import com.mrmention.nextstay.domain.stay.dto.StayResponse
import com.mrmention.nextstay.domain.stay.entity.Stay
import com.mrmention.nextstay.domain.stay.repository.StayRepository
import com.mrmention.nextstay.global.exception.BusinessException
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.atomic.AtomicLong

@Service
@Transactional(readOnly = true)
class StayService(
    private val stayRepository: StayRepository,
    private val memberRepository: MemberRepository
) {
    private val sequence = AtomicLong(1)

    /**
     * 숙소 등록
     */
    @Transactional
    fun createStay(request: StayRequest, userNo: String): String {
        val host = memberRepository.findByUserNo(userNo)
            .orElseThrow { BusinessException(HttpStatus.NOT_FOUND, "호스트 정보를 찾을 수 없습니다.") }

        val stayNo = generateStayNo()
        
        val stay = Stay(
            stayNo = stayNo,
            host = host,
            name = request.name,
            description = request.description,
            address = request.address,
            city = request.city,
            category = request.category
        )

        val savedStay = stayRepository.save(stay)
        return savedStay.stayNo
    }

    /**
     * 숙소 목록 조회 (심플 버전)
     */
    fun getAllStays(): List<StayResponse> {
        return stayRepository.findAll().map { it.toResponse() }
    }

    private fun generateStayNo(): String {
        val date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        val seq = String.format("%03d", sequence.getAndIncrement())
        return "s$date-$seq"
    }

    private fun Stay.toResponse() = StayResponse(
        stayNo = this.stayNo,
        name = this.name,
        description = this.description,
        address = this.address,
        city = this.city,
        category = this.category,
        hostName = this.host.name
    )
}

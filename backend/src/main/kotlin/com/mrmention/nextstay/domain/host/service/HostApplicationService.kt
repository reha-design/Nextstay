package com.mrmention.nextstay.domain.host.service

import com.mrmention.nextstay.domain.host.dto.HostApplicationRequest
import com.mrmention.nextstay.domain.host.dto.HostApplicationResponse
import com.mrmention.nextstay.domain.host.entity.HostApplication
import com.mrmention.nextstay.domain.host.entity.HostApplicationStatus
import com.mrmention.nextstay.domain.host.repository.HostApplicationRepository
import com.mrmention.nextstay.domain.member.entity.MemberRole
import com.mrmention.nextstay.domain.member.entity.OnboardingStatus
import com.mrmention.nextstay.domain.member.repository.MemberRepository
import com.mrmention.nextstay.global.exception.BusinessException
import com.mrmention.nextstay.global.util.IdGenerator
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true)
class HostApplicationService(
    private val hostApplicationRepository: HostApplicationRepository,
    private val memberRepository: MemberRepository,
    private val idGenerator: IdGenerator
) {
    @Transactional
    fun submitApplication(userNo: String, request: HostApplicationRequest): String {
        val member = memberRepository.findByUserNo(UUID.fromString(userNo))
            ?: throw BusinessException(HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다.")

        if (hostApplicationRepository.findByMemberId(member.id) != null) {
            throw BusinessException(HttpStatus.BAD_REQUEST, "이미 신청 내역이 존재합니다.")
        }

        val application = HostApplication(
            id = idGenerator.generate(),
            member = member,
            stayName = request.stayName,
            businessNo = request.businessNo,
            documentUrls = request.documentUrls,
            status = HostApplicationStatus.PENDING
        )

        val saved = hostApplicationRepository.save(application)

        member.onboardingStatus = OnboardingStatus.PENDING
        memberRepository.save(member)

        return saved.id.toString()
    }

    fun getMyApplication(userNo: String): HostApplicationResponse {
        val member = memberRepository.findByUserNo(UUID.fromString(userNo))
            ?: throw BusinessException(HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다.")

        val application = hostApplicationRepository.findByMemberId(member.id)
            ?: throw BusinessException(HttpStatus.NOT_FOUND, "신청 내역이 없습니다.")

        return application.toResponse()
    }

    // --- Admin Methods ---

    fun getAllApplications(): List<HostApplicationResponse> = hostApplicationRepository.findAll().map { it.toResponse() }

    @Transactional
    fun updateApplicationStatus(id: UUID, status: HostApplicationStatus): HostApplicationResponse {
        val application = hostApplicationRepository.findById(id).orElseThrow {
            BusinessException(HttpStatus.NOT_FOUND, "신청 정보를 찾을 수 없습니다.")
        }

        application.status = status

        // 승인 시 회원의 역할과 온보딩 상태 변경
        if (status == HostApplicationStatus.APPROVED) {
            val member = application.member
            member.onboardingStatus = OnboardingStatus.APPROVED
            member.role = MemberRole.HOST // role이 var로 변경됨
            memberRepository.save(member)
        } else if (status == HostApplicationStatus.REJECTED) {
            application.member.onboardingStatus = OnboardingStatus.NONE
            memberRepository.save(application.member)
        }

        return application.toResponse()
    }

    private fun HostApplication.toResponse() = HostApplicationResponse(
        id = this.id.toString(),
        stayName = this.stayName,
        businessNo = this.businessNo,
        status = this.status,
        createdAt = this.createdAt
    )
}

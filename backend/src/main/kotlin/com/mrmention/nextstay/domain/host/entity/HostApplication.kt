package com.mrmention.nextstay.domain.host.entity

import com.mrmention.nextstay.domain.member.entity.Member
import com.mrmention.nextstay.global.entity.BaseEntity
import jakarta.persistence.*
import java.util.UUID

enum class HostApplicationStatus { PENDING, AUDIT, REJECTED, APPROVED }

@Entity
@Table(name = "host_applications")
class HostApplication(
    @Id
    @Column(columnDefinition = "BINARY(16)")
    val id: UUID,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val member: Member,

    @Column(name = "stay_name", nullable = false)
    val stayName: String,

    @Column(name = "business_no", nullable = false)
    val businessNo: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: HostApplicationStatus = HostApplicationStatus.PENDING,

    @ElementCollection
    @CollectionTable(name = "host_application_documents", joinColumns = [JoinColumn(name = "application_id")])
    @Column(name = "document_url")
    val documentUrls: List<String> = mutableListOf()
) : BaseEntity()

package com.mrmention.nextstay.global.config

import com.mrmention.nextstay.domain.member.entity.Member
import com.mrmention.nextstay.domain.member.entity.MemberRole
import com.mrmention.nextstay.domain.member.repository.MemberRepository
import com.mrmention.nextstay.domain.room.entity.Room
import com.mrmention.nextstay.domain.room.repository.RoomRepository
import com.mrmention.nextstay.domain.stay.entity.Stay
import com.mrmention.nextstay.domain.stay.entity.StayCategory
import com.mrmention.nextstay.domain.stay.repository.StayRepository
import com.mrmention.nextstay.domain.price.entity.*
import com.mrmention.nextstay.domain.price.repository.*
import com.mrmention.nextstay.domain.booking.repository.BookingRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

@Component
class DataLoader(
    private val memberRepository: MemberRepository,
    private val stayRepository: StayRepository,
    private val roomRepository: RoomRepository,
    private val priceScheduleRepository: RoomPriceScheduleRepository,
    private val discountPolicyRepository: DiscountPolicyRepository,
    private val roomDiscountMappingRepository: RoomDiscountMappingRepository,
    private val bookingRepository: BookingRepository,
    private val pricingCoefficientRepository: PricingCoefficientRepository,
    private val passwordEncoder: PasswordEncoder
) : CommandLineRunner {

    @Transactional
    override fun run(vararg args: String?) {
        // 이미 데이터가 존재한다면 초기화를 건너뜁니다.
        if (memberRepository.count() > 0) {
            println(">>> 데이터가 이미 존재합니다. 초기화 과정을 건너뜁니다.")
            return
        }

        println(">>> 데이터가 존재하지 않습니다. 초기 데이터 생성을 시작합니다...")

        // 기존 데이터 초기화 (외래 키 제약 조건 고려)
        // 사실 count() 체크로 건너뛰므로 아래 deleteAll은 비어있는 상태에서만 실행되거나 아예 실행되지 않아야 합니다.
        bookingRepository.deleteAll()
        roomDiscountMappingRepository.deleteAll()
        priceScheduleRepository.deleteAll()
        roomRepository.deleteAll()
        stayRepository.deleteAll()
        memberRepository.deleteAll()
        discountPolicyRepository.deleteAll()
        pricingCoefficientRepository.deleteAll()

        // 0. 시스템 가격 계수 초기화
        val coefficients = initPricingCoefficients()

        // 1. 호스트 및 게스트 계정 생성
        val host = Member(
            userNo = "m2603090001",
            email = "host@nextstay.com",
            password = passwordEncoder.encode("password123"),
            name = "안중근",
            phone = "010-1234-5678",
            role = MemberRole.HOST,
            termsAgreed = true
        )
        memberRepository.save(host)

        val guest = Member(
            userNo = "m2603107777",
            email = "testguest@nextstay.com",
            password = passwordEncoder.encode("password123"),
            name = "테스트게스트",
            phone = "010-9999-8888",
            role = MemberRole.GUEST,
            termsAgreed = true
        )
        memberRepository.save(guest)

        // 2. 숙소 데이터 30개 생성
        val citiesWithImages = listOf(
            CityInfo("제주", 33.4890, 126.4983, (1..10).map { "https://picsum.photos/seed/jeju_$it/1200/800" }),
            CityInfo("서울", 37.5665, 126.9780, (1..10).map { "https://picsum.photos/seed/seoul_$it/1200/800" }),
            CityInfo("부산", 35.1796, 129.0756, (1..10).map { "https://picsum.photos/seed/busan_$it/1200/800" }),
            CityInfo("강원", 37.8853, 127.7298, (1..10).map { "https://picsum.photos/seed/gangwon_$it/1200/800" }),
            CityInfo("경주", 35.8562, 129.2247, (1..10).map { "https://picsum.photos/seed/gyeongju_$it/1200/800" })
        )

        val categories = StayCategory.values()
        val random = Random()

        for (i in 1..30) {
            val cityInfo = citiesWithImages[i % citiesWithImages.size]
            val category = categories[random.nextInt(categories.size)]
            val stayNo = "s${System.currentTimeMillis() % 10000000}$i"
            
            // 시청 좌표 기준 근처 분포 (Jitter 적용)
            val jitterLat = (random.nextDouble() - 0.5) * 0.05
            val jitterLng = (random.nextDouble() - 0.5) * 0.05

            val stay = Stay(
                stayNo = stayNo,
                host = host,
                name = "[${cityInfo.name}/${category.name.lowercase()}] 진정한 휴식과 힐링... 한달살기 숙소 ${i}호점",
                description = "넥스트스테이가 엄선한 ${cityInfo.name} 지역의 특별한 ${category.name.lowercase()}입니다. 편안한 휴식을 보장합니다.",
                address = "${cityInfo.name}특별시 ${cityInfo.name}구 ${cityInfo.name}동 ${i}번지",
                city = cityInfo.name,
                category = category,
                latitude = cityInfo.lat + jitterLat,
                longitude = cityInfo.lng + jitterLng,
                images = cityInfo.images.shuffled().take(6)
            )
            stayRepository.save(stay)

            // 3. 각 숙소에 종속된 독립적 할인 정책 생성 (플랫폼 권장 계수 활용)
            val p1 = DiscountPolicy(
                policyName = "단기 거주 할인",
                policyType = DiscountPolicyType.LONG_STAY,
                minStayNights = 6,
                discountRate = coefficients["SHORT_STAY_DISCOUNT"],
                badgeText = "단 6일만 38% 세일 ⚡",
                priority = 10
            )
            val p2 = DiscountPolicy(
                policyName = "중기 거주 할인",
                policyType = DiscountPolicyType.LONG_STAY,
                minStayNights = 14,
                discountRate = coefficients["MEDIUM_STAY_DISCOUNT"],
                badgeText = "단 14일만 49% 세일 ⚡",
                priority = 20
            )
            val p3 = DiscountPolicy(
                policyName = "한 달 살기 특가",
                policyType = DiscountPolicyType.LONG_STAY,
                minStayNights = 29,
                discountRate = coefficients["LONG_STAY_DISCOUNT"],
                badgeText = "단 1일만 60% 세일 ⚡",
                priority = 30
            )
            val savedPolicies = discountPolicyRepository.saveAll(listOf(p1, p2, p3))

            // 4. 각 숙소당 객실 3~5개 생성
            val roomCount = 3 + random.nextInt(3)
            for (j in 1..roomCount) {
                val roomPrice = (5 + random.nextInt(21)) * 10000
                val room = Room(
                    roomNo = "r${stayNo.substring(1)}$j",
                    stay = stay,
                    name = "Room $j - ${if (j==1) "Standard" else if (j==2) "Deluxe" else "Suite"}",
                    pricePerNight = roomPrice,
                    capacity = 2 + random.nextInt(3),
                    description = "모던한 인테리어와 깨끗한 침구가 준비된 객실입니다."
                )
                roomRepository.save(room)

                // 해당 객실에 독립된 할인 정책 매핑
                savedPolicies.forEach { policy ->
                    roomDiscountMappingRepository.save(RoomDiscountMapping(room = room, discountPolicy = policy))
                }

                // 성수기 요금 스케줄 (DB의 할증 계수 활용)
                val peakMultiplier = coefficients["PEAK_SEASON_SURCHARGE"] ?: BigDecimal("1.5")
                val weekendMultiplier = coefficients["WEEKEND_SURCHARGE"] ?: BigDecimal("1.8")

                priceScheduleRepository.save(RoomPriceSchedule(
                    room = room,
                    startDate = LocalDate.of(2026, 7, 1),
                    endDate = LocalDate.of(2026, 8, 31),
                    basePrice = roomPrice,
                    peakPrice = roomPrice.toBigDecimal().multiply(peakMultiplier).toInt(),
                    weekendPrice = roomPrice.toBigDecimal().multiply(weekendMultiplier).toInt(),
                    periodName = "2026 여름 성수기"
                ))
            }
        }
    }

    private fun initPricingCoefficients(): Map<String, BigDecimal> {
        val data = mapOf(
            "PEAK_SEASON_SURCHARGE" to (BigDecimal("1.5") to "성수기 요금 할증 계수"),
            "WEEKEND_SURCHARGE" to (BigDecimal("1.8") to "주말 요금 할증 계수"),
            "SHORT_STAY_DISCOUNT" to (BigDecimal("0.386") to "6박 이상 단기 체류 할인율"),
            "MEDIUM_STAY_DISCOUNT" to (BigDecimal("0.490") to "14박 이상 중기 체류 할인율"),
            "LONG_STAY_DISCOUNT" to (BigDecimal("0.601") to "29박 이상 장기 체류 할인율")
        )

        val result = mutableMapOf<String, BigDecimal>()
        data.forEach { (key, value) ->
            val coefficient = pricingCoefficientRepository.save(
                PricingCoefficient(key = key, value = value.first, description = value.second)
            )
            result[key] = coefficient.value
        }
        return result
    }

    private data class CityInfo(val name: String, val lat: Double, val lng: Double, val images: List<String>)
}

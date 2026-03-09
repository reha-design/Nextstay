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
            CityInfo("제주", listOf(
                "https://images.unsplash.com/photo-1590424744257-fdb03ed78ae0",
                "https://images.unsplash.com/photo-1502444330042-d1a1ddf9bb5b",
                "https://images.unsplash.com/photo-1543734007-885743a1af6b",
                "https://images.unsplash.com/photo-1601614949506-6f81e18cd953",
                "https://images.unsplash.com/photo-1596394516093-501ba68a0ba6",
                "https://images.unsplash.com/photo-1621244249243-431fd01a91cf"
            )),
            CityInfo("서울", listOf(
                "https://images.unsplash.com/photo-1538481199705-c710c4e965fc",
                "https://images.unsplash.com/photo-1578130334547-41804e028bbd",
                "https://images.unsplash.com/photo-1510627581559-07f153a5925a",
                "https://images.unsplash.com/photo-1591113069116-29175f0a05a8",
                "https://images.unsplash.com/photo-1505705426101-da47ada33278",
                "https://images.unsplash.com/photo-1517154421773-0529f29ea451"
            )),
            CityInfo("부산", listOf(
                "https://images.unsplash.com/photo-1548115184-bc6544d06a58",
                "https://images.unsplash.com/photo-1616712134469-dc3989c645c7",
                "https://images.unsplash.com/photo-1620353164996-03f6f1c7d77a",
                "https://images.unsplash.com/photo-1579717614051-7892dfd62c3e",
                "https://images.unsplash.com/photo-1590664082210-fe390212ef84",
                "https://images.unsplash.com/photo-1563297607-e9108ed354c0"
            )),
            CityInfo("강원", listOf(
                "https://images.unsplash.com/photo-1520215024434-2e90f23f8599",
                "https://images.unsplash.com/photo-1501785888041-af3ef285b470",
                "https://images.unsplash.com/photo-1500382017468-9049fee74a62",
                "https://images.unsplash.com/photo-1470770841072-f978cf4d019e",
                "https://images.unsplash.com/photo-1593181629936-11c609b8db9b",
                "https://images.unsplash.com/photo-1542662565-7e4b66bae529"
            )),
            CityInfo("경주", listOf(
                "https://images.unsplash.com/photo-1505873242700-f289a29e1e0f",
                "https://images.unsplash.com/photo-1511116410292-0f5f84d339e3",
                "https://images.unsplash.com/photo-1528127269322-539801943a42",
                "https://images.unsplash.com/photo-1548114687-247570483424",
                "https://images.unsplash.com/photo-1522075469751-3a6694fb2f61",
                "https://images.unsplash.com/photo-1557991953-911854bb978d"
            ))
        )

        val categories = StayCategory.values()
        val random = Random()

        for (i in 1..30) {
            val cityInfo = citiesWithImages[i % citiesWithImages.size]
            val category = categories[random.nextInt(categories.size)]
            val stayNo = "s${System.currentTimeMillis() % 10000000}$i"
            
            val stay = Stay(
                stayNo = stayNo,
                host = host,
                name = "[${cityInfo.name}/${category.name.lowercase()}] 진정한 휴식과 힐링... 한달살기 숙소 ${i}호점",
                description = "넥스트스테이가 엄선한 ${cityInfo.name} 지역의 특별한 ${category.name.lowercase()}입니다. 편안한 휴식을 보장합니다.",
                address = "${cityInfo.name}특별시 ${cityInfo.name}구 ${cityInfo.name}동 ${i}번지",
                city = cityInfo.name,
                category = category,
                latitude = 33.0 + random.nextDouble() * 5.0,
                longitude = 126.0 + random.nextDouble() * 3.0
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

    private data class CityInfo(val name: String, val images: List<String>)
}

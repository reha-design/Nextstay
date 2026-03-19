package com.mrmention.nextstay.global.util

import com.fasterxml.uuid.Generators
import com.fasterxml.uuid.NoArgGenerator
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class IdGenerator {
    private val generator: NoArgGenerator = Generators.timeBasedEpochGenerator()

    /**
     * UUID v7 생성 (Time-ordered)
     */
    fun generate(): UUID = generator.generate()

    /**
     * UUID v7 문자열 생성
     */
    fun generateString(): String = generate().toString()
}

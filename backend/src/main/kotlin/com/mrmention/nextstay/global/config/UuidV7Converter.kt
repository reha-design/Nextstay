package com.mrmention.nextstay.global.config

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import java.nio.ByteBuffer
import java.util.UUID

@Converter(autoApply = true)
class UuidV7Converter : AttributeConverter<UUID?, ByteArray?> {

    override fun convertToDatabaseColumn(attribute: UUID?): ByteArray? {
        if (attribute == null) return null
        val bb = ByteBuffer.wrap(ByteArray(16))
        bb.putLong(attribute.mostSignificantBits)
        bb.putLong(attribute.leastSignificantBits)
        return bb.array()
    }

    override fun convertToEntityAttribute(dbData: ByteArray?): UUID? {
        if (dbData == null || dbData.size != 16) return null
        val bb = ByteBuffer.wrap(dbData)
        val high = bb.long
        val low = bb.long
        return UUID(high, low)
    }
}

package com.dertefter.design.common

import java.time.Instant
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder

object DateParser {
    private val formatter = DateTimeFormatterBuilder()
        .append(DateTimeFormatter.ISO_LOCAL_DATE)
        .appendOptional(DateTimeFormatterBuilder().appendLiteral('T').toFormatter())
        .appendOptional(DateTimeFormatterBuilder().appendLiteral(' ').toFormatter())
        .append(DateTimeFormatter.ISO_LOCAL_TIME)
        .appendOptional(
            DateTimeFormatterBuilder()
                .appendOffset("+HHmm", "Z")
                .toFormatter()
        )
        .appendOptional(
            DateTimeFormatterBuilder()
                .appendOffset("+HH", "Z")
                .toFormatter()
        )
        .toFormatter()

    fun parseToInstant(dateString: String?): Instant? {
        if (dateString == null) return null
        return try {
            Instant.parse(dateString)
        } catch (_: Exception) {
            try {
                OffsetDateTime.parse(dateString, formatter).toInstant()
            } catch (_: Exception) {
                try {
                    val normalized = dateString.replace(" ", "T")
                    OffsetDateTime.parse(normalized).toInstant()
                } catch (_: Exception) {
                    null
                }
            }
        }
    }
}

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
            // Try standard Instant.parse first (ISO-8601)
            Instant.parse(dateString)
        } catch (e: Exception) {
            try {
                // Try parsing with flexible formatter
                OffsetDateTime.parse(dateString, formatter).toInstant()
            } catch (e2: Exception) {
                // Fallback: replace space with T and try Instant.parse again
                try {
                    val normalized = dateString.replace(" ", "T")
                    // If it ends with +XX, append :00 to make it +XX:00 which Instant.parse often prefers if it's not using a formatter
                    // Actually OffsetDateTime should handle it.
                    OffsetDateTime.parse(normalized).toInstant()
                } catch (e3: Exception) {
                    null
                }
            }
        }
    }
}

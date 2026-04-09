package com.edutrack.core.presentation

import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Format a kotlin.time.Instant for display in the UI.
 * Converts to local date-time and returns "yyyy-MM-dd HH:mm" format.
 */
@OptIn(ExperimentalTime::class)
fun Instant.formatDisplay(): String {
    if (this == Instant.fromEpochMilliseconds(0)) return ""
    val kxInstant = kotlinx.datetime.Instant.fromEpochMilliseconds(this.toEpochMilliseconds())
    val local = kxInstant.toLocalDateTime(TimeZone.currentSystemDefault())
    val month = local.monthNumber.toString().padStart(2, '0')
    val day = local.dayOfMonth.toString().padStart(2, '0')
    val hour = local.hour.toString().padStart(2, '0')
    val minute = local.minute.toString().padStart(2, '0')
    return "${local.year}-$month-$day $hour:$minute"
}

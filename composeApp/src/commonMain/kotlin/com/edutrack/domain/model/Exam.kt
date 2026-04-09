package com.edutrack.domain.model

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
data class Exam(
    val id: String = "",
    val name: String = "",
    val subject: String = "",
    val dateTime: Instant = Instant.fromEpochMilliseconds(0),
    val description: String = "",
    val studyReference: String = ""
)

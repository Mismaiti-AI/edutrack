package com.edutrack.domain.model

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
data class Exam(
    val id: String = "",
    val title: String = "",
    val subject: String = "",
    val examDate: Instant = Instant.fromEpochMilliseconds(0),
    val topics: String = "",
    val description: String = ""
)

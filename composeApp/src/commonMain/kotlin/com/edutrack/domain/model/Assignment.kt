package com.edutrack.domain.model

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
data class Assignment(
    val id: String = "",
    val title: String = "",
    val subject: String = "",
    val dueDate: Instant = Instant.fromEpochMilliseconds(0),
    val instructions: String = ""
)

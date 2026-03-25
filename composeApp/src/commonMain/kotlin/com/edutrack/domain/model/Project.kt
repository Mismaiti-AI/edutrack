package com.edutrack.domain.model

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
data class Project(
    val id: String = "",
    val title: String = "",
    val subject: String = "",
    val startDate: Instant = Instant.fromEpochMilliseconds(0),
    val dueDate: Instant = Instant.fromEpochMilliseconds(0),
    val description: String = "",
    val milestones: String = "",
    val attachmentUrl: String = ""
)

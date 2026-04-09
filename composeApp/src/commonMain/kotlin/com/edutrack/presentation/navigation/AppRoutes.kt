package com.edutrack.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface AppRoutes {
    @Serializable
    data object Dashboard : AppRoutes

    @Serializable
    data object AssignmentList : AppRoutes

    @Serializable
    data class AssignmentDetail(val itemId: String) : AppRoutes

    @Serializable
    data object ExamList : AppRoutes

    @Serializable
    data class ExamDetail(val itemId: String) : AppRoutes

    @Serializable
    data object ProjectList : AppRoutes

    @Serializable
    data class ProjectDetail(val itemId: String) : AppRoutes

    @Serializable
    data object Settings : AppRoutes

    @Serializable
    data object Profile : AppRoutes

    @Serializable
    data object GSheetSetup : AppRoutes
}

package com.edutrack.domain.usecase

import com.edutrack.data.repository.assignment.AssignmentRepository
import com.edutrack.data.repository.exam.ExamRepository
import com.edutrack.data.repository.project.ProjectRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.MutableStateFlow

data class DashboardOverview(
    val assignments: Int = 0,
    val exams: Int = 0,
    val projects: Int = 0,
    val recentAssignments: List<com.edutrack.domain.model.Assignment> = emptyList(),
    val recentExams: List<com.edutrack.domain.model.Exam> = emptyList(),
    val recentProjects: List<com.edutrack.domain.model.Project> = emptyList(),
)

class GetDashboardOverviewUseCase(
    private val assignmentRepository: AssignmentRepository,
    private val examRepository: ExamRepository,
    private val projectRepository: ProjectRepository,
) {
    operator fun invoke(): StateFlow<DashboardOverview> = _overview

    private val _overview = MutableStateFlow(DashboardOverview())

    suspend fun load() {
        assignmentRepository.loadAll()
        examRepository.loadAll()
        projectRepository.loadAll()
        _overview.value = DashboardOverview(
            assignments = assignmentRepository.items.value.size,
            exams = examRepository.items.value.size,
            projects = projectRepository.items.value.size,
            recentAssignments = assignmentRepository.items.value.take(5),
        )
    }

    suspend fun refresh() { load() }
}

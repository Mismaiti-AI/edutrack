package com.edutrack.domain.usecase

import kotlinx.coroutines.flow.StateFlow
import com.edutrack.domain.model.Project
import com.edutrack.data.repository.project.ProjectRepository

class GetProjectDetailUseCase(
    private val repository: ProjectRepository
) {
    operator fun invoke(): StateFlow<Project?> = repository.selectedItem

    suspend fun load(id: String) {
        repository.selectItem(id)
    }
}

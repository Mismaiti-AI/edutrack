package com.edutrack.domain.usecase

import kotlinx.coroutines.flow.StateFlow
import com.edutrack.domain.model.Project
import com.edutrack.data.repository.project.ProjectRepository

class GetProjectListUseCase(
    private val repository: ProjectRepository
) {
    operator fun invoke(): StateFlow<List<Project>> = repository.items

    suspend fun load() {
        repository.loadAll()
    }

    suspend fun refresh() {
        repository.loadAll()
    }
}

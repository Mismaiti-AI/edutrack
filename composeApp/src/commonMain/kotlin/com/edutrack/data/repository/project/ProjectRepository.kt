package com.edutrack.data.repository.project

import kotlinx.coroutines.flow.StateFlow
import com.edutrack.domain.model.Project

interface ProjectRepository {
    val items: StateFlow<List<Project>>
    val isLoading: StateFlow<Boolean>
    val error: StateFlow<String?>
    val selectedItem: StateFlow<Project?>
    suspend fun loadAll()
    suspend fun getById(id: String): Project?
    suspend fun selectItem(id: String)
    suspend fun insert(item: Project)
    suspend fun update(item: Project)
    suspend fun delete(id: String)
}

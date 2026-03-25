package com.edutrack.data.repository.assignment

import kotlinx.coroutines.flow.StateFlow
import com.edutrack.domain.model.Assignment

interface AssignmentRepository {
    val items: StateFlow<List<Assignment>>
    val isLoading: StateFlow<Boolean>
    val error: StateFlow<String?>
    val selectedItem: StateFlow<Assignment?>
    suspend fun loadAll()
    suspend fun getById(id: String): Assignment?
    suspend fun selectItem(id: String)
    suspend fun insert(item: Assignment)
    suspend fun update(item: Assignment)
    suspend fun delete(id: String)
}

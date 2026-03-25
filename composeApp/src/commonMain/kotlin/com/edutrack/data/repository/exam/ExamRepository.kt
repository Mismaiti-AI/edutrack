package com.edutrack.data.repository.exam

import kotlinx.coroutines.flow.StateFlow
import com.edutrack.domain.model.Exam

interface ExamRepository {
    val items: StateFlow<List<Exam>>
    val isLoading: StateFlow<Boolean>
    val error: StateFlow<String?>
    val selectedItem: StateFlow<Exam?>
    suspend fun loadAll()
    suspend fun getById(id: String): Exam?
    suspend fun selectItem(id: String)
    suspend fun insert(item: Exam)
    suspend fun update(item: Exam)
    suspend fun delete(id: String)
}

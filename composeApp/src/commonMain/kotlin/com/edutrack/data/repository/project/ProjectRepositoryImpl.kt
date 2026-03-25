package com.edutrack.data.repository.project

import kotlinx.coroutines.flow.StateFlow
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import com.edutrack.core.data.gsheets.GoogleSheetsRepository
import com.edutrack.core.data.gsheets.GoogleSheetsService
import com.edutrack.core.data.gsheets.GoogleSheetsConfig
import com.edutrack.domain.model.Project

@OptIn(ExperimentalTime::class)
class ProjectRepositoryImpl(
    sheetsService: GoogleSheetsService,
    sheetsConfig: GoogleSheetsConfig
) : GoogleSheetsRepository<Project>(sheetsService, sheetsConfig),
    ProjectRepository {

    override val sheetTabName: String = "projects"

    private val _selectedItem = kotlinx.coroutines.flow.MutableStateFlow<Project?>(null)
    override val selectedItem: StateFlow<Project?> = _selectedItem

    override fun mapRow(row: Map<String, String>): Project? {
        val id = row["id"] ?: return null
        return Project(
            id = id,
            title = row["title"] ?: "",
            subject = row["subject"] ?: "",
            startDate = row["startDate"]?.let { parseDateToInstant(it) } ?: Instant.fromEpochMilliseconds(0),
            dueDate = row["dueDate"]?.let { parseDateToInstant(it) } ?: Instant.fromEpochMilliseconds(0),
            description = row["description"] ?: "",
            milestones = row["milestones"] ?: "",
            attachmentUrl = row["attachmentUrl"] ?: "",
        )
    }

    override suspend fun loadAll() {
        loadFromSheet()
    }

    override suspend fun getById(id: String): Project? {
        return items.value.find { it.id == id }
    }

    override suspend fun selectItem(id: String) {
        _selectedItem.value = getById(id)
    }

    override suspend fun insert(item: Project) {
        // Read-only mode; refresh data from sheet
        refresh()
    }

    override suspend fun update(item: Project) {
        // Read-only mode; refresh data from sheet
        refresh()
    }

    override suspend fun delete(id: String) {
        // Read-only mode; refresh data from sheet
        refresh()
    }
}

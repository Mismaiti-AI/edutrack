package com.edutrack.data.repository.assignment

import kotlinx.coroutines.flow.StateFlow
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import com.edutrack.core.data.gsheets.GoogleSheetsRepository
import com.edutrack.core.data.gsheets.GoogleSheetsService
import com.edutrack.core.data.gsheets.GoogleSheetsConfig
import com.edutrack.domain.model.Assignment

@OptIn(ExperimentalTime::class)
class AssignmentRepositoryImpl(
    sheetsService: GoogleSheetsService,
    sheetsConfig: GoogleSheetsConfig
) : GoogleSheetsRepository<Assignment>(sheetsService, sheetsConfig),
    AssignmentRepository {

    override val sheetTabName: String = "assignments"

    private val _selectedItem = kotlinx.coroutines.flow.MutableStateFlow<Assignment?>(null)
    override val selectedItem: StateFlow<Assignment?> = _selectedItem

    override fun mapRow(row: Map<String, String>): Assignment? {
        val id = row["id"] ?: return null
        return Assignment(
            id = id,
            title = row["title"] ?: "",
            subject = row["subject"] ?: "",
            dueDate = row["dueDate"]?.let { parseDateToInstant(it) } ?: Instant.fromEpochMilliseconds(0),
            description = row["description"] ?: "",
            attachmentUrl = row["attachmentUrl"] ?: "",
        )
    }

    override suspend fun loadAll() {
        loadFromSheet()
    }

    override suspend fun getById(id: String): Assignment? {
        return items.value.find { it.id == id }
    }

    override suspend fun selectItem(id: String) {
        _selectedItem.value = getById(id)
    }

    override suspend fun insert(item: Assignment) {
        // Read-only mode; refresh data from sheet
        refresh()
    }

    override suspend fun update(item: Assignment) {
        // Read-only mode; refresh data from sheet
        refresh()
    }

    override suspend fun delete(id: String) {
        // Read-only mode; refresh data from sheet
        refresh()
    }
}

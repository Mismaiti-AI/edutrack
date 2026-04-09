package com.edutrack.data.repository.exam

import kotlinx.coroutines.flow.StateFlow
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import com.edutrack.core.data.gsheets.GoogleSheetsRepository
import com.edutrack.core.data.gsheets.GoogleSheetsService
import com.edutrack.core.data.gsheets.GoogleSheetsConfig
import com.edutrack.domain.model.Exam

@OptIn(ExperimentalTime::class)
class ExamRepositoryImpl(
    sheetsService: GoogleSheetsService,
    sheetsConfig: GoogleSheetsConfig
) : GoogleSheetsRepository<Exam>(sheetsService, sheetsConfig),
    ExamRepository {

    override val sheetTabName: String = "exams"

    private val _selectedItem = kotlinx.coroutines.flow.MutableStateFlow<Exam?>(null)
    override val selectedItem: StateFlow<Exam?> = _selectedItem

    override fun mapRow(row: Map<String, String>): Exam? {
        val id = row["id"] ?: return null
        return Exam(
            id = id,
            name = row["name"] ?: "",
            subject = row["subject"] ?: "",
            dateTime = row["dateTime"]?.let { parseDateToInstant(it) } ?: Instant.fromEpochMilliseconds(0),
            description = row["description"] ?: "",
            studyReference = row["studyReference"] ?: "",
        )
    }

    override suspend fun loadAll() {
        loadFromSheet()
    }

    override suspend fun getById(id: String): Exam? {
        return items.value.find { it.id == id }
    }

    override suspend fun selectItem(id: String) {
        _selectedItem.value = getById(id)
    }

    override suspend fun insert(item: Exam) {
        // Read-only mode; refresh data from sheet
        refresh()
    }

    override suspend fun update(item: Exam) {
        // Read-only mode; refresh data from sheet
        refresh()
    }

    override suspend fun delete(id: String) {
        // Read-only mode; refresh data from sheet
        refresh()
    }
}

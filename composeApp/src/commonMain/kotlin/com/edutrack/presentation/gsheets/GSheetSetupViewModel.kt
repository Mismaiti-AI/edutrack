package com.edutrack.presentation.gsheets

import com.edutrack.core.presentation.BaseViewModel
import com.edutrack.core.data.gsheets.GoogleSheetsConfig
import com.edutrack.core.data.gsheets.GoogleSheetsService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class GSheetSetupUiState(
    val sheetUrl: String = "",
    val isSaving: Boolean = false,
    val isConfigured: Boolean = false,
    val errorMessage: String? = null,
)

class GSheetSetupViewModel(
    private val sheetsConfig: GoogleSheetsConfig,
    private val sheetsService: GoogleSheetsService,
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(GSheetSetupUiState())
    val uiState: StateFlow<GSheetSetupUiState> = _uiState.asStateFlow()

    init {
        checkExistingConfig()
    }

    private fun checkExistingConfig() {
        if (sheetsConfig.isConfigured) {
            _uiState.update { it.copy(
                isConfigured = true,
                sheetUrl = sheetsConfig.defaultSheetId ?: "",
            ) }
        }
    }

    fun onSheetUrlChanged(url: String) {
        _uiState.update { it.copy(sheetUrl = url, errorMessage = null) }
    }

    fun onSaveClicked() {
        val input = _uiState.value.sheetUrl.trim()
        if (input.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter a Google Sheet URL or ID") }
            return
        }

        // Validate format
        val sheetId = resolveSheetId(input)
        if (sheetId == null) {
            _uiState.update { it.copy(errorMessage = "Invalid Google Sheet URL. Please paste a valid link or sheet ID.") }
            return
        }

        _uiState.update { it.copy(isSaving = true, errorMessage = null) }

        // Validate sheet structure via Apps Script then save
        safeLaunch {
            try {
                val scriptUrl = sheetsConfig.scriptUrl
                if (scriptUrl.isBlank()) {
                    _uiState.update { it.copy(
                        isSaving = false,
                        errorMessage = "Apps Script URL is not configured. Please contact the app developer.",
                    ) }
                    return@safeLaunch
                }

                val validationError = validateSheetStructure(scriptUrl, sheetId)
                if (validationError != null) {
                    _uiState.update { it.copy(isSaving = false, errorMessage = validationError) }
                    return@safeLaunch
                }

                sheetsConfig.setDefaultSheetId(sheetId)
                _uiState.update { it.copy(isSaving = false, isConfigured = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isSaving = false,
                    errorMessage = e.message ?: "Failed to validate sheet. Please try again.",
                ) }
            }
        }
    }

    /**
     * Validate that the sheet has the expected tabs and columns.
     * Returns an error message if validation fails, null if valid.
     */
    private suspend fun validateSheetStructure(scriptUrl: String, sheetId: String): String? {
        for ((tabName, requiredColumns) in EXPECTED_SCHEMA) {
            val rows = try {
                sheetsService.getAll(scriptUrl, sheetId, tabName)
            } catch (e: Exception) {
                return "Could not find tab \"$tabName\" in the spreadsheet. " +
                    "Make sure your sheet has a tab named \"$tabName\"."
            }

            if (rows.isEmpty()) {
                // Empty tab is OK — columns can't be verified without data,
                // but the tab exists (no exception was thrown)
                continue
            }

            // Check that expected columns exist in the first row's keys
            val actualColumns = rows.first().keys
            val missingColumns = requiredColumns.filter { it !in actualColumns }
            if (missingColumns.isNotEmpty()) {
                return "Tab \"$tabName\" is missing columns: ${missingColumns.joinToString(", ")}. " +
                    "Expected: ${requiredColumns.joinToString(", ")}."
            }
        }
        return null
    }

    /**
     * Resolve user input to a clean sheet ID.
     * Returns null if input looks like a URL but can't be parsed.
     */
    private fun resolveSheetId(input: String): String? {
        val looksLikeUrl = input.contains("/") || input.contains("google.com")
        if (looksLikeUrl) {
            return GoogleSheetsConfig.extractSheetId(input)
        }
        val idPattern = Regex("^[a-zA-Z0-9_-]{10,}$")
        return if (idPattern.matches(input)) input else null
    }

    companion object {
        /**
         * Expected sheet structure: tab name → list of required column headers.
         * Generated from the app's data models at build time.
         */
        private val EXPECTED_SCHEMA = mapOf(
            "assignments" to listOf("id", "title", "subject", "dueDate", "instructions"),
            "exams" to listOf("id", "name", "subject", "dateTime", "description", "studyReference"),
            "projects" to listOf("id", "title", "subject", "dueDate", "description", "teamMembers"),
        )
    }
}

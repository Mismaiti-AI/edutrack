package com.edutrack.core.data.gsheets

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.ExperimentalTime

/**
 * Pre-built abstract base for repositories backed by Google Sheets via the user's Apps Script.
 *
 * Provides:
 * - Thread-safe data loading with Mutex
 * - StateFlow-based state management (items, isLoading, error)
 * - Row → domain model mapping via abstract [mapRow]
 * - Optional write operations via [toMap] + [insertItem]/[updateItem]/[deleteItem]
 * - Multi-sheet support: each repo resolves its sheet ID via [GoogleSheetsConfig.getSheetId]
 * - Graceful error handling (invalid rows skipped, never crashes)
 *
 * Subclass example (read-only):
 * ```kotlin
 * class EventRepository(
 *     sheetsService: GoogleSheetsService,
 *     sheetsConfig: GoogleSheetsConfig
 * ) : GoogleSheetsRepository<Event>(sheetsService, sheetsConfig) {
 *     override val sheetTabName = "Events"
 *     override fun mapRow(row: Map<String, String>): Event? {
 *         return Event(
 *             id = row["id"] ?: return null,
 *             title = row["title"] ?: ""
 *         )
 *     }
 * }
 * ```
 *
 * Subclass example (full CRUD):
 * ```kotlin
 * class EventRepository(...) : GoogleSheetsRepository<Event>(...) {
 *     override val sheetTabName = "Events"
 *     override fun mapRow(row: Map<String, String>): Event? { ... }
 *     override fun toMap(item: Event): Map<String, String> = mapOf(
 *         "id" to item.id,
 *         "title" to item.title
 *     )
 * }
 * ```
 */
@OptIn(ExperimentalTime::class)
abstract class GoogleSheetsRepository<T>(
    private val sheetsService: GoogleSheetsService,
    private val sheetsConfig: GoogleSheetsConfig
) {
    private val mutex = Mutex()

    private val _items = MutableStateFlow<List<T>>(emptyList())
    val items: StateFlow<List<T>> = _items

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    /**
     * The Google Sheets tab name to fetch (e.g., "Products", "Events").
     */
    abstract val sheetTabName: String

    /**
     * Map a single row (column → value) to a domain model.
     * Return null to skip invalid/incomplete rows.
     */
    abstract fun mapRow(row: Map<String, String>): T?

    /**
     * Convert a domain model to a flat map for write operations.
     * Override this to enable [insertItem], [updateItem], [deleteItem].
     * Default throws UnsupportedOperationException (read-only).
     */
    open fun toMap(item: T): Map<String, String> =
        throw UnsupportedOperationException("Write not supported for $sheetTabName")

    // =========================================================================
    // READ OPERATIONS
    // =========================================================================

    /**
     * Fetch data from Google Sheets via Apps Script, parse, and update state.
     */
    suspend fun loadFromSheet() {
        val scriptUrl = sheetsConfig.scriptUrl
        val sheetId = sheetsConfig.getSheetId(sheetTabName)

        if (scriptUrl.isBlank()) {
            _error.value = "No Apps Script URL configured"
            return
        }
        if (sheetId.isNullOrBlank()) {
            _error.value = "No sheet ID configured for $sheetTabName"
            return
        }

        mutex.withLock {
            try {
                _isLoading.value = true
                _error.value = null

                val rows = sheetsService.getAll(scriptUrl, sheetId, sheetTabName)
                val mapped = rows.mapNotNull { row ->
                    try {
                        mapRow(row)
                    } catch (_: Exception) {
                        null // Skip invalid rows
                    }
                }

                _items.value = mapped
                onDataLoaded(mapped)
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load $sheetTabName from Google Sheets"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Refresh data (alias for loadFromSheet).
     */
    suspend fun refresh() = loadFromSheet()

    // =========================================================================
    // WRITE OPERATIONS
    // =========================================================================

    /**
     * Insert a new item into the sheet. Requires [toMap] to be overridden.
     */
    open suspend fun insertItem(item: T) {
        val scriptUrl = sheetsConfig.scriptUrl
        val sheetId = sheetsConfig.getSheetId(sheetTabName) ?: return
        sheetsService.insert(scriptUrl, sheetId, sheetTabName, toMap(item))
        loadFromSheet()
    }

    /**
     * Update an existing item in the sheet. Requires [toMap] to be overridden.
     */
    open suspend fun updateItem(item: T, id: String) {
        val scriptUrl = sheetsConfig.scriptUrl
        val sheetId = sheetsConfig.getSheetId(sheetTabName) ?: return
        sheetsService.update(scriptUrl, sheetId, sheetTabName, id, toMap(item))
        loadFromSheet()
    }

    /**
     * Delete an item from the sheet by ID.
     */
    open suspend fun deleteItem(id: String) {
        val scriptUrl = sheetsConfig.scriptUrl
        val sheetId = sheetsConfig.getSheetId(sheetTabName) ?: return
        sheetsService.delete(scriptUrl, sheetId, sheetTabName, id)
        loadFromSheet()
    }

    // =========================================================================
    // HOOKS
    // =========================================================================

    /**
     * Override to persist loaded data to Room or perform post-load actions.
     * Default implementation does nothing.
     */
    protected open suspend fun onDataLoaded(items: List<T>) {}

    // =========================================================================
    // UTILITIES
    // =========================================================================

    /**
     * Helper: parse a date string to kotlin.time.Instant.
     * Supports: yyyy-MM-dd, yyyy/MM/dd, dd/MM/yyyy, MM/dd/yyyy.
     * Returns null if unparseable.
     */
    protected fun parseDateToInstant(dateString: String): kotlin.time.Instant? {
        val millis = sheetsService.parseDateToEpochMillis(dateString) ?: return null
        return kotlin.time.Instant.fromEpochMilliseconds(millis)
    }

    /**
     * Helper: parse a boolean string ("true", "yes", "1" → true).
     */
    protected fun parseBoolean(value: String?): Boolean {
        return value?.trim()?.lowercase() in listOf("true", "yes", "1")
    }
}

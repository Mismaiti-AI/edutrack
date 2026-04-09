package com.edutrack.core.data.gsheets

import com.edutrack.core.data.local.AppSettings

/**
 * Pre-built configuration for Google Sheets integration.
 *
 * Stores the Apps Script URL (build-time constant) and spreadsheet IDs
 * (configurable at runtime) in AppSettings.
 *
 * Supports:
 * - **Hardcoded**: Set [DEFAULT_SHEET_ID] and the app uses it immediately
 * - **Configurable**: User enters sheet ID (or pastes URL) at runtime
 * - **Multi-sheet**: Different models can use different spreadsheets via overrides
 *
 * Usage in repository:
 * ```kotlin
 * class MyRepository(
 *     sheetsService: GoogleSheetsService,
 *     sheetsConfig: GoogleSheetsConfig
 * ) : GoogleSheetsRepository<MyModel>(sheetsService, sheetsConfig) {
 *     override val sheetTabName = "Products"
 *     // getSheetId(sheetTabName) resolves override or default automatically
 * }
 * ```
 */
class GoogleSheetsConfig(private val settings: AppSettings) {

    /**
     * The Apps Script web app URL. Set at build time, never changes.
     */
    val scriptUrl: String get() = SCRIPT_URL

    /**
     * The default spreadsheet ID. Used by all models unless overridden.
     * Returns stored ID, falls back to [DEFAULT_SHEET_ID], or null.
     */
    val defaultSheetId: String?
        get() {
            val stored = settings.getString(KEY_DEFAULT_SHEET_ID, "")
            return stored.ifBlank { DEFAULT_SHEET_ID.ifBlank { null } }
        }

    /**
     * Whether the config is ready (script URL and at least a default sheet ID).
     */
    val isConfigured: Boolean
        get() = scriptUrl.isNotBlank() && !defaultSheetId.isNullOrBlank()

    /**
     * Get the sheet ID for a specific collection/tab name.
     * Checks overrides first, then falls back to [defaultSheetId].
     */
    fun getSheetId(collectionName: String): String? {
        val overrideKey = "$KEY_OVERRIDE_PREFIX$collectionName"
        val override = settings.getString(overrideKey, "")
        return override.ifBlank { null } ?: defaultSheetId
    }

    /**
     * Set the default spreadsheet ID (applies to all models without overrides).
     * Accepts either a raw sheet ID or a full Google Sheets URL.
     */
    fun setDefaultSheetId(idOrUrl: String) {
        val id = extractSheetId(idOrUrl) ?: idOrUrl.trim()
        settings.putString(KEY_DEFAULT_SHEET_ID, id)
    }

    /**
     * Set a sheet ID override for a specific collection/tab name.
     * This model will use a different spreadsheet than the default.
     */
    fun setSheetIdOverride(collectionName: String, idOrUrl: String) {
        val id = extractSheetId(idOrUrl) ?: idOrUrl.trim()
        settings.putString("$KEY_OVERRIDE_PREFIX$collectionName", id)
    }

    /**
     * Remove a sheet ID override (model reverts to default).
     */
    fun removeSheetIdOverride(collectionName: String) {
        settings.remove("$KEY_OVERRIDE_PREFIX$collectionName")
    }

    /**
     * Clear the default sheet ID (reverts to [DEFAULT_SHEET_ID] if set).
     */
    fun clearDefaultSheetId() {
        settings.remove(KEY_DEFAULT_SHEET_ID)
    }

    companion object {
        private const val KEY_DEFAULT_SHEET_ID = "gsheets_default_sheet_id"
        private const val KEY_OVERRIDE_PREFIX = "gsheets_override_"

        /**
         * Apps Script web app URL. Set by code generation from the user's deployment.
         * The script is deployed under the user's Google account and can access
         * any of their spreadsheets via sheetId parameter.
         */
        const val SCRIPT_URL = ""

        /**
         * Default spreadsheet ID for hardcoded mode.
         * Leave blank for configurable mode (user enters ID at runtime).
         */
        const val DEFAULT_SHEET_ID = ""

        /**
         * Extract spreadsheet ID from any Google Sheets URL.
         * Supports edit, pubhtml, and sharing link formats.
         * Returns null if the input is not a recognizable URL (assumes raw ID).
         */
        fun extractSheetId(input: String): String? {
            val trimmed = input.trim()
            // If it doesn't look like a URL, return null (caller uses as-is)
            if (!trimmed.contains("google.com") && !trimmed.contains("/")) return null

            val patterns = listOf(
                Regex("""/spreadsheets/d/([a-zA-Z0-9-_]+)"""),
                Regex("""id=([a-zA-Z0-9-_]+)"""),
                Regex("""d/([a-zA-Z0-9-_]+)/""")
            )
            for (pattern in patterns) {
                val match = pattern.find(trimmed)
                if (match != null) return match.groupValues[1]
            }
            return null
        }
    }
}

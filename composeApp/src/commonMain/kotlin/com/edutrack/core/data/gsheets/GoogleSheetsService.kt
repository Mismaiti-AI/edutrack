package com.edutrack.core.data.gsheets

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Pre-built service for communicating with the user's standalone Apps Script.
 *
 * All operations go through the user's Apps Script URL via POST requests.
 * The script is deployed under the user's Google account during OAuth setup.
 * Each request includes a `sheetId` to identify the target spreadsheet
 * and a `sheet` (tab name) to identify the target tab within it.
 *
 * Supports:
 * - Read operations: GET (list with optional filters), GET_BY_ID
 * - Write operations: INSERT, UPDATE, DELETE
 * - Admin operations: SETUP (create tabs/columns), GET_SCHEMA, PING
 * - Robust date parsing for multiple formats
 *
 * Usage:
 * ```kotlin
 * val rows = service.getAll(scriptUrl, sheetId, "Products")
 * val name = rows[0]["name"] // column-based access
 *
 * service.insert(scriptUrl, sheetId, "Products", mapOf("name" to "Widget", "price" to "9.99"))
 * ```
 */
class GoogleSheetsService(private val httpClient: HttpClient) {

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    // =========================================================================
    // READ OPERATIONS
    // =========================================================================

    /**
     * Fetch all rows from a sheet tab, optionally filtered.
     *
     * @param scriptUrl The Apps Script web app URL
     * @param sheetId The spreadsheet ID
     * @param sheetName The tab name (e.g., "Products", "Events")
     * @param filters Optional column→value filters (case-insensitive contains match)
     * @return List of row maps (column header → cell value as String)
     */
    suspend fun getAll(
        scriptUrl: String,
        sheetId: String,
        sheetName: String,
        filters: Map<String, String>? = null
    ): List<Map<String, String>> {
        val payload = buildMap<String, JsonElement> {
            put("action", JsonPrimitive("GET"))
            put("sheetId", JsonPrimitive(sheetId))
            put("sheet", JsonPrimitive(sheetName))
            if (!filters.isNullOrEmpty()) {
                put("filters", JsonObject(filters.mapValues { JsonPrimitive(it.value) }))
            }
        }

        val response = postToScript(scriptUrl, JsonObject(payload))

        // GET returns an array of row objects directly (not wrapped in {success, data})
        return when (response) {
            is JsonArray -> response.map { element ->
                element.jsonObject.entries.associate { (k, v) ->
                    k to jsonElementToString(v)
                }
            }
            is JsonObject -> {
                // Error response: {success: false, error: "..."}
                if (response["success"]?.jsonPrimitive?.booleanOrNull == false) {
                    throw IllegalStateException(
                        response["error"]?.jsonPrimitive?.content ?: "GET failed"
                    )
                }
                emptyList()
            }
            else -> emptyList()
        }
    }

    /**
     * Fetch a single row by ID.
     *
     * @return Row map, or null if not found
     */
    suspend fun getById(
        scriptUrl: String,
        sheetId: String,
        sheetName: String,
        id: String
    ): Map<String, String>? {
        val payload = JsonObject(mapOf(
            "action" to JsonPrimitive("GET_BY_ID"),
            "sheetId" to JsonPrimitive(sheetId),
            "sheet" to JsonPrimitive(sheetName),
            "id" to JsonPrimitive(id)
        ))

        val response = postToScript(scriptUrl, payload)

        return when (response) {
            is JsonObject -> {
                if (response["success"]?.jsonPrimitive?.booleanOrNull == false) return null
                // GET_BY_ID returns the row object directly, or null
                if (response.containsKey("id")) {
                    response.entries.associate { (k, v) -> k to jsonElementToString(v) }
                } else {
                    null
                }
            }
            is JsonNull -> null
            else -> null
        }
    }

    // =========================================================================
    // WRITE OPERATIONS
    // =========================================================================

    /**
     * Insert a new row. Auto-generates `id` and `createdAt` if not provided.
     *
     * @return The ID of the created row
     */
    suspend fun insert(
        scriptUrl: String,
        sheetId: String,
        sheetName: String,
        data: Map<String, String>
    ): String {
        val payload = JsonObject(mapOf(
            "action" to JsonPrimitive("INSERT"),
            "sheetId" to JsonPrimitive(sheetId),
            "sheet" to JsonPrimitive(sheetName),
            "data" to JsonObject(data.mapValues { JsonPrimitive(it.value) })
        ))

        val response = postToScript(scriptUrl, payload)
        val result = response.jsonObject
        if (result["success"]?.jsonPrimitive?.booleanOrNull != true) {
            throw IllegalStateException(
                result["error"]?.jsonPrimitive?.content ?: "INSERT failed"
            )
        }
        return result["id"]?.jsonPrimitive?.content ?: ""
    }

    /**
     * Update an existing row by ID. Only provided fields are updated.
     */
    suspend fun update(
        scriptUrl: String,
        sheetId: String,
        sheetName: String,
        id: String,
        data: Map<String, String>
    ) {
        val payload = JsonObject(mapOf(
            "action" to JsonPrimitive("UPDATE"),
            "sheetId" to JsonPrimitive(sheetId),
            "sheet" to JsonPrimitive(sheetName),
            "id" to JsonPrimitive(id),
            "data" to JsonObject(data.mapValues { JsonPrimitive(it.value) })
        ))

        val response = postToScript(scriptUrl, payload)
        val result = response.jsonObject
        if (result["success"]?.jsonPrimitive?.booleanOrNull != true) {
            throw IllegalStateException(
                result["error"]?.jsonPrimitive?.content ?: "UPDATE failed"
            )
        }
    }

    /**
     * Delete a row by ID.
     */
    suspend fun delete(
        scriptUrl: String,
        sheetId: String,
        sheetName: String,
        id: String
    ) {
        val payload = JsonObject(mapOf(
            "action" to JsonPrimitive("DELETE"),
            "sheetId" to JsonPrimitive(sheetId),
            "sheet" to JsonPrimitive(sheetName),
            "id" to JsonPrimitive(id)
        ))

        val response = postToScript(scriptUrl, payload)
        val result = response.jsonObject
        if (result["success"]?.jsonPrimitive?.booleanOrNull != true) {
            throw IllegalStateException(
                result["error"]?.jsonPrimitive?.content ?: "DELETE failed"
            )
        }
    }

    // =========================================================================
    // ADMIN OPERATIONS
    // =========================================================================

    /**
     * Ping the script to verify it's running.
     */
    suspend fun ping(scriptUrl: String): Boolean {
        val payload = JsonObject(mapOf(
            "action" to JsonPrimitive("PING")
        ))
        return try {
            val response = postToScript(scriptUrl, payload)
            response.jsonObject["success"]?.jsonPrimitive?.booleanOrNull == true
        } catch (_: Exception) {
            false
        }
    }

    // =========================================================================
    // DATE UTILITIES
    // =========================================================================

    /**
     * Parse a date string from Google Sheets into epoch milliseconds.
     * Supports common formats: yyyy-MM-dd, yyyy/MM/dd, dd/MM/yyyy, MM/dd/yyyy.
     *
     * @return Epoch milliseconds, or null if unparseable
     */
    fun parseDateToEpochMillis(dateString: String): Long? {
        if (dateString.isBlank()) return null

        val trimmed = dateString.trim()
        val parts = when {
            trimmed.contains("-") -> trimmed.split("-")
            trimmed.contains("/") -> trimmed.split("/")
            else -> return null
        }
        if (parts.size != 3) return null

        val candidates = listOf(
            Triple(parts[0], parts[1], parts[2]), // yyyy-MM-dd or yyyy/MM/dd
            Triple(parts[2], parts[1], parts[0]), // dd/MM/yyyy
            Triple(parts[2], parts[0], parts[1])  // MM/dd/yyyy
        )

        for ((yStr, mStr, dStr) in candidates) {
            val y = yStr.toIntOrNull() ?: continue
            val m = mStr.toIntOrNull() ?: continue
            val d = dStr.toIntOrNull() ?: continue
            if (y < 1970 || y > 2100 || m !in 1..12 || d !in 1..31) continue

            val daysFromEpoch = daysSinceEpoch(y, m, d)
            return daysFromEpoch * MILLIS_PER_DAY
        }
        return null
    }

    // =========================================================================
    // INTERNAL HELPERS
    // =========================================================================

    private suspend fun postToScript(scriptUrl: String, payload: JsonObject): JsonElement {
        val responseText = httpClient.post(scriptUrl) {
            contentType(ContentType.Application.Json)
            setBody(json.encodeToString(JsonObject.serializer(), payload))
        }.bodyAsText()

        if (responseText.isBlank()) {
            throw IllegalStateException("Empty response from Apps Script")
        }
        if (responseText.trimStart().startsWith("<!DOCTYPE", ignoreCase = true) ||
            responseText.trimStart().startsWith("<html", ignoreCase = true)
        ) {
            throw IllegalStateException("Received HTML response instead of JSON from Apps Script")
        }

        return json.parseToJsonElement(responseText)
    }

    private fun jsonElementToString(element: JsonElement): String = when (element) {
        is JsonPrimitive -> element.content
        is JsonNull -> ""
        else -> element.toString()
    }

    /**
     * Calculate days since 1970-01-01 for a given date.
     * Pure Kotlin — no JVM APIs.
     */
    private fun daysSinceEpoch(year: Int, month: Int, day: Int): Long {
        val daysInMonth = intArrayOf(0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        fun isLeapYear(y: Int) = (y % 4 == 0 && y % 100 != 0) || (y % 400 == 0)

        var totalDays = 0L
        for (y in 1970 until year) {
            totalDays += if (isLeapYear(y)) 366 else 365
        }
        for (m in 1 until month) {
            totalDays += daysInMonth[m]
            if (m == 2 && isLeapYear(year)) totalDays++
        }
        totalDays += day - 1
        return totalDays
    }

    companion object {
        private const val MILLIS_PER_DAY = 86_400_000L
    }
}

package com.edutrack.presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.edutrack.presentation.theme.LocalThemeIsDark
import com.edutrack.ui.atoms.*
import com.edutrack.core.data.gsheets.GoogleSheetsConfig
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Spacer(Modifier.height(8.dp))

            // General section
            Text(
                "General",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            AppCard {
                Column(modifier = Modifier.padding(4.dp)) {
                    var isDark by LocalThemeIsDark.current
                    AppListTile(
                        title = "Dark Mode",
                        subtitle = if (isDark) "On" else "Off",
                        leadingContent = {
                            Icon(
                                if (isDark) Icons.Default.DarkMode else Icons.Default.LightMode,
                                null,
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        },
                        trailingContent = {
                            Switch(
                                checked = isDark,
                                onCheckedChange = { isDark = it },
                            )
                        },
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                    AppListTile(
                        title = "Notifications",
                        subtitle = "Manage notification preferences",
                        leadingContent = { Icon(Icons.Default.Notifications, null, tint = MaterialTheme.colorScheme.primary) },
                        onClick = { },
                    )
                }
            }

            // Data Source section
            Text(
                "Data Source",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            AppCard {
                val sheetsConfig = koinInject<GoogleSheetsConfig>()
                var showEditDialog by remember { mutableStateOf(false) }
                var editUrl by remember { mutableStateOf("") }
                var editError by remember { mutableStateOf<String?>(null) }
                var currentId by remember { mutableStateOf(sheetsConfig.defaultSheetId ?: "Not configured") }

                Column(modifier = Modifier.padding(4.dp)) {
                    AppListTile(
                        title = "Google Sheet",
                        subtitle = if (currentId.length > 30) currentId.take(30) + "..." else currentId,
                        leadingContent = {
                            Icon(Icons.Default.TableChart, null, tint = MaterialTheme.colorScheme.primary)
                        },
                        onClick = {
                            editUrl = sheetsConfig.defaultSheetId ?: ""
                            editError = null
                            showEditDialog = true
                        },
                    )
                }

                if (showEditDialog) {
                    AlertDialog(
                        onDismissRequest = { showEditDialog = false },
                        title = { Text("Change Data Source") },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = editUrl,
                                    onValueChange = { editUrl = it; editError = null },
                                    label = { Text("Google Sheet URL or ID") },
                                    singleLine = true,
                                    isError = editError != null,
                                    supportingText = editError?.let { msg ->
                                        { Text(msg, color = MaterialTheme.colorScheme.error) }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                )
                                Text(
                                    "Restart the app after changing the data source to load new data.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    val trimmed = editUrl.trim()
                                    if (trimmed.isBlank()) {
                                        editError = "Please enter a URL or ID"
                                        return@TextButton
                                    }
                                    val looksLikeUrl = trimmed.contains("/") || trimmed.contains("google.com")
                                    if (looksLikeUrl) {
                                        val extracted = GoogleSheetsConfig.extractSheetId(trimmed)
                                        if (extracted == null) {
                                            editError = "Invalid Google Sheet URL"
                                            return@TextButton
                                        }
                                    } else {
                                        val idPattern = Regex("^[a-zA-Z0-9_-]{10,}$")
                                        if (!idPattern.matches(trimmed)) {
                                            editError = "Invalid sheet ID format"
                                            return@TextButton
                                        }
                                    }
                                    sheetsConfig.setDefaultSheetId(trimmed)
                                    currentId = sheetsConfig.defaultSheetId ?: trimmed
                                    showEditDialog = false
                                }
                            ) { Text("Save") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showEditDialog = false }) { Text("Cancel") }
                        },
                    )
                }
            }

            // About section
            Text(
                "About",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            AppCard {
                Column(modifier = Modifier.padding(4.dp)) {
                    AppListTile(
                        title = "App Version",
                        trailingContent = {
                            Text("1.0.0", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        },
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                    AppListTile(
                        title = "About EduTrack",
                        subtitle = "Terms, privacy & licenses",
                        leadingContent = { Icon(Icons.Default.Info, null, tint = MaterialTheme.colorScheme.primary) },
                        onClick = { },
                    )
                }
            }

        }
    }
}

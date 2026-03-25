package com.edutrack.presentation.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import com.edutrack.core.presentation.screens.GenericSettingsScreen
import com.edutrack.core.presentation.screens.SettingsSection
import com.edutrack.core.presentation.screens.SettingsItem

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
) {
    GenericSettingsScreen(
        title = "Settings",
        onBackClick = onBackClick,
        sections = listOf(
            SettingsSection(
                title = "General",
                items = listOf(
                    SettingsItem.Navigation(
                        title = "Notifications",
                        subtitle = "Manage notification preferences",
                        icon = Icons.Default.Notifications,
                        onClick = {}
                    ),
                )
            ),
            SettingsSection(
                title = "About",
                items = listOf(
                    SettingsItem.Info(
                        title = "App Version",
                        value = "1.0.0"
                    ),
                    SettingsItem.Navigation(
                        title = "About EduTrack",
                        subtitle = "Terms, privacy & licenses",
                        icon = Icons.Default.Info,
                        onClick = {}
                    ),
                )
            ),
            SettingsSection(
                title = "Account",
                items = listOf(
                    SettingsItem.Navigation(
                        title = "Log Out",
                        icon = Icons.Default.Lock,
                        onClick = onLogoutClick
                    ),
                )
            ),
        )
    )
}

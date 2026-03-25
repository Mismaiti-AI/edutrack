package com.edutrack.presentation.profile

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import com.edutrack.core.presentation.screens.GenericProfileScreen
import com.edutrack.core.presentation.screens.ProfileMenuSection
import com.edutrack.core.presentation.screens.ProfileMenuItem
import com.edutrack.core.presentation.screens.ProfileStat

@Composable
fun ProfileScreen(
    userName: String = "User",
    userEmail: String = "",
    onBackClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
) {
    GenericProfileScreen(
        name = userName,
        subtitle = userEmail,
        avatarText = userName.take(2).uppercase(),
        showBack = false,
        stats = listOf(
            ProfileStat(label = "Assignment Tracker", value = "0"),
            ProfileStat(label = "Exam Planner", value = "0"),
            ProfileStat(label = "Project Manager", value = "0"),
        ),
        menuSections = listOf(
            ProfileMenuSection(
                title = "Preferences",
                items = listOf(
                    ProfileMenuItem(
                        title = "Settings",
                        icon = Icons.Default.Settings,
                        onClick = onSettingsClick
                    ),
                )
            ),
            ProfileMenuSection(
                title = "Account",
                items = listOf(
                    ProfileMenuItem(
                        title = "Log Out",
                        icon = Icons.Default.Lock,
                        onClick = onLogoutClick
                    ),
                )
            ),
        )
    )
}

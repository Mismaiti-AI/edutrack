package com.edutrack.presentation.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.edutrack.ui.atoms.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userName: String = "User",
    userEmail: String = "",
    onBackClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile", fontWeight = FontWeight.ExtraBold) },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(24.dp))

            // Avatar
            AppAvatar(
                size = 80.dp,
                initials = userName.take(2).uppercase(),
                hasRing = true,
            )
            Spacer(Modifier.height(12.dp))

            // Name + email
            Text(
                userName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
            )
            if (userEmail.isNotBlank()) {
                Text(
                    userEmail,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(Modifier.height(20.dp))

            // Stats row
            StatRow(
                stats = listOf(
                    StatItem(value = "0", label = "Assignments"),
                    StatItem(value = "0", label = "Exams"),
                    StatItem(value = "0", label = "Projects"),
                ),
            )
            Spacer(Modifier.height(24.dp))

            // Menu items
            AppCard {
                Column(modifier = Modifier.padding(4.dp)) {
                    AppListTile(
                        title = "Settings",
                        leadingContent = { Icon(Icons.Default.Settings, null, tint = MaterialTheme.colorScheme.primary) },
                        onClick = onSettingsClick,
                    )
                }
            }
        }
    }
}

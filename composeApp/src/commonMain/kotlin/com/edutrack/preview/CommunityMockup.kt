package com.edutrack.preview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.edutrack.core.presentation.components.ListItemCard
import com.edutrack.core.presentation.components.SectionHeader
import com.edutrack.core.presentation.components.StatCard
import com.edutrack.core.presentation.components.StatusBadge

private data class CommunityPost(
    val author: String,
    val initials: String,
    val snippet: String,
    val timeAgo: String,
    val tag: String
)

private val mockComPosts = listOf(
    CommunityPost("Sarah M.", "SM", "Just moved to Elm Street — any recommendations for local parks?", "2h ago", "Introduction"),
    CommunityPost("David K.", "DK", "Reminder: neighborhood cleanup this Saturday at 9 AM!", "5h ago", "Event"),
    CommunityPost("Aisha R.", "AR", "Lost tabby cat near Oak Ave. Please contact if found.", "8h ago", "Alert"),
    CommunityPost("James L.", "JL", "Great turnout at the block party yesterday! Photos inside.", "1d ago", "Social"),
    CommunityPost("Maria G.", "MG", "Anyone know a good plumber? Kitchen sink is leaking.", "1d ago", "Help")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityMockup() {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Community Hub") },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Notifications, "Notifications")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Icon(Icons.Default.Home, "Home") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = {},
                    icon = { Icon(Icons.Default.Search, "Explore") },
                    label = { Text("Explore") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = {},
                    icon = { Icon(Icons.Default.DateRange, "Events") },
                    label = { Text("Events") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = {},
                    icon = { Icon(Icons.Default.Person, "Profile") },
                    label = { Text("Profile") }
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Greeting
            item {
                Text(
                    text = "Welcome back, Alex",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // Stats
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatCard(modifier = Modifier.weight(1f), label = "Members", value = "1,248", icon = Icons.Default.Person)
                    StatCard(modifier = Modifier.weight(1f), label = "Posts", value = "87", icon = Icons.Default.Create)
                    StatCard(modifier = Modifier.weight(1f), label = "Events", value = "12", icon = Icons.Default.DateRange)
                }
            }

            // Quick Actions
            item { SectionHeader(title = "Quick Actions") }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FilledTonalButton(onClick = {}, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.Create, null, modifier = Modifier.padding(end = 8.dp))
                        Text("New Post", maxLines = 1)
                    }
                    FilledTonalButton(onClick = {}, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.DateRange, null, modifier = Modifier.padding(end = 8.dp))
                        Text("Create Event", maxLines = 1)
                    }
                }
            }

            // Recent Activity
            item {
                SectionHeader(title = "Recent Activity", actionText = "See All", onActionClick = {})
            }
            items(mockComPosts) { post ->
                Surface(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    shape = MaterialTheme.shapes.medium,
                    tonalElevation = 1.dp
                ) {
                    ListItemCard(
                        title = post.author,
                        subtitle = post.snippet,
                        caption = post.timeAgo,
                        avatarText = post.initials,
                        trailingContent = { StatusBadge(text = post.tag) }
                    )
                }
            }
        }
    }
}

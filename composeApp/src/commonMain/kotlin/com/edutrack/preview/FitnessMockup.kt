package com.edutrack.preview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Stars
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.edutrack.core.data.charts.ChartDataPoint
import com.edutrack.core.presentation.components.BarChart
import com.edutrack.core.presentation.components.ChartCard
import com.edutrack.core.presentation.components.ListItemCard
import com.edutrack.core.presentation.components.SectionHeader
import com.edutrack.core.presentation.components.StatCard
import com.edutrack.core.presentation.components.StatusBadge

private data class Workout(
    val name: String,
    val duration: String,
    val calories: String,
    val timeAgo: String,
    val type: String
)

private val weeklyActivity = listOf(
    ChartDataPoint("Mon", 45f),
    ChartDataPoint("Tue", 30f),
    ChartDataPoint("Wed", 60f),
    ChartDataPoint("Thu", 0f),
    ChartDataPoint("Fri", 50f),
    ChartDataPoint("Sat", 75f),
    ChartDataPoint("Sun", 40f)
)

private val recentWorkouts = listOf(
    Workout("Morning Run", "32 min", "320 cal", "Today", "Cardio"),
    Workout("Upper Body Strength", "45 min", "280 cal", "Yesterday", "Strength"),
    Workout("Yoga Flow", "60 min", "180 cal", "2 days ago", "Flexibility"),
    Workout("HIIT Circuit", "25 min", "350 cal", "3 days ago", "Cardio"),
    Workout("Leg Day", "50 min", "310 cal", "4 days ago", "Strength")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FitnessMockup() {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("FitTrack") },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Settings, "Settings")
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
                    icon = { Icon(Icons.Default.FitnessCenter, "Workouts") },
                    label = { Text("Workouts") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = {},
                    icon = { Icon(Icons.AutoMirrored.Filled.ShowChart, "Stats") },
                    label = { Text("Stats") }
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
                    text = "Let's crush it, Taylor!",
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
                    StatCard(modifier = Modifier.weight(1f), label = "Workouts", value = "18", icon = Icons.Default.FitnessCenter)
                    StatCard(modifier = Modifier.weight(1f), label = "Calories", value = "8.4K", icon = Icons.Default.LocalFireDepartment, iconTint = Color(0xFFFF5722))
                    StatCard(modifier = Modifier.weight(1f), label = "Streak", value = "6 days", icon = Icons.Default.Stars, iconTint = Color(0xFFFF9800))
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
                        Icon(Icons.Default.FitnessCenter, null, modifier = Modifier.padding(end = 8.dp))
                        Text("Start Workout", maxLines = 1)
                    }
                    FilledTonalButton(onClick = {}, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.LocalFireDepartment, null, modifier = Modifier.padding(end = 8.dp))
                        Text("Log Activity", maxLines = 1)
                    }
                }
            }

            // Chart
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    ChartCard(
                        title = "Weekly Activity",
                        subtitle = "Minutes per day"
                    ) {
                        BarChart(
                            data = weeklyActivity,
                            modifier = Modifier.fillMaxWidth().height(180.dp),
                            barColor = Color(0xFF4CAF50)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Recent Workouts
            item {
                SectionHeader(title = "Recent Workouts", actionText = "See All", onActionClick = {})
            }
            items(recentWorkouts) { workout ->
                Surface(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    shape = MaterialTheme.shapes.medium,
                    tonalElevation = 1.dp
                ) {
                    ListItemCard(
                        title = workout.name,
                        subtitle = "${workout.duration} - ${workout.calories}",
                        caption = workout.timeAgo,
                        leadingIcon = Icons.Default.FitnessCenter,
                        trailingContent = { StatusBadge(text = workout.type) }
                    )
                }
            }
        }
    }
}

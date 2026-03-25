package com.edutrack.preview

// ============================================================
// MOCK FITNESS APP - Delete this entire 'preview' package when done
// ============================================================

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsScore
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.edutrack.core.presentation.components.DetailCard
import com.edutrack.core.presentation.components.DetailRow
import com.edutrack.core.presentation.components.InfoRow
import com.edutrack.core.presentation.components.ListItemCard
import com.edutrack.core.presentation.components.ProgressCard
import com.edutrack.core.presentation.components.StatusBadge
import com.edutrack.core.presentation.navigation.AppOrchestrator
import com.edutrack.core.presentation.navigation.NavigationTab
import com.edutrack.core.presentation.screens.DashboardStat
import com.edutrack.core.presentation.screens.FieldType
import com.edutrack.core.presentation.screens.FormField
import com.edutrack.core.presentation.screens.GenericDashboardScreen
import com.edutrack.core.presentation.screens.GenericDetailScreen
import com.edutrack.core.presentation.screens.GenericFormScreen
import com.edutrack.core.presentation.screens.GenericProfileScreen
import com.edutrack.core.presentation.screens.GenericSearchScreen
import com.edutrack.core.presentation.screens.GenericSettingsScreen
import com.edutrack.core.presentation.screens.GenericSplashScreen
import com.edutrack.core.presentation.screens.ProfileMenuItem
import com.edutrack.core.presentation.screens.ProfileMenuSection
import com.edutrack.core.presentation.screens.ProfileStat
import com.edutrack.core.presentation.screens.QuickAction
import com.edutrack.core.presentation.screens.SearchFilterChip
import com.edutrack.core.presentation.screens.SettingsItem
import com.edutrack.core.presentation.screens.SettingsSection
import kotlinx.serialization.Serializable

// ============================================================
// Fitness Routes (extend Routes for this domain)
// ============================================================

object FitnessRoutes {
    @Serializable object Dashboard
    @Serializable object Activity
    @Serializable object Profile
    @Serializable data class WorkoutDetail(val workoutId: String)
    @Serializable data class EditWorkout(val workoutId: String)
    @Serializable object LogWorkout
    @Serializable object Settings
}

// ============================================================
// Mock Data
// ============================================================

data class MockWorkout(
    val id: String,
    val name: String,
    val type: String,
    val duration: String,
    val calories: Int,
    val date: String,
    val intensity: String
)

val mockWorkouts = listOf(
    MockWorkout("w1", "Morning Run", "Cardio", "32 min", 320, "Today", "High"),
    MockWorkout("w2", "Upper Body", "Strength", "45 min", 280, "Today", "Medium"),
    MockWorkout("w3", "Yoga Flow", "Flexibility", "60 min", 180, "Yesterday", "Low"),
    MockWorkout("w4", "HIIT Session", "Cardio", "25 min", 400, "Yesterday", "High"),
    MockWorkout("w5", "Leg Day", "Strength", "50 min", 350, "Feb 14", "High"),
    MockWorkout("w6", "Evening Walk", "Cardio", "40 min", 150, "Feb 13", "Low"),
    MockWorkout("w7", "Core Blast", "Strength", "20 min", 200, "Feb 13", "Medium"),
)

// ============================================================
// Mock Fitness App Entry Point
// ============================================================

@Composable
fun MockFitnessApp() {
    var appState by remember { mutableStateOf(com.mismaiti.core.presentation.navigation.AppState.Splash) }

    AppOrchestrator(
        appState = appState,

        // --- Splash ---
        splashContent = {
            GenericSplashScreen(
                appName = "FitTrack",
                tagline = "Your personal fitness companion",
                icon = Icons.Default.FitnessCenter,
                durationMillis = 1500,
                onFinished = {
                    appState =
                        com.mismaiti.core.presentation.navigation.AppState.Home
                }
            )
        },

        // --- Home (bottom tabs) ---
        tabs = listOf(
            NavigationTab(
                FitnessRoutes.Dashboard,
                "Home",
                Icons.Default.Home
            ),
            NavigationTab(
                FitnessRoutes.Activity,
                "Activity",
                Icons.AutoMirrored.Filled.DirectionsRun
            ),
            NavigationTab(
                FitnessRoutes.Profile,
                "Profile",
                Icons.Default.Person
            ),
        ),
        homeStartDestination = FitnessRoutes.Dashboard,
        showTopBar = false,
        homeBuilder = { nav ->
            // Tab screens
            composable<FitnessRoutes.Dashboard> {
                FitnessDashboardTab(nav)
            }
            composable<FitnessRoutes.Activity> {
                FitnessActivityTab(nav)
            }
            composable<FitnessRoutes.Profile> {
                FitnessProfileTab(nav)
            }

            // Detail routes
            composable<FitnessRoutes.WorkoutDetail> { entry ->
                val detail = entry.toRoute<FitnessRoutes.WorkoutDetail>()
                FitnessWorkoutDetail(detail.workoutId, nav)
            }
            composable<FitnessRoutes.EditWorkout> { entry ->
                val edit = entry.toRoute<FitnessRoutes.EditWorkout>()
                FitnessWorkoutForm(edit.workoutId, nav)
            }
            composable<FitnessRoutes.LogWorkout> {
                FitnessWorkoutForm(null, nav)
            }
            composable<FitnessRoutes.Settings> {
                FitnessSettingsScreen(nav)
            }
        }
    )
}

// ============================================================
// Fitness Screens
// ============================================================

@Composable
private fun FitnessDashboardTab(nav: NavHostController) {
    GenericDashboardScreen(
        title = "FitTrack",
        greeting = "Keep going, Sarah!",
        stats = listOf(
            DashboardStat(
                "Calories", "1,430", Icons.Default.LocalFireDepartment,
                iconTint = MaterialTheme.colorScheme.error
            ),
            DashboardStat(
                "Workouts",
                "5",
                Icons.Default.FitnessCenter
            ),
            DashboardStat(
                "Minutes",
                "212",
                Icons.Default.Timer
            ),
            DashboardStat(
                "Streak",
                "12 days",
                Icons.AutoMirrored.Filled.TrendingUp
            ),
        ),
        quickActions = listOf(
            QuickAction(
                "Log Workout",
                Icons.Default.Add
            ) {
                nav.navigate(FitnessRoutes.LogWorkout)
            },
            QuickAction(
                "Activity",
                Icons.AutoMirrored.Filled.DirectionsRun
            ) {
                nav.navigate(FitnessRoutes.Activity)
            },
        ),
        recentItems = mockWorkouts.take(3),
        recentTitle = "Today's Workouts",
        onRecentItemClick = { nav.navigate(FitnessRoutes.WorkoutDetail(it.id)) },
        recentItemContent = { workout ->
            ListItemCard(
                title = workout.name,
                subtitle = "${workout.type} · ${workout.duration}",
                caption = "${workout.calories} cal",
                leadingIcon = when (workout.type) {
                    "Cardio" -> Icons.AutoMirrored.Filled.DirectionsRun
                    "Strength" -> Icons.Default.FitnessCenter
                    else -> Icons.Default.SportsScore
                },
                showChevron = true,
                trailingContent = {
                    StatusBadge(text = workout.intensity)
                }
            )
        },
        onSettingsClick = { nav.navigate(FitnessRoutes.Settings) },
        extraContent = {
            ProgressCard(
                title = "Weekly Goal",
                progress = 0.72f,
                subtitle = "5 of 7 workouts completed"
            )
            ProgressCard(
                title = "Calorie Target",
                progress = 0.85f,
                subtitle = "1,430 of 1,680 cal burned"
            )
        }
    )
}

@Composable
private fun FitnessActivityTab(nav: NavHostController) {
    var query by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("all") }

    val filters = listOf(
        SearchFilterChip(
            "all",
            "All",
            selectedFilter == "all"
        ),
        SearchFilterChip(
            "cardio",
            "Cardio",
            selectedFilter == "cardio"
        ),
        SearchFilterChip(
            "strength",
            "Strength",
            selectedFilter == "strength"
        ),
        SearchFilterChip(
            "flexibility",
            "Flexibility",
            selectedFilter == "flexibility"
        ),
    )

    val filtered = mockWorkouts.filter { workout ->
        val matchesQuery = query.isBlank() ||
                workout.name.contains(query, ignoreCase = true)
        val matchesFilter = selectedFilter == "all" ||
                workout.type.equals(selectedFilter, ignoreCase = true)
        matchesQuery && matchesFilter
    }

    GenericSearchScreen(
        query = query,
        onQueryChange = { query = it },
        results = filtered,
        filterChips = filters,
        onFilterClick = { selectedFilter = it.key },
        onResultClick = { nav.navigate(FitnessRoutes.WorkoutDetail(it.id)) },
        recentSearches = listOf("HIIT", "Morning Run", "Yoga"),
        onRecentClick = { query = it },
        onClearRecent = { },
        emptyMessage = "No workouts found"
    ) { workout ->
       ListItemCard(
            title = workout.name,
            subtitle = "${workout.type} · ${workout.duration} · ${workout.calories} cal",
            caption = workout.date,
            leadingIcon = when (workout.type) {
                "Cardio" -> Icons.AutoMirrored.Filled.DirectionsRun
                "Strength" -> Icons.Default.FitnessCenter
                else -> Icons.Default.SportsScore
            },
            showChevron = true,
            trailingContent = {
                StatusBadge(text = workout.intensity)
            }
        )
    }
}

@Composable
private fun FitnessProfileTab(nav: NavHostController) {
    GenericProfileScreen(
        name = "Sarah Johnson",
        subtitle = "Member since Jan 2025",
        showBack = false,
        stats = listOf(
            ProfileStat("Workouts", "156"),
            ProfileStat("Hours", "98"),
            ProfileStat("Streak", "12"),
        ),
        menuSections = listOf(
            ProfileMenuSection(
                title = "Body",
                items = listOf(
                    ProfileMenuItem(
                        title = "Weight Log",
                        subtitle = "68 kg · Last updated today",
                        icon = Icons.Default.Scale,
                        onClick = { }
                    ),
                    ProfileMenuItem(
                        title = "Heart Rate",
                        subtitle = "Avg 72 bpm",
                        icon = Icons.Default.MonitorHeart,
                        onClick = { }
                    ),
                )
            ),
            ProfileMenuSection(
                title = "Goals",
                items = listOf(
                    ProfileMenuItem(
                        title = "Weekly Target",
                        subtitle = "7 workouts per week",
                        icon = Icons.Default.SportsScore,
                        onClick = { }
                    ),
                    ProfileMenuItem(
                        title = "Calorie Goal",
                        subtitle = "1,680 cal / day",
                        icon = Icons.Default.LocalFireDepartment,
                        onClick = { }
                    ),
                )
            ),
            ProfileMenuSection(
                title = "Account",
                items = listOf(
                    ProfileMenuItem(
                        title = "Edit Profile",
                        icon = Icons.Default.Edit,
                        onClick = { }
                    ),
                    ProfileMenuItem(
                        title = "Settings",
                        icon = Icons.Default.Settings,
                        onClick = { nav.navigate(FitnessRoutes.Settings) }
                    ),
                )
            ),
        )
    )
}

@Composable
private fun FitnessWorkoutDetail(workoutId: String, nav: NavHostController) {
    val workout = mockWorkouts.find { it.id == workoutId }

    GenericDetailScreen(
        title = workout?.name ?: "Workout",
        item = workout,
        onBackClick = { nav.popBackStack() },
        onEditClick = { nav.navigate(FitnessRoutes.EditWorkout(workoutId)) },
        onDeleteClick = { nav.popBackStack() },
    ) { w ->
        DetailCard(
            title = "Workout Summary",
            icon = Icons.Default.FitnessCenter,
            rows = listOf(
                DetailRow(
                    "Type",
                    w.type
                ),
                DetailRow("Intensity") {
                    StatusBadge(text = w.intensity)
                },
                DetailRow(
                    "Duration",
                    w.duration
                ),
                DetailRow(
                    "Calories",
                    "${w.calories} cal"
                ),
                DetailRow(
                    "Date",
                    w.date
                ),
            )
        )

        InfoRow(
            label = "Category",
            value = w.type,
            icon = Icons.Default.FitnessCenter
        )
        InfoRow(
            label = "Duration",
            value = w.duration,
            icon = Icons.Default.Timer
        )
        InfoRow(
            label = "Calories Burned", value = "${w.calories} cal",
            icon = Icons.Default.LocalFireDepartment
        )
        InfoRow(
            label = "Date",
            value = w.date,
            icon = Icons.Default.CalendarMonth
        )
    }
}

@Composable
private fun FitnessWorkoutForm(workoutId: String?, nav: NavHostController) {
    val isEdit = workoutId != null
    val workout = workoutId?.let { id -> mockWorkouts.find { it.id == id } }

    val fieldValues = remember {
        mutableStateMapOf(
            "name" to (workout?.name ?: ""),
            "type" to (workout?.type ?: "Cardio"),
            "duration" to (workout?.duration?.removeSuffix(" min") ?: ""),
            "calories" to (workout?.calories?.toString() ?: ""),
            "intensity" to (workout?.intensity ?: "Medium"),
        )
    }

    GenericFormScreen(
        title = if (isEdit) "Edit Workout" else "Log Workout",
        fields = listOf(
            FormField(
                key = "name",
                label = "Workout Name",
                value = fieldValues["name"] ?: "",
                required = true,
                placeholder = "e.g. Morning Run"
            ),
            FormField(
                key = "type",
                label = "Type",
                value = fieldValues["type"] ?: "",
                type = FieldType.Dropdown,
                options = listOf("Cardio", "Strength", "Flexibility", "HIIT", "Sports")
            ),
            FormField(
                key = "duration",
                label = "Duration (minutes)",
                value = fieldValues["duration"] ?: "",
                type = FieldType.Number,
                required = true,
                placeholder = "30"
            ),
            FormField(
                key = "calories",
                label = "Calories Burned",
                value = fieldValues["calories"] ?: "",
                type = FieldType.Number,
                placeholder = "0"
            ),
            FormField(
                key = "intensity",
                label = "Intensity",
                value = fieldValues["intensity"] ?: "",
                type = FieldType.RadioGroup,
                options = listOf("Low", "Medium", "High")
            ),
        ),
        onFieldChange = { key, value -> fieldValues[key] = value },
        onSubmit = { nav.popBackStack() },
        onBackClick = { nav.popBackStack() },
        submitText = if (isEdit) "Update" else "Log Workout"
    )
}

@Composable
private fun FitnessSettingsScreen(nav: NavHostController) {
    var notifications by remember { mutableStateOf(true) }
    var reminders by remember { mutableStateOf(true) }
    var darkMode by remember { mutableStateOf(false) }

    GenericSettingsScreen(
        title = "Settings",
        onBackClick = { nav.popBackStack() },
        sections = listOf(
            SettingsSection(
                title = "Notifications",
                items = listOf(
                    SettingsItem.Toggle(
                        title = "Workout Reminders",
                        subtitle = "Daily at 7:00 AM",
                        icon = Icons.Default.Notifications,
                        checked = reminders,
                        onCheckedChange = { reminders = it }
                    ),
                    SettingsItem.Toggle(
                        title = "Progress Updates",
                        subtitle = "Weekly summary",
                        icon = Icons.AutoMirrored.Filled.TrendingUp,
                        checked = notifications,
                        onCheckedChange = { notifications = it }
                    ),
                )
            ),
            SettingsSection(
                title = "Preferences",
                items = listOf(
                    SettingsItem.Toggle(
                        title = "Dark Mode",
                        icon = Icons.Default.DarkMode,
                        checked = darkMode,
                        onCheckedChange = { darkMode = it }
                    ),
                    SettingsItem.Navigation(
                        title = "Units",
                        subtitle = "Metric (kg, km)",
                        icon = Icons.Default.Scale,
                        onClick = { }
                    ),
                    SettingsItem.Navigation(
                        title = "Language",
                        subtitle = "English",
                        icon = Icons.Default.Language,
                        onClick = { }
                    ),
                )
            ),
            SettingsSection(
                title = "About",
                items = listOf(
                    SettingsItem.Info(
                        title = "Version",
                        value = "2.1.0"
                    ),
                    SettingsItem.Info(
                        title = "Total Workouts",
                        value = "156"
                    ),
                )
            ),
        )
    )
}

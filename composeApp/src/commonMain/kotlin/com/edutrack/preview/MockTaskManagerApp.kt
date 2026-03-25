package com.edutrack.preview

// ============================================================
// MOCK TASK MANAGER APP - Delete this entire 'preview' package when done
// ============================================================

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
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
import com.edutrack.core.presentation.components.ChipGroup
import com.edutrack.core.presentation.components.ConfirmDialog
import com.edutrack.core.presentation.components.DetailCard
import com.edutrack.core.presentation.components.DetailRow
import com.edutrack.core.presentation.components.InfoRow
import com.edutrack.core.presentation.components.ListItemCard
import com.edutrack.core.presentation.components.ProgressCard
import com.edutrack.core.presentation.components.StatusBadge
import com.edutrack.core.presentation.navigation.AppOrchestrator
import com.edutrack.core.presentation.navigation.AppState
import com.edutrack.core.presentation.navigation.NavigationTab
import com.edutrack.core.presentation.screens.DashboardStat
import com.edutrack.core.presentation.screens.FieldType
import com.edutrack.core.presentation.screens.FormField
import com.edutrack.core.presentation.screens.GenericDashboardScreen
import com.edutrack.core.presentation.screens.GenericDetailScreen
import com.edutrack.core.presentation.screens.GenericFormScreen
import com.edutrack.core.presentation.screens.GenericListScreen
import com.edutrack.core.presentation.screens.GenericProfileScreen
import com.edutrack.core.presentation.screens.GenericSettingsScreen
import com.edutrack.core.presentation.screens.GenericSplashScreen
import com.edutrack.core.presentation.screens.GenericTabScreen
import com.edutrack.core.presentation.screens.ProfileMenuSection
import com.edutrack.core.presentation.screens.ProfileMenuItem
import com.edutrack.core.presentation.screens.ProfileStat
import com.edutrack.core.presentation.screens.QuickAction
import com.edutrack.core.presentation.screens.SettingsItem
import com.edutrack.core.presentation.screens.SettingsSection
import com.edutrack.core.presentation.screens.TabItem
import kotlinx.serialization.Serializable

// ============================================================
// Task Manager Routes
// ============================================================

object TaskRoutes {
    @Serializable object Dashboard
    @Serializable object Lists
    @Serializable object Profile
    @Serializable data class TaskDetail(val taskId: String)
    @Serializable data class EditTask(val taskId: String)
    @Serializable object CreateTask
    @Serializable object Settings
}

// ============================================================
// Mock Data
// ============================================================

data class MockTask(
    val id: String,
    val title: String,
    val description: String,
    val project: String,
    val priority: String,
    val status: String,
    val dueDate: String,
    val tags: List<String>
)

val mockTasks = listOf(
    MockTask("t1", "Design homepage wireframe", "Create lo-fi wireframes for the new landing page", "Website Redesign", "High", "In Progress", "Today", listOf("design", "urgent")),
    MockTask("t2", "Fix login bug on iOS", "Users report crash on iOS 17 when using biometric login", "Mobile App", "High", "To Do", "Today", listOf("bug", "ios")),
    MockTask("t3", "Write API documentation", "Document all REST endpoints for v2 API", "Backend", "Medium", "In Progress", "Tomorrow", listOf("docs", "api")),
    MockTask("t4", "Set up CI/CD pipeline", "Configure GitHub Actions for automated testing and deployment", "DevOps", "Medium", "To Do", "Feb 18", listOf("devops", "automation")),
    MockTask("t5", "Review pull request #42", "Code review for the new payment integration", "Mobile App", "Low", "To Do", "Feb 19", listOf("review", "payment")),
    MockTask("t6", "Update onboarding flow", "Simplify the 5-step onboarding to 3 steps", "Mobile App", "Medium", "Done", "Feb 14", listOf("ux", "mobile")),
    MockTask("t7", "Database migration script", "Migrate user table to new schema with soft deletes", "Backend", "High", "Done", "Feb 13", listOf("database", "migration")),
    MockTask("t8", "Weekly team standup notes", "Prepare agenda and share meeting notes", "General", "Low", "Done", "Feb 13", listOf("meeting")),
)

val mockProjects = listOf("Website Redesign", "Mobile App", "Backend", "DevOps", "General")

// ============================================================
// Mock Task Manager App Entry Point
// ============================================================

@Composable
fun MockTaskManagerApp() {
    var appState by remember { mutableStateOf(AppState.Splash) }

    AppOrchestrator(
        appState = appState,

        splashContent = {
            GenericSplashScreen(
                appName = "Taskly",
                tagline = "Get things done, effortlessly",
                icon = Icons.AutoMirrored.Filled.Assignment,
                durationMillis = 1500,
                onFinished = {
                    appState =
                        AppState.Home
                }
            )
        },

        tabs = listOf(
            NavigationTab(
                TaskRoutes.Dashboard,
                "Home",
                Icons.Default.Home
            ),
            NavigationTab(
                TaskRoutes.Lists,
                "Lists",
                Icons.Default.Folder
            ),
            NavigationTab(
                TaskRoutes.Profile,
                "Profile",
                Icons.Default.Person
            ),
        ),
        homeStartDestination = TaskRoutes.Dashboard,
        showTopBar = false,
        homeBuilder = { nav ->
            composable<TaskRoutes.Dashboard> {
                TaskDashboardTab(nav)
            }
            composable<TaskRoutes.Lists> {
                TaskListsTab(nav)
            }
            composable<TaskRoutes.Profile> {
                TaskProfileTab(nav)
            }
            composable<TaskRoutes.TaskDetail> { entry ->
                val detail = entry.toRoute<TaskRoutes.TaskDetail>()
                TaskDetailScreen(detail.taskId, nav)
            }
            composable<TaskRoutes.EditTask> { entry ->
                val edit = entry.toRoute<TaskRoutes.EditTask>()
                TaskFormScreen(edit.taskId, nav)
            }
            composable<TaskRoutes.CreateTask> {
                TaskFormScreen(null, nav)
            }
            composable<TaskRoutes.Settings> {
                TaskSettingsScreen(nav)
            }
        }
    )
}

// ============================================================
// Dashboard Tab
// ============================================================

@Composable
private fun TaskDashboardTab(nav: NavHostController) {
    val activeTasks = mockTasks.filter { it.status != "Done" }
    val doneTasks = mockTasks.filter { it.status == "Done" }

    GenericDashboardScreen<MockTask>(
        title = "Taskly",
        greeting = "Good morning, Alex!",
        stats = listOf(
            DashboardStat(
                "Active",
                "${activeTasks.size}",
                Icons.Default.RadioButtonUnchecked
            ),
            DashboardStat(
                "Done", "${doneTasks.size}", Icons.Default.CheckCircle,
                iconTint = MaterialTheme.colorScheme.primary
            ),
            DashboardStat(
                "High", "${mockTasks.count { it.priority == "High" }}", Icons.Default.Flag,
                iconTint = MaterialTheme.colorScheme.error
            ),
            DashboardStat(
                "Projects",
                "${mockProjects.size}",
                Icons.Default.Folder
            ),
        ),
        quickActions = listOf(
            QuickAction(
                "New Task",
                Icons.Default.Add
            ) {
                nav.navigate(TaskRoutes.CreateTask)
            },
            QuickAction(
                "Lists",
                Icons.Default.Folder
            ) {
                nav.navigate(TaskRoutes.Lists)
            },
        ),
        recentItems = activeTasks.take(3),
        recentTitle = "Due Today",
        onRecentItemClick = { nav.navigate(TaskRoutes.TaskDetail(it.id)) },
        recentItemContent = { task ->
            ListItemCard(
                title = task.title,
                subtitle = "${task.project} · ${task.priority} priority",
                caption = task.dueDate,
                leadingIcon = when (task.priority) {
                    "High" -> Icons.Default.Flag
                    "Medium" -> Icons.Default.Schedule
                    else -> Icons.Default.RadioButtonUnchecked
                },
                showChevron = true,
                trailingContent = {
                    StatusBadge(text = task.status)
                }
            )
        },
        onSettingsClick = { nav.navigate(TaskRoutes.Settings) },
        extraContent = {
            ProgressCard(
                title = "Weekly Progress",
                progress = doneTasks.size.toFloat() / mockTasks.size,
                subtitle = "${doneTasks.size} of ${mockTasks.size} tasks completed"
            )
            ProgressCard(
                title = "High Priority",
                progress = mockTasks.count { it.priority == "High" && it.status == "Done" }
                    .toFloat() /
                        mockTasks.count { it.priority == "High" }.coerceAtLeast(1),
                subtitle = "Critical tasks remaining: ${mockTasks.count { it.priority == "High" && it.status != "Done" }}"
            )
        }
    )
}

// ============================================================
// Lists Tab - Uses GenericTabScreen + GenericListScreen
// ============================================================

@Composable
private fun TaskListsTab(nav: NavHostController) {
    val allTasks = mockTasks.filter { it.status != "Done" }
    val doneTasks = mockTasks.filter { it.status == "Done" }

    GenericTabScreen(
        title = "Task Lists",
        showBack = false,
        tabs = listOf(
            TabItem(
                title = "Active",
                icon = Icons.Default.RadioButtonUnchecked,
                badge = "${allTasks.size}"
            ) {
                GenericListScreen(
                    title = "Active Tasks",
                    items = allTasks,
                    onItemClick = { nav.navigate(TaskRoutes.TaskDetail(it.id)) },
                    onAddClick = { nav.navigate(TaskRoutes.CreateTask) },
                    emptyMessage = "No active tasks. Take a break!"
                ) { task ->
                    ListItemCard(
                        title = task.title,
                        subtitle = "${task.project} · Due ${task.dueDate}",
                        caption = task.priority,
                        leadingIcon = when (task.priority) {
                            "High" -> Icons.Default.Flag
                            "Medium" -> Icons.Default.Schedule
                            else -> Icons.Default.RadioButtonUnchecked
                        },
                        showChevron = true,
                        trailingContent = {
                            StatusBadge(
                                text = task.status
                            )
                        }
                    )
                }
            },
            TabItem(
                title = "Completed",
                icon = Icons.Default.CheckCircle,
                badge = "${doneTasks.size}"
            ) {
                GenericListScreen(
                    title = "Completed",
                    items = doneTasks,
                    onItemClick = { nav.navigate(TaskRoutes.TaskDetail(it.id)) },
                    emptyMessage = "No completed tasks yet"
                ) { task ->
                    ListItemCard(
                        title = task.title,
                        subtitle = "${task.project} · Completed ${task.dueDate}",
                        leadingIcon = Icons.Default.CheckCircle,
                        showChevron = true
                    )
                }
            },
            TabItem(
                title = "By Project",
                icon = Icons.Default.Folder
            ) {
                ProjectGroupedList(nav)
            }
        )
    )
}

@Composable
private fun ProjectGroupedList(nav: NavHostController) {
    val projectTasks = mockProjects.map { project ->
        project to mockTasks.filter { it.project == project }
    }.filter { it.second.isNotEmpty() }

    GenericListScreen(
        title = "By Project",
        items = projectTasks,
        onItemClick = { },
        emptyMessage = "No projects found"
    ) { (project, tasks) ->
        ListItemCard(
            title = project,
            subtitle = "${tasks.count { it.status != "Done" }} active · ${tasks.count { it.status == "Done" }} done",
            caption = "${tasks.size} total tasks",
            leadingIcon = Icons.Default.Folder,
            showChevron = true
        )
    }
}

// ============================================================
// Profile Tab
// ============================================================

@Composable
private fun TaskProfileTab(nav: NavHostController) {
    val completedCount = mockTasks.count { it.status == "Done" }

    GenericProfileScreen(
        name = "Alex Rivera",
        subtitle = "Product Designer · Using Taskly since Jan 2025",
        showBack = false,
        stats = listOf(
            ProfileStat(
                "Tasks",
                "${mockTasks.size}"
            ),
            ProfileStat(
                "Done",
                "$completedCount"
            ),
            ProfileStat("Streak", "5 days"),
        ),
        menuSections = listOf(
            ProfileMenuSection(
                title = "Productivity",
                items = listOf(
                    ProfileMenuItem(
                        title = "This Week",
                        subtitle = "$completedCount tasks completed",
                        icon = Icons.Default.CheckCircle,
                        onClick = { }
                    ),
                    ProfileMenuItem(
                        title = "Favorites",
                        subtitle = "3 starred tasks",
                        icon = Icons.Default.Star,
                        onClick = { }
                    ),
                )
            ),
            ProfileMenuSection(
                title = "Workspace",
                items = listOf(
                    ProfileMenuItem(
                        title = "Projects",
                        subtitle = "${mockProjects.size} active projects",
                        icon = Icons.Default.Folder,
                        onClick = { }
                    ),
                    ProfileMenuItem(
                        title = "Categories",
                        subtitle = "Manage task categories",
                        icon = Icons.Default.Category,
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
                        onClick = { nav.navigate(TaskRoutes.Settings) }
                    ),
                )
            ),
        )
    )
}

// ============================================================
// Task Detail - Uses ConfirmDialog, ChipGroup
// ============================================================

@Composable
private fun TaskDetailScreen(taskId: String, nav: NavHostController) {
    val task = mockTasks.find { it.id == taskId }
    var showDeleteDialog by remember { mutableStateOf(false) }

    ConfirmDialog(
        show = showDeleteDialog,
        title = "Delete Task",
        message = "Are you sure you want to delete \"${task?.title}\"? This action cannot be undone.",
        onConfirm = {
            showDeleteDialog = false
            nav.popBackStack()
        },
        onDismiss = { showDeleteDialog = false },
        confirmText = "Delete",
        isDestructive = true
    )

    GenericDetailScreen(
        title = task?.title ?: "Task",
        item = task,
        onBackClick = { nav.popBackStack() },
        onEditClick = { nav.navigate(TaskRoutes.EditTask(taskId)) },
        onDeleteClick = { showDeleteDialog = true },
    ) { t ->
        DetailCard(
            title = "Task Details",
            icon = Icons.AutoMirrored.Filled.Assignment,
            rows = listOf(
                DetailRow("Status") {
                    StatusBadge(text = t.status)
                },
                DetailRow("Priority") {
                    StatusBadge(text = t.priority)
                },
                DetailRow(
                    "Project",
                    t.project
                ),
                DetailRow(
                    "Due Date",
                    t.dueDate
                ),
            )
        )

        DetailCard(
            title = "Description",
            icon = Icons.Default.Edit,
            rows = listOf(
                DetailRow(
                    "",
                    t.description
                ),
            )
        )

        ChipGroup(
            chips = t.tags,
            selectedChips = t.tags.toSet(),
            onChipClick = { }
        )

        InfoRow(
            label = "Project",
            value = t.project,
            icon = Icons.Default.Folder
        )
        InfoRow(
            label = "Priority",
            value = t.priority,
            icon = Icons.Default.Flag
        )
        InfoRow(
            label = "Due Date",
            value = t.dueDate,
            icon = Icons.Default.CalendarMonth
        )
        InfoRow(
            label = "Status",
            value = t.status,
            icon = Icons.AutoMirrored.Filled.Assignment
        )
    }
}

// ============================================================
// Task Form - Uses Checkbox (SwitchRow) for toggles
// ============================================================

@Composable
private fun TaskFormScreen(taskId: String?, nav: NavHostController) {
    val isEdit = taskId != null
    val task = taskId?.let { id -> mockTasks.find { it.id == id } }

    val fieldValues = remember {
        mutableStateMapOf(
            "title" to (task?.title ?: ""),
            "description" to (task?.description ?: ""),
            "project" to (task?.project ?: "Website Redesign"),
            "priority" to (task?.priority ?: "Medium"),
            "status" to (task?.status ?: "To Do"),
            "dueDate" to (task?.dueDate ?: ""),
        )
    }

    GenericFormScreen(
        title = if (isEdit) "Edit Task" else "New Task",
        fields = listOf(
            FormField(
                key = "title",
                label = "Task Title",
                value = fieldValues["title"] ?: "",
                required = true,
                placeholder = "What needs to be done?"
            ),
            FormField(
                key = "description",
                label = "Description",
                value = fieldValues["description"] ?: "",
                type = FieldType.MultiLine,
                placeholder = "Add details about this task..."
            ),
            FormField(
                key = "project",
                label = "Project",
                value = fieldValues["project"] ?: "",
                type = FieldType.Dropdown,
                options = mockProjects
            ),
            FormField(
                key = "priority",
                label = "Priority",
                value = fieldValues["priority"] ?: "",
                type = FieldType.RadioGroup,
                options = listOf("Low", "Medium", "High")
            ),
            FormField(
                key = "status",
                label = "Status",
                value = fieldValues["status"] ?: "",
                type = FieldType.Dropdown,
                options = listOf("To Do", "In Progress", "Done")
            ),
            FormField(
                key = "dueDate",
                label = "Due Date",
                value = fieldValues["dueDate"] ?: "",
                placeholder = "e.g. Tomorrow, Feb 20"
            ),
        ),
        onFieldChange = { key, value -> fieldValues[key] = value },
        onSubmit = { nav.popBackStack() },
        onBackClick = { nav.popBackStack() },
        submitText = if (isEdit) "Update Task" else "Create Task"
    )
}

// ============================================================
// Settings - Uses SwitchRow standalone
// ============================================================

@Composable
private fun TaskSettingsScreen(nav: NavHostController) {
    var notifications by remember { mutableStateOf(true) }
    var dailyReminder by remember { mutableStateOf(true) }
    var darkMode by remember { mutableStateOf(false) }

    GenericSettingsScreen(
        title = "Settings",
        onBackClick = { nav.popBackStack() },
        sections = listOf(
            SettingsSection(
                title = "Notifications",
                items = listOf(
                    SettingsItem.Toggle(
                        title = "Push Notifications",
                        subtitle = "Get notified about due tasks",
                        icon = Icons.Default.Notifications,
                        checked = notifications,
                        onCheckedChange = { notifications = it }
                    ),
                    SettingsItem.Toggle(
                        title = "Daily Reminder",
                        subtitle = "Morning summary at 8:00 AM",
                        icon = Icons.Default.Schedule,
                        checked = dailyReminder,
                        onCheckedChange = { dailyReminder = it }
                    ),
                )
            ),
            SettingsSection(
                title = "Appearance",
                items = listOf(
                    SettingsItem.Toggle(
                        title = "Dark Mode",
                        icon = Icons.Default.DarkMode,
                        checked = darkMode,
                        onCheckedChange = { darkMode = it }
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
                title = "Data",
                items = listOf(
                    SettingsItem.Navigation(
                        title = "Export Tasks",
                        subtitle = "Download as CSV",
                        icon = Icons.AutoMirrored.Filled.Assignment,
                        onClick = { }
                    ),
                    SettingsItem.Navigation(
                        title = "Delete All Data",
                        subtitle = "Remove all tasks and projects",
                        icon = Icons.Default.Delete,
                        onClick = { }
                    ),
                )
            ),
            SettingsSection(
                title = "About",
                items = listOf(
                    SettingsItem.Info(
                        title = "Version",
                        value = "1.2.0"
                    ),
                    SettingsItem.Info(
                        title = "Total Tasks",
                        value = "${mockTasks.size}"
                    ),
                )
            ),
        )
    )
}

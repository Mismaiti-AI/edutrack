package com.edutrack.preview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.edutrack.core.presentation.components.ListItemCard
import com.edutrack.core.presentation.components.ProgressCard
import com.edutrack.core.presentation.components.SectionHeader
import com.edutrack.core.presentation.components.StatusBadge
import com.edutrack.core.presentation.screens.DashboardStat
import com.edutrack.core.presentation.screens.GenericDashboardScreen
import com.edutrack.core.presentation.screens.QuickAction

private data class Course(
    val title: String,
    val instructor: String,
    val progress: Float,
    val lessons: String,
    val tag: String
)

private val activeCourses = listOf(
    Course("Kotlin for Beginners", "Dr. Elena Park", 0.72f, "18/25 lessons", "In Progress"),
    Course("UI/UX Design Fundamentals", "Mark Thompson", 0.45f, "9/20 lessons", "In Progress"),
    Course("Data Science with Python", "Prof. Amir Hassan", 0.15f, "3/20 lessons", "Just Started")
)

private val recommendedCourses = listOf(
    Course("Advanced Android Architecture", "Sarah Chen", 0f, "30 lessons", "New"),
    Course("Machine Learning Basics", "Dr. James Wu", 0f, "24 lessons", "Popular"),
    Course("Cloud Computing Essentials", "Lisa Fernandez", 0f, "16 lessons", "Trending")
)

@Composable
fun EducationMockup() {
    GenericDashboardScreen(
        title = "LearnHub",
        greeting = "Keep it up, Jordan!",
        stats = listOf(
            DashboardStat(label = "Courses", value = "5", icon = Icons.Default.MenuBook),
            DashboardStat(label = "Progress", value = "44%", icon = Icons.Default.School),
            DashboardStat(label = "Certificates", value = "2", icon = Icons.Default.Star),
        ),
        quickActions = listOf(
            QuickAction(label = "Browse", icon = Icons.Default.MenuBook, onClick = {}),
            QuickAction(label = "Resume", icon = Icons.Default.School, onClick = {})
        ),
        recentItems = recommendedCourses,
        recentTitle = "Recommended",
        onSeeAllClick = {},
        onSettingsClick = {},
        recentItemContent = { course ->
            ListItemCard(
                title = course.title,
                subtitle = course.instructor,
                caption = course.lessons,
                leadingIcon = Icons.Default.MenuBook,
                trailingContent = { StatusBadge(text = course.tag) }
            )
        },
        extraContent = {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                SectionHeader(title = "Active Courses")
                activeCourses.forEach { course ->
                    ProgressCard(
                        title = course.title,
                        progress = course.progress,
                        subtitle = "${(course.progress * 100).toInt()}% complete - ${course.lessons}",
                        icon = Icons.Default.MenuBook
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    )
}
